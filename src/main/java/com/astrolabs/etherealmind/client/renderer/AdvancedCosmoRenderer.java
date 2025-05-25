package com.astrolabs.etherealmind.client.renderer;

import com.astrolabs.etherealmind.EtherealMind;
import com.astrolabs.etherealmind.client.model.CosmoModel;
import com.astrolabs.etherealmind.common.entity.CosmoEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import javax.annotation.Nullable;

public class AdvancedCosmoRenderer extends GeoEntityRenderer<CosmoEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(EtherealMind.MOD_ID, "textures/entity/cosmo_advanced_cute.png");
    private static final ResourceLocation EMISSIVE_TEXTURE = new ResourceLocation(EtherealMind.MOD_ID, "textures/entity/cosmo_advanced_cute.png");
    
    private final CosmoSpeechBubbleRenderer speechBubbleRenderer;
    private float time = 0;
    
    // Glow layer for emissive parts
    private class CosmoGlowLayer extends AutoGlowingGeoLayer<CosmoEntity> {
        public CosmoGlowLayer(GeoEntityRenderer<CosmoEntity> renderer) {
            super(renderer);
        }
        
        @Override
        protected RenderType getRenderType(CosmoEntity animatable) {
            return RenderType.entityTranslucentEmissive(getTextureLocation(animatable));
        }
    }
    
    public AdvancedCosmoRenderer(EntityRendererProvider.Context context) {
        super(context, new CosmoModel());
        this.shadowRadius = 0.0f; // No shadow for ethereal being
        this.speechBubbleRenderer = new CosmoSpeechBubbleRenderer();
        
        // Add glow layer for emissive textures
        addRenderLayer(new CosmoGlowLayer(this));
    }
    
    @Override
    public ResourceLocation getTextureLocation(CosmoEntity entity) {
        // Delegate to the model to get the correct texture (geometric model texture)
        return this.getGeoModel().getTextureResource(entity);
    }
    
    @Override
    public void preRender(PoseStack poseStack, CosmoEntity entity, BakedGeoModel model, 
                         MultiBufferSource bufferSource, VertexConsumer buffer, 
                         boolean isReRender, float partialTick, int packedLight, 
                         int packedOverlay, float red, float green, float blue, float alpha) {
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, 
                       partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        
        // Update time for effects
        time = entity.tickCount + partialTick;
        
        // Scale based on energy level with smooth transitions
        float targetScale = 0.7f + (entity.getEnergyLevel() * 0.5f);
        float currentScale = entity.getRenderScale();
        float smoothScale = currentScale + (targetScale - currentScale) * 0.1f;
        entity.setRenderScale(smoothScale);
        poseStack.scale(smoothScale, smoothScale, smoothScale);
        
        // Add slight rotation based on mood
        if ("happy".equals(entity.getMood())) {
            float wobble = (float)Math.sin(time * 0.1f) * 2.0f;
            poseStack.mulPose(Axis.ZP.rotationDegrees(wobble));
        }
    }
    
    @Override
    public void renderRecursively(PoseStack poseStack, CosmoEntity animatable, GeoBone bone, 
                                 RenderType renderType, MultiBufferSource bufferSource, 
                                 VertexConsumer buffer, boolean isReRender, float partialTick, 
                                 int packedLight, int packedOverlay, float red, float green, 
                                 float blue, float alpha) {
        
        // Custom rendering for specific bones
        if (bone.getName().equals("inner_core")) {
            // Add pulsing glow to inner core
            float pulse = (float)(Math.sin(time * 0.05f) * 0.2f + 0.8f);
            red *= pulse;
            green *= pulse;
            blue *= pulse;
        } else if (bone.getName().startsWith("energy_ring")) {
            // Make energy rings semi-transparent with color based on mood
            alpha = 0.7f;
            int[] colors = getMoodColors(animatable);
            red = colors[0] / 255.0f;
            green = colors[1] / 255.0f;
            blue = colors[2] / 255.0f;
        } else if (bone.getName().equals("antenna_tip")) {
            // Bright antenna tip
            float brightness = (float)(Math.sin(time * 0.2f) * 0.5f + 1.5f);
            red *= brightness;
            green *= brightness;
            blue *= brightness;
        }
        
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, 
                              buffer, isReRender, partialTick, packedLight, packedOverlay, 
                              red, green, blue, alpha);
    }
    
    @Override
    public void render(CosmoEntity entity, float entityYaw, float partialTick, 
                      PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        // Render main entity
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        
        // Only render additional effects if entity is properly visible
        if (entity.isInvisible()) return;
        
        // Render additional effects
        renderHolographicField(entity, partialTick, poseStack, bufferSource, packedLight);
        renderEnergyParticles(entity, partialTick, poseStack, bufferSource);
        renderTrailEffect(entity, partialTick, poseStack, bufferSource);
        
        // Render speech bubble
        speechBubbleRenderer.tick();
        speechBubbleRenderer.render(entity, poseStack, bufferSource, packedLight);
    }
    
    private void renderHolographicField(CosmoEntity entity, float partialTick, 
                                       PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        
        // Holographic field effect
        float fieldScale = 1.5f + (float)Math.sin(time * 0.03f) * 0.1f;
        poseStack.scale(fieldScale, fieldScale, fieldScale);
        
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(TEXTURE));
        Matrix4f matrix = poseStack.last().pose();
        
        int[] colors = getMoodColors(entity);
        float r = colors[0] / 255.0f;
        float g = colors[1] / 255.0f;
        float b = colors[2] / 255.0f;
        float alpha = 0.1f + (float)Math.sin(time * 0.1f) * 0.05f;
        
        // Render holographic sphere
        int segments = 16;
        int rings = 12;
        for (int ring = 0; ring < rings; ring++) {
            float theta1 = (float)(ring * Math.PI / rings);
            float theta2 = (float)((ring + 1) * Math.PI / rings);
            
            for (int seg = 0; seg < segments; seg++) {
                float phi1 = (float)(seg * 2 * Math.PI / segments);
                float phi2 = (float)((seg + 1) * 2 * Math.PI / segments);
                
                // Add distortion
                float distortion = (float)Math.sin(time * 0.05f + theta1 * 3 + phi1 * 2) * 0.05f;
                float radius = 0.8f + distortion;
                
                // Calculate vertices
                Vector3f v1 = new Vector3f(
                    radius * (float)(Math.sin(theta1) * Math.cos(phi1)),
                    radius * (float)Math.cos(theta1),
                    radius * (float)(Math.sin(theta1) * Math.sin(phi1))
                );
                Vector3f v2 = new Vector3f(
                    radius * (float)(Math.sin(theta1) * Math.cos(phi2)),
                    radius * (float)Math.cos(theta1),
                    radius * (float)(Math.sin(theta1) * Math.sin(phi2))
                );
                Vector3f v3 = new Vector3f(
                    radius * (float)(Math.sin(theta2) * Math.cos(phi2)),
                    radius * (float)Math.cos(theta2),
                    radius * (float)(Math.sin(theta2) * Math.sin(phi2))
                );
                Vector3f v4 = new Vector3f(
                    radius * (float)(Math.sin(theta2) * Math.cos(phi1)),
                    radius * (float)Math.cos(theta2),
                    radius * (float)(Math.sin(theta2) * Math.sin(phi1))
                );
                
                // Draw quad
                consumer.vertex(matrix, v1.x, v1.y + 1, v1.z).color(r, g, b, alpha).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(v1.x, v1.y, v1.z).endVertex();
                consumer.vertex(matrix, v2.x, v2.y + 1, v2.z).color(r, g, b, alpha).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(v2.x, v2.y, v2.z).endVertex();
                consumer.vertex(matrix, v3.x, v3.y + 1, v3.z).color(r, g, b, alpha).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(v3.x, v3.y, v3.z).endVertex();
                consumer.vertex(matrix, v4.x, v4.y + 1, v4.z).color(r, g, b, alpha).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(v4.x, v4.y, v4.z).endVertex();
            }
        }
        
        poseStack.popPose();
    }
    
    private void renderEnergyParticles(CosmoEntity entity, float partialTick, 
                                      PoseStack poseStack, MultiBufferSource bufferSource) {
        // Energy particles are handled by the particle system
        // This is just for additional visual effects
        
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(TEXTURE));
        Matrix4f matrix = poseStack.last().pose();
        
        // Render floating energy orbs
        int orbCount = 5;
        for (int i = 0; i < orbCount; i++) {
            float angle = (float)(i * 2 * Math.PI / orbCount) + time * 0.02f;
            float radius = 1.2f + (float)Math.sin(time * 0.05f + i) * 0.2f;
            float height = (float)Math.sin(time * 0.03f + i * 0.5f) * 0.5f;
            
            float x = (float)Math.cos(angle) * radius;
            float z = (float)Math.sin(angle) * radius;
            float y = height + 1.0f;
            
            float size = 0.1f;
            int[] colors = getMoodColors(entity);
            float r = colors[0] / 255.0f;
            float g = colors[1] / 255.0f;
            float b = colors[2] / 255.0f;
            
            // Draw energy orb
            consumer.vertex(matrix, x - size, y - size, z).color(r, g, b, 0.8f).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 0, 1).endVertex();
            consumer.vertex(matrix, x + size, y - size, z).color(r, g, b, 0.8f).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 0, 1).endVertex();
            consumer.vertex(matrix, x + size, y + size, z).color(r, g, b, 0.8f).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 0, 1).endVertex();
            consumer.vertex(matrix, x - size, y + size, z).color(r, g, b, 0.8f).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 0, 1).endVertex();
        }
    }
    
    private void renderTrailEffect(CosmoEntity entity, float partialTick, 
                                   PoseStack poseStack, MultiBufferSource bufferSource) {
        if (entity.getDeltaMovement().length() < 0.01) return; // Only show trail when moving
        
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(TEXTURE));
        Matrix4f matrix = poseStack.last().pose();
        
        Vec3 motion = entity.getDeltaMovement();
        float trailLength = Math.min((float)motion.length() * 5.0f, 2.0f);
        
        int[] colors = getMoodColors(entity);
        float r = colors[0] / 255.0f;
        float g = colors[1] / 255.0f;
        float b = colors[2] / 255.0f;
        
        // Render motion trail
        for (int i = 0; i < 5; i++) {
            float offset = i * 0.2f;
            float alpha = (1.0f - offset / 1.0f) * 0.3f;
            float size = 0.3f * (1.0f - offset / 1.0f);
            
            float x = (float)-motion.x * offset * trailLength;
            float y = (float)-motion.y * offset * trailLength;
            float z = (float)-motion.z * offset * trailLength;
            
            consumer.vertex(matrix, x - size, y - size, z).color(r, g, b, alpha).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 0, 1).endVertex();
            consumer.vertex(matrix, x + size, y - size, z).color(r, g, b, alpha).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 0, 1).endVertex();
            consumer.vertex(matrix, x + size, y + size, z).color(r, g, b, alpha).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 0, 1).endVertex();
            consumer.vertex(matrix, x - size, y + size, z).color(r, g, b, alpha).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 0, 1).endVertex();
        }
    }
    
    private int[] getMoodColors(CosmoEntity entity) {
        String mood = entity.getMood();
        return switch (mood) {
            case "happy" -> new int[]{100, 255, 100}; // Green
            case "sad" -> new int[]{100, 100, 255}; // Blue
            case "excited" -> new int[]{255, 255, 100}; // Yellow
            case "curious" -> new int[]{255, 100, 255}; // Magenta
            case "bored" -> new int[]{150, 150, 150}; // Gray
            case "confused" -> new int[]{255, 150, 100}; // Orange
            default -> new int[]{100, 200, 255}; // Default cyan
        };
    }
    
    @Override
    public RenderType getRenderType(CosmoEntity animatable, ResourceLocation texture, 
                                   @Nullable MultiBufferSource bufferSource, float partialTick) {
        // Use translucent for proper rendering
        return RenderType.entityTranslucent(texture);
    }
    
    @Override
    public int getPackedOverlay(CosmoEntity entity, float u) {
        // Add damage overlay when hurt
        return OverlayTexture.pack(0, entity.hurtTime > 0);
    }
}