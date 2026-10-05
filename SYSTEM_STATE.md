# SYSTEM_STATE: Multi-Cloner (V-Space)

> **Project**: Multi-Cloner (Multi-Account Virtualization Space for Social Media)  
> **Repository Path**: `d:\porto\multi-cloner`  
> **Status**: Inisialisasi & Arsitektur (Phase 1)  
> **Last Updated**: 2026-10-05  

---

## 🎯 Project Mission
Aplikasi Android berbasis **User-Space Virtualization Engine** untuk mengkloning dan menjalankan banyak akun sosial media (WhatsApp, Telegram, Facebook, Instagram) secara simultan dalam lingkungan sandbox terisolasi tanpa perlu re-install APK dan bebas dari deteksi anti-tamper.

---

## 🛠️ Tech Stack & Key References
- **UI/Management Layer**: Android Kotlin (Modern Material 3 / Jetpack Compose) atau Flutter Native Platform Channel.
- **Virtual Core Engine**: Berbasis arsitektur **NewBlackbox (ALEX5402)** & **BlackBox Core** (C++ NDK Binder Hooking, ART method proxying, multi-architecture ARM64/ARMv7, Android 5.0 - 15+ support).
- **Stealth & Identity Spoofing**: Custom Virtual Device Identity (Spoofed Android ID, IMEI, Build Fingerprint, MAC address per profile).
- **Storage Virtualization**: Virtual Scoped Storage redirecting `/data/data/<package>` ke `/data/data/com.porto.multicloner/virtual/users/<user_id>/`.
- **Background Keep-Alive**: Persistent Foreground Service dengan battery optimization whitelist.

---

## 📋 Feature Roadmap & Progress Checklist

### Phase 1: Architecture & Project Scaffolding
- [x] Analisis kelayakan teknis (System Analyst, QA, Mobile Dev Expert)
- [x] Riset engine industri teruji di GitHub (`ALEX5402/NewBlackbox`, `BlackBox`)
- [x] Inisialisasi Layer 1 (`SYSTEM_STATE.md`, `docs/architecture.md`, `AGENTS.md`, `.gitignore`, `.env.example`)

### Phase 2: Core Virtualization Module Setup
- [x] Inisialisasi Android project Gradle multi-module structure (`:app` & `:core`)
- [x] Integrasi NDK C++ Hooking binaries (`arm64-v8a` & `armeabi-v7a`) via CMakeLists.txt & vcore_hook.cpp
- [x] Setup `VirtualCore` lifecycle di Android `Application` class (`attachBaseContext`)
- [x] Pendaftaran stub multi-processes `:p0`–`:p4` & `:daemon` di `AndroidManifest.xml`
- [x] Virtual File System (`VFileSystem`) storage directory isolation

### Phase 3: Identity Spoofing & Anti-Detection
- [x] Virtual Identity Provider (`DeviceSpoofManager`) untuk generate Android ID, IMEI, Build Model unik per profil
- [x] Native JNI Stealth hook untuk menyembunyikan signature virtualization container
- [x] Persistent background service daemon (`DaemonService`)

### Phase 4: UI & App Management
- [x] Dashboard launcher Jetpack Compose Material 3 (`DashboardScreen.kt`) dengan status engine
- [x] App picker modal (`AppPickerModal.kt`) untuk memindai aplikasi sosial media terpasang
- [x] Profile alias manager & KTP hardware display (Android ID pill)
- [x] One-tap launch per user space ke stub process terisolasi

### Phase 5: QA Testing & Edge Case Verification
- [ ] Uji coba isolasi SQLite database antar klon
- [ ] Uji coba background notification keep-alive
- [ ] Stress test multi-proses RAM & thermal throttling

---

## 💻 Local Runbook & Environment
- **JDK Version**: OpenJDK 17 / 21
- **Android SDK**: compileSdk 34+, minSdk 24 (Android 7.0 - 15)
- **NDK**: NDK r25c+ (C++20 support)
