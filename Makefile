.PHONY: help infra-up infra-down infra-clean stack-up logs dev-backend test-backend lint-backend format-backend test-client

COMPOSE_FILE = infra/docker/compose.yml

help: ## Display available targets
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "\033[36m%-20s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)

infra-up: ## Start PostgreSQL and Redis in the background
	docker compose -f $(COMPOSE_FILE) up -d postgres redis

infra-down: ## Stop local infrastructure services
	docker compose -f $(COMPOSE_FILE) down

infra-clean: ## Stop infrastructure and delete local data volumes (WARNING: wipes DB/Redis data)
	docker compose -f $(COMPOSE_FILE) down -v

stack-up: ## Build and start the entire stack (PostgreSQL, Redis, FastAPI Backend)
	docker compose -f $(COMPOSE_FILE) up -d --build

logs: ## View real-time logs from Docker Compose services
	docker compose -f $(COMPOSE_FILE) logs -f

dev-backend: ## Run FastAPI backend locally with hot-reload (requires infra-up)
	cd backend && uv run uvicorn app.main:app --reload --port 8000

test-backend: ## Run backend unit test suite
	cd backend && uv run pytest

lint-backend: ## Run backend linter and format check
	cd backend && uv run ruff check . && uv run ruff format --check .

format-backend: ## Format backend code with Ruff
	cd backend && uv run ruff check --fix . && uv run ruff format .

test-client: ## Run client multiplatform tests
	cd client && ./gradlew :shared:allTests

artemis-setup: ## Set up Google ARTEMIS environment and install MCP integration
	cd tools/artemis && uv sync && uv run artemis mcp --install antigravity

artemis-web: ## Start Google ARTEMIS Web Dashboard
	cd tools/artemis && ./start.sh

artemis-doctor: ## Check ARTEMIS and Android environment readiness
	cd tools/artemis && uv run artemis doctor
