// BaseNode.js
// shared wrapper for all pipeline nodes

import { Handle, Position } from 'reactflow';
import { useStore } from '../store';

export default function BaseNode({ id, title, color = '#7c3aed', inputs = [], outputs = [], children, style, isConnectable }) {
    const removeNode = useStore((s) => s.removeNode);

    return (
        <div className="node-card" style={{ borderTopColor: color, ...style }}>
            <div className="node-header">
                <span className="node-title">{title}</span>
                <button className="node-close" onClick={() => removeNode(id)} title="Delete node">
                    &times;
                </button>
            </div>
            <div className="node-content">
                {children}
            </div>
            {inputs.map((inp, i) => (
                <Handle
                    key={inp.id}
                    type="target"
                    position={Position.Left}
                    id={`${id}-${inp.id}`}
                    className="handle handle-left"
                    style={{ top: `${((i + 1) / (inputs.length + 1)) * 100}%` }}
                    isConnectable={isConnectable}
                />
            ))}
            {outputs.map((out, i) => (
                <Handle
                    key={out.id}
                    type="source"
                    position={Position.Right}
                    id={`${id}-${out.id}`}
                    className="handle handle-right"
                    style={{ top: `${((i + 1) / (outputs.length + 1)) * 100}%` }}
                    isConnectable={isConnectable}
                />
            ))}
        </div>
    );
}
