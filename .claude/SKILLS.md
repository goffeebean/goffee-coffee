# Technical Skills & Implementation

This document details the specific skills implemented in this portfolio project.

## Backend Development (Java/Spring Boot)

### Core Competencies
- **Spring Boot 4**: Rapid application development with auto-configuration.
- **Data Persistence**: **Spring Data JPA** with **PostgreSQL** for robust relational data management.
- **API Design**: RESTful architecture with proper HTTP status codes, validation, and error handling.

### Project Implementation
- Designed a secure REST API with role-based access control (Admin vs. User).
- Implemented asynchronous Ollama client for LLM interactions without blocking the main thread.
- Used **DTOs (Data Transfer Objects)** to prevent over-exposure of entities.

## Frontend Development (React/Tailwind/DaisyUI)

### Core Competencies
- **React 19**: Functional components, Hooks (`useState`, `useEffect`, `useContext`), and React Router.
- **Styling**: **TailwindCSS** for utility-first styling and **DaisyUI** for pre-built, accessible components.
- **State Management**: Context API and `useReducer` for complex global state.
- **API Integration**: Axios/Fetch wrappers with interceptors for token handling and error logging.

### Project Implementation
- Built a fully responsive dashboard that adapts from mobile to desktop.
- Created reusable DaisyUI components (e.g., `Card`, `Modal`, `Button`) with Tailwind customization.
- Implemented real-time feedback loops when calling the Ollama API.

## DevOps & Infrastructure (Docker/Ollama)

### Core Competencies
- **Containerization**: Dockerfile optimization (multi-stage builds) for both Java and Node.js apps.
- **Orchestration**: `docker-compose.yml` to manage dependencies (DB, App, AI Service) in a single network.
- **AI Deployment**: Integration with **Ollama** for running local LLMs (e.g., Llama 3, Mistral) without cloud costs.
- **CI/CD**: Created a ``

### Project Implementation
- Created a `docker-compose.yml` that spins up the entire stack with one command.
- Configured Ollama as a separate container with a volume mount for model persistence.
- Implemented health checks for the database and Ollama services in Docker.

## Soft Skills & Best Practices

- **Clean Code**: Adhering to SOLID principles and consistent naming conventions.
- **Documentation**: Comprehensive `README.md` and inline JavaDoc/TypeDoc.
- **Version Control**: Git workflow with meaningful commit messages and branching strategies.
- **Problem Solving**: Debugging cross-origin (CORS) issues and optimizing Docker network latency.