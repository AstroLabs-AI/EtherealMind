# Natural Language Commands for COSMO

## Overview

COSMO now features an advanced natural language command system that allows players to interact with their companion through chat. Simply type messages in chat that include "COSMO" or "cosmo" to communicate with your companion.

## Basic Commands

### Movement Commands

- **Follow Me**: "COSMO, follow me" or "COSMO follow"
  - Makes COSMO follow you around
  - Default behavior when first spawned
  - Indicated by heart particles (♥)

- **Stay**: "COSMO, stay here" or "COSMO wait"
  - COSMO will remain at current position
  - Indicated by snowflake particles (⏸)

- **Come Here**: "COSMO, come here" or "COSMO come"
  - COSMO will move directly to you
  - Switches to following once close
  - Indicated by sweep attack particles (→)

### Advanced Commands

- **Guard**: "COSMO, guard this area" or "COSMO protect"
  - COSMO will patrol a small area around current position
  - Will return if moving too far away
  - Indicated by enchanted hit particles (🛡)

- **Patrol**: "COSMO, patrol 10" or "COSMO patrol"
  - COSMO will patrol in a radius (default 10 blocks)
  - Can specify radius from 5-20 blocks
  - Indicated by end rod particles (🔄)

### Item Management

- **Fetch Specific Items**: "COSMO, bring me [item name]" or "COSMO fetch [item]"
  - COSMO will search storage for matching items
  - Delivers item to your inventory or drops nearby
  - Example: "COSMO, bring me diamond"

- **Fetch by Category**: "COSMO, bring me [category]"
  - Categories: food, tools, weapons, armor, blocks, materials, valuables, potions
  - Example: "COSMO, bring me food" - fetches any edible item
  - Example: "COSMO, I need tools" - fetches pickaxes, axes, etc.

### Utility Commands

- **Help**: "COSMO, help" or "COSMO what can you do?"
  - Shows list of available commands
  - Explains COSMO's capabilities

- **Storage**: "COSMO, open storage" or "COSMO inventory"
  - Opens COSMO's dimensional storage interface

## Command Format

Commands are flexible and support natural variations:
- "COSMO, follow me please"
- "Hey COSMO, stay here"
- "cosmo come"
- "COSMO guard this spot"

## Visual Feedback

COSMO provides visual and audio feedback:
- **Speech Bubbles**: Messages appear above COSMO's head
- **Emotes**: Emoji reactions for different situations (❤️, 🛡️, 📦, etc.)
- **Particles**: Different particle effects for each behavior state
- **Sounds**: Happy, confused, acknowledgment, and alert sounds
- **Chat Messages**: Confirmation messages in purple text
- **Action Bar**: Current state displayed with icons

## Environmental Awareness

COSMO comments on the environment:
- **Weather**: "The storm is intense! ⛈️"
- **Time**: "The stars are beautiful tonight ✨"
- **Danger**: "3 hostiles detected nearby! ⚠️"
- **Biomes**: "This desert heat is intense! 🏜️"
- **Light**: "It's quite dark here... 🌑"

## Tips

1. COSMO responds to conversational commands - no need for exact syntax
2. Commands work from any distance within the same dimension
3. Only the bound player can command their COSMO
4. Some abilities require certain levels to unlock
5. COSMO remembers the last command even after server restarts

## Examples

```
Player: COSMO, follow me to the mine
COSMO: Following you! ♥

Player: COSMO stay here and guard my base
COSMO: Guarding this area! 🛡

Player: Hey COSMO, can you bring me some iron?
COSMO: *searches storage and delivers iron*

Player: COSMO patrol 15
COSMO: Patrolling area (radius: 15)! 🔄
```

## Combat Assistance (Level 5+)

When COSMO reaches level 5, combat assistance features unlock:

### Combat Commands
- **Enable Combat**: "COSMO, combat on" or "protect me"
  - Activates combat assistance mode
  - COSMO will help during battles

- **Defensive Mode**: "COSMO, defend me" 
  - Prioritizes healing and protection
  - Marks dangerous enemies with glowing effect
  - Creates distractions to help you escape
  - Heals you when health is low

- **Offensive Mode**: "COSMO, attack mode"
  - Focuses on debuffing enemies
  - Applies weakness to targets (Level 7+)
  - Slows enemies (Level 9+)

- **Disable Combat**: "COSMO, combat off"
  - Turns off combat assistance

### Combat Features
- **Auto-Detection**: COSMO detects when you're in combat
- **Enemy Marking**: Dangerous enemies glow for visibility
- **Emergency Healing**: Heals 2 hearts when you're below 30% health
- **Distraction**: Creates flashy effects to confuse enemies
- **Smart Positioning**: Moves between you and threats

## Resource Gathering

COSMO can help collect resources and harvest crops:

### Gathering Commands
- **Auto-Collect**: "COSMO, collect items" or "gather drops"
  - Automatically collects nearby dropped items
  - Stores them in dimensional storage
  - Works within 8 block radius

- **Harvest Crops** (Level 3+): "COSMO, harvest crops" or "help me farm"
  - Automatically harvests mature crops
  - Replants seeds when possible
  - Works within 6 block radius

- **Stop Gathering**: "COSMO, stop collecting"
  - Disables auto-collection features

### Gathering Features
- **Smart Storage**: Items go directly to COSMO's storage
- **XP Collection**: Pulls experience orbs to you
- **Crop Detection**: Only harvests fully grown crops
- **Auto-Replant**: Uses seeds from storage to replant
- **Progress Reports**: Tells you collection statistics

## Personality Responses

COSMO's responses vary based on:
- Current mood level
- Trust level with player
- Time spent together
- Recent interactions
- Environmental context
- Combat situations

Higher trust levels unlock more enthusiastic responses and special emotes!