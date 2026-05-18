# Android App — Traffic Fine Payment System

Native Android application (Kotlin) that allows drivers to pay traffic fines on the spot when stopped by a traffic police officer.

## Tech Stack

- Kotlin
- Android SDK (min API 26 / Android 8)
- Retrofit (HTTP client)
- ViewModel + LiveData / StateFlow
- Material Design 3

## Features

- Enter fine reference number and category code
- Display fine details before payment
- Mock card payment form
- Payment confirmation / failure screen
- SMS notification triggered automatically after payment

## Setup

1. Open `android-app/` in Android Studio.
2. Create a `local.properties` file (gitignored) and set:
   ```
   BASE_URL=http://10.0.2.2:8080/api/
   ```
   (`10.0.2.2` is the Android emulator alias for `localhost`.)
3. Run on emulator or physical device.
