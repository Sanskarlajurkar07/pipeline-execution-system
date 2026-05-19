// apiNode.js

import { useState } from 'react';
import BaseNode from './BaseNode';

export const APINode = ({ id, data }) => {
    const [url, setUrl] = useState(data?.url || '');
    const [method, setMethod] = useState(data?.method || 'GET');

    return (
        <BaseNode id={id} title="API Call" color="#3b82f6" inputs={[{ id: 'body' }]} outputs={[{ id: 'response' }]}>
            <label className="field">
                <span>URL</span>
                <input type="text" value={url} onChange={(e) => setUrl(e.target.value)} placeholder="https://api.example.com" />
            </label>
            <label className="field">
                <span>Method</span>
                <select value={method} onChange={(e) => setMethod(e.target.value)}>
                    <option value="GET">GET</option>
                    <option value="POST">POST</option>
                    <option value="PUT">PUT</option>
                    <option value="DELETE">DELETE</option>
                </select>
            </label>
        </BaseNode>
    );
};
