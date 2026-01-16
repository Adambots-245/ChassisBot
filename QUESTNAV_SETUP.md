# QuestNav Setup Guide for ChassisBot

This guide explains how to set up and use QuestNav with the ChassisBot for Meta Quest 3 odometry tracking.

## Overview

QuestNav uses the Meta Quest headset's Visual-Inertial Odometry (VIO) system to track the robot's position with high accuracy. The Quest's SLAM cameras and IMU provide up to 120Hz pose updates that are fused with wheel odometry for improved localization.

---

## Hardware Requirements

### Required Components

| Item | Recommendation | Notes |
|------|----------------|-------|
| Meta Quest Headset | Quest 3S (recommended) or Quest 3 | Quest 3S is cheaper with equivalent tracking performance |
| USB-Ethernet Adapter | Belkin INC019btBK | Must be from QuestNav compatible list - not all adapters work |
| Ethernet Cable | Cat5e or better | Connects adapter to robot radio/switch |
| 5V Power Source | USB port or battery bank | For adapter power pass-through (optional but recommended) |
| Mounting Hardware | 3D printed mount + zip ties | See mounting section below |

### Recommended USB-Ethernet Adapters

These adapters are tested and confirmed to work with Quest:
- **Belkin INC019btBK** (100W PD) - ~$25-35
- **Cable Matters 5-Port USB-C Hub** - ~$35-45
- **onn. 8-in-1 USB-C Adapter** (Walmart) - ~$25-30

