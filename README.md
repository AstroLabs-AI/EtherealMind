# 🌌 EtherealMind

[![Minecraft](https://img.shields.io/badge/Minecraft-1.20.1-green.svg)](https://www.minecraft.net/)
[![Forge](https://img.shields.io/badge/Forge-47.2.0+-blue.svg)](https://files.minecraftforge.net/)
[![License](https://img.shields.io/badge/License-MIT-purple.svg)](LICENSE)
[![Release](https://img.shields.io/github/v/release/AstroLabs-AI/EtherealMind?color=orange)](https://github.com/AstroLabs-AI/EtherealMind/releases)

A Minecraft Forge mod that introduces COSMO - an advanced AI companion with dimensional storage, leveling system, special abilities, and reality-warping powers.

## ✨ Features

### 🌟 COSMO - Your Ethereal Companion
- **Animated Entity**: Floating companion with 8 different facial expressions that cycle naturally
- **Bonding System**: COSMO bonds with the first player who interacts, becoming your loyal companion
- **Intelligent AI**: Follows you around, teleports if too far, remembers interactions
- **Leveling System**: Progress from level 1-10, unlocking new abilities and features
- **Personality System**: COSMO's mood and behavior evolve based on your interactions

### 📦 Dimensional Storage System
- **Massive Capacity**: Starting with 432 slots (8 pages), scaling up to 3,456 slots at max level
- **Quick Access Bar**: 9 special slots with hotkey support (number keys 1-9)
- **Smart Organization**: Automatic item categorization and sorting
- **Search Functionality**: Find items quickly with advanced search
- **Page Navigation**: Easy browsing through multiple storage pages
- **Performance Optimized**: Lazy loading and efficient data handling

### 🎯 Leveling & Abilities
Each level unlocks new features:

| Level | Ability | Description |
|-------|---------|-------------|
| 1 | **Item Magnet** | Pulls items and XP orbs within range |
| 2 | **Increased Storage** | +50% storage capacity |
| 3 | **Auto-Deposit** | Automatically stores picked up items |
| 4 | **Enhanced Magnet** | Increased magnet range |
| 5 | **Healing Aura** | Slowly regenerates your health |
| 6 | **Void Storage** | 2x storage capacity |
| 7 | **Teleport Home** | Return to saved location instantly |
| 8 | **Reality Ripple** | Special visual effects |
| 9 | **Quantum Storage** | 4x storage capacity |
| 10 | **Ethereal Mastery** | All abilities enhanced |

### 🎮 Commands
- `/cosmo level` - Check your COSMO's current level and XP progress
- `/cosmo level set <level>` - Set COSMO's level (requires operator permissions)
- `/cosmo ability <magnet|autodeposit|healing|home>` - Toggle abilities on/off
- `/cosmo stats` - View detailed statistics about your COSMO

### 🎨 Visual Features
- **Fully Opaque Design**: COSMO is clearly visible with a purple ethereal glow
- **Animated Face**: 8 expressions including happy, winking, sleepy, curious, and more
- **Particle Effects**: Floating sparkles and ethereal particles
- **Custom GUI**: Beautiful dimensional storage interface with ethereal theme
- **GeckoLib Animations**: Smooth floating and movement animations

## 📋 Requirements

- Minecraft 1.20.1
- Minecraft Forge 47.2.0 or higher
- [GeckoLib 4.2.4](https://www.curseforge.com/minecraft/mc-mods/geckolib)
- Java 17 or higher

## 📥 Installation

1. **Install Minecraft Forge**
   - Download Forge 1.20.1 from [MinecraftForge.net](https://files.minecraftforge.net/)
   - Run the installer and select "Install Client"

2. **Download Dependencies**
   - Download [GeckoLib 4.2.4](https://www.curseforge.com/minecraft/mc-mods/geckolib) for 1.20.1

3. **Download EtherealMind**
   - Get the latest release from our [Releases Page](https://github.com/AstroLabs-AI/EtherealMind/releases)

4. **Install Mods**
   - Navigate to your `.minecraft/mods` folder
   - Place both GeckoLib and EtherealMind JAR files in the folder
   - Launch Minecraft with the Forge profile

## 🚀 Getting Started

1. **Obtain COSMO**
   ```
   /give @p etherealmind:cosmo_spawn_egg
   ```
   Or find the spawn egg in the Creative menu under the "Misc" tab

2. **Spawn Your Companion**
   - Right-click with the spawn egg to summon COSMO
   - COSMO will immediately bond with you

3. **Basic Interactions**
   - **Right-click**: Open dimensional storage
   - **Shift + Right-click**: Set home position for teleportation
   - **Feed Items**: Some items increase trust and grant XP

4. **Using Storage**
   - Click and drag items between inventories
   - Use number keys (1-9) for quick access slots
   - Navigate pages with arrow buttons
   - Search items using the search bar

5. **Managing Abilities**
   - Use `/cosmo ability <name>` to toggle abilities
   - Abilities are unlocked as you level up
   - Check your level with `/cosmo level`

## 🔧 Tips & Tricks

- **Leveling Up**: Interact with COSMO frequently and spend time together to gain XP
- **Home Teleport**: Set home with Shift+Right-click, teleport with `/cosmo ability home`
- **Auto-Deposit**: Enable at level 3 to automatically store picked up items
- **Storage Management**: Use the search bar with item names or tags
- **Quick Access**: Put your most used items in the quick access bar

## 🛠️ Building from Source

```bash
git clone https://github.com/AstroLabs-AI/EtherealMind.git
cd EtherealMind
./gradlew build
```

The compiled JAR will be located in `build/libs/`

## 📖 Documentation

- [Complete Guide](docs/GUIDE.md) - Comprehensive guide to all features
- [Technical Documentation](docs/TECHNICAL_ARCHITECTURE.md) - For developers
- [Troubleshooting](docs/TROUBLESHOOTING.md) - Common issues and solutions

## 🐛 Reporting Issues

Found a bug? Please report it on our [Issue Tracker](https://github.com/AstroLabs-AI/EtherealMind/issues) with:
- Minecraft version
- Forge version  
- Mod version
- Description of the issue
- Steps to reproduce
- Crash log (if applicable)

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guidelines](CONTRIBUTING.md) for details on:
- Code style guidelines
- How to submit pull requests
- Feature request process

## 📜 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Credits

- **Development**: AstroLabs AI Team
- **AI Assistant**: Claude (Anthropic)
- **Animation Framework**: GeckoLib Team
- **Special Thanks**: All our testers and contributors!

## 🔗 Links

- [Latest Release](https://github.com/AstroLabs-AI/EtherealMind/releases/latest)
- [Issue Tracker](https://github.com/AstroLabs-AI/EtherealMind/issues)
- [Wiki](https://github.com/AstroLabs-AI/EtherealMind/wiki) (Coming Soon)
- [Discord](https://discord.gg/etherealmind) (Coming Soon)

---

*Your ethereal companion awaits! Download now and begin your journey with COSMO.* ✨🌌