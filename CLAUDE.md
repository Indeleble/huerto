# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

HuertoPlan is an Android application for garden/farm plot management built with:
- **Kotlin** and **Jetpack Compose** for UI
- **Hilt** for dependency injection
- **Navigation Compose** for navigation
- **MVVM architecture** with ViewModels and Repository pattern
- **In-memory data storage** (no persistent database yet)

## Build & Development Commands

### Build the project
```bash
./gradlew build
```

### Run tests
```bash
# Unit tests
./gradlew test

# Android instrumentation tests
./gradlew connectedAndroidTest
```

### Clean build
```bash
./gradlew clean
```

### Check for dependency updates
```bash
./gradlew dependencyUpdates
```

## Architecture Overview

### Core Structure
- **HuertoApp.kt**: Application class with Hilt setup
- **MainActivity.kt**: Single activity hosting Compose navigation
- **AppNavigation.kt**: Navigation setup with routes defined in AppScreens.kt

### Data Layer
- **MainRepository**: Singleton repository managing in-memory collections of Users, Terrains, Sectors, and Bancales
- **Model classes**: Simple data classes (User, Terrain, Sector, Bancal) representing the domain
- **UserViewModel**: ViewModel layer connecting UI to repository

### Domain Model Hierarchy
```
User
├── Terrain (garden plots)
    ├── Sector (sections within terrain)
        ├── Bancal (individual planting beds with position and dimensions)
```

### UI Layer
- **Screens**: BancalesScreen (main management), LoginScreen/SingUpScreen
- **Components**: Reusable UI components in ui/components/
- **Popups**: Dialog components for creating terrains and sectors
- **Theme**: Material3 theming in ui/theme/

## Key Implementation Details

### Dependency Injection
- Uses Hilt with @HiltAndroidApp, @AndroidEntryPoint, and @Singleton annotations
- Repository is injected as a singleton through constructor injection

### Data Management
- All data is currently stored in-memory using mutable lists
- Mock data is automatically created for new users
- Bancales have position (x, y) and dimension (width, height) properties for visual layout

### Navigation
- Uses Navigation Compose with slide animations
- Currently has two main screens: SingUpScreen and BancalesScreen
- ViewModels are scoped using hiltViewModel()

### Testing Setup
- Unit testing with JUnit 4, MockK, and Coroutines Test
- Android instrumentation testing with Compose UI testing
- Test structure mirrors main source structure

## Development Notes

- The app uses compileSdk 35 and minSdk 31 (Android 12+)
- Compose BOM version 2023.08.00 manages Compose library versions
- JitPack repository is configured for external dependencies (compose-free-scroll)
- All navigation routes are centralized in AppScreens sealed class