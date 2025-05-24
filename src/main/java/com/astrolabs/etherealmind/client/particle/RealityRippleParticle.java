package com.astrolabs.etherealmind.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class RealityRippleParticle extends TextureSheetParticle {
    private final float maxScale;
    
    protected RealityRippleParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z, 0, 0, 0);
        
        this.lifetime = 20;
        this.hasPhysics = false;
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        
        this.maxScale = 2.0F + random.nextFloat() * 2.0F;
        this.quadSize = 0.1F;
        
        // Ethereal blue-purple color
        this.rCol = 0.4F + random.nextFloat() * 0.2F;
        this.gCol = 0.6F + random.nextFloat() * 0.2F;
        this.bCol = 0.9F + random.nextFloat() * 0.1F;
        this.alpha = 0.0F;
    }
    
    @Override
    public void tick() {
        super.tick();
        
        float progress = (float) this.age / (float) this.lifetime;
        
        // Expand outward
        this.quadSize = this.maxScale * progress;
        
        // Fade in then out
        if (progress < 0.3F) {
            this.alpha = progress / 0.3F * 0.8F;
        } else {
            this.alpha = (1.0F - progress) * 0.8F;
        }
    }
    
    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        // Custom rendering for a flat, expanding ring effect
        Vector3f cameraPos = camera.getPosition().toVector3f();
        float x = (float)(Mth.lerp(partialTicks, this.xo, this.x) - cameraPos.x());
        float y = (float)(Mth.lerp(partialTicks, this.yo, this.y) - cameraPos.y());
        float z = (float)(Mth.lerp(partialTicks, this.zo, this.z) - cameraPos.z());
        
        Quaternionf quaternion = new Quaternionf();
        quaternion.rotationXYZ(0, 0, 0); // Face up
        
        Vector3f[] vertices = new Vector3f[]{
            new Vector3f(-1.0F, 0.0F, -1.0F),
            new Vector3f(-1.0F, 0.0F, 1.0F),
            new Vector3f(1.0F, 0.0F, 1.0F),
            new Vector3f(1.0F, 0.0F, -1.0F)
        };
        
        float size = this.getQuadSize(partialTicks);
        
        for(int i = 0; i < 4; ++i) {
            Vector3f vertex = vertices[i];
            vertex.rotate(quaternion);
            vertex.mul(size);
            vertex.add(x, y, z);
        }
        
        float u0 = this.getU0();
        float u1 = this.getU1();
        float v0 = this.getV0();
        float v1 = this.getV1();
        int light = this.getLightColor(partialTicks);
        
        buffer.vertex(vertices[0].x(), vertices[0].y(), vertices[0].z()).uv(u1, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        buffer.vertex(vertices[1].x(), vertices[1].y(), vertices[1].z()).uv(u1, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        buffer.vertex(vertices[2].x(), vertices[2].y(), vertices[2].z()).uv(u0, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        buffer.vertex(vertices[3].x(), vertices[3].y(), vertices[3].z()).uv(u0, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
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
            RealityRippleParticle particle = new RealityRippleParticle(level, x, y, z);
            particle.pickSprite(this.sprites);
            return particle;
        }
    }
}