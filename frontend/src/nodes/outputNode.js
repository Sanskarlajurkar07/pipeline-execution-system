// outputNode.js

import { useState } from 'react';
import BaseNode from './BaseNode';

export const OutputNode = ({ id, data, isConnectable }) => {
  const [name, setName] = useState(data?.outputName || id.replace('customOutput-', 'output_'));
  const [type, setType] = useState(data?.outputType || 'Text');

  return (
    <BaseNode id={id} title="Output" color="#f59e0b" inputs={[{ id: 'value' }]} isConnectable={isConnectable}>
      <label className="field">
        <span>Name</span>
        <input type="text" value={name} onChange={(e) => setName(e.target.value)} />
      </label>
      <label className="field">
        <span>Type</span>
        <select value={type} onChange={(e) => setType(e.target.value)}>
          <option value="Text">Text</option>
          <option value="Image">Image</option>
        </select>
      </label>
    </BaseNode>
  );
};
