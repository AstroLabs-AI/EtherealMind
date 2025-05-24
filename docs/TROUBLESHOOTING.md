# 🔧 Troubleshooting Guide

This guide helps resolve common issues with EtherealMind. If your issue isn't covered here, please [report it on GitHub](https://github.com/AstroLabs-AI/EtherealMind/issues).

## Table of Contents

1. [Installation Issues](#installation-issues)
2. [COSMO Problems](#cosmo-problems)
3. [Storage Issues](#storage-issues)
4. [Performance Problems](#performance-problems)
5. [Crash Solutions](#crash-solutions)
6. [Compatibility Issues](#compatibility-issues)
7. [Known Bugs](#known-bugs)
8. [Getting Help](#getting-help)

---

## 🔴 Installation Issues

### Mod Not Loading

**Symptoms:**
- EtherealMind doesn't appear in mod list
- COSMO spawn egg not available
- Commands don't work

**Solutions:**
1. **Check Minecraft Version**
   - Must be exactly 1.20.1
   - Use Forge 47.2.0 or higher

2. **Verify File Location**
   - Ensure JAR is in `.minecraft/mods` folder
   - File should be named `etherealmind-1.x.x.jar`

3. **Check Dependencies**
   - GeckoLib 4.2.4 must be installed
   - Both mods must be in mods folder

4. **Verify Forge Installation**
   ```
   - Open Minecraft Launcher
   - Check if Forge 1.20.1 profile exists
   - If not, reinstall Forge
   ```

### GeckoLib Errors

**Error:** `Missing or unsupported mandatory dependencies`

**Solution:**
- Download GeckoLib from [CurseForge](https://www.curseforge.com/minecraft/mc-mods/geckolib)
- Ensure version is 4.2.4 for Minecraft 1.20.1
- Place in same mods folder as EtherealMind

---

## 👻 COSMO Problems

### COSMO Won't Spawn

**Issue:** Right-clicking spawn egg does nothing

**Solutions:**
1. **Check Player Limit**
   - Each player can only have ONE COSMO
   - If you already have one, new ones won't spawn

2. **Space Requirements**
   - Need 3x3x3 clear space
   - Cannot spawn in water or lava
   - Must be in valid dimension

3. **Permission Issues**
   - In multiplayer, check spawn permissions
   - Some servers restrict entity spawning

### COSMO Is Invisible

**Issue:** COSMO spawned but can't see it

**Solutions:**
1. **Update Mod Version**
   - Versions before 1.2.1 had transparency issues
   - Download latest version

2. **Graphics Settings**
   - Enable particles in video settings
   - Set entity render distance higher
   - Turn on entity shadows

3. **Resource Pack Conflicts**
   - Disable resource packs temporarily
   - Some packs override entity textures

### COSMO Not Following

**Issue:** COSMO stays in place, doesn't follow player

**Solutions:**
1. **Check Bonding**
   - Right-click COSMO to ensure bonded
   - Only bonded player is followed

2. **Distance Check**
   - If >50 blocks away, COSMO should teleport
   - Check if teleportation is working

3. **Chunk Loading**
   - COSMO might be in unloaded chunk
   - Return to spawn location

---

## 📦 Storage Issues

### Storage GUI Won't Open

**Issue:** Right-clicking COSMO doesn't open storage

**Solutions:**
1. **Interaction Check**
   - Must right-click directly on COSMO
   - Not while sneaking (that sets home)
   - Check if you're bonded player

2. **GUI Conflicts**
   - Disable other GUI mods temporarily
   - Check for keybind conflicts

3. **Server Sync**
   - In multiplayer, may have sync issues
   - Try relogging

### Items Disappearing

**Issue:** Items vanish when placed in storage

**Solutions:**
1. **Check Storage Limit**
   - Use `/cosmo stats` to see capacity
   - Higher levels = more storage

2. **Save Issues**
   - Items save when GUI closes
   - Don't force-quit game with GUI open

3. **Auto-Deposit**
   - Check if auto-deposit is enabled
   - Items might be auto-stored

### Container Click Crash

**Error:** `NullPointerException: Container click`

**Solution:**
- Update to version 1.2.1 or later
- This was fixed in recent versions
- If persists, report with crash log

---

## ⚡ Performance Problems

### FPS Drops Near COSMO

**Solutions:**
1. **Reduce Particles**
   - Lower particle settings in video options
   - Disable particles if severe

2. **Entity Limit**
   - Too many entities in area
   - Move COSMO to less crowded area

3. **Storage Size**
   - Very full storage can impact performance
   - Organize and remove unnecessary items

### Lag When Opening Storage

**Solutions:**
1. **Reduce Page Count**
   - Only use pages you need
   - Higher levels = more lag potential

2. **Clear Cache**
   - Close and reopen Minecraft
   - Delete `.minecraft/cosmo_cache` if exists

---

## 💥 Crash Solutions

### Startup Crashes

**Common Causes:**
1. **Wrong Minecraft Version**
   - Must be 1.20.1 exactly
   - Check launcher settings

2. **Mod Conflicts**
   - Remove other mods temporarily
   - Add back one by one

3. **Corrupted Download**
   - Redownload from official source
   - Verify file size matches

### Runtime Crashes

**If COSMO causes crashes during gameplay:**

1. **Collect Information**
   ```
   - Crash report from .minecraft/crash-reports
   - Latest.log from .minecraft/logs
   - Mod versions
   ```

2. **Common Fixes**
   - Update all mods to latest versions
   - Allocate more RAM (4GB recommended)
   - Update Java to version 17

3. **Report Issue**
   - Create GitHub issue with crash report
   - Include steps to reproduce

---

## 🔄 Compatibility Issues

### Known Incompatibilities

**Partially Compatible:**
- **Optifine**: May cause rendering issues
- **Sodium**: Use Rubidium instead for Forge
- **Better Animals Plus**: Entity ID conflicts

**Solutions:**
1. **Use Alternatives**
   - Rubidium instead of Optifine/Sodium
   - Entity Culling for performance

2. **Load Order**
   - Try loading EtherealMind last
   - Some mods need specific order

### Multiplayer Issues

**Common Problems:**
1. **Desync Issues**
   - Storage not updating
   - COSMO position incorrect

2. **Permission Problems**
   - Server may restrict commands
   - Check with server admin

**Solutions:**
- Ensure server has same mod version
- Check server entity limits
- Verify permissions for /cosmo commands

---

## 🐛 Known Bugs

### Current Issues (v1.2.1)

1. **Minor Texture Flickering**
   - Occurs rarely with shaders
   - Workaround: Disable shaders near COSMO

2. **Page Navigation Delay**
   - Slight delay when changing pages
   - More noticeable with many items

3. **Sound Missing**
   - Some abilities lack sound effects
   - Will be added in future update

### Fixed in Latest Version

- ✅ COSMO invisible/transparent
- ✅ Container click crashes
- ✅ Storage not opening
- ✅ Texture animation errors

---

## 🆘 Getting Help

### Before Reporting

1. **Check Version**
   - Ensure using latest version
   - Update if needed

2. **Read Logs**
   - Check .minecraft/logs/latest.log
   - Look for [EtherealMind] entries

3. **Test Vanilla**
   - Try with only EtherealMind + GeckoLib
   - Identifies mod conflicts

### Reporting Issues

**Required Information:**
```
Minecraft Version: 1.20.1
Forge Version: XX.X.X
EtherealMind Version: X.X.X
GeckoLib Version: 4.2.4

Issue Description:
[Describe what happens]

Steps to Reproduce:
1. [First step]
2. [Second step]
3. [What happens]

Expected Behavior:
[What should happen]

Crash Log/Screenshots:
[Attach if applicable]
```

### Contact Methods

1. **GitHub Issues** (Preferred)
   - [Issue Tracker](https://github.com/AstroLabs-AI/EtherealMind/issues)
   - Use issue templates
   - Search existing issues first

2. **Discord** (Coming Soon)
   - Real-time help
   - Community support

3. **CurseForge Comments**
   - For general questions
   - Not for bug reports

---

## 🔍 Debug Commands

**Enable Debug Mode** (Planned)
```
/cosmo debug enable
```

Shows:
- Entity UUID
- Storage statistics  
- Performance metrics
- Ability cooldowns

**Export Debug Info**
```
/cosmo debug export
```

Creates file with:
- All COSMO data
- Configuration
- System information

---

*Remember: Most issues are resolved by updating to the latest version!*

---

[Back to Guide](GUIDE.md) | [Back to README](../README.md)