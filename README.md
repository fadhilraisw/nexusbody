# NexusBody v2.0 - Medical Grade Bodybuilding Intelligence

NexusBody adalah platform ekosistem kesehatan digital yang dirancang khusus untuk atlet binaraga profesional. Aplikasi ini menggabungkan presisi medis dengan teknologi AI tercanggih untuk memantau performa dan kesehatan organ dalam secara real-time.

---

## 🏛️ Arsitektur & Struktur Proyek

Aplikasi ini menggunakan **Single Activity Architecture** dengan Jetpack Compose. Seluruh navigasi dikelola oleh **Navigation Compose**.

### 📂 Struktur Lengkap File & Folder
```markdown
nexusbody/
├── app/                        # Modul Utama Aplikasi Android
│   ├── src/main/java/com/rais/nexusbody/
│   │   ├── di/                 # Dependency Injection (Hilt Modules)
│   │   │   ├── NetworkModule.kt     # Penyedia Retrofit & API Service
│   │   │   ├── DatabaseModule.kt    # Penyedia Room Database & DAO
│   │   │   └── RepositoryModule.kt  # Binding Interface ke Implementasi Repo
│   │   ├── core/               # Infrastruktur Global & Shared
│   │   │   ├── database/       # Konfigurasi Database Lokal (Room)
│   │   │   ├── network/        # Client Komunikasi (Food API, AI, Supabase)
│   │   │   ├── ui/             # Tema (Glassmorphism) & Komponen UI Premium
│   │   │   └── util/           # Helper, Extensions, & Safe Call Wrappers
│   │   ├── data/               # Layer Data (Implementasi Konkret)
│   │   │   ├── local/          # Room DAO, Entity (Tabel), & Type Converters
│   │   │   ├── remote/         # Data Transfer Objects (DTO) untuk API
│   │   │   └── repository/     # Logika Sinkronisasi Lokal (Room) & Cloud (Supabase)
│   │   ├── domain/             # Layer Bisnis (Murni Kotlin, No Android Framework)
│   │   │   ├── model/          # Data Model murni untuk Logika Bisnis
│   │   │   └── repository/     # Kontrak (Interface) untuk Akses Data
│   │   ├── feature/            # Modul Fitur (UI Compose & Logic ViewModel)
│   │   │   ├── auth/           # Login & Registrasi (Supabase Auth)
│   │   │   ├── health/         # Biomarker PAGD, Lab, & Meds Scheduler
│   │   │   ├── nutrition/      # Log Makan & Library Gizi (OpenFoodFacts)
│   │   │   ├── workout/        # Latihan, Muscle Mastery, & XP System
│   │   │   ├── gamification/   # Quests & Profil Leveling
│   │   │   ├── dashboard/      # Hub Utama Reaktif (Combine Stream)
│   │   │   └── ai_report/      # Analisa Medis AI (Gemini 3.1 & Groq)
│   │   ├── MainActivity.kt     # Entry Point Tunggal & NavHost
│   │   └── NexusBodyApplication.kt # Kelas Aplikasi (Hilt Inisialisasi)
├── init_supabase.sql           # Script SQL untuk Setup Tabel di Cloud
└── local.properties            # File Rahasia (API Keys Gemini, Groq, Supabase)
```

---

## 🛠️ Tech Stack & Konfigurasi

### Front-End (UI/UX)
*   **Jetpack Compose:** Framework UI deklaratif modern.
*   **Navigation Compose:** Pengatur alur layar tunggal (Tanpa Fragment).
*   **Coil:** Library pemuatan gambar asinkron.
*   **Material 3:** Standar desain terbaru dari Google.

### Back-End & Data (Logic)
*   **Hilt (Dagger):** Dependency Injection untuk efisiensi instansiasi objek.
*   **Room Database:** Penyimpanan lokal Offline-First (SSOT).
*   **Supabase (Postgrest):** Backend Cloud untuk sinkronisasi data antar perangkat.
*   **Supabase Auth:** Manajemen akun dan session keamanan.
*   **Kotlin Coroutines & Flow:** Menangani asinkronitas data secara reaktif.

### Intelligence Engine (AI)
*   **Google AI Studio (Gemini 3.1 Pro/Flash):** Analisis medis mendalam.
*   **Groq API (Llama 4 Scout / Llama 3.3):** Respon cepat berbasis variabel klinis.

---

## 📋 Detail Pemetaan Seluruh File Kotlin

