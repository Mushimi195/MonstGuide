# MonstGuide Implementation Walkthrough

## Status: Complete

The basic implementation of the Monster Strike Trajectory Guide (MonstGuide) is complete.

## Features

### 1. Overlay System
- **Permission Management**: The app correctly checks and requests `SYSTEM_ALERT_WINDOW` permission needed to draw over other apps.
- **Foreground Service**: Uses a foreground service (`OverlayService`) to keep the overlay active even when the main app is closed.

### 2. Trajectory View
- **Draggable Ball**: A cyan ball represents the character. You can drag it to match your character's position in the game.
- **Aiming Mechanism**: Drag anywhere else on the screen (slingshot style) to aim.
- **Trajectory Prediction**: Draws a red line showing the predicted path, including reflections off the screen edges (walls).
- **Close Button**: A simple "X" button to close the overlay.

## Files Created

- `MainActivity.kt`: Entry point, handles permissions.
- `OverlayService.kt`: Manages the overlay window.
- `TrajectoryView.kt`: Custom view for drawing the guide.
- `AndroidManifest.xml`: Permissions and service declarations.
- `layout/activity_main.xml`: Main UI with start button.
- `values/strings.xml`: String resources.

## How to Use

1. **Build & Run**: Install the app on your Android device.
2. **Grant Permission**: Tap "Start Guide". If permission is missing, you will be redirected to Settings. Enable "Display over other apps".
3. **Start Overlay**: Tap "Start Guide" again. The overlay will appear.
4. **Position Character**: Drag the cyan ball to your character's position.
5. **Aim**: Drag on the screen (pull back like a slingshot) to see the trajectory line.
6. **Close**: Tap the "X" button to stop the service.

## Next Steps (Future Improvements)
- Add "Pass-through" mode to allow interacting with the game while the overlay is visible.
- Add more complex reflection logic (e.g., block types).
- Improve UI aesthetics.
