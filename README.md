# Effortless

> Speak in one language. Type in another.

Effortless is a cross-platform AI-powered keyboard/input application that converts spoken input into translated text, ready for insertion into any active text field.

## How It Works

```
User speaks (Hindi)  →  "मुझे कल ऑफिस जाना है"
                              │
                     Speech Recognition (Saaras)
                              │
                     Translation (Mayura)
                              │
                     "I have to go to the office tomorrow."
                              │
                     Text inserted into active app
```

## Architecture

- **Backend**: Python, FastAPI, async-first
- **AI Provider**: Sarvam AI (Saaras for speech, Mayura/Sarvam-Translate for translation)
- **Client**: Kotlin Multiplatform + Compose Multiplatform
- **Platforms**: Android (IME), iOS (Keyboard Extension), Desktop
- **Realtime**: WebSocket streaming for live speech-to-translation

## Project Structure

```
├── AGENTS.md                    # Agent rules
├── README.md                    # This file
│
├── docs/
│   ├── architecture/            # System design documents
│   │   ├── overview.md
│   │   ├── backend.md
│   │   ├── ai-pipeline.md
│   │   ├── realtime-pipeline.md
│   │   ├── provider-abstraction.md
│   │   ├── language-system.md
│   │   ├── security.md
│   │   ├── privacy.md
│   │   ├── observability.md
│   │   └── scalability.md
│   │
│   ├── ai/                      # AI model documentation
│   │   ├── sarvam-model-strategy.md
│   │   ├── future-llm-layer.md
│   │   └── evaluation.md
│   │
│   ├── api/                     # API contracts
│   │   ├── overview.md
│   │   ├── authentication.md
│   │   ├── languages.md
│   │   ├── translation.md
│   │   ├── speech.md
│   │   ├── realtime.md
│   │   ├── errors.md
│   │   └── versioning.md
│   │
│   └── client/                  # Client platform documentation
│       ├── kmp-architecture.md
│       ├── android-keyboard.md
│       ├── ios-keyboard.md
│       └── desktop.md
│
├── skills/                      # Agent skill definitions
│   ├── backend-architecture/
│   ├── fastapi/
│   ├── sarvam-ai/
│   ├── speech-pipeline/
│   ├── translation/
│   ├── realtime-websocket/
│   ├── api-design/
│   ├── security/
│   ├── privacy/
│   ├── testing/
│   ├── ai-evaluation/
│   ├── observability/
│   └── kmp-integration/
│
├── backend/                     # FastAPI backend service
│   ├── app/                     # Clean architecture layers
│   │   ├── main.py              # FastAPI app factory
│   │   ├── api/                 # Endpoints & dependencies
│   │   ├── application/         # Use case orchestration
│   │   ├── core/                # Config, logging, exceptions
│   │   ├── domain/              # Pure domain & provider protocols
│   │   ├── infrastructure/      # Sarvam SDK & external integrations
│   │   └── schemas/             # API request/response schemas
│   ├── tests/                   # Pytest test suite
│   ├── Dockerfile               # Multi-stage production container
│   ├── pyproject.toml           # Backend dependencies and tools
│   └── README.md                # Backend quickstart guide
│
└── client/                      # Kotlin Multiplatform client (Android, iOS, Desktop)
    ├── shared/                  # KMP module (domain, data, core, Compose UI)
    ├── androidApp/              # Android application runner (APK)
    ├── iosApp/                  # iOS Xcode project wrapper
    ├── gradle/libs.versions.toml # Dependency version catalog
    └── README.md                # Client quickstart & build instructions
```

## Development Phases

| Phase | Status | Description |
|---|---|---|
| **0 — Research** | ✅ Complete | Sarvam API research and model evaluation |
| **1 — Documentation** | ✅ Complete | Architecture, skills, API contracts |
| **2 — Backend Bootstrap** | ✅ Complete | FastAPI project in `backend/`, config, tests, Docker |
| **3 — Client Bootstrap** | ✅ Complete | KMP project in `client/` (Android, iOS, Desktop, Compose) |
| **4 — CI/CD Foundation** | ✅ Complete | Monorepo GitHub Actions (backend, client, release) |
| **5 — Translation MVP** | ⬜ Next | `POST /api/v1/translation` pipeline integration |
| **6 — Speech MVP** | ⬜ | `POST /api/v1/speech/translate` audio pipeline |
| **7 — Realtime Voice** | ⬜ | WebSocket speech-to-translation streaming |
| **8 — Platform Keyboards**| ⬜ | Android IME & iOS Keyboard Extension integrations |

## Key Documentation

- [Architecture Overview](docs/architecture/overview.md) — System design
- [Development Environment & MCP](docs/development-environment.md) — Tooling strategy, Chucker, ARTEMIS, workflows
- [MCP Security Policy](docs/mcp-security.md) — Security tiers, access scopes, least privilege
- [Local Infrastructure & Docker](docs/infrastructure.md) — PostgreSQL, Redis, Docker Compose & networking
- [CI/CD Infrastructure](docs/ci-cd.md) — GitHub Actions workflows, caching & secrets
- [API Contract Specification](docs/contracts/api-contract.md) — Shared backend/client contracts
- [Sarvam Model Strategy](docs/ai/sarvam-model-strategy.md) — AI model selection
- [API Overview](docs/api/overview.md) — API design and contracts
- [Agent Rules](AGENTS.md) — Development rules and constraints

## Local Infrastructure Quickstart

Effortless provides reproducible local Docker services for **PostgreSQL 16** and **Redis 7**.

### 1. Hybrid Workflow (Recommended for backend development)

Run Postgres and Redis in Docker, while running FastAPI directly on your host machine:

```bash
# 1. Start PostgreSQL and Redis containers in the background
make infra-up

# 2. Run FastAPI locally with auto-reload
make dev-backend

# 3. Check health and dependency readiness
curl http://localhost:8000/health/ready
```

### 2. Full Container Stack

Run all services (FastAPI, Postgres, Redis) in Docker:

```bash
make stack-up      # Build & start all containers
make logs          # Follow logs
make infra-down    # Stop containers
make infra-clean   # Stop and wipe persistent database volumes
```

For full details, environment variables, and architecture diagrams, see [docs/infrastructure.md](docs/infrastructure.md).

## Supported Languages

23 languages supported: English + 22 scheduled Indian languages (Hindi, Bengali, Tamil, Telugu, Malayalam, Marathi, Gujarati, Kannada, Odia, Punjabi, Assamese, Urdu, and 11 more).

See [docs/architecture/language-system.md](docs/architecture/language-system.md) for the complete language matrix.