| Lokasi / Folder | Nama File | Peran / Departemen | Fungsi Spesifik |
| :--- | :--- | :--- | :--- |
| **`Root`** | `MainActivity.kt` | **Front-End** | Navigator pusat; mengelola `NavHost` dan Tema. |
| | `NexusBodyApplication.kt` | **Infrastructure** | Inisialisasi Hilt dan lifecycle aplikasi global. |
| **`di/`** | `NetworkModule.kt` | **Back-End (Data)** | Menyediakan koneksi Retrofit untuk Food API & Groq. |
| | `DatabaseModule.kt` | **Back-End (Data)** | Menyediakan instansi Room Database dan seluruh DAO. |
| | `RepositoryModule.kt` | **Back-End (Logic)** | Menghubungkan Interface Domain ke Implementasi Data. |
| **`core/network/`** | `AiManager.kt` | **Back-End (Bridge)** | Switcher "Otak" AI; mengelola Gemini & Groq. |
| | `AiApiService.kt` | **Back-End (Data)** | Definisi API Retrofit khusus untuk Groq. |
| | `FoodApiService.kt` | **Back-End (Data)** | Definisi API Retrofit untuk OpenFoodFacts. |
| **`core/database/`** | `NexusBodyDatabase.kt` | **Back-End (Data)** | Konfigurasi utama Room; definisi versi & tabel. |
| **`core/ui/`** | `Theme/Color/Type.kt` | **Front-End** | Definisi identitas visual (Glassmorphism). |
| | `PremiumGlassCard.kt` | **Front-End** | Komponen UI kartu kaca yang digunakan di semua fitur. |
| **`data/local/`** | `Converters.kt` | **Back-End (Data)** | Mengubah tipe data kompleks (List/Map) ke format SQL. |
| | `entity/*.kt` | **Back-End (Data)** | Definisi skema tabel (Health, Workout, Quest, dll). |
| | `dao/*.kt` | **Back-End (Data)** | Interface eksekutor query SQL (Insert, Query, Delete). |
| **`data/repository/`** | `*Impl.kt` | **Back-End (Logic)** | Logika sinkronisasi: Ambil Lokal -> Sync ke Cloud. |
| **`domain/model/`** | `*.kt` | **Domain Layer** | Data model murni (POJO) untuk transportasi antar layer. |
| **`feature/auth/`** | `AuthScreen.kt` | **Front-End** | UI Login & Register premium. |
| | `AuthViewModel.kt` | **Back-End (Logic)** | Mengelola autentikasi Supabase & rate limiting. |
| **`feature/health/`** | `HealthAssessmentScreen.kt` | **Front-End** | Form 20+ Biomarker & Rekam Medis (Read/Write). |
| | `HealthViewModel.kt` | **Back-End (Logic)** | Logika "Smart Upsert" harian & meds scheduler. |
| **`feature/nutrition/`** | `NutritionLogScreen.kt` | **Front-End** | UI Log makan dengan pencarian database global. |
| | `NutritionViewModel.kt` | **Back-End (Logic)** | Kalkulasi makro reaktif & pencarian network. |
| **`feature/workout/`** | `WorkoutLogScreen.kt` | **Front-End** | UI Riwayat latihan & detail gerakan (Read/Write). |
| | `WorkoutViewModel.kt` | **Back-End (Logic)** | Logika Gamifikasi (+10 XP) & Muscle Mastery. |
| **`feature/dashboard/`** | `SpatialDashboardScreen.kt`| **Front-End** | UI Dashboard Hub; menampilkan ringkasan reaktif. |
| | `DashboardViewModel.kt` | **Back-End (Logic)** | Agregator data; mengolah 4 aliran database jadi 1 UI. |

---

## 🔄 Alur & Sequence Program (Event Flow)

### 1. Alur Autentikasi (First Entry)
`MainActivity` -> `AuthScreen` (UI) -> `AuthViewModel` -> `Supabase Auth` (Cloud) -> `MainActivity` (Navigasi ke Dashboard).

### 2. Alur Pengisian Data Medis (PAGD)
`HealthAssessmentScreen` (Form) -> `HealthViewModel` -> `HealthAssessmentRepositoryImpl` -> **Parallel Save:**
1.  Simpan ke `Room` (Local).
2.  `Upsert` ke `Supabase` (Cloud).
3.  `DashboardViewModel` mendeteksi perubahan -> Update UI Dashboard seketika.

### 3. Alur Analisa AI (Intelligence Flow)
`AiClinicalReportScreen` -> `AiReportViewModel` -> **Context Collector:**
1.  Ambil Tensi dari `HealthRepo`.
2.  Ambil Kalori dari `NutritionRepo`.
3.  Ambil Latihan dari `WorkoutRepo`.
-> Kirim ke `AiManager` -> Pilih Model (Gemini/Groq) -> Tampilkan Hasil Analisa.

---



