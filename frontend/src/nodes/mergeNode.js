// mergeNode.js

import { useState } from 'react';
import BaseNode from './BaseNode';

export const MergeNode = ({ id, data }) => {
    const [strategy, setStrategy] = useState(data?.strategy || 'concat');

    return (
        <BaseNode id={id} title="Merge" color="#ec4899" inputs={[{ id: 'input1' }, { id: 'input2' }]} outputs={[{ id: 'merged' }]}>
            <label className="field">
                <span>Strategy</span>
                <select value={strategy} onChange={(e) => setStrategy(e.target.value)}>
                    <option value="concat">Concat</option>
                    <option value="zip">Zip</option>
                    <option value="union">Union</option>
                </select>
            </label>
        </BaseNode>
    );
};
