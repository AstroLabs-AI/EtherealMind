#!/bin/bash

# Release script for EtherealMind
# Usage: ./scripts/release.sh [major|minor|patch]

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Default to patch release
RELEASE_TYPE=${1:-patch}

echo -e "${GREEN}Starting $RELEASE_TYPE release process...${NC}"

# Check for uncommitted changes
if ! git diff-index --quiet HEAD --; then
    echo -e "${RED}Error: You have uncommitted changes. Please commit or stash them first.${NC}"
    exit 1
fi

# Get current version
CURRENT_VERSION=$(grep "version = " build.gradle | sed "s/version = '\(.*\)'/\1/")
echo -e "Current version: ${YELLOW}$CURRENT_VERSION${NC}"

# Calculate new version
IFS='.' read -ra VERSION_PARTS <<< "$CURRENT_VERSION"
MAJOR=${VERSION_PARTS[0]}
MINOR=${VERSION_PARTS[1]}
PATCH=${VERSION_PARTS[2]}

case "$RELEASE_TYPE" in
    major)
        MAJOR=$((MAJOR + 1))
        MINOR=0
        PATCH=0
        ;;
    minor)
        MINOR=$((MINOR + 1))
        PATCH=0
        ;;
    patch)
        PATCH=$((PATCH + 1))
        ;;
    *)
        echo -e "${RED}Invalid release type. Use: major, minor, or patch${NC}"
        exit 1
        ;;
esac

NEW_VERSION="${MAJOR}.${MINOR}.${PATCH}"
echo -e "New version: ${GREEN}$NEW_VERSION${NC}"

# Update version in files
echo "Updating version in build.gradle..."
sed -i "s/version = '.*'/version = '$NEW_VERSION'/" build.gradle

echo "Updating version in mods.toml..."
sed -i "s/version=\".*\"/version=\"$NEW_VERSION\"/" src/main/resources/META-INF/mods.toml

# Build the mod
echo -e "${YELLOW}Building mod...${NC}"
./gradlew clean build

# Generate changelog
echo -e "${YELLOW}Generating changelog...${NC}"
LAST_TAG=$(git describe --tags --abbrev=0 2>/dev/null || echo "")
if [ -n "$LAST_TAG" ]; then
    echo "## Changes since $LAST_TAG" > RELEASE_NOTES.md
    git log $LAST_TAG..HEAD --pretty=format:"- %s" >> RELEASE_NOTES.md
else
    echo "## Initial Release" > RELEASE_NOTES.md
fi

# Commit version bump
echo -e "${YELLOW}Committing version bump...${NC}"
git add build.gradle src/main/resources/META-INF/mods.toml
git commit -m "chore: bump version to $NEW_VERSION"

# Create tag
echo -e "${YELLOW}Creating tag v$NEW_VERSION...${NC}"
git tag -a "v$NEW_VERSION" -m "Release v$NEW_VERSION"

# Push changes
echo -e "${YELLOW}Pushing to remote...${NC}"
git push origin main
git push origin "v$NEW_VERSION"

echo -e "${GREEN}✅ Release v$NEW_VERSION completed!${NC}"
echo -e "${GREEN}The GitHub Action will now build and create the release automatically.${NC}"