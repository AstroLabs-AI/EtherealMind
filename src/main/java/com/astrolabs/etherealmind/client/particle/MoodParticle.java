package com.astrolabs.etherealmind.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MoodParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private final float rotSpeed;
    private final ParticleType particleType;
    
    public enum ParticleType {
        HAPPY(new float[]{1.0f, 0.87f, 0.0f}, 1.2f, true),      // Gold, larger, sparkles
        CURIOUS(new float[]{0.58f, 0.44f, 0.86f}, 0.8f, false), // Purple, smaller, floats
        ALERT(new float[]{1.0f, 0.27f, 0.0f}, 1.0f, true),      // Orange-red, pulses
        TIRED(new float[]{0.39f, 0.58f, 0.93f}, 0.6f, false),   // Soft blue, slow drift
        EXCITED(new float[]{1.0f, 0.08f, 0.58f}, 1.5f, true),   // Pink, fast movement
        NEUTRAL(new float[]{0.54f, 0.17f, 0.89f}, 1.0f, false); // Blue-violet, standard
        
        public final float[] color;
        public final float sizeMultiplier;
        public final boolean sparkles;
        
        ParticleType(float[] color, float sizeMultiplier, boolean sparkles) {
            this.color = color;
            this.sizeMultiplier = sizeMultiplier;
            this.sparkles = sparkles;
        }
    }
    
    protected MoodParticle(ClientLevel level, double x, double y, double z, 
                          double xSpeed, double ySpeed, double zSpeed,
                          SpriteSet sprites, ParticleType type) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.sprites = sprites;
        this.particleType = type;
        
        // Set particle properties based on mood
        this.lifetime = 20 + random.nextInt(20);
        this.hasPhysics = false;
        this.friction = 0.96f;
        
        // Set color based on particle type
        this.rCol = type.color[0];
        this.gCol = type.color[1];
        this.bCol = type.color[2];
        
        // Size variation
        this.quadSize = (0.05f + random.nextFloat() * 0.05f) * type.sizeMultiplier;
        
        // Rotation speed
        this.rotSpeed = (random.nextFloat() - 0.5f) * 0.1f;
        this.roll = random.nextFloat() * 360.0f;
        
        // Movement pattern based on type
        applyMovementPattern();
        
        this.setSpriteFromAge(sprites);
    }
    
    private void applyMovementPattern() {
        switch (particleType) {
            case HAPPY -> {
                // Upward spiral motion
                this.xd *= 2.0;
                this.yd = Math.abs(this.yd) * 1.5;
                this.zd *= 2.0;
            }
            case CURIOUS -> {
                // Gentle floating motion
                this.xd *= 0.5;
                this.yd *= 0.3;
                this.zd *= 0.5;
            }
            case ALERT -> {
                // Quick, erratic movement
                this.xd *= 3.0;
                this.yd *= 2.0;
                this.zd *= 3.0;
            }
            case TIRED -> {
                // Slow drift downward
                this.xd *= 0.2;
                this.yd = -Math.abs(this.yd) * 0.5;
                this.zd *= 0.2;
            }
            case EXCITED -> {
                // Rapid random movement
                this.xd = (random.nextFloat() - 0.5f) * 0.3f;
                this.yd = (random.nextFloat() - 0.5f) * 0.3f;
                this.zd = (random.nextFloat() - 0.5f) * 0.3f;
            }
        }
    }
    
    @Override
    public void tick() {
        super.tick();
        
        this.oRoll = this.roll;
        this.roll += this.rotSpeed;
        
        // Sparkle effect for certain moods
        if (particleType.sparkles && random.nextInt(3) == 0) {
            this.alpha = 0.5f + random.nextFloat() * 0.5f;
        }
        
        // Special behaviors
        switch (particleType) {
            case HAPPY -> {
                // Spiral upward
                double angle = age * 0.2;
                this.xd = Math.cos(angle) * 0.02;
                this.zd = Math.sin(angle) * 0.02;
            }
            case ALERT -> {
                // Pulse effect
                this.quadSize *= 0.95f + Math.sin(age * 0.5) * 0.05f;
            }
            case CURIOUS -> {
                // Gentle wave motion
                this.xd += Math.sin(age * 0.1) * 0.001;
            }
            case EXCITED -> {
                // Chaotic movement
                if (age % 5 == 0) {
                    this.xd = (random.nextFloat() - 0.5f) * 0.1f;
                    this.zd = (random.nextFloat() - 0.5f) * 0.1f;
                }
            }
        }
        
        this.setSpriteFromAge(sprites);
        
        // Fade out
        if (age > lifetime - 5) {
            this.alpha *= 0.8f;
        }
    }
    
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
    
    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        private final ParticleType type;
        
        public Factory(SpriteSet sprites, ParticleType type) {
            this.sprites = sprites;
            this.type = type;
        }
        
        @Override
        public Particle createParticle(SimpleParticleType particleType, ClientLevel level,
                                     double x, double y, double z,
                                     double xSpeed, double ySpeed, double zSpeed) {
            return new MoodParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites, type);
        }
    }
}