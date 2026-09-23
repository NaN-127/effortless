# Effortless Client (Kotlin Multiplatform)

Cross-platform AI voice translation keyboard application built with **Kotlin Multiplatform (KMP)** and **Compose Multiplatform (CMP)** targeting **Android, iOS, and Desktop (JVM)**.

---

## Architecture Overview

The client adheres to **Clean Architecture** and **MVVM** with **Unidirectional Data Flow**:

```
client/
├── shared/                             # Kotlin Multiplatform Module
│   └── src/
│       ├── commonMain/                 # Shared Business Logic & Compose UI
│       │   └── kotlin/com/effortless/
│       │       ├── core/               # Result, AppError, Network, Koin DI, Platform Info
│       │       ├── domain/             # Pure Domain Models, Repository Contracts, Use Cases
│       │       ├── data/               # DTOs, Mappers, Remote API, Fake Repositories
│       │       └── presentation/       # Theme, Screens, ViewModels, Compose App()
│       │
│       ├── androidMain/                # Android actual implementations (OkHttp, PlatformInfo)
│       ├── iosMain/                    # iOS actual implementations (Darwin, MainViewController)
│       ├── desktopMain/                # Desktop actual implementations (CIO, Main.kt JVM Window)
│       └── commonTest/                 # Multiplatform unit tests (Use Cases, Mappers, ViewModels)
│
├── androidApp/                         # Android Application Runner (generates APK)
├── iosApp/                             # Native iOS Xcode Project (links Shared.framework)
└── gradle/libs.versions.toml           # Centralized dependency catalog
```

---

## Prerequisites

- **Java JDK**: Java 17+ (Homebrew OpenJDK 17 or Azul Zulu 21)
- **Android SDK**: API 34+ (SDK path configured in `local.properties`)
- **Xcode**: Xcode 15+ with iOS Simulator (for macOS builds)

---

## Build & Run Commands

From the `client/` directory:

### Run Shared Unit Tests
```bash
./gradlew :shared:allTests
```

### Build Android Debug APK
```bash
./gradlew :androidApp:assembleDebug
```
The resulting APK is generated at:
`androidApp/build/outputs/apk/debug/androidApp-debug.apk`

### Run Desktop Application (JVM)
```bash
./gradlew :shared:run
```
Or package the desktop JAR:
```bash
./gradlew :shared:desktopJar
```

### Build iOS Framework
```bash
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
```
The resulting framework is generated at:
`shared/build/bin/iosSimulatorArm64/debugFramework/Shared.framework`

---

## Offline Simulation & Test Doubles

During early client development before the backend is running, the client uses `FakeTranslationRepository`:
- Returns instant simulated translations for common Hindi/English phrases.
- Simulates realistic latency (300ms) with zero external network dependencies.
- Toggle between `FakeTranslationRepository` and `TranslationRepositoryImpl` via `core/di/AppModule.kt`.