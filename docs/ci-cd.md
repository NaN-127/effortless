# Effortless Monorepo CI/CD Architecture

This document describes the continuous integration and continuous deployment (CI/CD) pipelines for the Effortless project.

---

## 1. Architecture Overview

Effortless uses **GitHub Actions** organized into independent, monorepo-aware workflows:

```
.github/workflows/
├── backend-ci.yml     # Validates FastAPI backend (Lint, Format, Test, Docker Build)
├── client-ci.yml      # Validates KMP client (Android, Desktop, iOS)
└── release.yml        # Generates release artifacts on version tags (v*)
```

### Key Principles
1. **Independent Pipelines**: Modifying `backend/` does not trigger client builds; modifying `client/` does not trigger backend builds.
2. **Least Privilege**: All workflows run with `permissions: contents: read`.
3. **Zero Leaked Secrets**: Production secrets and API keys are never printed, checked into Git, or sent to pull-request runs.
4. **Local Parity**: Every command run in CI has a direct, 1-to-1 equivalent runnable on a developer's local machine.

---

## 2. Backend Pipeline (`backend-ci.yml`)

- **Runner**: `ubuntu-latest`
- **Triggers**:
  - Push / Pull Request to `main` or `master` matching `backend/**` or `.github/workflows/backend-ci.yml`.
  - Manual trigger via `workflow_dispatch`.

### Stages & Jobs

```
Backend CI
├── Job: Backend / Lint & Test
│   ├── 1. Checkout repository (actions/checkout@v4)
│   ├── 2. Setup uv & dependency cache (astral-sh/setup-uv@v5)
│   ├── 3. Setup Python 3.12 (actions/setup-python@v5)
│   ├── 4. Install dependencies (uv sync)
│   ├── 5. Lint check (uv run ruff check .)
│   ├── 6. Format check (uv run ruff format --check .)
│   └── 7. Unit tests (uv run pytest)
│
└── Job: Backend / Docker Build (depends on Lint & Test)
    ├── 1. Setup Docker Buildx (docker/setup-buildx-action@v3)
    └── 2. Build image validation (docker/build-push-action@v6)
```

---

## 3. Client Pipeline (`client-ci.yml`)

- **Runners**:
  - `ubuntu-latest` for Android & Desktop JVM
  - `macos-latest` for iOS Simulator framework compilation & native tests
- **Triggers**:
  - Push / Pull Request to `main` or `master` matching `client/**` or `.github/workflows/client-ci.yml`.
  - Manual trigger via `workflow_dispatch`.

### Stages & Jobs

```
Client CI
├── Job: Client / Android & Desktop (Linux)
│   ├── 1. Checkout repository
│   ├── 2. Setup JDK 21 (Temurin)
│   ├── 3. Setup Android SDK (android-actions/setup-android@v3)
│   ├── 4. Setup Gradle with automatic dependency caching (gradle/actions/setup-gradle@v4)
│   ├── 5. Run Desktop tests (./gradlew :shared:desktopTest)
│   ├── 6. Build Desktop JAR (./gradlew :shared:desktopJar)
│   ├── 7. Build Android Debug APK (./gradlew :androidApp:assembleDebug)
│   └── 8. Upload Android Debug APK artifact (retention: 7 days)
│
└── Job: Client / iOS Framework & Tests (macOS)
    ├── 1. Checkout repository
    ├── 2. Setup JDK 21 (Temurin)
    ├── 3. Setup Gradle (gradle/actions/setup-gradle@v4)
    ├── 4. Build iOS Framework (./gradlew :shared:linkDebugFrameworkIosSimulatorArm64)
    └── 5. Run iOS Simulator tests (./gradlew :shared:iosSimulatorArm64Test)
```

---

## 4. Release Pipeline (`release.yml`)

- **Trigger**: Tag push matching `v*` (e.g. `v0.1.0`), plus `workflow_dispatch`.
- **Packaging Jobs**:
  1. `Release / Backend Docker Image`: Builds production container tagged with the Git release tag.
  2. `Release / Client Android APK`: Assembles Android package and stores build artifact (30-day retention).
  3. `Release / Client Desktop Distribution`: Packages desktop binaries.

---

## 5. Caching Strategy

| Technology | Action | Cache Target | Invalidation Condition |
|---|---|---|---|
| **Python / uv** | `astral-sh/setup-uv@v5` | `~/.cache/uv` | Changes to `backend/uv.lock` |
| **Gradle / KMP** | `gradle/actions/setup-gradle@v4` | `~/.gradle/caches`, Gradle wrapper | Changes to `client/gradle/libs.versions.toml` or `gradle-wrapper.properties` |

---

## 6. Secrets Specification

> [!CAUTION]
> Secrets must NEVER be checked into source control or printed in CI logs.

### Planned GitHub Actions Secrets

| Secret Name | Intended Workflow | Purpose | Required for PRs? |
|---|---|---|---|
| `SARVAM_API_KEY` | Integration Tests / Deployment | Real Sarvam API authentication | **No** (CI uses dummy keys and test doubles) |
| `DOCKER_REGISTRY_TOKEN` | `release.yml` | Authenticates Docker registry image publishing | **No** (Only for tagged releases) |
| `ANDROID_KEYSTORE_BASE64` | `release.yml` | Base64-encoded Android release keystore | **No** (Only for signed release builds) |
| `ANDROID_KEYSTORE_PASSWORD` | `release.yml` | Android keystore decryption password | **No** (Only for signed release builds) |
| `ANDROID_KEY_ALIAS` | `release.yml` | Release key alias | **No** |
| `ANDROID_KEY_PASSWORD` | `release.yml` | Release key password | **No** |
| `APPLE_CERTIFICATES_BASE64` | `release.yml` | Apple distribution certificates | **No** |
| `APPLE_PROVISIONING_PROFILE`| `release.yml` | Mobileprovision profile | **No** |

---

## 7. Local Parity (Run CI Checks Locally)

Run these commands locally to guarantee that your changes will pass GitHub Actions:

### Backend Checks
```bash
cd backend
uv run ruff check .
uv run ruff format --check .
uv run pytest
docker build -t typeless-backend .
```

### Client Checks
```bash
cd client
# Run unit tests across Desktop & iOS Simulator
./gradlew :shared:allTests

# Build Android APK
./gradlew :androidApp:assembleDebug

# Build Desktop JAR
./gradlew :shared:desktopJar

# Build iOS Framework (macOS only)
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
```

---

## 8. Troubleshooting Guide

### 1. Backend Lint / Format Failures
- **Issue**: Ruff flags unformatted imports or style errors.
- **Fix**: Run `uv run ruff check --fix .` and `uv run ruff format .` inside `backend/`.

### 2. Gradle Out of Memory
- **Issue**: Gradle daemon crashes or runs out of metaspace.
- **Fix**: Memory is configured in `client/gradle.properties`:
  `org.gradle.jvmargs=-Xmx4096m -XX:MaxMetaspaceSize=1024m`.

### 3. iOS Compilation Error on Linux
- **Issue**: iOS targets fail to compile on `ubuntu-latest`.
- **Fix**: iOS compilation and simulator testing require Xcode and must only run on the `macos-latest` runner (configured in `client-ci.yml`).
