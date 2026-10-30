# MG-BlackHole

A standalone Minecraft Forge 1.20.1 client-side visual black hole effect.

## Features
- Press **B** to toggle the visual black hole in front of your camera.
- Dark center with a rotating particle accretion ring and inward particle flow.
- Client-side cosmetic effect only: it does not pull entities, damage players, or modify blocks.

## Requirements
- Minecraft 1.20.1
- Minecraft Forge 47.2.0
- Java 17

## Build
Use Gradle 8.8 with Java 17:

```bash
gradle build
```

The mod JAR will be in `build/libs/`.


Initial project structure committed. GitHub Actions builds the mod on pushes to `main`.
