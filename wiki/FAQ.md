# Frequently Asked Questions

Common questions and answers about EtherealMind.

## General Questions

### What is EtherealMind?
EtherealMind is a Minecraft Forge mod that adds COSMO, an advanced AI companion with dimensional storage capabilities. COSMO learns from your playstyle, manages a massive inventory system, and develops its own personality over time.

### What versions does it support?
- **Minecraft**: 1.20.1
- **Forge**: 47.2.0 or higher
- **Dependencies**: GeckoLib 4.2.4

### Is it server-side or client-side?
EtherealMind is required on **both server and client**. The AI calculations run on the server, while visual effects and GUI are handled client-side.

### Can I use it in modpacks?
Yes! EtherealMind is released under the MIT license. You're free to include it in any modpack. We'd appreciate credit, but it's not required.

## COSMO Questions

### How do I get COSMO?
Use the command `/give @p etherealmind:cosmo_spawn_egg` to get a spawn egg, then right-click to spawn COSMO. In survival, you'll need to craft the spawn egg (recipe coming in future updates).

### Can I have multiple COMSOs?
No, each player can only bond with one COSMO. If you try to spawn another, it won't bond with you. This is to ensure each COSMO develops a unique relationship with its player.

### What happens if COSMO dies?
COSMO respawns automatically after 5 minutes at your location. All storage contents and personality data are preserved. You can also use `/etherealmind:recall` to summon it immediately.

### Can other players access my COSMO?
No, COSMO and its storage are bound to you. Other players cannot interact with your COSMO or access your dimensional storage.

### How do I increase trust level?
- Regular interaction (daily logins)
- Feeding valuable items (diamonds, emeralds)
- Time spent online together
- Completing adventures together
- Keeping COSMO safe from damage

### What do the different moods mean?
- **Happy** 😊: Increased efficiency, better organization
- **Curious** 🤔: Learns faster, explores more
- **Playful** 🎮: More particles, playful animations
- **Protective** 🛡️: Stays closer, warns of dangers

## Storage Questions

### How much can COSMO store?
COSMO has 3,456 item slots (64 pages × 54 slots per page), plus 9 quick access slots. This is equivalent to about 64 double chests!

### How do I organize items?
COSMO automatically categorizes items, but you can:
- Create custom categories
- Use the search function
- Manually organize pages
- Set up quick access slots

### Can I access storage from anywhere?
Yes, as long as COSMO is within 32 blocks (configurable). The storage exists in a pocket dimension, so it's accessible from any Minecraft dimension.

### What happens to items if I uninstall the mod?
Items remain in COSMO's dimensional storage. If you reinstall the mod, all items will still be there. However, without the mod, you cannot access them.

### How does the search function work?
Basic search matches item names. Advanced search supports:
- Boolean operators (AND, OR, NOT)
- Regex patterns (with `/regex` prefix)
- Mod filtering (`@mod:minecraft`)
- Tag filtering (`#ores`)
- Special filters (enchantments, damage, etc.)

## Technical Questions

### Why is COSMO not moving?
Check:
- Trust level (needs level 1+ for following)
- Energy level (feed items to restore)
- Distance (recall if too far)
- Chunk loading (COSMO needs loaded chunks)

### I'm getting lag when opening storage
Try:
- Reducing particle effects in config
- Clearing unused storage pages
- Updating graphics drivers
- Allocating more RAM to Minecraft

### Storage items disappeared
- Check if search filter is active (clear search bar)
- Verify you're on the correct page
- Use `/etherealmind:storage validate` to check integrity
- Items may be in a different category

### COSMO won't spawn
- Ensure you have permission for commands
- Check you're in a valid dimension
- Verify mod is properly installed on both client and server
- Look for error messages in logs

### Mod conflicts
Known compatibility:
- ✅ JEI (full integration)
- ✅ Inventory Tweaks
- ⚠️ Some backpack mods (may have GUI conflicts)
- ❌ Certain AI mods (may interfere with COSMO AI)

## Performance Questions

### How much does COSMO impact performance?
COSMO is designed to be lightweight:
- AI updates every second, not every tick
- Particle effects can be disabled
- Storage is optimized for large inventories
- Typical impact: 1-3% CPU, <50MB RAM

### Can I disable certain features?
Yes, via the config file:
- Particle effects
- AI personality evolution  
- Storage auto-organization
- Following behavior
- Visual effects

### Does COSMO work with shaders?
Yes, but some shader packs may not render the custom effects correctly. The mod includes fallback rendering for compatibility.

## Multiplayer Questions

### Do all players need the mod?
Yes, all players connecting to a server with EtherealMind need the mod installed client-side.

### Can COMSOs interact with each other?
Currently, COMSOs can see each other and will acknowledge presence with particles. Future updates will add more interaction features.

### Server configuration
Server admins can configure:
- Max storage pages per player
- Trust gain rates
- Allowed dimensions
- Performance settings

## Future Features

### What's planned for future updates?
- Crafting recipes for spawn egg
- More personality traits
- COSMO interactions
- API expansions
- Visual customization
- Voice lines (with resource pack)

### Can I suggest features?
Absolutely! Open an issue on our [GitHub](https://github.com/AstroLabs-AI/EtherealMind/issues) with the "enhancement" tag.

### When will textures be improved?
We're working on professional textures and models. Current textures are placeholders. Expect updates in version 1.1.0.

---

*Still have questions? Join our [Discord](#) or open an [issue](https://github.com/AstroLabs-AI/EtherealMind/issues)!*