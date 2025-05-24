# COSMO Cute AI Agent Design Document

## Overview
COSMO is being redesigned as an adorable AI companion with a focus on cuteness and approachability. This document outlines the design specifications and implementation details.

## Core Design Philosophy
- **Personality**: Friendly, helpful, enthusiastic AI assistant
- **Visual Style**: Soft, rounded, pastel colors with holographic elements
- **Behavior**: Expressive, responsive, and emotionally engaging

## Visual Specifications

### Body Structure
- **Main Body**: Soft sphere approximately 0.8 blocks in diameter
- **Proportions**: Head takes up 80% of body, large eye is 40% of head
- **Material**: Frosted glass effect with 85% transparency
- **Color Palette**:
  - Primary: Soft sky blue (#87CEEB)
  - Secondary: Light pink accents (#FFB6C1)
  - Glow: Warm white-blue (#E0F3FF)

### Eye Design
- **Size**: 0.3 blocks diameter (large and expressive)
- **Structure**:
  - Outer ring: White glow
  - Iris: Gradient blue with data patterns
  - Pupil: Black dot that dilates with emotions
  - Sparkles: 4-6 star particles when happy

### Appendages
- **Arm Count**: 2-3 stubby holographic arms
- **Arm Length**: 0.2 blocks each
- **Arm Features**:
  - Rounded ends (no fingers)
  - Can stretch slightly when reaching
  - Semi-transparent with energy trails

## Expression System

### Digital Face Expressions
| Emotion | Expression | Trigger |
|---------|------------|---------|
| Happy | ^_^ | Player interaction, successful task |
| Excited | :D | New discovery, level up |
| Surprised | :o | Unexpected event |
| Thinking | .-. | Processing command |
| Sleepy | z_z | Long idle time |
| Confused | @_@ | Unknown command |
| Love | ♥‿♥ | High trust level |

### Particle Effects
- **Happy**: Rainbow sparkles in spiral pattern
- **Working**: Binary code floating upward
- **Idle**: Soft star particles orbiting
- **Alert**: Exclamation marks popping

## Animation States

### Idle Animations (cycling)
1. **Gentle Bob**: Slow up/down movement (2 second cycle)
2. **Look Around**: Eye moves left/right curiously
3. **Lens Cleaning**: Wipes eye with arm
4. **Stretch**: Arms extend and yawn animation
5. **Data Check**: Small holographic panels appear/disappear

### Reaction Animations
- **Pet Response**: Eye closes to happy line, pink blush appears
- **Task Complete**: Spin with confetti particles
- **Greeting**: Enthusiastic arm wave
- **Thinking**: Question marks orbit head
- **Error**: Head tilt with single "?"

### Movement Animations
- **Following**: Bouncy hovering motion
- **Approaching Item**: Grabby hands animation
- **Combat Stance**: Puffs up to look bigger
- **Retreating**: Shrinks slightly while backing away

## Technical Implementation

### Model Structure
```
COSMO_Root
├── Body_Core
│   ├── Main_Sphere
│   ├── Glow_Layer
│   └── Glass_Effect
├── Eye_Assembly
│   ├── Eye_Base
│   ├── Iris
│   ├── Pupil
│   └── Sparkle_Emitters
├── Arms_Group
│   ├── Arm_1 (Right)
│   ├── Arm_2 (Left)
│   └── Arm_3 (Back, optional)
├── Particle_Emitters
│   ├── Emotion_Particles
│   ├── Idle_Particles
│   └── Trail_Particles
└── UI_Elements
    ├── Speech_Bubble
    ├── Status_Indicators
    └── Thought_Bubble
```

### Shader Requirements
- **Rim Lighting**: Soft glow around edges
- **Fresnel Effect**: More opaque at edges
- **Chromatic Aberration**: Slight color shift for holographic feel
- **Bloom**: Gentle glow effect on bright parts

### Performance Considerations
- **LOD 0 (Close)**: Full detail with all particles
- **LOD 1 (Medium)**: Reduced particles, simplified shaders
- **LOD 2 (Far)**: Basic glow orb
- **Culling**: Hide arms when not in use
- **Particle Budget**: Max 20 particles at once

## Behavioral Implementation

### Emotion System
- Tracks current emotion state
- Smooth transitions between emotions
- Emotion affects:
  - Eye shape and pupil size
  - Particle colors and patterns
  - Animation speed
  - Sound pitch

### Trust Level Visual Indicators
| Level | Visual Change |
|-------|--------------|
| 0-20% | Basic appearance, nervous movements |
| 21-40% | Slightly brighter, occasional smiles |
| 41-60% | More expressive, decorative particles |
| 61-80% | Glowing stronger, heart particles |
| 81-100% | Crown/halo effect, maximum sparkles |

## Sound Design Notes
- **Voice**: Soft beeps and chirps (R2-D2 style but cuter)
- **Happy**: Rising melodic tones
- **Sad**: Descending gentle tones
- **Working**: Typewriter/keyboard sounds
- **Success**: Cheerful ding
- **Error**: Soft "uh-oh" sound

## Implementation Phases

### Phase 1: Core Model
- Create base sphere geometry
- Implement eye with basic expressions
- Add glow and transparency effects

### Phase 2: Animations
- Idle animation cycle
- Basic emotion expressions
- Movement behaviors

### Phase 3: Particles & Effects
- Emotion-based particle systems
- Trail effects
- Special ability visualizations

### Phase 4: Polish
- Sound integration
- Advanced shaders
- Performance optimization