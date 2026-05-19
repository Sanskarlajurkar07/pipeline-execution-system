# Pipeline Backend - Spring Boot

A Spring Boot backend service for executing DAG-based pipelines with support for text processing, variable substitution, and LLM integration via Google Gemini API.

## 🚀 Features

- **DAG Validation**: Validates pipeline structure using Kahn's algorithm
- **Topological Sorting**: Executes nodes in dependency order
- **Variable Substitution**: Mustache-style template variables (`{{ variable }}`)
- **Node Processors**:
  - Custom Input: Reads input values
  - Text: Template processing with variable substitution
  - Custom Output: Returns processed output
  - LLM: Google Gemini API integration
- **REST API**: Three endpoints for health check, parse, and run
- **CORS Support**: Configured for cross-origin requests
- **Error Handling**: Global exception handler with proper HTTP status codes

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- Google Gemini API key (for LLM nodes)
- Docker (optional, for containerized deployment)

## 🛠️ Tech Stack

- **Framework**: Spring Boot 3.3.5
- **Language**: Java 17
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito, AssertJ
- **API Client**: Google Cloud Vertex AI SDK 1.8.0

## 📦 Installation

### Local Development

1. **Clone the repository**
   ```bash
   cd pipeline-backend
   ```

2. **Configure API Key**
   
   Create a `.env` file in the project root:
   ```env
   GEMINI_API_KEY=your_api_key_here
   ```

3. **Build the project**
   ```bash
   ./mvnw clean install
   ```

4. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

   The server will start on `http://localhost:8080`

### Docker Deployment

1. **Build Docker image**
   ```bash
   docker build -t pipeline-backend .
   ```

2. **Run with Docker Compose**
   ```bash
   docker-compose up -d
   ```

3. **Stop the container**
   ```bash
   docker-compose down
   ```

## 🧪 Running Tests

### Run all tests
```bash
./mvnw test
```

### Run specific test class
```bash
./mvnw test -Dtest=PipelineServiceTest
```

### Run with coverage
```bash
./mvnw clean test jacoco:report
```

## 📡 API Endpoints

### 1. Health Check
```http
GET /
```

**Response:**
```json
{
  "status": "ok"
}
```

### 2. Parse Pipeline
```http
POST /pipelines/parse
Content-Type: application/json
```

**Request Body:**
```json
{
  "nodes": [
    {
      "id": "input1",
      "type": "customInput",
      "data": {}
    },
    {
      "id": "output1",
      "type": "customOutput",
      "data": {}
    }
  ],
  "edges": [
    {
      "source": "input1",
      "target": "output1",
      "sourceHandle": "value",
      "targetHandle": "input"
    }
  ]
}
```

**Response:**
```json
{
  "num_nodes": 2,
  "num_edges": 1,
  "is_dag": true
}
```

### 3. Run Pipeline
```http
POST /pipelines/run
Content-Type: application/json
```

**Request Body:**
```json
{
  "nodes": [
    {
      "id": "input1",
      "type": "customInput",
      "data": {}
    },
    {
      "id": "text1",
      "type": "text",
      "data": {
        "text": "Hello {{ name }}!"
      }
    },
    {
      "id": "output1",
      "type": "customOutput",
      "data": {}
    }
  ],
  "edges": [
    {
      "source": "input1",
      "target": "text1",
      "sourceHandle": "value",
      "targetHandle": "text1-name"
    },
    {
      "source": "text1",
      "target": "output1",
      "sourceHandle": "output",
      "targetHandle": "input"
    }
  ],
  "inputs": {
    "input1": "Alice"
  }
}
```

**Response:**
```json
{
  "outputs": {
    "input1": "Alice",
    "text1": "Hello Alice!",
    "output1": "Hello Alice!"
  },
  "trace": [
    {
      "node": "input1",
      "type": "customInput",
      "output": "Alice",
      "time": 2,
      "error": null
    },
    {
      "node": "text1",
      "type": "text",
      "output": "Hello Alice!",
      "time": 5,
      "error": null
    },
    {
      "node": "output1",
      "type": "customOutput",
      "output": "Hello Alice!",
      "time": 1,
      "error": null
    }
  ],
  "num_nodes": 3,
  "num_edges": 2,
  "is_dag": true
}
```

## 🔧 Configuration

