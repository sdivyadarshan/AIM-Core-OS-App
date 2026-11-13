# 🤖 AIM Core OS (AIM-Bot Companion OS)

> **🚧 Project Status: Work in Progress (Ongoing Development)**  
> AIM Core OS is currently under active development. Core UI modules, expressive face animations, TTS voice feedback, and hardware simulation mode are functional, while physical ESP32 firmware integration, AI vision pipelines, and persistence features are still being expanded.

**AIM Core OS** is an Android-powered robot operating system and kiosk/launcher interface built with **Kotlin** and **Jetpack Compose** for **AIM-Bot** — an autonomous indoor mapping and smart room companion robot powered by an **ESP32** controller.

---

## ✨ Key Features

### 🎭 Expressive Kawaii Robot Face (`LiveFaceScreen`)
- **EMO / Kawaii-Style Animated Eyes:** Large squircle eyes with glossy catchlight sparkles, gentle breathing bounce, horizontal pupil sway, blinking, rosy cheeks, animated eyebrows, and expressive mouth states (`:3`, open smile, shocked `o`, sad pout).
- **14 Dynamic Eye States:** Automatically adapts color and expression across `IDLE`, `SCANNING`, `THINKING`, `SEARCHING`, `HAPPY`, `WARNING`, `LOW_BATTERY`, `DISCONNECTED`, `OBSTACLE_DETECTED`, `CHARGING`, `NAVIGATING`, `EXPLORING`, `RC_MODE`, and `ASSISTANT_MODE`.
- **Interactive Quick Dialer:** Built-in right-side dialer menu and live sensor telemetry panel.

### 🔊 Cute Pet Voice Synthesizer (`TtsManager`)
- Uses Android `TextToSpeech` tuned with a higher pitch (`1.55f`) and playful speech rate (`1.18f`) for a friendly pet-robot personality.
- Announces mode and tab transitions with expressive lines and supports instant mute/unmute from Settings.

### 📡 ESP32 Wi-Fi Telemetry & Dual-Mode Operation (`Esp32NetworkManager`)
- **Live Hardware Mode:** Connects over local Wi-Fi (`OkHttp`) to an ESP32 endpoint (default `192.168.4.1:80`):
  - `GET /status` — Polls ToF distance (`cm`), LDR light sensor, IR hex codes, battery %, Wi-Fi RSSI, and robot status.
  - `POST /command` — Dispatches JSON movement/control commands (`{"cmd": "...", "timestamp": ...}`).
  - `GET /ping` — Measures round-trip latency and connectivity health.
- **Built-in Simulation Mode:** Automatically generates realistic indoor mapping telemetry, sweeping ToF distances, LDR variations, IR signals, and battery drain so the app can be tested without physical hardware.

### 🧭 Multi-Screen Robot Control Suite
- **Home (`LiveFaceScreen`):** Full-screen immersive robot face with quick navigation.
- **Explore (`ExploreScreen`):** Indoor mapping view with occupancy grid points, heading angle, path trail, and exploration percentage.
- **RC Mode (`RcScreen`):** Manual directional control pad with configurable speed limits.
- **Vision (`VisionScreen` / `CameraScreen`):** Live **CameraX** preview with obstacle snapshot logging and AI detection toggle.
- **Room Assistant (`AssistantScreen`):** Smart room appliance cards with IR power code controls (`irCodeOn` / `irCodeOff`).
- **Engineering (`EngineeringScreen` / `DiagnosticsScreen`):** Live terminal logs, component status badges (ESP32, Camera, Wi-Fi, ToF, IR, LDR), RAM/CPU metrics, and self-diagnostics runner.
- **Settings & Guide (`SettingsScreen` / `UnderstandingAimbotScreen`):** IP/Port configuration, landscape/portrait toggle, developer/simulation switches, and an interactive Eye Expression Guide.
- **Dedicated Robot Launcher:** Supports `CATEGORY_HOME` in `AndroidManifest.xml` to run as the robot's dedicated Android launcher in immersive landscape mode.

