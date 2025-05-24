# COSMO Texture Troubleshooting Guide

## Quick Fix Steps

### 1. Verify Basic Setup
```bash
# Check that texture file exists
ls -la src/main/resources/assets/etherealmind/textures/entity/cosmo.png

# Check file is valid PNG
file src/main/resources/assets/etherealmind/textures/entity/cosmo.png
```

### 2. Test with Simple Texture
Create a simple 64x64 solid color texture:
1. Use any image editor to create a 64x64 purple square
2. Save as `cosmo_test.png` in the same directory
3. Update `CosmoModel.java` to use `"textures/entity/cosmo_test.png"`

### 3. Check In-Game
1. Run the game with logging enabled
2. Spawn COSMO: `/give @p etherealmind:cosmo_spawn_egg`
3. Check logs for:
   - "COSMO render called at position"
   - "COSMO texture requested"
   - Any texture loading errors

### 4. Common Issues & Solutions

#### Issue: Invisible Entity
**Symptoms**: Entity exists but is completely invisible
**Solutions**:
- Change render type to `RenderType.entitySolid(texture)`
- Remove alpha channel from texture
- Check if entity has a hitbox (F3+B)

#### Issue: Pink/Black Texture
**Symptoms**: Shows missing texture pattern
**Solutions**:
- Verify mod ID is "etherealmind" (all lowercase)
- Check texture path has no typos
- Ensure PNG is not corrupted

#### Issue: Transparent Entity
**Symptoms**: Entity is see-through or barely visible
**Solutions**:
- Check texture alpha channel
- Use `RenderType.entityCutout(texture)` for textures with transparency
- Verify texture isn't mostly transparent pixels

### 5. Advanced Debugging

Add to `CosmoRenderer.java`:
```java
@Override
public void render(CosmoEntity entity, float entityYaw, float partialTick, 
                  PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
    // Test with solid color cube
    VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entitySolid(getTextureLocation(entity)));
    poseStack.pushPose();
    // Render a purple cube at entity position
    renderColoredCube(poseStack, vertexConsumer, 1.0f, 0.0f, 1.0f, 1.0f);
    poseStack.popPose();
    
    // Then render normally
    super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
}
```

### 6. Texture Requirements
- Format: PNG with RGBA
- Size: Powers of 2 (64x64, 128x128, etc.)
- Location: `assets/etherealmind/textures/entity/`
- No spaces or special characters in filename

### 7. If All Else Fails
1. Copy a working entity texture from vanilla Minecraft
2. Rename it to `cosmo.png`
3. Place in the correct directory
4. This confirms the rendering pipeline works

## Expected Results
When working correctly:
- COSMO appears as a purple/cosmic entity
- Has visible texture (not pink/black)
- Shows particle effects
- Responds to interactions

## Still Having Issues?
Check:
1. Forge version compatibility
2. GeckoLib version compatibility
3. Other mods that might interfere with rendering
4. Graphics drivers and settings