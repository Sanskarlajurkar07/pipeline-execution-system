// textNode.js

import { useState, useEffect, useRef, useMemo } from 'react';
import { Handle, Position } from 'reactflow';
import BaseNode from './BaseNode';

export const TextNode = ({ id, data, isConnectable }) => {
  const [text, setText] = useState(data?.text || '{{input}}');
  const textareaRef = useRef(null);

  // extract variable names from {{ varName }} patterns
  const variables = useMemo(() => {
    const regex = /\{\{\s*([a-zA-Z_$][a-zA-Z0-9_$]*)\s*\}\}/g;
    const vars = [];
    let m;
    while ((m = regex.exec(text)) !== null) {
      if (!vars.includes(m[1])) vars.push(m[1]);
    }
    return vars;
  }, [text]);

  // auto-resize the textarea as user types
  useEffect(() => {
    const el = textareaRef.current;
    if (!el) return;
    el.style.height = 'auto';
    el.style.height = el.scrollHeight + 'px';
  }, [text]);

  // figure out how wide the node should be
  const lines = text.split('\n');
  const longest = Math.max(...lines.map((l) => l.length), 20);
  const nodeWidth = Math.min(480, Math.max(240, longest * 8 + 40));

  return (
    <BaseNode
      id={id}
      title="Text"

      color="#06b6d4"
      outputs={[{ id: 'output' }]}
      style={{ width: nodeWidth }}
      isConnectable={isConnectable}
    >
      <label className="field">
        <span>Text</span>
        <textarea
          ref={textareaRef}
          value={text}
          onChange={(e) => setText(e.target.value)}
          rows={1}
          className="node-textarea"
        />
      </label>

      {/* dynamic handles for each {{ variable }} */}
      {variables.map((v, i) => (
        <Handle
          key={v}
          type="target"
          position={Position.Left}
          id={`${id}-${v}`}
          className="handle handle-left"
          style={{ top: `${((i + 1) / (variables.length + 1)) * 100}%` }}
          isConnectable={isConnectable}
        />
      ))}
    </BaseNode>
  );
};
