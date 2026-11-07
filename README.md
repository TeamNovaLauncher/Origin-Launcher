# Origin Launcher

An Android home-screen launcher inspired by the visual language of OriginOS.

## Current starter features
- Android HOME launcher entry point
- App list populated from installed launchable apps
- Search installed apps by name
- Tap an app to launch it
- Separate launcher settings screen

## Build
Requires JDK 17 and Gradle 8.13, with Android SDK platform 36 and build tools installed.

```bash
gradle --no-daemon :app:assembleDebug
```

The debug APK is created at `app/build/outputs/apk/debug/app-debug.apk`.

## Notes
This is a third-party launcher, not Vivo firmware. It cannot replace protected system settings or privileged OriginOS features.
