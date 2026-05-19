// timerNode.js

import { useState } from 'react';
import BaseNode from './BaseNode';

export const TimerNode = ({ id, data }) => {
    const [delay, setDelay] = useState(data?.delay || 1000);
    const [unit, setUnit] = useState(data?.unit || 'ms');

    return (
        <BaseNode id={id} title="Timer" color="#14b8a6" inputs={[{ id: 'trigger' }]} outputs={[{ id: 'output' }]}>
            <label className="field">
                <span>Delay</span>
                <input type="number" value={delay} onChange={(e) => setDelay(Number(e.target.value))} min={0} />
            </label>
            <label className="field">
                <span>Unit</span>
                <select value={unit} onChange={(e) => setUnit(e.target.value)}>
                    <option value="ms">Milliseconds</option>
                    <option value="s">Seconds</option>
                    <option value="min">Minutes</option>
                </select>
            </label>
        </BaseNode>
    );
};
