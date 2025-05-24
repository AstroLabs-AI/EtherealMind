# Dimensional Storage System

A comprehensive guide to COSMO's dimensional storage capabilities.

## Table of Contents
1. [Storage Overview](#storage-overview)
2. [User Interface](#user-interface)
3. [Organization Features](#organization-features)
4. [Search System](#search-system)
5. [Quick Access Bar](#quick-access-bar)
6. [Advanced Features](#advanced-features)

## Storage Overview

### Capacity
- **Total Slots**: 3,456 items
- **Pages**: 64 pages
- **Slots per Page**: 54 slots (6×9 grid)
- **Quick Access**: 9 additional slots
- **Stack Limit**: Standard Minecraft stacking rules apply

### Dimensional Properties
The storage exists in a pocket dimension, providing:
- ✅ **Chunk Independence**: Not affected by chunk loading
- ✅ **Cross-Dimensional**: Accessible from any dimension  
- ✅ **Persistent**: Survives server restarts
- ✅ **Protected**: Cannot be accessed by other players
- ✅ **Weightless**: No inventory weight penalties

## User Interface

### Main Storage GUI
```
┌─────────────────────────────────────┐
│ [Search Bar........................] │
├─────────────────────────────────────┤
│ ┌─┬─┬─┬─┬─┬─┬─┬─┬─┐   Categories:  │
│ ├─┼─┼─┼─┼─┼─┼─┼─┼─┤   [All.......]  │
│ ├─┼─┼─┼─┼─┼─┼─┼─┼─┤   [Blocks....]  │
│ ├─┼─┼─┼─┼─┼─┼─┼─┼─┤   [Items.....]  │
│ ├─┼─┼─┼─┼─┼─┼─┼─┼─┤   [Tools.....]  │
│ ├─┼─┼─┼─┼─┼─┼─┼─┼─┤   [Food......]  │
│ └─┴─┴─┴─┴─┴─┴─┴─┴─┘   [Combat....]  │
├─────────────────────────────────────┤
│ Quick Access: ┌─┬─┬─┬─┬─┬─┬─┬─┬─┐ │
│               └─┴─┴─┴─┴─┴─┴─┴─┴─┘ │
│ [<] Page 1/64 [>]                   │
└─────────────────────────────────────┘
```

### Controls
- **Left Click**: Pick up full stack
- **Right Click**: Pick up half stack
- **Shift + Click**: Quick transfer
- **Middle Click**: Clone item (Creative only)
- **Number Keys**: Assign to quick access

## Organization Features

### Automatic Categorization
COSMO automatically categorizes items into:

1. **Blocks**
   - Building blocks
   - Decorative blocks
   - Redstone components
   - Natural blocks

2. **Items**
   - Raw materials
   - Crafting components
   - Miscellaneous items

3. **Tools**
   - Pickaxes, axes, shovels
   - Specialized tools
   - Enchanted variants

4. **Food**
   - Raw ingredients
   - Cooked meals
   - Potions and drinks

5. **Combat**
   - Weapons
   - Armor
   - Arrows and projectiles

6. **Special**
   - Enchanted books
   - Rare items
   - Custom mod items

### Smart Sorting
COSMO learns your preferences:
```java
// Sorting algorithm considers:
- Item frequency of use
- Recent access patterns  
- Player-defined categories
- Similar item grouping
- Enchantment levels
```

### Custom Categories
Create your own categories:
1. Open storage GUI
2. Click "Manage Categories"
3. Create new category with name and rules
4. Drag items to assign

## Search System

### Basic Search
- Type in the search bar to filter items
- Case-insensitive matching
- Searches item names and tooltips

### Advanced Search Syntax

#### Operators
- `AND`: `sword AND diamond`
- `OR`: `iron OR gold`
- `NOT`: `NOT damaged`
- `"..."`: Exact phrase match

#### Special Filters
- `@mod:modname` - Items from specific mod
- `#tag` - Items with specific tag
- `ench:enchantment` - Enchanted items
- `dmg:<value` - Damaged items
- `stack:>32` - Stack size filters

#### Regex Support
Enable regex mode with `/regex`:
- `/regex dia.*` - Matches "diamond", "dias", etc.
- `/regex ^iron` - Items starting with "iron"
- `/regex sword$` - Items ending with "sword"

### Search Examples
```
"diamond sword" ench:sharpness
@mod:minecraft #ores
food NOT rotten
armor dmg:<50
/regex (pick|axe|shovel)
```

## Quick Access Bar

### Configuration
The 9-slot quick access bar can be customized:
1. **Drag & Drop**: Move items from storage
2. **Lock Slots**: Prevent accidental removal
3. **Auto-Refill**: Automatically refill from storage
4. **Hotkey Access**: Bind to number keys

### Smart Features
- **Predictive Loading**: Pre-loads commonly used items
- **Context Awareness**: Changes based on activity
- **Tool Durability**: Swaps damaged tools automatically
- **Food Priority**: Keeps best food available

### Templates
Save and load quick access configurations:
- **Mining**: Pickaxes, torches, food
- **Building**: Blocks, scaffolding, tools  
- **Combat**: Weapons, armor, healing
- **Farming**: Seeds, hoes, bone meal

## Advanced Features

### Bulk Operations

#### Multi-Select
- Hold `Ctrl` to select multiple items
- `Ctrl+A` to select all on page
- Drag selection box with mouse

#### Bulk Actions
- **Move**: Transfer multiple items between pages
- **Compress**: Auto-craft blocks from items
- **Sort**: Reorganize selected items
- **Export**: Output to regular chests

### Storage Upgrades
As trust increases, unlock:

#### Trust Level 2
- Category customization
- Search history
- Favorite items

#### Trust Level 3  
- Regex search
- Bulk operations
- Import/export

#### Trust Level 4
- Storage sharing
- Team categories
- Cross-COSMO search

#### Trust Level 5
- Quantum compression (2x capacity)
- Reality fold (instant access)
- Temporal storage (item history)

### Integration Features

#### Hopper Compatibility
- Hoppers can insert items with permission
- Configure input rules per side
- Auto-sort incoming items

#### Mod Support
- JEI integration for recipes
- AE2/RS external storage
- Inventory tweaks compatibility

#### Redstone Integration
- Comparator output based on fullness
- Redstone control for access
- Automation possibilities

### Performance Tips

1. **Organization**
   - Keep frequently used items on early pages
   - Use categories to reduce search time
   - Clean out junk regularly

2. **Search Optimization**
   - Use specific terms
   - Save common searches
   - Learn regex patterns

3. **Quick Access**
   - Configure for your playstyle
   - Use templates for activities
   - Enable auto-refill

### Troubleshooting

**Items disappeared:**
- Check search filter isn't active
- Verify correct page
- Use `/etherealmind:storage recover`

**Can't access storage:**
- Ensure trust level is sufficient
- Check COSMO is nearby
- Verify no permissions issues

**Lag when opening:**
- Reduce particles in settings
- Clear unused pages
- Defragment storage

---

*See also: [COSMO Guide](COSMO-Guide) | [Commands](Commands-and-Permissions)*