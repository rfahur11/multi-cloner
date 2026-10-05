# 📱 Multi-Cloner (V-Space)

> An advanced Android User-Space Virtualization Engine & Identity Sandbox designed for running multiple isolated social media accounts (WhatsApp, Telegram, Instagram, Facebook) simultaneously with hardware identity spoofing and anti-tamper stealth mechanisms.

---

## 🚀 Key Features

- **Multi-Account Virtualization**: Run multiple instances of social media apps without installing duplicate APKs.
- **Hardware Identity Spoofing**: Generate unique virtual Android IDs, IMEIs, and device fingerprints per profile to prevent server-side account grouping and anti-fraud flags.
- **Virtual Scoped Storage**: Complete data and SQLite isolation for each cloned profile (`/data/user/<uid>/`).
- **Stealth & Anti-Detection**: Conceal virtualization and hooking signatures from target apps.
- **Background Daemon Keep-Alive**: Foreground service maintaining push notification delivery and background sync without high battery drain.

---

## 🏗️ Architecture Overview

The project is structured into two main modules:
- `:app`: Jetpack Compose launcher UI, dashboard, profile manager, and app selector.
- `:core`: Virtualization core engine (Kotlin + C++ NDK) handling Binder IPC interception, process isolation (`:p0`, `:p1`, ...), and dynamic proxies.

For detailed architecture diagrams, component mappings, and failure recovery protocols, see [docs/architecture.md](docs/architecture.md).

---

## 🛠️ Tech Stack

- **UI**: Kotlin, Jetpack Compose, Material 3, ViewModel, Coroutines
- **Core Engine**: C++20 (Android NDK), CMake, Java Reflection & Dynamic Proxy
- **Database**: Room Database (SQLite ORM) for profile metadata
- **Target OS**: Android 7.0 (API 24) to Android 15+ (API 35), ARM64 (`arm64-v8a`) & ARMv7

---

## 📄 License
This project is for educational and research purposes.
