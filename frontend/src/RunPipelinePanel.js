// RunPipelinePanel.js

import { useState, useEffect, useMemo } from 'react';
import { useStore } from './store';

export const RunPipelinePanel = () => {
    const { nodes, edges, showRunPanel, setRunPanel } = useStore();

    const [inputValues, setInputValues] = useState({});
    const [outputs, setOutputs] = useState({});
    const [running, setRunning] = useState(false);
    const [error, setError] = useState(null);

    const inputNodes = useMemo(() => nodes.filter((n) => n.type === 'customInput'), [nodes]);
    const outputNodes = useMemo(() => nodes.filter((n) => n.type === 'customOutput'), [nodes]);

    useEffect(() => {
        if (showRunPanel) {
            setInputValues((prev) => {
                const vals = {};
                inputNodes.forEach((n) => {
                    vals[n.id] = prev[n.id] || '';
                });
                return vals;
            });
        }
    }, [showRunPanel, inputNodes, inputNodes.length]);

    const getInputName = (node) => node.data?.inputName || node.id.replace('customInput-', 'input_');
    const getOutputName = (node) => node.data?.outputName || node.id.replace('customOutput-', 'output_');

    const handleRun = async () => {
        setRunning(true);
        setError(null);
        setOutputs({});

        try {
            const inputPayload = {};
            Object.keys(inputValues).forEach((nodeId) => {
                inputPayload[nodeId] = inputValues[nodeId];
            });

            const res = await fetch('http://localhost:8080/pipelines/run', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ nodes, edges, inputs: inputPayload }),
            });

            if (!res.ok) throw new Error(`Server error: ${res.status}`);

            const data = await res.json();
            const outputResults = {};

            if (data.trace) {
                data.trace.forEach((t) => {
                    if (t.type === 'Output') outputResults[t.node] = t.output || '';
                });
            }

            if (data.outputs) {
                outputNodes.forEach((n) => {
                    if (data.outputs[n.id]) outputResults[n.id] = data.outputs[n.id];
                });
            }

            setOutputs(outputResults);
        } catch (err) {
            setError(err.message);
        } finally {
            setRunning(false);
        }
    };

    if (!showRunPanel) return null;

    return (
        <div className="run-panel">
            <button className="run-panel-close" onClick={() => setRunPanel(false)}>✕</button>
            <div className="run-panel-header">
                <span className="run-panel-header-title">Run Pipeline</span>
            </div>

            <div className="run-panel-body">
                <div className="run-panel-section">
                    <div className="run-panel-section-header">
                        <span className="run-panel-section-title">Inputs</span>
                    </div>

                    {inputNodes.length === 0 ? (
                        <div className="run-panel-empty">No Input nodes in pipeline.</div>
                    ) : (
                        inputNodes.map((node) => (
                            <div key={node.id} className="run-panel-input-group">
                                <div className="run-panel-input-label">
                                    <span>{getInputName(node)}</span>
                                </div>
                                <textarea
                                    className="run-panel-textarea"
                                    rows={3}
                                    value={inputValues[node.id] || ''}
                                    onChange={(e) => setInputValues(prev => ({ ...prev, [node.id]: e.target.value }))}
                                    placeholder="Enter input..."
                                />
                            </div>
                        ))
                    )}
                </div>

                <div className="run-panel-section">
                    <div className="run-panel-section-header">
                        <span className="run-panel-section-title">Outputs</span>
                    </div>

                    {outputNodes.length === 0 ? (
                        <div className="run-panel-empty">No Output nodes in pipeline.</div>
                    ) : (
                        outputNodes.map((node) => (
                            <div key={node.id} className="run-panel-output-group">
                                <div className="run-panel-input-label">
                                    <span>{getOutputName(node)}</span>
                                </div>
                                {outputs[node.id] ? (
                                    <div className="run-panel-output-result">{outputs[node.id]}</div>
                                ) : (
                                    <div className="run-panel-output-empty">
                                        {running ? 'Running...' : 'Waiting for results...'}
                                    </div>
                                )}
                            </div>
                        ))
                    )}
                </div>

                {error && <div className="run-panel-error">{error}</div>}
            </div>

            <div className="run-panel-footer">
                <button className="run-panel-run-btn" onClick={handleRun} disabled={running}>
                    {running ? 'Running...' : 'Run'}
                </button>
            </div>
        </div>
    );
};
