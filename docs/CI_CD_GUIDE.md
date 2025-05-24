# CI/CD Guide for EtherealMind

## Overview

This project uses GitHub Actions for continuous integration and deployment. Every push triggers builds, and releases are automatically created when tags are pushed.

## Workflows

### 1. Build Workflow (`build.yml`)
- **Triggers**: Every push to main, every PR
- **Actions**: 
  - Sets up Java 17
  - Builds the mod
  - Uploads artifacts
- **Purpose**: Ensures code always compiles

### 2. Release Workflow (`release.yml`)
- **Triggers**: When tags matching `v*.*.*` are pushed
- **Actions**:
  - Builds the mod
  - Generates changelog
  - Creates GitHub release
  - Uploads JAR file
- **Purpose**: Automates release creation

### 3. Advanced Release (`release-advanced.yml`)
- **Features**:
  - Better changelog generation
  - SHA256 hash calculation
  - Detailed release notes
  - Version validation

### 4. CI/CD Pipeline (`ci-cd.yml`)
- **Complete pipeline**:
  - Runs tests
  - Builds mod
  - Auto-versions based on commits
  - Creates releases automatically

## How to Create a Release

### Option 1: Manual Tag (Recommended)
```bash
# Update version in build.gradle and mods.toml
# Commit changes
git add .
git commit -m "chore: bump version to X.Y.Z"

# Create and push tag
git tag -a vX.Y.Z -m "Release vX.Y.Z"
git push origin main
git push origin vX.Y.Z
```

### Option 2: Use Release Script
```bash
# For patch release (X.Y.Z -> X.Y.Z+1)
./scripts/release.sh patch

# For minor release (X.Y.Z -> X.Y+1.0)
./scripts/release.sh minor

# For major release (X.Y.Z -> X+1.0.0)
./scripts/release.sh major
```

### Option 3: Automatic Versioning
The CI/CD pipeline automatically creates releases based on commit messages:
- Commits with `feat:` trigger minor version bump
- Commits with `fix:` trigger patch version bump
- Commits with `BREAKING CHANGE` trigger major version bump

## Commit Message Format

Follow conventional commits for automatic versioning:

```
<type>: <description>

[optional body]

[optional footer(s)]
```

Types:
- `feat`: New feature (triggers minor bump)
- `fix`: Bug fix (triggers patch bump)
- `docs`: Documentation only
- `style`: Code style changes
- `refactor`: Code refactoring
- `test`: Adding tests
- `chore`: Maintenance

Examples:
```
feat: add natural language commands for COSMO
fix: resolve texture loading issue
docs: update installation guide
```

## Required Secrets

Add these to your repository settings (Settings → Secrets → Actions):

1. **GITHUB_TOKEN** (automatic) - For creating releases
2. **CURSEFORGE_TOKEN** (optional) - For CurseForge uploads
3. **MODRINTH_TOKEN** (optional) - For Modrinth uploads

## Workflow Permissions

Ensure Actions have write permissions:
1. Go to Settings → Actions → General
2. Under "Workflow permissions", select "Read and write permissions"

## Build Artifacts

All workflows upload build artifacts that can be downloaded:
1. Go to Actions tab
2. Click on a workflow run
3. Download artifacts from the bottom of the page

## Troubleshooting

### Build Fails
- Check Java version (must be 17)
- Verify Gradle wrapper is present
- Check for syntax errors in build.gradle

### Release Not Created
- Ensure tag follows `vX.Y.Z` format
- Check workflow permissions
- Verify version numbers match in all files

### Changelog Issues
- Use conventional commit format
- Ensure git history is fetched (`fetch-depth: 0`)

## Best Practices

1. **Always test locally** before pushing
2. **Use semantic versioning** (MAJOR.MINOR.PATCH)
3. **Write clear commit messages**
4. **Tag releases properly** with `v` prefix
5. **Update documentation** with major changes

## Manual Release Checklist

- [ ] Update version in `build.gradle`
- [ ] Update version in `mods.toml`
- [ ] Update changelog/release notes
- [ ] Test the build locally
- [ ] Commit changes
- [ ] Create and push tag
- [ ] Verify GitHub Action completes
- [ ] Check release page for JAR