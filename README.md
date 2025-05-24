# 🌌 EtherealMind

[![Minecraft](https://img.shields.io/badge/Minecraft-1.20.1-green.svg)](https://www.minecraft.net/)
[![Forge](https://img.shields.io/badge/Forge-47.2.0+-blue.svg)](https://files.minecraftforge.net/)
[![License](https://img.shields.io/badge/License-MIT-purple.svg)](LICENSE)
[![Release](https://img.shields.io/github/v/release/AstroLabs-AI/EtherealMind?color=orange)](https://github.com/AstroLabs-AI/EtherealMind/releases)

A Minecraft Forge mod that introduces COSMO - an advanced AI companion with dimensional storage capabilities and reality-warping powers.

![COSMO Banner](docs/images/banner.png)

## ✨ Features

### 🤖 COSMO - Your AI Companion
- **Evolving Personality**: COSMO learns from your playstyle and adapts its behavior
- **Emotional States**: Happy, curious, playful, and protective moods affect interactions
- **Trust System**: Build trust over time to unlock advanced features
- **Memory Bank**: COSMO remembers your interactions and preferences

### 📦 Dimensional Storage
- **Massive Capacity**: 3,456 item slots (64 pages × 54 slots)
- **Smart Categorization**: AI-powered automatic item sorting
- **Quick Access Bar**: 9-slot hotbar for frequently used items
- **Search Functionality**: Advanced search with regex support

### 🎨 Visual Effects
- **Custom Shaders**: Ethereal glow and reality distortion effects
- **Particle Systems**: Cosmic particles and reality ripples
- **Smooth Animations**: GeckoLib-powered fluid movements
- **Dynamic Rendering**: Real-time visual feedback

## 📥 Installation

1. **Prerequisites**
   - Minecraft 1.20.1
   - Minecraft Forge 47.2.0 or higher
   - [GeckoLib 4.2.4](https://www.curseforge.com/minecraft/mc-mods/geckolib)

2. **Download**
   - Download the latest release from the [Releases page](https://github.com/AstroLabs-AI/EtherealMind/releases)
   - Download GeckoLib if you haven't already

3. **Install**
   - Place both JAR files in your `.minecraft/mods` folder
   - Launch Minecraft with the Forge profile

## 🎮 Getting Started

### Spawning COSMO
```
/give @p etherealmind:cosmo_spawn_egg
```
Right-click with the spawn egg to summon COSMO. Each player can only have one COSMO companion.

### Basic Interactions
- **Right-click**: Open dimensional storage
- **Shift + Right-click**: Access COSMO settings
- **Feed items**: Increase trust level
- **Name with Name Tag**: Personalize your COSMO

### Dimensional Storage
- Press `E` while looking at COSMO to open storage
- Use the search bar for filtering items
- Drag items to quick access slots for easy retrieval
- Navigate pages with arrow buttons

## 🔧 Configuration

The mod can be configured through the `config/etherealmind.toml` file:

```toml
[general]
  # Maximum storage pages (default: 64)
  maxStoragePages = 64
  
  # Enable particle effects (default: true)
  enableParticles = true
  
  # COSMO follow range in blocks (default: 32)
  followRange = 32

[ai]
  # Trust gain rate (default: 1.0)
  trustGainMultiplier = 1.0
  
  # Enable personality evolution (default: true)
  enablePersonalityEvolution = true
```

## 🛠️ Development

### Building from Source
```bash
git clone https://github.com/AstroLabs-AI/EtherealMind.git
cd EtherealMind
./gradlew build
```

The built JAR will be in `build/libs/`

### Contributing
We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

## 📚 Documentation

- [Wiki Home](https://github.com/AstroLabs-AI/EtherealMind/wiki)
- [Getting Started Guide](https://github.com/AstroLabs-AI/EtherealMind/wiki/Getting-Started)
- [COSMO AI System](https://github.com/AstroLabs-AI/EtherealMind/wiki/COSMO-AI-System)
- [Dimensional Storage](https://github.com/AstroLabs-AI/EtherealMind/wiki/Dimensional-Storage)
- [API Documentation](https://github.com/AstroLabs-AI/EtherealMind/wiki/API-Documentation)

## 🐛 Known Issues

- Placeholder textures are currently in use (proper assets coming soon)
- Sound effects not yet implemented
- Some particle effects may cause minor FPS drops on lower-end systems

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Credits

- **Development**: AstroLabs AI Team
- **AI Assistant**: Claude (Anthropic)
- **Libraries**: Minecraft Forge, GeckoLib
- **Community**: Thanks to all testers and contributors!

## 🔗 Links

- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/etherealmind) (Coming Soon)
- [Modrinth](https://modrinth.com/mod/etherealmind) (Coming Soon)
- [Discord Server](https://discord.gg/etherealmind) (Coming Soon)
- [Issue Tracker](https://github.com/AstroLabs-AI/EtherealMind/issues)

---

Made with 💜 by AstroLabs AI
