# 📖 EtherealMind Complete Guide

Welcome to the comprehensive guide for EtherealMind! This guide will walk you through everything you need to know about your ethereal companion COSMO.

## Table of Contents

1. [Getting Started](#getting-started)
2. [First Steps with COSMO](#first-steps-with-cosmo)
3. [Dimensional Storage System](#dimensional-storage-system)
4. [Leveling and Progression](#leveling-and-progression)
5. [Abilities Guide](#abilities-guide)
6. [Commands Reference](#commands-reference)
7. [Tips and Strategies](#tips-and-strategies)
8. [Troubleshooting](#troubleshooting)
9. [Advanced Features](#advanced-features)

---

## 🚀 Getting Started

### Prerequisites
Before you begin, ensure you have:
- ✅ Minecraft 1.20.1
- ✅ Minecraft Forge 47.2.0 or higher
- ✅ GeckoLib 4.2.4
- ✅ Java 17 or higher

### Installation Steps

1. **Download Required Files**
   - [GeckoLib 4.2.4](https://www.curseforge.com/minecraft/mc-mods/geckolib) (Required dependency)
   - [EtherealMind Latest Release](https://github.com/AstroLabs-AI/EtherealMind/releases/latest)

2. **Install the Mods**
   - Open your Minecraft directory
     - Windows: `%appdata%\.minecraft`
     - macOS: `~/Library/Application Support/minecraft`
     - Linux: `~/.minecraft`
   - Navigate to the `mods` folder (create it if it doesn't exist)
   - Place both JAR files in this folder

3. **Launch Minecraft**
   - Start the Minecraft Launcher
   - Select the Forge 1.20.1 profile
   - Click Play!

---

## 🌟 First Steps with COSMO

### Obtaining COSMO

There are several ways to get your COSMO companion:

**Method 1: Creative Mode**
- Open Creative inventory (E key)
- Search for "COSMO" or "Ethereal"
- Find the COSMO Spawn Egg in the Miscellaneous tab

**Method 2: Commands**
```
/give @p etherealmind:cosmo_spawn_egg
```

**Method 3: Crafting** (Future Feature)
- Crafting recipe will be added in a future update

### Spawning COSMO

1. **Hold the spawn egg** in your hand
2. **Right-click** on the ground where you want COSMO to appear
3. COSMO will materialize with a particle effect
4. **Important**: Each player can only have ONE COSMO

### First Interaction

When you first interact with COSMO:
1. **Right-click** COSMO to bond with it
2. COSMO will display a bonding message
3. Your companion is now linked to you permanently
4. COSMO will follow you and teleport if you get too far away

### Understanding COSMO's Appearance

COSMO has several visual features:
- **Purple ethereal body** with a glowing effect
- **Animated face** with 8 different expressions:
  - 😊 Normal (happy)
  - 😑 Blinking
  - 😍 Super happy (heart eyes)
  - 😮 Curious (wide eyes)
  - 😴 Sleepy (with Z's)
  - ✨ Excited (star eyes)
  - 😉 Winking
  - 😌 Peaceful
- **Floating sparkles** that orbit around COSMO
- **Size changes** based on level and energy

---

## 📦 Dimensional Storage System

### Opening Storage
- **Right-click** COSMO to open the storage interface
- The GUI will show:
  - Storage slots (54 per page)
  - Quick access bar (9 slots)
  - Your inventory
  - Navigation buttons
  - Search bar

### Storage Layout

```
┌─────────────────────────────────┐
│         COSMO Storage           │
├─────────────────────────────────┤
│ [Slot][Slot][Slot]...[Slot] x9 │ ← Storage Grid
│ [Slot][Slot][Slot]...[Slot] x9 │   (6 rows x 9 columns)
│ [Slot][Slot][Slot]...[Slot] x9 │   = 54 slots per page
│ [Slot][Slot][Slot]...[Slot] x9 │
│ [Slot][Slot][Slot]...[Slot] x9 │
│ [Slot][Slot][Slot]...[Slot] x9 │
├─────────────────────────────────┤
│ [1][2][3][4][5][6][7][8][9]    │ ← Quick Access Bar
├─────────────────────────────────┤
│        Player Inventory         │
└─────────────────────────────────┘
```

### Navigation

- **Page Navigation**: Use `<` and `>` buttons to browse pages
- **Current Page**: Displayed as "Page X/Y" 
- **Storage Info**: Shows total items stored

### Quick Access Bar

The 9 slots at the bottom are special:
- **Hotkey Access**: Press number keys 1-9 to quickly access items
- **Priority Storage**: Items here are never auto-sorted
- **Cross-Page Access**: Available from any storage page

### Search Functionality

The search bar supports:
- **Item Names**: Type "diamond" to find all diamond items
- **Partial Matching**: "pick" will find pickaxes
- **Case Insensitive**: "SWORD" and "sword" work the same
- **Real-time Filtering**: Results update as you type

### Storage Capacity by Level

| Level | Pages | Total Slots | Storage Multiplier |
|-------|-------|-------------|-------------------|
| 1     | 8     | 432         | 1.0x              |
| 2     | 12    | 648         | 1.5x              |
| 3-5   | 12    | 648         | 1.5x              |
| 6     | 16    | 864         | 2.0x              |
| 7-8   | 16    | 864         | 2.0x              |
| 9-10  | 32    | 1,728       | 4.0x              |
| Max   | 64    | 3,456       | 8.0x (with items) |

---

## 📈 Leveling and Progression

### How to Gain XP

COSMO gains experience through:
- **Time**: 1 XP per minute while you're online
- **Interactions**: 5 XP every 10 interactions
- **Item Storage**: Small XP for using storage actively
- **Exploration**: Bonus XP for visiting new areas (planned)

### Level Requirements

| Level | XP Required | Total XP | New Unlock |
|-------|-------------|----------|------------|
| 1→2   | 100         | 100      | Increased Storage |
| 2→3   | 300         | 400      | Auto-Deposit |
| 3→4   | 600         | 1,000    | Enhanced Magnet |
| 4→5   | 1,000       | 2,000    | Healing Aura |
| 5→6   | 1,500       | 3,500    | Void Storage |
| 6→7   | 2,100       | 5,600    | Teleport Home |
| 7→8   | 2,800       | 8,400    | Reality Ripple |
| 8→9   | 3,600       | 12,000   | Quantum Storage |
| 9→10  | 4,500       | 16,500   | Ethereal Mastery |

### Checking Progress

Use `/cosmo level` to see:
- Current level
- XP progress to next level
- Percentage complete
- Recently unlocked abilities

---

## 🎯 Abilities Guide

### Level 1: Item Magnet
**Description**: Pulls items and experience orbs toward you
- **Range**: 8 blocks (16 blocks at level 4+)
- **Toggle**: `/cosmo ability magnet`
- **Effect**: Items flow smoothly toward player
- **Particles**: End rod particles on affected items

### Level 3: Auto-Deposit
**Description**: Automatically stores picked up items in COSMO's storage
- **Range**: 2 block radius around player
- **Toggle**: `/cosmo ability autodeposit`
- **Priority**: Tries to stack with existing items first
- **Effect**: Portal particles when items are deposited
- **Sound**: Enderman teleport sound on deposit

### Level 5: Healing Aura
**Description**: Slowly regenerates your health
- **Rate**: 0.5 hearts per second
- **Toggle**: `/cosmo ability healing`
- **Condition**: Only active when below max health
- **Particles**: Heart particles when healing
- **Range**: Must be within 32 blocks of COSMO

### Level 7: Teleport Home
**Description**: Instantly return to a saved location
- **Set Home**: Shift + Right-click COSMO
- **Activate**: `/cosmo ability home`
- **Cooldown**: 60 seconds
- **Effect**: Reverse portal particles
- **Cross-Dimension**: Works within same dimension only

### Special Abilities (Planned)
- **Reality Ripple** (Level 8): Visual effects and mob deterrent
- **Ethereal Mastery** (Level 10): All abilities enhanced, special rewards

---

## 💻 Commands Reference

### Basic Commands

**Check Level and XP**
```
/cosmo level
```
Shows: Current level, XP progress, percentage to next level

**View Statistics**
```
/cosmo stats
```
Shows: Level, total XP, playtime, storage usage, ability states

### Ability Management

**Toggle Abilities**
```
/cosmo ability <ability_name>
```
Valid abilities: `magnet`, `autodeposit`, `healing`, `home`

**Examples:**
```
/cosmo ability magnet        # Toggle item magnet
/cosmo ability autodeposit   # Toggle auto-deposit
/cosmo ability healing       # Toggle healing aura
/cosmo ability home          # Teleport to saved home
```

### Admin Commands

**Set Level** (Requires OP)
```
/cosmo level set <level>
```
Example: `/cosmo level set 5` - Sets COSMO to level 5

**Debug Commands** (Planned)
```
/cosmo debug              # Show debug information
/cosmo reset              # Reset COSMO to default state
```

---

## 💡 Tips and Strategies

### Early Game (Levels 1-3)
1. **Focus on Storage**: Use COSMO as a mobile chest
2. **Organize Early**: Set up your quick access bar with tools
3. **Enable Magnet**: Great for mining and mob farms
4. **Interact Often**: Build XP through regular interaction

### Mid Game (Levels 4-6)
1. **Auto-Deposit Mining**: Enable auto-deposit for mining trips
2. **Healing Aura**: Useful for dangerous exploration
3. **Storage Categories**: Start organizing by item type
4. **Set Multiple Homes**: Update home position for current base

### Late Game (Levels 7-10)
1. **Home Network**: Set homes at key locations
2. **Max Storage**: Utilize full 3,456 slot capacity
3. **Quick Swapping**: Master the hotkey system
4. **Ability Combos**: Use multiple abilities together

### Storage Organization Tips

**Quick Access Setup:**
- Slot 1: Primary weapon
- Slot 2: Pickaxe
- Slot 3: Food
- Slot 4: Blocks
- Slot 5: Torches
- Slots 6-9: Situational items

**Page Organization:**
- Page 1: Tools and weapons
- Page 2: Building blocks
- Page 3: Redstone and technical
- Page 4: Food and potions
- Page 5+: Bulk storage

---

## 🔧 Troubleshooting

### Common Issues

**COSMO Won't Spawn**
- Ensure you're in a valid dimension (Overworld, Nether, End)
- Check if you already have a COSMO (only one per player)
- Verify spawn area has enough space (3x3x3 minimum)

**Storage Won't Open**
- Right-click directly on COSMO, not near it
- Ensure you're the bonded player
- Check for mod conflicts with GUI mods

**COSMO Is Invisible**
- Update to latest version (v1.2.1+ has visibility fixes)
- Check video settings (particles must be enabled)
- Verify GeckoLib is installed correctly

**Abilities Not Working**
- Check if ability is unlocked (correct level)
- Verify ability is enabled (`/cosmo stats`)
- Ensure you're in range of COSMO

**Performance Issues**
- Disable particles in areas with many entities
- Reduce storage pages if not needed
- Lower particle settings in video options

### Error Messages

**"You already have a COSMO companion!"**
- Each player can only have one COSMO
- If COSMO is lost, it will teleport back to you

**"COSMO needs to be level X"**
- Ability is locked until specified level
- Use `/cosmo level` to check current level

**"No home position set!"**
- Set home with Shift + Right-click on COSMO
- Home must be set before using teleport

---

## 🌟 Advanced Features

### Keyboard Shortcuts

| Key | Action |
|-----|--------|
| 1-9 | Access quick slot items |
| E | Open inventory (close storage) |
| ESC | Close storage GUI |
| Tab | Focus search bar (planned) |

### Hidden Features

1. **COSMO Emotions**: COSMO's expression changes based on:
   - Your health level
   - Time of day
   - Recent interactions
   - Storage usage

2. **Particle Patterns**: Different moods create unique particle effects

3. **Secret Interactions**: Try different items for special responses

### Integration with Other Mods

**JEI (Just Enough Items)**
- COSMO's storage is searchable through JEI
- Recipes can be viewed while in storage GUI

**WAILA/Jade**
- Shows COSMO's level and mood
- Displays storage capacity

### Planned Features

1. **COSMO Customization**
   - Dye COSMO different colors
   - Unlock special skins
   - Particle effect options

2. **Advanced Abilities**
   - Item sorting automation
   - Crafting assistance
   - Resource gathering

3. **Multiplayer Features**
   - COSMO trading post
   - Shared storage networks
   - Companion interactions

---

## 📚 Additional Resources

- **Wiki**: [Coming Soon]
- **Discord**: [Coming Soon]
- **Video Tutorials**: [Coming Soon]
- **API Documentation**: For mod developers (Coming Soon)

---

*Thank you for choosing EtherealMind! We hope you and COSMO have many great adventures together.* ✨

---

[Back to README](../README.md) | [Report an Issue](https://github.com/AstroLabs-AI/EtherealMind/issues)