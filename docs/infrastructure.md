# Effortless — Infrastructure & Docker Architecture

This document describes the local infrastructure and container architecture for the Effortless project.

---

## Table of Contents

1. [Overview & Philosophy](#overview--philosophy)
2. [Docker Concepts: Image vs Container](#docker-concepts-image-vs-container)
3. [Architecture Overview](#architecture-overview)
4. [Docker Compose Services](#docker-compose-services)
   - [PostgreSQL Service](#postgresql-service)
   - [Redis Service](#redis-service)
   - [FastAPI Backend Service](#fastapi-backend-service)
5. [Networking & Service Discovery](#networking--service-discovery)
6. [Data Volumes & Persistence](#data-volumes--persistence)
7. [Health Checks & Readiness](#health-checks--readiness)
8. [Configuration & Environment Management](#configuration--environment-management)
9. [Development Workflows](#development-workflows)
   - [Workflow A: Hybrid (Recommended for Daily Dev)](#workflow-a-hybrid-recommended-for-daily-dev)
   - [Workflow B: Full Container Stack (CI / Pre-deployment)](#workflow-b-full-container-stack-ci--pre-deployment)
10. [Makefile Command Reference](#makefile-command-reference)
11. [Data Reset Procedure](#data-reset-procedure)
12. [Troubleshooting Guide](#troubleshooting-guide)
13. [Future Production Architecture](#future-production-architecture)

---

## Overview & Philosophy

The Effortless local infrastructure provides a reproducible, lightweight, and production-aligned foundation for running the FastAPI backend alongside its core backing services (**PostgreSQL 16** and **Redis 7**).

### Core Principles

- **Simplicity First (Rule 4)**: No unneeded distributed systems (Kafka, RabbitMQ, Celery, Kubernetes, microservice orchestration) in the MVP foundation.
- **Portability**: All developers and CI runners run identical service versions (`postgres:16-alpine`, `redis:7-alpine`, Python 3.12).
- **Fast Developer Feedback**: Dual workflow support allows running FastAPI directly on the host (with instant hot-reload and fast unit tests) while backing services run in isolated containers.
- **Zero Secrets Committed (Rule 7)**: No secrets, passwords, or provider API keys are ever baked into Docker images or committed to version control.

---

## Docker Concepts: Image vs Container

To ensure clarity across cross-platform teams:

- **Docker Image**: An immutable, read-only blueprint package containing the operating system layer (Alpine Linux), runtime environment (Python 3.12 / PostgreSQL 16), dependencies, and application code. Images are built once and versioned.
- **Docker Container**: A runnable, isolated instance of an image executing as a sandboxed process on the host kernel. Containers are ephemeral and can be stopped, started, and destroyed without losing state stored in volumes.

---

## Architecture Overview

```text
                             Developer Host
                                   │
                                   ▼
                 Docker Compose (infra/docker/compose.yml)
                                   │
            ┌──────────────────────┴──────────────────────┐
            │ private bridge: effortless-network          │
            ▼                                             ▼
    PostgreSQL 16                                      Redis 7
    (effortless-postgres)                       (effortless-redis)
    Port: 5432                                  Port: 6379
    Volume: effortless_postgres_data            Volume: effortless_redis_data
            │                                             │
            └──────────────────────┬──────────────────────┘
                                   │
                                   ▼
                    FastAPI Backend (effortless-backend)
                    Port: 8000
                    Health: /health (liveness), /health/ready (readiness)
                                   │
                                   ▼
                           Sarvam AI Cloud API
```

---

## Docker Compose Services

Configuration path: `infra/docker/compose.yml`

### PostgreSQL Service

- **Image**: `postgres:16-alpine` (pinned stable major version on minimal Alpine base).
- **Container Name**: `effortless-postgres`.
- **Purpose**: Primary relational datastore for user profiles, keyboard preferences, session history, and usage quotas.
- **Host Port**: `5432:5432`.
- **Persistent Volume**: `effortless_postgres_data` mapped to `/var/lib/postgresql/data`.
- **Init Scripts**: `infra/docker/postgres/init/` mounted to `/docker-entrypoint-initdb.d` for running any local development initialization scripts automatically on first startup.
- **Health Check**:
  ```yaml
  test: ["CMD-SHELL", "pg_isready -U ${POSTGRES_USER:-effortless_user} -d ${POSTGRES_DB:-effortless_db}"]
  interval: 5s
  timeout: 5s
  retries: 5
  start_period: 10s
  ```

### Redis Service

- **Image**: `redis:7-alpine` (pinned stable major version on minimal Alpine base).
- **Container Name**: `effortless-redis`.
- **Purpose**: Low-latency cache, transient session storage for realtime speech-to-translation state, rate limiting, and upcoming pub/sub for WebSocket coordination.
- **Host Port**: `6379:6379`.
- **Persistent Volume**: `effortless_redis_data` mapped to `/data`.
- **Persistence Decision**: Redis is run with default append-only file (AOF) disabled for local ephemeral caching, but mounted with a named volume to allow local testing of session persistence across restarts without data corruption.
- **Health Check**:
  ```yaml
  test: ["CMD", "redis-cli", "ping"]
  interval: 5s
  timeout: 5s
  retries: 5
  start_period: 5s
  ```

### FastAPI Backend Service

- **Build**: `./backend/Dockerfile` (context: `backend/`).
- **Container Name**: `effortless-backend`.
- **Purpose**: HTTP and WebSocket API engine.
- **Host Port**: `8000:8000`.
- **Security**: Runs under an unprivileged user `appuser` (UID 10001).
- **Dependencies**: Depends on `postgres` and `redis` reaching `service_healthy` state.
- **Health Check**:
  ```yaml
  test: ["CMD", "python", "-c", "import urllib.request; urllib.request.urlopen('http://localhost:8000/health')"]
  interval: 10s
  timeout: 5s
  retries: 3
  start_period: 5s
  ```

---

## Networking & Service Discovery

All services attach to a dedicated private user bridge network: `effortless-network`.

### Service Discovery inside Docker vs Host

| Context | PostgreSQL URL | Redis URL |
|---|---|---|
| **Inside Docker Containers** (`effortless-backend`) | `postgresql://...@postgres:5432/...` | `redis://redis:6379/0` |
| **From Developer Host** (`uv run uvicorn`) | `postgresql://...@localhost:5432/...` | `redis://localhost:6379/0` |

> **Critical Rule**: Inside Docker Compose, services communicate via their Docker service names (`postgres`, `redis`), **never** `localhost`. `localhost` inside a container refers strictly to that container's own loopback interface.

---

## Data Volumes & Persistence

Local data persistence is managed by Docker named volumes defined at the bottom of `infra/docker/compose.yml`:

```yaml
volumes:
  effortless_postgres_data:
    name: effortless_postgres_data
  effortless_redis_data:
    name: effortless_redis_data
```

These volumes ensure that stopping or restarting containers (`docker compose down`, `docker compose up`) preserves database records and Redis cache state.

---

## Health Checks & Readiness

The backend provides two distinct health check endpoints:

### 1. Liveness Probe (`GET /health`)

- **Purpose**: Verifies that the FastAPI process is alive, listening, and accepting HTTP requests.
- **Dependency coupling**: **Zero**. Does not check PostgreSQL or Redis. If Redis is temporarily restarting, the liveness probe remains HTTP 200 OK.
- **Response**:
  ```json
  {
    "status": "ok",
    "timestamp": "2026-09-24T01:15:30.000000Z",
    "version": "0.1.0",
    "app_name": "Effortless Backend"
  }
  ```

### 2. Readiness Probe (`GET /health/ready`)

- **Purpose**: Verifies that required backing dependencies are reachable before routing traffic to the instance.
- **Checks**: Asynchronous non-blocking TCP socket connect checks to PostgreSQL (`postgres_host:postgres_port`) and Redis (`redis_host:redis_port`).
- **Response (Healthy - HTTP 200)**:
  ```json
  {
    "status": "ready",
    "timestamp": "2026-09-24T01:15:30.000000Z",
    "version": "0.1.0",
    "dependencies": {
      "postgres": "connected",
      "redis": "connected"
    }
  }
  ```
- **Response (Degraded - HTTP 503 Service Unavailable)**:
  ```json
  {
    "status": "degraded",
    "timestamp": "2026-09-24T01:15:30.000000Z",
    "version": "0.1.0",
    "dependencies": {
      "postgres": "connected",
      "redis": "unreachable: [Errno 111] Connection refused"
    }
  }
  ```

---

## Configuration & Environment Management

Environment configuration follows strict 12-factor application standards:

1. `backend/.env.example` is committed to Git with safe development defaults and placeholder documentation.
2. `backend/.env` is strictly gitignored (Rule 7) and created by the developer locally:
   ```bash
   cp backend/.env.example backend/.env
   ```
3. Configuration is centralized in `backend/app/core/config.py` using Pydantic Settings.

### Key Environment Variables

| Variable | Default (Host / Dev) | Docker Compose Default | Description |
|---|---|---|---|
| `APP_ENV` | `development` | `development` | Runtime environment (`development`, `staging`, `production`, `test`) |
| `APP_HOST` | `0.0.0.0` | `0.0.0.0` | Application bind host |
| `APP_PORT` | `8000` | `8000` | Application bind port |
| `POSTGRES_HOST` | `localhost` | `postgres` | PostgreSQL hostname |
| `POSTGRES_PORT` | `5432` | `5432` | PostgreSQL port |
| `POSTGRES_USER` | `effortless_user` | `effortless_user` | PostgreSQL username |
| `POSTGRES_PASSWORD` | `effortless_dev_password` | `effortless_dev_password` | PostgreSQL dev password |
| `POSTGRES_DB` | `effortless_db` | `effortless_db` | PostgreSQL database name |
| `REDIS_HOST` | `localhost` | `redis` | Redis hostname |
| `REDIS_PORT` | `6379` | `6379` | Redis port |
| `SARVAM_API_KEY` | *(empty string)* | *(passed from host/env)* | Sarvam AI API subscription key |

---

## Development Workflows

### Workflow A: Hybrid (Recommended for Daily Dev)

In this workflow, PostgreSQL and Redis run in Docker, while the FastAPI backend runs natively on your machine with hot reload:

1. **Start Backing Services**:
   ```bash
   make infra-up
   # Or: docker compose -f infra/docker/compose.yml up -d postgres redis
   ```
2. **Start FastAPI Backend Locally**:
   ```bash
   make dev-backend
   # Or: cd backend && uv run uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
   ```
3. **Verify Health**:
   ```bash
   curl http://localhost:8000/health/ready
   ```

### Workflow B: Full Container Stack (CI / Pre-deployment)

Run the entire stack including the containerized FastAPI backend:

1. **Build and Run All Services**:
   ```bash
   make stack-up
   # Or: docker compose -f infra/docker/compose.yml up --build -d
   ```
2. **Follow Logs**:
   ```bash
   make logs
   ```
3. **Stop Stack**:
   ```bash
   make infra-down
   ```

---

## Makefile Command Reference

A top-level `Makefile` is provided at the repository root:

| Command | Action |
|---|---|
| `make help` | Display available targets and descriptions |
| `make infra-up` | Start PostgreSQL and Redis in the background |
| `make infra-down` | Stop all Docker infrastructure containers |
| `make infra-clean` | Stop containers and **permanently delete** local volumes (`-v`) |
| `make stack-up` | Build and start all services (FastAPI, Postgres, Redis) in Docker |
| `make logs` | Tail logs from all Docker containers |
| `make dev-backend` | Run FastAPI backend on host with hot-reloading |
| `make test-backend` | Run backend test suite via `pytest` |
| `make lint-backend` | Check backend formatting and lints via `ruff` |
| `make format-backend` | Auto-format backend code via `ruff` |
| `make test-client` | Run KMP client unit tests |

---

## Data Reset Procedure

If you need to reset PostgreSQL and Redis to an empty state:

> [!WARNING]
> This command will permanently delete all local database tables, records, and cached Redis sessions stored in named Docker volumes.

```bash
make infra-clean
# Or: docker compose -f infra/docker/compose.yml down -v
```

To reinitialize clean containers:
```bash
make infra-up
```

---

## Troubleshooting Guide

### 1. Port 5432 or 6379 Already in Use

**Symptom**: `Bind for 0.0.0.0:5432 failed: port is already allocated`.

**Fix**: You likely have a local PostgreSQL or Redis service running natively on macOS (e.g. via Homebrew). Stop them first:
```bash
brew services stop postgresql
brew services stop redis
```

### 2. Backend Cannot Connect to Postgres inside Docker

**Symptom**: `/health/ready` returns `"postgres": "unreachable: [Errno 111] Connection refused"`.

**Fix**: Check `POSTGRES_HOST`. Inside Docker Compose, it must be `postgres`, not `localhost`. When running FastAPI on the host machine, it must be `localhost`.

### 3. Container Fails Health Check

**Inspect logs**:
```bash
docker compose -f infra/docker/compose.yml logs postgres
docker compose -f infra/docker/compose.yml logs redis
docker compose -f infra/docker/compose.yml logs backend
```

---

## Future Production Architecture

```text
                                Internet
                                   │
                                   ▼
                         Cloud Load Balancer
                       (AWS ALB / GCP Cloud LB)
                                   │
                                   ▼
                   FastAPI Backend Container Cluster
                 (AWS ECS Fargate / GCP Cloud Run)
                               /       \
                              /         \
                             ▼           ▼
                      Managed PostgreSQL  Managed Redis
                      (AWS Aurora /       (AWS ElastiCache /
                       GCP Cloud SQL)      GCP Memorystore)
                             │
                             ▼
                     Sarvam AI Cloud API
```

In production:
- PostgreSQL and Redis are managed cloud services (automated backups, multi-AZ failover, automated patching).
- Backend instances are stateless containers scaling horizontally behind a load balancer.
- Secrets are securely injected via cloud secret managers (AWS Secrets Manager / GCP Secret Manager).
