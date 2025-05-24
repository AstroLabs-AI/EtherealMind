package com.astrolabs.etherealmind.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CosmicParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    
    protected CosmicParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.sprites = sprites;
        
        this.lifetime = 40 + this.random.nextInt(20);
        this.hasPhysics = false;
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        
        this.quadSize *= 0.75F + random.nextFloat() * 0.5F;
        this.setSpriteFromAge(sprites);
        
        // Random color tint
        float hue = random.nextFloat();
        this.rCol = 0.7F + 0.3F * hue;
        this.gCol = 0.7F + 0.3F * (1 - hue);
        this.bCol = 0.9F + 0.1F * random.nextFloat();
    }
    
    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(sprites);
        
        // Fade out
        if (this.age > this.lifetime - 10) {
            this.alpha = (this.lifetime - this.age) / 10.0F;
        }
        
        // Slight upward drift
        this.yd += 0.001F;
        
        // Orbital motion
        double angle = this.age * 0.1;
        this.xd = Math.cos(angle) * 0.02;
        this.zd = Math.sin(angle) * 0.02;
    }
    
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
    
    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        
        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }
        
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new CosmicParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}