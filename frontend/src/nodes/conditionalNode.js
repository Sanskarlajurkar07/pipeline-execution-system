// conditionalNode.js

import { useState } from 'react';
import BaseNode from './BaseNode';

export const ConditionalNode = ({ id, data }) => {
    const [operator, setOperator] = useState(data?.operator || '==');

    return (
        <BaseNode id={id} title="Conditional" color="#f97316" inputs={[{ id: 'value' }, { id: 'condition' }]} outputs={[{ id: 'true' }, { id: 'false' }]}>
            <label className="field">
                <span>Operator</span>
                <select value={operator} onChange={(e) => setOperator(e.target.value)}>
                    <option value="==">Equals (==)</option>
                    <option value="!=">Not Equals (!=)</option>
                    <option value=">">Greater Than (&gt;)</option>
                    <option value="<">Less Than (&lt;)</option>
                    <option value="contains">Contains</option>
                </select>
            </label>
        </BaseNode>
    );
};
