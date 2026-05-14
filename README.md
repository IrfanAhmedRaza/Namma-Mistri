# 🏗️ Namma Mistri — Android Construction Assistant App

![Platform](https://img.shields.io/badge/Platform-Android-green)
![Language](https://img.shields.io/badge/Language-Kotlin-blue)
![Database](https://img.shields.io/badge/Database-Firebase%20Firestore-orange)
![Auth](https://img.shields.io/badge/Auth-Firebase%20Authentication-yellow)
![Min SDK](https://img.shields.io/badge/Min%20SDK-API%2024-red)

---

## 📖 About the Project

**Namma Mistri** is an Android application designed to assist local masons (Mistris) and construction workers in rural India. It solves two major real-world problems:

1. **Material Estimation** — Accurately calculates bricks, cement bags, and sand required for walls, slabs, and plastering.
2. **Labor Management** — Tracks daily attendance and wages of workers, and auto-calculates balance due for each worker.

> Built as part of an Android App Development internship project using GenAI tools.

---

## 📱 Screenshots

| Login | Register | Calculator |
|-------|----------|------------|
| Email + Password login with Firebase Auth | Create account with email verification | Wall / Slab / Plaster material calculator |

| Sites | Workers | Wage Summary |
|-------|---------|--------------|
| Manage multiple construction sites | Per-site worker list with balance due | Log attendance and advance payments |

---

## ✨ Features

- 🔐 **Firebase Authentication** — Email & password login with email verification OTP
- 👥 **Multiple User Support** — Each user has their own data stored separately in Firestore
- 🔑 **Forgot Password** — Reset password via email link
- 🧮 **Material Calculator** — Calculates materials for:
  - Brick Wall (4.5", 9", 13.5" thickness)
  - Concrete Slab (M20 grade)
  - Plastering (12mm thickness)
- 🏗️ **Site Manager** — Add, view, and delete multiple active construction sites
- 👷 **Labor Diary** — Add workers with daily wage rate per site
- 📋 **Work Log** — Log daily attendance (Full Day / Half Day / Multiple Days) and advance payments
- 💰 **Auto Balance Calculation** — Automatically calculates balance due = Earned - Advance Paid
- ☁️ **Cloud Storage** — All data stored in Firebase Firestore, accessible from any device
- 🚪 **Logout** — Secure logout from the app

---

## 🛠️ Tech Stack

| Component | Technology |
|-----------|-----------|
| Language | Kotlin |
| UI | Android XML + Material Components |
| Navigation | ViewPager2 + TabLayout |
| Authentication | Firebase Authentication |
| Database | Firebase Firestore (Cloud) |
| Architecture | Repository Pattern |
| Background Tasks | Kotlin Coroutines |
| UI Binding | ViewBinding |
| Build System | Gradle 8.7 |
| Min SDK | API 24 (Android 7.0+) |
| Target SDK | API 34 (Android 14) |

---

## 🚀 Getting Started

### Prerequisites

- Android Studio (latest version)
- JDK 17 or higher (use Embedded JDK from Android Studio)
- A Firebase account (free)
- Internet connection (for Gradle sync and Firebase)

### Setup Instructions

#### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/NammaMistri.git
cd NammaMistri
```

#### 2. Set Up Firebase

1. Go to [Firebase Console](https://console.firebase.google.com)
2. Create a new project named `NammaMistri`
3. Add an Android app with package name: `com.nammamistri`
4. Download `google-services.json`
5. Place it inside the `app/` folder:
```
NammaMistri/
└── app/
    └── google-services.json   ← place here
```

#### 3. Enable Firebase Services

In your Firebase Console:
- **Authentication** → Sign-in method → Enable **Email/Password**
- **Firestore Database** → Create database → Start in **test mode**

#### 4. Open in Android Studio

1. Open Android Studio
2. Click **File → Open**
3. Select the `NammaMistri` folder (the one containing `app/`)
4. Wait for Gradle sync to complete
5. Click ▶️ **Run** to launch on emulator or device

---

## 📁 Project Structure

```
NammaMistri/
├── app/
│   ├── google-services.json          ← Firebase config (add manually)
│   └── src/main/
│       ├── java/com/nammamistri/
│       │   ├── data/
│       │   │   └── FirebaseRepository.kt    ← All Firestore operations
│       │   ├── ui/
│       │   │   ├── calculator/
│       │   │   │   └── CalculatorFragment.kt
│       │   │   ├── sites/
│       │   │   │   ├── SitesFragment.kt
│       │   │   │   └── SiteAdapter.kt
│       │   │   ├── workers/
│       │   │   │   ├── WorkersFragment.kt
│       │   │   │   ├── WorkerAdapter.kt
│       │   │   │   └── WorkerDetailBottomSheet.kt
│       │   │   └── MainPagerAdapter.kt
│       │   ├── util/
│       │   │   └── MaterialCalculator.kt    ← Civil engineering formulas
│       │   ├── MainActivity.kt
│       │   ├── LoginActivity.kt
│       │   ├── RegisterActivity.kt
│       │   ├── AddSiteActivity.kt
│       │   ├── SiteDetailActivity.kt
│       │   └── AddWorkerActivity.kt
│       ├── res/
│       │   ├── layout/                      ← All XML screen layouts
│       │   ├── values/                      ← Colors, strings, themes
│       │   └── menu/                        ← Toolbar menu
│       └── AndroidManifest.xml
├── build.gradle
├── settings.gradle
└── gradle.properties
```

---

## 🧮 Material Calculation Formulas

### Brick Wall
- **Bricks** = (Length × Height × Thickness) / 0.002 × 1.05 (5% wastage)
- **Cement** = Mortar Volume × 1.5 bags/m³
- **Sand** = Mortar Volume × 0.9 m³ (converted to CFT)
- Mortar Volume = 30% of Wall Volume
- Cement:Sand ratio = 1:6

### Concrete Slab (M20 Grade)
- **Cement** = Volume × 8 bags/m³
- **Sand** = Volume × 14 CFT/m³
- **Aggregate** = Volume × 28 CFT/m³
- **Steel** = Volume × 80 kg/m³
- Mix ratio: 1:1.5:3

### Plastering (12mm thickness)
- **Cement** = Volume × 6.3 bags/m³
- **Sand** = Volume × 4 m³ (converted to CFT)
- Mix ratio: 1:4

---

## 👥 How Multiple Users Work

Each user registers with a unique email. Their data is stored in Firestore under their unique User ID:

```
users/
└── {userId}/
    ├── fullName
    ├── email
    ├── sites/
    │   └── {siteId}/
    ├── workers/
    │   └── {workerId}/
    └── logs/
        └── {logId}/
```

This means User A cannot see User B's sites or workers. Complete data isolation per user.

---

## 🔮 Future Scope

- 🌐 **Kannada Language Support** — Full Kannada UI for local masons
- 📸 **Site Photo Gallery** — Capture and store work progress photos
- 💵 **Material Price List** — User-updatable local material rates with total cost estimation
- 📄 **PDF Wage Report** — Generate and share monthly wage reports
- ☁️ **Offline Support** — Cache Firestore data for use without internet
- 📊 **Analytics Dashboard** — Visual charts for project costs and worker payments

---

## 🤝 Contributing

Pull requests are welcome! For major changes, please open an issue first to discuss what you would like to change.

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

---

## 👨‍💻 Author

**Irfan Ahmed Raza**
- Internship Project — Android App Development using GenAI
- Project ID: 27 — Namma Mistri (Self-Employment Domain)

---

> *"Giving rural builders the same tools that large construction firms have."* 🏗️
