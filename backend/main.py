import os
import re
from dotenv import load_dotenv
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import List, Optional, Dict, Any
from collections import defaultdict, deque
import google.generativeai as genai
import time

load_dotenv()

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


class Pipeline(BaseModel):
    nodes: List[Dict[str, Any]]
    edges: List[Dict[str, Any]]


class RunPipeline(BaseModel):
    nodes: List[Dict[str, Any]]
    edges: List[Dict[str, Any]]
    inputs: Optional[Dict[str, str]] = {}


def is_dag(nodes: List[Dict[str, Any]], edges: List[Dict[str, Any]]) -> bool:
    """Check if graph is DAG using Kahn's algorithm."""
    node_ids = {node["id"] for node in nodes}
    adj = defaultdict(list)
    in_degree = defaultdict(int)

    for nid in node_ids:
        in_degree[nid] = 0

    for edge in edges:
        src = edge.get("source")
        tgt = edge.get("target")
        if src and tgt:
            adj[src].append(tgt)
            in_degree[tgt] += 1

    queue = deque([nid for nid in node_ids if in_degree[nid] == 0])
    visited = 0

    while queue:
        node = queue.popleft()
        visited += 1
        for neighbor in adj[node]:
            in_degree[neighbor] -= 1
            if in_degree[neighbor] == 0:
                queue.append(neighbor)

    return visited == len(node_ids)


def topological_sort(nodes, edges):
    node_ids = [n["id"] for n in nodes]
    adj = defaultdict(list)
    in_degree = {nid: 0 for nid in node_ids}

    for edge in edges:
        src = edge.get("source")
        tgt = edge.get("target")
        if src and tgt:
            adj[src].append(tgt)
            in_degree[tgt] = in_degree.get(tgt, 0) + 1

    queue = deque([nid for nid in node_ids if in_degree[nid] == 0])
    order = []

    while queue:
        nid = queue.popleft()
        order.append(nid)
        for neighbor in adj[nid]:
            in_degree[neighbor] -= 1
            if in_degree[neighbor] == 0:
                queue.append(neighbor)

    return order


def substitute_variables(template: str, variables: Dict[str, str]) -> str:
    """Basic mustache-style variable substitution {{ var }}"""
    def replacer(match):
        var_name = match.group(1).strip()
        return variables.get(var_name, match.group(0))

    return re.sub(r'\{\{\s*([a-zA-Z_$][a-zA-Z0-9_.$]*)\s*\}\}', replacer, template)


async def call_gemini(system: str, prompt: str, model: str, api_key: str = None):
    try:
        key = api_key or os.environ.get("GEMINI_API_KEY", "")
        if key:
            key = key.strip()
        
        if not key:
            return {"error": "Missing API Key. Please provide one in the node or .env"}

        genai.configure(api_key=key)

        m = genai.GenerativeModel(
            model_name=model,
            generation_config={"temperature": 0.7, "max_output_tokens": 2048},
            system_instruction=system if system else None,
        )

        response = m.generate_content(prompt)
        return {"text": response.text}

    except Exception as e:
        return {"error": str(e)}


@app.get("/")
def read_root():
    return {"status": "ok"}


@app.post("/pipelines/parse")
def parse_pipeline(pipeline: Pipeline):
    return {
        "num_nodes": len(pipeline.nodes),
        "num_edges": len(pipeline.edges),
        "is_dag": is_dag(pipeline.nodes, pipeline.edges)
    }


@app.post("/pipelines/run")
async def run_pipeline(pipeline: RunPipeline):
    nodes = pipeline.nodes
    edges = pipeline.edges
    input_values = pipeline.inputs or {}

    node_map = {n["id"]: n for n in nodes}
    
    # Topological sort ensures we process dependencies first
    order = topological_sort(nodes, edges)
    
    node_outputs = {}
    trace = []

    for nid in order:
        node = node_map.get(nid)
        if not node:
            continue

        node_type = node.get("type", "")
        data = node.get("data", {})
        
        output = ""
        error = None

        if node_type == "customInput":
            output = input_values.get(nid, data.get("inputValue", ""))
            
        elif node_type == "text":
            template = data.get("text", "")
            variables = {}
            
            # Find input values from connected nodes
            for edge in edges:
                if edge.get("target") == nid:
                    src_id = edge.get("source")
                    handle = edge.get("targetHandle", "")
                    var_name = handle.replace(f"{nid}-", "")
                    
                    if src_id in node_outputs:
                        variables[var_name] = node_outputs[src_id]

            output = substitute_variables(template, variables)

        elif node_type == "llm":
            sys_text = data.get("systemInstructions", "")
            prompt_text = data.get("prompt", "")
            model = data.get("model", "gemini-2.5-flash")
            api_key = data.get("apiKey", "")

            # Resolve inputs
            for edge in edges:
                if edge.get("target") == nid:
                    src_id = edge.get("source")
                    handle = edge.get("targetHandle", "")
                    
                    if src_id in node_outputs:
                        val = node_outputs[src_id]
                        if handle.endswith("-system"):
                            sys_text = val
                        elif handle.endswith("-prompt"):
                            prompt_text = val

            start = time.time()
            res = await call_gemini(sys_text, prompt_text, model, api_key)
            elapsed = round(time.time() - start, 3)

            if "error" in res:
                output = f"Error: {res['error']}"
                error = res["error"]
            else:
                output = res["text"]
                
            trace.append({
                "node": nid, 
                "type": "LLM", 
                "output": output, 
                "time": elapsed, 
                "error": error
            })
            
            node_outputs[nid] = output
            continue  # Already added to trace

        elif node_type == "customOutput":
            # Just grab the first input
            for edge in edges:
                if edge.get("target") == nid:
                    src_id = edge.get("source")
                    if src_id in node_outputs:
                        output = node_outputs[src_id]
                        break

        # Common trace append for non-LLM nodes
        node_outputs[nid] = output
        trace.append({
            "node": nid, 
            "type": node_type, 
            "output": output, 
            "time": 0
        })

    return {
        "outputs": node_outputs,
        "trace": trace,
        "num_nodes": len(nodes),
        "num_edges": len(edges),
        "is_dag": is_dag(nodes, edges),
    }

