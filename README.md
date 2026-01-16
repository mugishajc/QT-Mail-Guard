# QTMail Guard

An Android application for viewing and verifying email integrity using Protocol Buffers and SHA-256 hash verification.

## Author

**Mugisha Jean Claude**
Senior Software Engineer, Mobile - Android Java/Kotlin
Kigali, Rwanda

## Overview

QTMail Guard loads email data from `.pb` (Protocol Buffer) files, verifies the integrity of the email body and attached images using SHA-256 hashing, and stores verified emails in an encrypted local database.

The app was built as a technical assessment for QT Global Software Ltd, demonstrating proficiency in modern Android architecture, security practices, and efficient data handling.

## Screenshots

*Coming soon - APK build pending*

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose with Material 3
- **Architecture**: MVVM + Clean Architecture
- **DI**: Hilt
- **Database**: Room with SQLCipher encryption
- **Data Format**: Protocol Buffers (protobuf-lite)
- **Async**: Kotlin Coroutines & Flow
- **Image Loading**: Coil

## Project Structure

```
app/src/main/java/rw/delasoft/qtmailguard/
├── core/
│   ├── constants/     # App-wide constants
│   ├── security/      # Hash generation, DB key management
│   └── util/          # Extensions, helpers
├── data/
│   ├── local/         # Room database, DAOs
│   ├── mapper/        # Entity <-> Domain mappers
│   └── repository/    # Repository implementations
├── di/                # Hilt modules
├── domain/
│   ├── model/         # Business entities
│   ├── repository/    # Repository contracts
│   └── usecase/       # Business logic
├── presentation/
│   ├── component/     # Reusable Compose components
│   ├── screen/        # Screen composables
│   ├── state/         # UI state classes
│   └── viewmodel/     # ViewModels
└── ui/theme/          # Compose theming
```

## How It Works

1. User selects a `.pb` file using the system file picker
2. App parses the Protocol Buffer to extract email fields
3. SHA-256 hashes are computed for the body and attached image
4. Computed hashes are compared against stored hashes in the file
5. If hashes match → "Verified" badge; otherwise → "Verification Failed"
6. Email is saved to encrypted database for later viewing

## Building the Project

### Prerequisites

- Android Studio Hedgehog or newer
- JDK 17
- Android SDK 35

### Steps

```bash
# Clone the repository
git clone https://github.com/mugishajc/QT-Mail-Guard.git

# Open in Android Studio and sync Gradle

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease
```

The release APK will be at `app/build/outputs/apk/release/`

## Generating Sample .pb Files

The app includes a `SampleEmailGenerator` utility. To generate a sample file:

1. Run the app in debug mode
2. The generator can be invoked programmatically to create test `.pb` files
3. Alternatively, use the protobuf CLI with the provided schema

## Proto Schema

```protobuf
message SecureEmail {
    string sender_name = 1;
    string sender_email_address = 2;
    string subject = 3;
    string body = 4;
    bytes attached_image = 5;
    string body_hash = 6;     // SHA-256 of body
    string image_hash = 7;    // SHA-256 of image
}
```

## Technical Decisions & Trade-offs

### Why SQLCipher over EncryptedSharedPreferences?

I went with SQLCipher because the app stores complete email data including binary image attachments. EncryptedSharedPreferences would work for small key-value pairs, but it's not designed for BLOBs. SQLCipher gives us proper relational storage with encryption at rest.

**Trade-off**: Adds ~2MB to APK size. For this use case, the security benefits outweigh the size increase.

### Why Clean Architecture for a "simple" app?

The challenge specifically asked for MVVM with proper separation. I pushed it further with clean architecture layers (data/domain/presentation) because:

- Makes the codebase easier to test
- Use cases encapsulate business logic independently
- Easy to swap implementations (e.g., different database, remote API)

**Trade-off**: More boilerplate code. For a production app with growing features, this pays off quickly.

### Protobuf-lite vs Full Protobuf

Chose `protobuf-lite` to keep the APK smaller. We don't need reflection or dynamic message handling for this use case.

### Hash Comparison Case-Insensitivity

The verification logic compares hashes case-insensitively. Some tools output uppercase hex, others lowercase. Rather than fail verification due to casing, I normalized the comparison.

### Image Loading in Compose

Used Coil for image loading, but for the attached image (which comes from bytes, not a URL), I decode it manually with BitmapFactory. This avoids unnecessary overhead and gives control over memory management via sampling.

### Room Database Versioning

Database version is 1 with destructive migration enabled. For a prototype this is fine. In production, I'd write proper migration paths.

### No CI/CD Pipeline

Given the 72-hour constraint, I didn't set up a CI/CD pipeline (GitHub Actions, Bitrise, etc.). In a production scenario, I'd configure:

- Automated builds on push/PR
- Lint and test runs on every commit
- Automatic APK deployment to internal testing tracks
- Code coverage reporting

### No Crash Reporting

For a production app, I'd integrate Firebase Crashlytics or Sentry for real-time crash monitoring. Helps identify issues users encounter in the wild before they report them. Left out here due to time constraints.

### No Analytics or Session Recording

Tools like PostHog or Amplitude would be useful for understanding user behavior, but they're overkill for a prototype. In production, I'd track:

- Feature usage patterns
- Session recordings for debugging UX issues
- Funnel analysis for key user flows

### No App Performance Monitoring

Skipped Firebase Performance Monitoring or similar tools. In production, these help identify slow screens, network bottlenecks, and memory issues affecting real users.

## Testing

```bash
# Run unit tests
./gradlew testDebugUnitTest
```

Tests cover:
- SHA-256 hash generation
- Hash verification logic
- Entity/Domain mapping
- Extension functions

## What I'd Add Given More Time

- **CI/CD Pipeline**: GitHub Actions for automated builds and deployments
- **Crash Reporting**: Firebase Crashlytics integration
- **Analytics**: PostHog or similar for session recording
- **UI Tests**: Compose testing with Espresso
- **Better Error Messages**: More granular error types shown to users
- **Export Feature**: Save verified emails as PDF
- **Search**: Filter previously loaded emails
- **Biometric Lock**: Require fingerprint to view emails

---

## Notice

> **QT Global Software Ltd - Technical Assessment**
>
> This project was developed as part of a technical assessment, demonstrating
> proficiency in modern Android development practices including clean architecture,
> security implementation, and efficient data handling.
>
> *Not intended for public distribution or commercial use.*

---

<div align="center">

### Crafted with passion in Kigali, Rwanda

**Jean Claude Mugisha**

Senior Software Engineer, Mobile - Android Java/Kotlin

[![GitHub](https://img.shields.io/badge/GitHub-mugishajc-181717?style=flat&logo=github)](https://github.com/mugishajc)

*January 2026*

</div>
