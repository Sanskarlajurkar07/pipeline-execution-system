// filterNode.js

import { useState } from 'react';
import BaseNode from './BaseNode';

export const FilterNode = ({ id, data }) => {
    const [condition, setCondition] = useState(data?.condition || '');

    return (
        <BaseNode id={id} title="Filter" color="#ef4444" inputs={[{ id: 'data' }, { id: 'condition' }]} outputs={[{ id: 'filtered' }]}>
            <label className="field">
                <span>Condition</span>
                <input type="text" value={condition} onChange={(e) => setCondition(e.target.value)} placeholder="e.g. value > 10" />
            </label>
        </BaseNode>
    );
};
