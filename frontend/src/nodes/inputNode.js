// inputNode.js

import { useState } from 'react';
import BaseNode from './BaseNode';
import { useStore } from '../store';

export const InputNode = ({ id, data, isConnectable }) => {
  const updateNodeField = useStore((s) => s.updateNodeField);
  const [name, setName] = useState(data?.inputName || id.replace('customInput-', 'input_'));
  const [type, setType] = useState(data?.inputType || 'Text');

  const handleNameChange = (e) => {
    setName(e.target.value);
    updateNodeField(id, 'inputName', e.target.value);
  };

  const handleTypeChange = (e) => {
    setType(e.target.value);
    updateNodeField(id, 'inputType', e.target.value);
  };

  return (
    <BaseNode id={id} title="Input" color="#22c55e" outputs={[{ id: 'value' }]} isConnectable={isConnectable}>
      <label className="field">
        <span>Name</span>
        <input type="text" value={name} onChange={handleNameChange} />
      </label>
      <label className="field">
        <span>Type</span>
        <select value={type} onChange={handleTypeChange}>
          <option value="Text">Text</option>
          <option value="File">File</option>
        </select>
      </label>
    </BaseNode>
  );
};
