# Pipeline Execution System

A full-stack application for building and executing data processing pipelines with AI integration. Built with React frontend and Spring Boot backend.

## Overview

This system allows users to create visual pipelines by connecting different types of nodes (input, text processing, LLM, output) and execute them to process data. It supports Google's Gemini AI models for text generation within pipelines.

## Tech Stack

### Backend
- **Java 17** with Spring Boot 3.3.5
- **Spring MVC** for REST APIs
- **Maven** for dependency management
- **Google Gemini API** integration for LLM capabilities

### Frontend
- **React 18** with React Flow for visual pipeline builder
- **Zustand** for state management
- **Modern CSS** for styling

## Features

- Visual pipeline builder with drag-and-drop interface
- Multiple node types: Input, Text, LLM (Gemini), Output
- Real-time pipeline validation (DAG checking)
- Support for multiple Gemini models (2.5-flash, 2.0-flash, 1.5-pro, 1.5-flash)
- Variable substitution in text nodes
- Pipeline execution with detailed trace information

## Getting Started

### Prerequisites

- Java 17 or higher
- Node.js 16+ and npm
- Docker (optional, for containerized deployment)
- Gemini API key from Google AI Studio

### Local Development

#### Backend Setup

```bash
cd pipeline-backend

# Set your Gemini API key
echo "GEMINI_API_KEY=your_api_key_here" > .env

# Run with Maven
./mvnw spring-boot:run
```

Backend will start on `http://localhost:8080`

#### Frontend Setup

```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm start
```

Frontend will start on `http://localhost:3000`

### Docker Deployment

The easiest way to run both frontend and backend:

```bash
# Set your API key
export GEMINI_API_KEY=your_api_key_here

# Build and run with docker-compose
docker-compose up --build
```

Access the application at `http://localhost`

## API Endpoints

### Health Check
```
GET /
Response: {"status": "ok"}
```

### Parse Pipeline
```
POST /pipelines/parse
Body: {
  "nodes": [...],
  "edges": [...]
}
Response: {
  "num_nodes": 5,
  "num_edges": 4,
  "is_dag": true
}
```

### Run Pipeline
```
POST /pipelines/run
Body: {
  "nodes": [...],
  "edges": [...],
  "inputs": {"input-1": "Hello"}
}
Response: {
  "outputs": {...},
  "trace": [...],
  "num_nodes": 5,
  "num_edges": 4,
  "is_dag": true
}
```

## Architecture

The system uses a directed acyclic graph (DAG) structure for pipelines:

1. **DAG Validation**: Ensures no cycles exist using Kahn's algorithm
2. **Topological Sorting**: Orders nodes for execution based on dependencies
3. **Node Processing**: Each node type has a dedicated processor
4. **Variable Substitution**: Text nodes support mustache-style variables `{{ var }}`

## Node Types

- **Input**: Provides input values to the pipeline
- **Text**: Performs variable substitution on template text
- **LLM**: Calls Gemini API for text generation
- **Output**: Captures final output values

## Testing

```bash
# Run backend tests
cd pipeline-backend
./mvnw test

# Run frontend tests
cd frontend
npm test
```

## Deployment Options

### 1. Docker (Recommended)
Use the provided `docker-compose.yml` for easy deployment

### 2. Cloud Platforms
- **AWS**: Deploy on EC2 or ECS
- **Google Cloud**: Deploy on Cloud Run or GKE
- **Azure**: Deploy on App Service or AKS
- **Heroku**: Use Heroku buildpacks for Java and Node.js

### 3. Traditional Hosting
- Build frontend: `npm run build`
- Build backend: `./mvnw clean package`
- Deploy JAR file and static files to your server

## Environment Variables

### Backend
- `GEMINI_API_KEY`: Your Google Gemini API key (required)
- `PORT`: Server port (default: 8080)

### Frontend
- `REACT_APP_API_URL`: Backend API URL (default: http://localhost:8080)

## Contributing

This is a portfolio project. Feel free to fork and modify for your own use.

## License

MIT License - feel free to use this code for learning and portfolio purposes.

## Author

Sanskar Lajurkar
- GitHub: [@Sanskarlajurkar07](https://github.com/Sanskarlajurkar07)
- LinkedIn: [sanskar-lajurkar](https://linkedin.com/in/sanskar-lajurkar)

## Acknowledgments

- Built as part of a technical assessment
- Uses Google's Gemini AI for LLM capabilities
- React Flow for visual pipeline builder
