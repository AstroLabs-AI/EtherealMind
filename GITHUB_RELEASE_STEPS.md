# GitHub Release Steps for EtherealMind v3.0.0

## Prerequisites
- Build the JAR file locally with proper Java version (Java 17)
- Ensure version numbers are updated in:
  - `build.gradle` (version = '3.0.0')
  - `src/main/resources/META-INF/mods.toml` (version="3.0.0")

## Build Steps (Local Machine)
```bash
# Ensure Java 17 is being used
java -version

# Clean and build
./gradlew clean build

# JAR will be in build/libs/etherealmind-1.20.1-3.0.0.jar
```

## Git Commands
```bash
# Add all changes
git add -A

# Commit with descriptive message
git commit -m "Release v3.0.0 - Natural Language Update

- Added natural language chat commands
- Implemented speech bubbles and emotes
- Added smart item categorization
- Added environmental awareness
- Added combat assistance (Level 5+)
- Added resource gathering features
- Fixed texture rendering issues
- Improved performance and stability"

# Tag the release
git tag -a v3.0.0 -m "Version 3.0.0 - Natural Language Update"

# Push changes and tags
git push origin main
git push origin v3.0.0
```

## GitHub Release Creation

### Using GitHub CLI
```bash
# Create release with the JAR file
gh release create v3.0.0 \
  --title "EtherealMind v3.0.0 - Natural Language Update" \
  --notes-file RELEASE_NOTES_v3.0.0.md \
  --draft \
  build/libs/etherealmind-1.20.1-3.0.0.jar
```

### Using GitHub Web Interface
1. Go to https://github.com/yourusername/EtherealMind/releases
2. Click "Draft a new release"
3. Choose tag: `v3.0.0`
4. Release title: `EtherealMind v3.0.0 - Natural Language Update`
5. Copy contents from `RELEASE_NOTES_v3.0.0.md`
6. Attach the JAR file from `build/libs/`
7. Check "Set as the latest release"
8. Click "Publish release"

## Post-Release
1. Update README.md with new version number
2. Update documentation wiki if applicable
3. Post announcement in Discord/Forums
4. Monitor issues for any problems

## Version Numbering
- Current: 3.0.0
- Format: MAJOR.MINOR.PATCH
- This is a MAJOR release due to significant new features