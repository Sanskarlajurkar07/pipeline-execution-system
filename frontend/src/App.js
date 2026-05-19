import { PipelineToolbar } from './toolbar';
import { PipelineUI } from './ui';
import { SubmitButton } from './submit';
import { RunPipelinePanel } from './RunPipelinePanel';

function App() {
  return (
    <div className="app">
      <PipelineToolbar />
      <div className="app-body">
        <PipelineUI />
        <RunPipelinePanel />
      </div>
      <SubmitButton />
    </div>
  );
}

export default App;