### Application Properties

Edit `src/main/resources/application.yml`:

```yaml
server:
  port: 8080

gemini:
  api:
    key: ${GEMINI_API_KEY:}
```

### Environment Variables

- `GEMINI_API_KEY`: Your Google Gemini API key (required for LLM nodes)

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     REST Controller                          │
│  (PipelineController + GlobalExceptionHandler)              │
└────────────────────┬────────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────────┐
│                    Service Layer                             │
│              (PipelineService)                               │
└─┬──────────────┬──────────────┬────────────────────────────┘
  │              │              │
  ▼              ▼              ▼
┌─────────┐  ┌──────────┐  ┌──────────────┐
│   DAG   │  │Topological│  │   Pipeline   │
│Validator│  │  Sorter   │  │   Executor   │
└─────────┘  └──────────┘  └──────┬───────┘
                                   │
                    ┌──────────────┴──────────────┐
                    │      Node Processors        │
                    │  - CustomInput              │
                    │  - Text (with Variables)    │
                    │  - CustomOutput             │
                    │  - LLM (Gemini)             │
                    └─────────────────────────────┘
```

## 📝 Node Types

### CustomInput
Reads input values from the inputs map.

**Data Fields:** None required

**Example:**
```json
{
  "id": "input1",
  "type": "customInput",
  "data": {}
}
```

### Text
Processes text templates with variable substitution.

**Data Fields:**
- `text`: Template string with `{{ variable }}` placeholders

**Example:**
```json
{
  "id": "text1",
  "type": "text",
  "data": {
    "text": "Hello {{ name }}, you are {{ age }} years old!"
  }
}
```

**Variable Extraction:**
Variables are extracted from incoming edges using the target handle format: `nodeId-variableName`

### CustomOutput
Returns the output from the first incoming edge.

**Data Fields:** None required

**Example:**
```json
{
  "id": "output1",
  "type": "customOutput",
  "data": {}
}
```

### LLM
Calls Google Gemini API for text generation.

**Data Fields:**
- `systemInstructions`: System-level instructions (optional)
- `prompt`: User prompt (optional)
- `model`: Model name (default: "gemini-2.0-flash-exp")
- `apiKey`: API key (optional, uses env var if not provided)

**Example:**
```json
{
  "id": "llm1",
  "type": "llm",
  "data": {
    "systemInstructions": "You are a helpful assistant",
    "prompt": "Explain quantum computing",
    "model": "gemini-pro"
  }
}
```

**Edge Overrides:**
- Target handle ending with `-system`: Overrides system instructions
- Target handle ending with `-prompt`: Overrides prompt

## 🧪 Testing

The project includes comprehensive test coverage:

- **Unit Tests**: 72 tests covering all components
- **Integration Tests**: 13 tests for REST endpoints and CORS
- **Total**: 85 tests

**Test Structure:**
```
src/test/java/
├── config/
│   └── CorsConfigTest.java (4 tests)
├── controller/
│   └── PipelineControllerTest.java (9 tests)
├── execution/
│   └── PipelineExecutorTest.java (8 tests)
├── processor/
│   ├── CustomInputProcessorTest.java (5 tests)
│   ├── CustomOutputProcessorTest.java (5 tests)
│   ├── LlmNodeProcessorTest.java (7 tests)
│   └── TextNodeProcessorTest.java (6 tests)
├── service/
│   └── PipelineServiceTest.java (8 tests)
├── sorter/
│   └── TopologicalSorterTest.java (8 tests)
├── util/
│   └── VariableSubstitutorTest.java (15 tests)
└── validator/
    └── DagValidatorTest.java (9 tests)
```

## 🐛 Error Handling

The application handles errors gracefully:

- **400 Bad Request**: Invalid JSON, cyclic graphs, illegal arguments
- **500 Internal Server Error**: Unexpected errors

**Error Response Format:**
```json
{
  "error": "Error message here"
}
```

## 🔒 Security

- CORS enabled for cross-origin requests
- API key validation for LLM nodes
- Input validation and sanitization
- Global exception handling

## 📄 License

This project is part of a technical assessment.

## 👤 Author

Created as part of the VectorShift technical assessment to demonstrate Spring Boot backend development skills.

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Google for the Gemini API
- VectorShift for the opportunity
