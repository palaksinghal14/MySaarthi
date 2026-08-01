MySaarthi 🪔
📌 Overview

MySaarthi is an Android application built for people walking the Bhagwat Marg — a spiritual path that can often feel isolating without the right community around you. Instead of juggling separate apps for daily scripture, practice tracking, and finding like-minded people, MySaarthi brings everything into one coherent experience.

Built as a portfolio project to demonstrate production-level Android development using modern Jetpack libraries, Clean Architecture, offline-first data handling, and real-time geo-based community features via GeoFirestore.

🚀 Features

Daily Scripture & Practice

Daily shloka served from all 700 Bhagavad Gita verses — fully offline after first launch
Sadhana tracker and evening check-in for consistent daily practice
Home screen widget via Jetpack Glance for at-a-glance spiritual reminders
WorkManager-powered daily reminders that work even when the app is closed

Geo-Based Spiritual Community

Discover nearby seekers walking the same spiritual path using GeoFirestore radius queries
Send and receive satsang connection requests with real-time FCM notifications
Explore nearby temples surfaced via Google Places API
Spiritual profiles visible to the community — connect with intent, not just proximity

Firebase Authentication

Google Sign-In and email/password both supported
Typed error handling across all auth failure states
🛠 Tech Stack
Category	Technology
Language	Kotlin
UI	Jetpack Compose
Architecture	Clean Architecture + MVVM
DI	Hilt
Database	Room
Networking	Retrofit
Backend / Auth	Firebase Authentication, Cloud Firestore, FCM
Location	GeoFirestore, Google Places API, Google Maps
Other	WorkManager, Jetpack Glance, DataStore, Coil
🏗 Architecture

MySaarthi follows Clean Architecture with MVVM and Hilt dependency injection, structured as a single module.

UI (Compose screens)
    ↓
ViewModel (StateFlow / UiState)
    ↓
Use Cases (domain layer)
    ↓
Repository (Room, Firebase, Retrofit, GeoFirestore)
    ↓
External Services (Firebase, Google Places, Bhagavad Gita API)
Data layer — Remote (Retrofit, Firebase, GeoFirestore) and local (Room) sources behind repository interfaces
Domain layer — Use cases and domain models fully decoupled from Android framework
Presentation layer — Compose screens consuming UiState from ViewModels; no business logic in UI
State management — Sealed UiState classes per feature; repositories return typed results
Error handling — AppException sealed hierarchy, toAppException() mapper, toUserMessage() for display
📁 Project Structure
app/src/main/java/com/palaksinghal/mysaarthi/
├── data/
│   ├── local/          # Room database, DAOs, entities
│   ├── remote/         # Retrofit API, Firebase, GeoFirestore
│   └── repository/     # Repository implementations
├── domain/
│   ├── model/          # Domain models, AppException
│   ├── repository/     # Repository interfaces
│   └── usecase/        # Use cases
├── presentation/
│   ├── onboarding/     # Welcome, auth, questionnaire screens
│   ├── home/           # Today tab — shloka, sadhana, check-in
│   ├── nearby/         # Seeker discovery, temple finder
│   ├── profile/        # Spiritual profile, settings
│   └── components/     # Reusable composables
├── core/
│   ├── utils/          # UiState, AppException, mappers
│   ├── constants/      # App-wide constants
│   └── navigation/     # Nav graphs
├── di/                 # Hilt modules
├── worker/             # WorkManager workers
├── MainActivity.kt
└── MySaarthi.kt        # @HiltAndroidApp Application class
⚙ Setup Instructions

Prerequisites

Android Studio Otter (2025.2.3) or later
A Firebase project with Authentication and Firestore enabled
Google Cloud project with Places API and Maps SDK enabled
Android device or emulator running API 26+

1. Clone the repository

bash
git clone <your-repo-url>
cd MySaarthi

2. Firebase configuration

Add an Android app in Firebase Console with package name com.palaksinghal.mysaarthi
Download google-services.json and place it in app/
Enable Email/Password and Google Sign-In under Authentication
Create a Firestore database

3. API Keys
Add to gradle.properties (gitignored):

MAPS_API_KEY=your_google_maps_api_key_here

4. Run
Open in Android Studio, let Gradle sync, connect a device or emulator and run.

🔑 Key Architecture Decisions

Offline-first content layer — Bhagavad Gita verse data is fetched once from the public API via Retrofit on first launch and persisted entirely in Room. All subsequent content access reads from the local database with zero network dependency — so daily practice works even without internet.

Hybrid offline-online design — The content layer is offline-first; the community layer (seeker discovery, satsang requests, temple finder) is intentionally real-time via Firestore and FCM. Each layer uses the approach that fits its purpose.

GeoFirestore for radius queries — Storing raw lat/long in Firestore and filtering client-side would mean pulling all user documents and discarding most. GeoFirestore handles geohash-based radius queries natively, keeping community discovery efficient regardless of user count.

Typed error handling — All errors flow through a sealed AppException hierarchy. Raw exceptions are mapped once via toAppException(); ViewModels receive typed exceptions; UI renders specific messages via toUserMessage().

🔐 Permissions Used
Permission	Purpose
INTERNET	Firebase, Retrofit, Google Places API
ACCESS_FINE_LOCATION	GeoFirestore seeker discovery and nearby temple search
ACCESS_COARSE_LOCATION	Declared alongside fine location


👤 Author

Palak Singhal
📧 palaksinghal148@gmail.com
🔗 LinkedIn
