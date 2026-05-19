// submit.js

import { useStore } from './store';

const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

export const SubmitButton = () => {
    const nodes = useStore((s) => s.nodes);
    const edges = useStore((s) => s.edges);
    const toggleRunPanel = useStore((s) => s.toggleRunPanel);
    const clearPipeline = useStore((s) => s.clearPipeline);

    const handleSubmit = async () => {
        try {
            const res = await fetch(`${API_URL}/pipelines/parse`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ nodes, edges }),
            });

            if (!res.ok) throw new Error(`Server error: ${res.status}`);

            const data = await res.json();
            alert(
                `Pipeline Analysis\n\n` +
                `Nodes: ${data.num_nodes}\n` +
                `Edges: ${data.num_edges}\n` +
                `Is DAG: ${data.is_dag ? 'Yes' : 'No'}`
            );
        } catch (err) {
            alert('Error: ' + err.message);
        }
    };

    return (
        <div className="submit-bar">
            <button className="submit-btn" onClick={handleSubmit}>
                Submit Pipeline
            </button>
            <button
                className="submit-btn run-btn"
                onClick={toggleRunPanel}
            >
                ▶ Run Pipeline
            </button>
            <button
                className="submit-btn"
                style={{ marginLeft: '10px', background: '#ef4444' }}
                onClick={clearPipeline}
            >
                🗑 Clear
            </button>
        </div>
    );
};
