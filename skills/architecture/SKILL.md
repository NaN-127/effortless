---
name: architecture
description: Global system architecture and monorepo coordination rules for Effortless
---

# Purpose
Governs the overall product architecture spanning both the Python FastAPI backend (`backend/`) and Kotlin Multiplatform client (`client/`).

# Scope
Monorepo layout, domain decoupling, cross-project dependencies, and service boundaries.

# When to use
Use when modifying top-level repository structures, introducing new service modules, or evaluating system integration points.

# Architecture rules
- Effortless is a single product split into `backend/` and `client/`.
- Domain models in both backend and client must remain pure and free of external platform/framework dependencies.
- The client NEVER calls Sarvam AI or cloud speech APIs directly; all requests route through `backend/`.
- The backend never embeds platform-specific logic (no Android/iOS branching).

# Preferred patterns
- Clean Architecture with Domain, Data, Presentation layers.
- Contract-first API development documented in `docs/contracts/`.
- Provider abstractions behind Protocols (Python) and Interfaces (Kotlin).

# Anti-patterns
- ❌ Direct communication between client and Sarvam AI.
- ❌ Committing secrets or environment files.
- ❌ Introducing microservices or distributed queues prematurely.

# Testing requirements
- Independent test suites in `backend/tests/` and `client/shared/src/commonTest/`.
- Continuous contract validation between client DTOs and backend Pydantic models.

# Platform considerations
- Shared business logic resides in `commonMain`; platform runners (`androidApp`, `iosApp`, `desktopMain`) remain lightweight wrappers.

# Related documentation
- [docs/contracts/api-contract.md](../../docs/contracts/api-contract.md)
- [AGENTS.md](../../AGENTS.md)
