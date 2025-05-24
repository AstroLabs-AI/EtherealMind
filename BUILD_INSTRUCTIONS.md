# Build Instructions for EtherealMind

## Requirements
- Java 17 (not Java 21)
- Gradle 8.1.1
- Minecraft Forge 1.20.1-47.2.0

## Building the JAR

### Option 1: Using Gradle Wrapper (Recommended)
```bash
# Ensure Java 17 is active
java -version  # Should show version 17

# Clean previous builds
./gradlew clean

# Build the mod
./gradlew build
```

### Option 2: Using System Gradle
```bash
# Clean and build
gradle clean build
```

### Build Output
The JAR file will be generated at:
```
build/libs/etherealmind-1.20.1-3.0.0.jar
```

## Common Issues

### Java Version Error
If you see "Unsupported class file major version 65":
- You're using Java 21, switch to Java 17
- Use SDKMAN or similar to manage Java versions

### Gradle Wrapper Missing
If gradlew fails:
1. Check gradle/wrapper/gradle-wrapper.jar exists
2. Make gradlew executable: `chmod +x gradlew`

## Testing the Build
1. Copy the JAR to your Minecraft mods folder
2. Ensure GeckoLib 4.2.4 is also installed
3. Launch Minecraft with Forge 1.20.1-47.2.0+
4. Check that EtherealMind appears in the mod list

## Release Build
For release builds:
1. Update version in build.gradle
2. Update version in mods.toml
3. Build with: `./gradlew clean build`
4. Test thoroughly before release