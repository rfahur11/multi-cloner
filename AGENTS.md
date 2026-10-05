# 🛡️ Project Protocol: Multi-Cloner (V-Space)

> **Project Root**: `d:\porto\multi-cloner`  
> **Parent Ecosystem**: `d:\porto`

---

## ⚡ 1. Pre-Flight Protocol (Sebelum Menulis/Mengubah Kode)
1. Baca selalu [SYSTEM_STATE.md](file:///d:/porto/multi-cloner/SYSTEM_STATE.md) untuk memahami roadmap saat ini.
2. Rujuk [docs/architecture.md](file:///d:/porto/multi-cloner/docs/architecture.md) sebelum memodifikasi IPC hook atau storage virtualization untuk mencegah regresi arsitektur.
3. Selalu periksa kompatibilitas arsitektur CPU (`arm64-v8a` vs `armeabi-v7a`) saat menyentuh modul C++/JNI.

---

## ⚖️ 2. Adaptive Rigor Matrix
- **Level 1 (UI & Dashboard Tweak)**: Eksekusi langsung komponen Jetpack Compose/Kotlin UI.
- **Level 2 (Identity Spoofing & Config Layer)**: Uji konsistensi data profil per user ID sebelum merge ke core engine.
- **Level 3 (Low-Level Hooking & VirtualCore Modification)**: Wajib menjalankan full verification (compile check NDK, runtime isolation test, check memory leaks & OOM).

---

## 🔍 3. Root Cause Analysis (RCA) Protocol untuk Crash Sandbox
Jika target app (misal WhatsApp) force close di dalam sandbox:
1. **Analisa Logcat**: Filter tag `AndroidRuntime`, `ActivityTaskManager`, dan `DEBUG`.
2. **Kategorikan Error**:
   - ClassNotFound / Hidden API Access Restriction (L-greylist) ➔ Atasi via hidden API bypass.
   - SecurityException / Authority conflict ➔ Atasi via ProviderClient proxy.
   - Native SigSEGV (11) di `.so` target app ➔ Atasi via inline C++ hook memory patch.