**WARNING:** Many USB-C Ethernet adapters do NOT work with Quest due to Android driver limitations. Only use adapters from the [QuestNav compatibility list](https://questnav.gg/docs/getting-started/adapters/).

---

## Hardware Setup

### Step 1: Mount the Quest on the Robot

**Location Requirements:**
- Mount at least **12 inches (30cm) above the floor**
- Ensure **clear, unobstructed view** of surroundings (not blocked by robot mechanisms)
- Use a **rigid mounting location** (robot frame/base, not a moving part)
- Keep the headset in **normal upright orientation** (as if wearing it)

**Mounting Tips:**
- Use PLA+, PETG, or CF-Nylon for 3D printed mounts (avoid ABS - it shrinks)
- Secure with at least **4 zip ties** (two on each side)
- Don't apply excessive pressure on the display or buttons
- Check [QuestNav mounting guide](https://questnav.gg/docs/getting-started/mounting/) for 3D print files

### Step 2: Connect the Ethernet Adapter

1. Plug the USB-Ethernet adapter into the Quest's USB-C port
2. Connect an Ethernet cable from the adapter to your robot's network switch/radio
3. (Optional) Connect 5V power to the adapter's pass-through port to keep Quest charged

**Network Configuration:**
- Quest must be on the same network as the robot (10.TE.AM.x subnet)
- Verify the Quest gets an IP address in the correct range

---

## Software Setup (Quest Side)

### Step 1: Enable Developer Mode

1. Create a Meta developer account at [developer.oculus.com](https://developer.oculus.com)
2. Open the Meta Quest app on your phone
3. Go to **Settings > Developer Mode** and enable it
4. Restart the Quest headset

### Step 2: Install QuestNav App

Download the latest QuestNav APK from [GitHub Releases](https://github.com/QuestNav/QuestNav/releases).

**Option A: Using ADB (Command Line)**
```bash
adb install QuestNav_v2026-1.0.0.apk
```

**Option B: Using Meta Quest Developer Hub (MQDH)**
1. Install MQDH on your computer
2. Connect Quest via USB
3. Go to Applications > Install
4. Select the QuestNav APK file

**Option C: Using SideQuest**
1. Connect Quest with SideQuest running
2. Click the install icon (box with arrow)
3. Select the QuestNav APK

### Step 3: Configure QuestNav App

1. Launch QuestNav from **Apps > Unknown Sources** on the Quest
2. Enter your **team number** (default is 9999 - change this!)
3. Enable **Auto Start On Boot** for convenience
4. Verify the Quest connects to the robot network

---

## Code Configuration

### Step 1: Measure and Update Mount Offset

Open `src/main/java/com/adambots/Constants.java` and update the `ROBOT_TO_QUEST` transform:

```java
public static final class QuestNavConstants {
    // Quest mounting offset relative to robot center (meters and radians)
    public static final Transform3d ROBOT_TO_QUEST = new Transform3d(
        new Translation3d(
            0.0,    // X: positive = Quest is forward of robot center
            0.0,    // Y: positive = Quest is left of robot center
            0.3     // Z: height above floor (meters)
        ),
        new Rotation3d(
            0.0,    // Roll (usually 0)
            0.0,    // Pitch (usually 0)
            0.0     // Yaw: rotation from robot forward (radians)
        )
    );
}
```

**How to measure:**
1. Measure from the robot's center point to the Quest's center
2. X = forward/backward distance (forward is positive)
3. Y = left/right distance (left is positive)
4. Z = height from floor to Quest center
5. If the Quest faces a different direction than the robot, set the Yaw angle

### Step 2: Adjust Trust Levels (Optional)

The standard deviation values control how much the pose estimator trusts QuestNav data:

```java
public static final Matrix<N3, N1> QUESTNAV_STD_DEVS = VecBuilder.fill(
    0.02,   // X position - lower = more trust (default: 2cm)
    0.02,   // Y position - lower = more trust (default: 2cm)
    0.035   // Rotation - lower = more trust (default: ~2 degrees)
);
```

- **Lower values** = More trust in QuestNav (use if Quest tracking is very stable)
- **Higher values** = Less trust in QuestNav (use if experiencing drift)

### Step 3: Enable/Disable QuestNav

To quickly disable QuestNav without removing code:

```java
public static final boolean QUESTNAV_ENABLED = false;  // Set to true to enable
```

---

## Testing

### Step 1: Build and Deploy

```bash
./gradlew build
./gradlew deploy
```

### Step 2: Verify Connection

1. Open **SmartDashboard**, **Shuffleboard**, or **AdvantageScope**
2. Look for the following values:
   - `QuestNav/Connected` - Should be `true`
   - `QuestNav/Tracking` - Should be `true` when Quest has good tracking
   - `QuestNav/Battery` - Quest battery percentage
   - `QuestNav/Latency` - Network latency (should be low, <50ms)

### Step 3: Test Pose Tracking

1. Place the robot at a known position
2. Watch `QuestNav/X`, `QuestNav/Y`, `QuestNav/Yaw` values
3. Push the robot around manually - values should update smoothly
4. Compare QuestNav pose with wheel odometry pose in AdvantageScope

### Step 4: Test Pose Reset

1. Place robot at a known field position
2. Press **Joystick Button 7** to reset QuestNav pose
3. Verify the pose aligns with actual robot position

### Step 5: Driving Test

1. Enable teleop and drive the robot
2. Monitor pose estimate stability in AdvantageScope
3. Drive in a square pattern and check for drift
4. Compare accuracy vs wheel-only odometry

---

## Joystick Button Reference

| Button | Function |
|--------|----------|
| Button 2 (Thumb) | Zero gyro |
| Button 3 | Lock wheels (X pattern) |
| Button 4 | Center modules |
| Button 5 | Enable vision |
| Button 6 | Disable vision |
| **Button 7** | **Reset QuestNav pose** |
| POV Hat | Snap to cardinal headings |

---

## Troubleshooting

### Quest Not Connecting

- Verify Quest is on the correct network (10.TE.AM.x)
- Check that team number is set correctly in QuestNav app
- Ensure Ethernet adapter is from the compatible list
- Try power cycling the Quest and adapter

### Tracking Lost Frequently

- Mount Quest higher (at least 12" above floor)
- Ensure Quest has clear view of surroundings
- Check for reflective surfaces that may confuse cameras
- Verify Quest is rigidly mounted (no vibration/wobble)

### Pose Drifting Over Time

- QuestNav uses dead reckoning - periodic resets are normal
- Use Button 7 to reset pose at known positions
- Consider combining with AprilTag vision for automatic correction
- Adjust `QUESTNAV_STD_DEVS` to trust QuestNav less if needed

### Quest Battery Draining Fast

- Use an adapter with power pass-through
- Connect 5V power source to adapter
- Monitor `QuestNav/Battery` on dashboard

### Build Errors with QuestNav Import

If you see "package gg.questnav does not exist":
1. Verify `vendordeps/questnavlib.json` exists
2. Run `./gradlew build --refresh-dependencies`
3. Restart your IDE to refresh dependencies

---

## Resources

- **QuestNav Documentation**: [questnav.gg](https://questnav.gg/docs/getting-started/about/)
- **QuestNav GitHub**: [github.com/QuestNav/QuestNav](https://github.com/QuestNav/QuestNav)
- **Chief Delphi Discussion**: [QuestNav Thread](https://www.chiefdelphi.com/t/questnav-the-best-robot-pose-tracking-system-in-frc/476083)
- **Compatible Adapters**: [questnav.gg/docs/getting-started/adapters](https://questnav.gg/docs/getting-started/adapters/)
- **QuestNav Discord**: For community support

---

## Quick Reference

### Files Modified for QuestNav

| File | Purpose |
|------|---------|
| `vendordeps/questnavlib.json` | QuestNav library dependency |
| `Constants.java` | Mount offset and configuration |
| `QuestNavSubsystem.java` | Reads Quest data, feeds to pose estimator |
| `RobotContainer.java` | Registers subsystem, button bindings |

### Dashboard Values

| Key | Description |
|-----|-------------|
| `QuestNav/Connected` | True if Quest is connected |
| `QuestNav/Tracking` | True if Quest is actively tracking |
| `QuestNav/Enabled` | True if QuestNav integration is enabled |
| `QuestNav/Battery` | Quest battery percentage |
| `QuestNav/Latency` | Network latency in seconds |
| `QuestNav/X`, `Y`, `Z` | Quest position (meters) |
| `QuestNav/Yaw` | Quest heading (degrees) |
