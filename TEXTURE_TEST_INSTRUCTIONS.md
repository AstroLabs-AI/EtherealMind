# COSMO Texture Visibility Testing

## Current Issue
COSMO entity is not showing its texture in-game. The entity exists (particles work, interactions work) but the main texture is invisible.

## Test Procedure

### 1. Test with Static Texture
Replace the animated texture with a static one:
```bash
# Backup current texture
cp src/main/resources/assets/etherealmind/textures/entity/cosmo.png src/main/resources/assets/etherealmind/textures/entity/cosmo_animated.png

# Use static texture
cp src/main/resources/assets/etherealmind/textures/entity/cosmo_static.png src/main/resources/assets/etherealmind/textures/entity/cosmo.png

# Remove animation metadata
mv src/main/resources/assets/etherealmind/textures/entity/cosmo.png.mcmeta src/main/resources/assets/etherealmind/textures/entity/cosmo.png.mcmeta.bak
```

### 2. Test with Different Renderer
If static texture doesn't work, the issue might be in the GeckoLib renderer setup.

### 3. Check Model File
Verify the model file exists and is valid:
```
src/main/resources/assets/etherealmind/geo/cosmo.geo.json
```

### 4. Debug Rendering
Add more debug logging to CosmoRenderer:
- Log texture loading
- Log model loading
- Log render type used

## Possible Solutions

1. **Wrong texture path**: Check ResourceLocation matches actual file location
2. **Model issue**: GeckoLib model might not be loading correctly
3. **Render type issue**: Entity might need a different render type
4. **Animation issue**: 64x512 animated texture might not work with current setup

## Quick Fix Options

### Option A: Use Simple Static Texture
1. Copy `cosmo_static.png` to `cosmo.png`
2. Remove `cosmo.png.mcmeta`
3. Test in-game

### Option B: Create New Simple Model
1. Use a basic cube model instead of complex GeckoLib model
2. Switch to vanilla renderer temporarily

### Option C: Fix GeckoLib Integration
1. Ensure model file is valid JSON
2. Check texture UV mapping in model
3. Verify GeckoLib version compatibility