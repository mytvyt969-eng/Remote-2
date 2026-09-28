# Masjid Prayer Time Editor – Android TV

This project implements the requested time-editing flow:

- Main prayer settings screen with Fajar, Dhuhr, Asr, Maghrib and Isha.
- Press **EDIT** on any prayer to open a large time-picker dialog inspired by the supplied design.
- Hour and minute are selectable with NumberPicker wheels.
- **Fajar is locked to AM.**
- **Dhuhr, Asr, Maghrib and Isha are locked to PM.**
- OK saves the value; Cancel discards it.
- Designed for landscape Android TV and also usable on touch devices.

## Build
Open the folder in Android Studio and let Gradle sync. Build with **Build > Build APK(s)**.

The repository also contains a GitHub Actions workflow that builds the debug APK automatically when pushed to GitHub.
