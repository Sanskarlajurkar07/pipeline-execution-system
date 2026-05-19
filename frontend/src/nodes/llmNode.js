// llmNode.js

import { useState } from 'react';
import { Handle, Position } from 'reactflow';
import { useStore } from '../store';
import { GoogleIcon } from './icons';

const MODELS = [
  'gemini-2.5-flash',
  'gemini-2.0-flash',
  'gemini-1.5-pro',
  'gemini-1.5-flash',
];

export const LLMNode = ({ id, data }) => {
  const removeNode = useStore((s) => s.removeNode);
  const updateNodeField = useStore((s) => s.updateNodeField);

  const [systemInstructions, setSystemInstructions] = useState(data?.systemInstructions || '');
  const [prompt, setPrompt] = useState(data?.prompt || '');
  const [model, setModel] = useState(data?.model || 'gemini-2.5-flash');
  const [usePersonalKey, setUsePersonalKey] = useState(data?.usePersonalKey || false);
  const [apiKey, setApiKey] = useState(data?.apiKey || '');

  const handleChange = (field, value, setter) => {
    setter(value);
    updateNodeField(id, field, value);
  };

  return (
    <div className="llm-node">
      <div className="llm-header">
        <div className="llm-header-left">
          <GoogleIcon />
          <span className="llm-title">Google</span>
        </div>
        <div className="llm-header-right">
          <button className="llm-hdr-btn" onClick={() => removeNode(id)} title="Delete">✕</button>
        </div>
      </div>

      <div className="llm-badge">{id.replace('llm-', 'google_')}</div>

      <div className="llm-field">
        <div className="llm-field-head">
          <span>System</span>
        </div>
        <textarea
          className="llm-textarea"
          rows={2}
          value={systemInstructions}
          onChange={(e) => handleChange('systemInstructions', e.target.value, setSystemInstructions)}
          placeholder="System instructions..."
        />
      </div>

      <div className="llm-field">
        <div className="llm-field-head">
          <span>Prompt</span>
        </div>
        <textarea
          className="llm-textarea"
          rows={2}
          value={prompt}
          onChange={(e) => handleChange('prompt', e.target.value, setPrompt)}
          placeholder="Prompt..."
        />
      </div>

      <div className="llm-field">
        <div className="llm-field-head">
          <span>Model</span>
        </div>
        <div className="llm-select-wrap">
          <select
            value={model}
            onChange={(e) => handleChange('model', e.target.value, setModel)}
          >
            {MODELS.map((m) => <option key={m} value={m}>{m}</option>)}
          </select>
        </div>
      </div>

      <div className="llm-field llm-field-last">
        <div className="llm-toggle-row">
          <span>Use Personal API Key</span>
          <div className="llm-toggle-right">
            <button
              className={`llm-switch${usePersonalKey ? ' on' : ''}`}
              onClick={() => {
                const newValue = !usePersonalKey;
                handleChange('usePersonalKey', newValue, setUsePersonalKey);
                if (!newValue) handleChange('apiKey', '', setApiKey);
              }}
            >
              <span className="llm-switch-thumb" />
            </button>
          </div>
        </div>
        {usePersonalKey && (
          <input
            type="password"
            className="llm-key-input"
            value={apiKey}
            onChange={(e) => handleChange('apiKey', e.target.value, setApiKey)}
            placeholder="Enter your Gemini API key"
          />
        )}
      </div>

      {/* ── ALL HANDLES — rendered LAST for proper z-index ── */}
      {/* Left: system input */}
      <Handle
        type="target"
        position={Position.Left}
        id={`${id}-system`} // system
        className="llm-handle-target"
        style={{ top: '30%', zIndex: 10 }}
        isConnectable={true}
      />

      {/* Left: prompt input */}
      <Handle
        type="target"
        position={Position.Left}
        id={`${id}-prompt`} // prompt
        className="llm-handle-target"
        style={{ top: '52%', zIndex: 10 }}
        isConnectable={true}
      />

      {/* Right: response output */}
      <Handle
        type="source"
        position={Position.Right}
        id={`${id}-response`}
        className="llm-handle-source"
        style={{ top: '50%', zIndex: 10 }}
        isConnectable={true}
      />
    </div>
  );
};
