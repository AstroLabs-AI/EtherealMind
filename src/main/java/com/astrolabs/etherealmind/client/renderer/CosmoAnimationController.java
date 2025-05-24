package com.astrolabs.etherealmind.client.renderer;

import com.astrolabs.etherealmind.EtherealMind;
import com.astrolabs.etherealmind.common.entity.CosmoEntity;
import net.minecraft.resources.ResourceLocation;

public class CosmoAnimationController {
    // Animation frames for different states
    private static final int IDLE_FRAMES = 8;
    private static final int HAPPY_FRAMES = 12;
    private static final int CURIOUS_FRAMES = 10;
    private static final int ALERT_FRAMES = 6;
    private static final int TIRED_FRAMES = 4;
    
    // Frame timing
    private static final int TICKS_PER_FRAME = 4;
    
    // Texture paths for different moods
    private static final String TEXTURE_BASE = "textures/entity/cosmo/";
    
    public static class AnimationState {
        public final String mood;
        public final int totalFrames;
        public final int currentFrame;
        public final float interpolation;
        
        public AnimationState(String mood, int totalFrames, int currentFrame, float interpolation) {
            this.mood = mood;
            this.totalFrames = totalFrames;
            this.currentFrame = currentFrame;
            this.interpolation = interpolation;
        }
    }
    
    public static AnimationState getAnimationState(CosmoEntity entity, float partialTick) {
        String mood = entity.getMood();
        int tickCount = entity.tickCount;
        
        // Determine frame count based on mood
        int frameCount = switch (mood) {
            case "happy" -> HAPPY_FRAMES;
            case "curious" -> CURIOUS_FRAMES;
            case "alert" -> ALERT_FRAMES;
            case "tired" -> TIRED_FRAMES;
            default -> IDLE_FRAMES;
        };
        
        // Calculate current frame with smooth interpolation
        float exactFrame = (tickCount + partialTick) / TICKS_PER_FRAME;
        int currentFrame = ((int) exactFrame) % frameCount;
        float interpolation = exactFrame % 1.0f;
        
        return new AnimationState(mood, frameCount, currentFrame, interpolation);
    }
    
    public static ResourceLocation getTextureForFrame(String mood, int frame) {
        // Use mood-specific texture sheets
        String textureName = String.format("%scosmo_%s_frame%d.png", TEXTURE_BASE, mood.toLowerCase(), frame);
        return new ResourceLocation(EtherealMind.MOD_ID, textureName);
    }
    
    public static ResourceLocation getAnimatedTexture(CosmoEntity entity, float partialTick) {
        AnimationState state = getAnimationState(entity, partialTick);
        
        // Use detailed texture for higher level COSMOs
        if (entity.getLevel() >= 3) {
            return new ResourceLocation(EtherealMind.MOD_ID, "textures/entity/cosmo_detailed.png");
        }
        
        // Return mood-specific texture if available
        String moodTexture = String.format("textures/entity/cosmo_%s.png", state.mood.toLowerCase());
        ResourceLocation moodLocation = new ResourceLocation(EtherealMind.MOD_ID, moodTexture);
        
        // Fall back to default animated texture
        return new ResourceLocation(EtherealMind.MOD_ID, "textures/entity/cosmo.png");
    }
    
    public static float[] getUVOffset(CosmoEntity entity, float partialTick) {
        AnimationState state = getAnimationState(entity, partialTick);
        
        // Calculate UV coordinates for texture atlas (8 frames vertically)
        float frameHeight = 1.0f / 8.0f;
        float vOffset = state.currentFrame * frameHeight;
        
        // Add slight offset based on mood for variety
        float uOffset = switch (state.mood) {
            case "happy" -> 0.1f * (float) Math.sin(entity.tickCount * 0.1f);
            case "curious" -> 0.05f * (float) Math.cos(entity.tickCount * 0.15f);
            default -> 0.0f;
        };
        
        return new float[] { uOffset, vOffset, frameHeight };
    }
    
    // Get particle color based on mood and animation state
    public static int[] getParticleColors(CosmoEntity entity) {
        String mood = entity.getMood();
        return switch (mood) {
            case "happy" -> new int[] { 255, 223, 0 }; // Gold
            case "curious" -> new int[] { 147, 112, 219 }; // Medium purple
            case "alert" -> new int[] { 255, 69, 0 }; // Red-orange
            case "tired" -> new int[] { 100, 149, 237 }; // Cornflower blue
            case "excited" -> new int[] { 255, 20, 147 }; // Deep pink
            default -> new int[] { 138, 43, 226 }; // Blue violet
        };
    }
}