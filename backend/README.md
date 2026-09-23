# Typeless Backend

FastAPI backend service powering the Typeless AI voice translation keyboard.

## Architecture

The backend follows Clean Architecture principles:

```
backend/
├── app/
│   ├── api/             # HTTP & WebSocket endpoints, routing, dependencies
│   ├── application/     # Use case orchestration and application services
│   ├── core/            # Configuration (Pydantic Settings), logging, exceptions
│   ├── domain/          # Pure business models and provider interfaces (Protocols)
│   ├── infrastructure/  # External integrations (Sarvam AI SDK, DB, cache)
│   ├── schemas/         # Shared Pydantic request/response schemas
│   └── main.py          # FastAPI application factory and entry point
├── tests/               # Automated test suite (pytest + pytest-asyncio + httpx)
├── Dockerfile           # Multi-stage production container definition
├── pyproject.toml       # Python project configuration and dependencies
└── .env.example         # Template for environment variables
```

## Prerequisites

- Python >= 3.12
- [uv](https://docs.astral.sh/uv/) package manager

## Quickstart

### 1. Install Dependencies

```bash
cd backend
uv sync
```

### 2. Configure Environment

```bash
cp .env.example .env
# Edit .env and supply your SARVAM_API_KEY
```

### 3. Run Development Server

```bash
uv run uvicorn app.main:app --reload --port 8000
```

Verify health check:
```bash
curl http://localhost:8000/health
# {"status":"ok"}
```

API documentation (available in debug mode):
- Swagger UI: `http://localhost:8000/docs`
- ReDoc: `http://localhost:8000/redoc`

## Development Commands

| Command | Action |
|---|---|
| `uv run pytest` | Run test suite |
| `uv run ruff check .` | Run linter |
| `uv run ruff format --check .` | Check code formatting |
| `uv run ruff format .` | Format code |

## Docker Build & Run

```bash
docker build -t typeless-backend .
docker run -p 8000:8000 --env-file .env typeless-backend
```