---

## 📱 Download APK (Preview Build)

> ⚠️ Early development build — features and UI may change frequently.

[![Download APK](https://img.shields.io/badge/Download-APK%20(WIP)-00E5FF?style=for-the-badge&logo=android&logoColor=white)](https://github.com/sdivyadarshan/AIM-Core-OS/raw/main/apk/AIM-Core-OS-debug.apk)

*(Or visit the [Releases](https://github.com/sdivyadarshan/AIM-Core-OS/releases) page if using GitHub Releases.)*

---

## 🛠️ Tech Stack

| Layer | Technology |
| :--- | :--- |
| **Language** | Kotlin (`2.2.10`) |
| **UI Framework** | Jetpack Compose + Material 3 |
| **Architecture** | MVVM (`ViewModel` + `StateFlow` + `Repository`) |
| **Camera** | AndroidX CameraX (`camera2`, `core`, `lifecycle`, `view`) |
| **Networking** | OkHttp, Retrofit, Moshi |
| **Async / Streams** | Kotlin Coroutines & Flow |
| **Storage / Config** | DataStore Preferences, Room (configured), Secrets Gradle Plugin |
| **Voice** | Android `TextToSpeech` |
| **Testing** | JUnit, Robolectric, Roborazzi, Compose UI Test |

- **Min SDK:** 24 (Android 7.0)  
- **Target / Compile SDK:** 36  

---

## 📂 Project Structure

```text
app/src/main/java/com/example/
├── MainActivity.kt                  # Immersive Activity, Scaffold, Tab switching & TTS hooks
├── data/
│   ├── model/RobotModels.kt         # Eye states, SensorData, MapData, Settings, Diagnostics
│   ├── network/Esp32NetworkManager.kt # HTTP client (/status, /command, /ping) + Simulation flow
│   └── repository/RobotRepository.kt
├── ui/
│   ├── components/                  # Eyes, GlassCard, ControlPad, Header, NavBar, SensorPanel
│   ├── screens/                     # LiveFace, Explore, Rc, Vision, Assistant, Engineering, Settings
│   ├── theme/                       # Cyber dark palette, Typography, Shapes
│   └── viewmodel/RobotViewModel.kt  # Central state management & diagnostics actions
└── utils/
    └── TtsManager.kt                # Cute pet-voice Text-to-Speech manager
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio (latest stable supporting AGP `9.1.1` / JDK 11+)
- Android device or emulator running Android 7.0+ (API 24+)
- *(Optional)* ESP32 running an HTTP server on `/status`, `/command`, and `/ping`

### Build & Run
1. Clone the repository:
   ```bash
   git clone https://github.com/sdivyadarshan/AIM-Core-OS.git
   cd YOUR_REPO
   ```
2. Copy `.env.example` to `.env` if needed for local secrets/config.
3. Open the project in Android Studio and sync Gradle, or build from the command line:
   ```bash
   ./gradlew assembleDebug
   ```
4. The debug APK will be generated at:
   `app/build/outputs/apk/debug/app-debug.apk`

---

## 🗺️ Roadmap (Ongoing)

- [x] Animated kawaii/EMO robot face & 14 expressive eye states
- [x] Cute pet-style TTS voice announcements
- [x] Simulation mode for offline telemetry & indoor mapping preview
- [x] CameraX vision screen & engineering diagnostics terminal
- [ ] Full physical ESP32 firmware sketch & WebSocket low-latency control
- [ ] Real SLAM / LiDAR or ToF occupancy map persistence
- [ ] Live on-device/cloud AI object detection & conversational voice assistant
- [ ] Signed release APK builds & automated GitHub Actions CI

---

## 🤝 Contributing

Since this is an ongoing project, suggestions, issues, and pull requests are welcome! Feel free to open an issue to discuss ideas or hardware integrations.

## 📄 License

Specify your preferred license here (e.g., `MIT License`).
