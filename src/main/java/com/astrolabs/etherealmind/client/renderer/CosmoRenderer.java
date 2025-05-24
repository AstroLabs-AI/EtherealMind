package com.astrolabs.etherealmind.client.renderer;

import com.astrolabs.etherealmind.EtherealMind;
import com.astrolabs.etherealmind.client.model.CosmoModel;
import com.astrolabs.etherealmind.common.entity.CosmoEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import org.joml.Matrix4f;
import javax.annotation.Nullable;

public class CosmoRenderer extends GeoEntityRenderer<CosmoEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(EtherealMind.MOD_ID, "textures/entity/cosmo_cute.png");
    private static final ResourceLocation ANIMATED_TEXTURE = new ResourceLocation(EtherealMind.MOD_ID, "textures/entity/cosmo_animated.png");
    private final CosmoSpeechBubbleRenderer speechBubbleRenderer;
    
    public CosmoRenderer(EntityRendererProvider.Context context) {
        super(context, new CosmoModel());
        this.shadowRadius = 0.0f; // No shadow for ethereal being
        this.speechBubbleRenderer = new CosmoSpeechBubbleRenderer();
        // Don't add glow layer yet - fix basic rendering first
    }
    
    @Override
    public ResourceLocation getTextureLocation(CosmoEntity entity) {
        // Delegate to the model to get the correct texture
        return this.getGeoModel().getTextureResource(entity);
    }
    
    @Override
    public void preRender(PoseStack poseStack, CosmoEntity entity, BakedGeoModel model, 
                         MultiBufferSource bufferSource, VertexConsumer buffer, 
                         boolean isReRender, float partialTick, int packedLight, 
                         int packedOverlay, float red, float green, float blue, float alpha) {
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, 
                       partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        
        // Scale based on energy level
        float scale = 0.8f + (entity.getEnergyLevel() * 0.4f);
        poseStack.scale(scale, scale, scale);
    }
    
    @Override
    public void render(CosmoEntity entity, float entityYaw, float partialTick, 
                      PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        // Render main entity first with proper transparency
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        
        // Only render additional effects if entity is properly visible
        if (entity.isInvisible()) return;
        
        // Render additional layers with reduced complexity
        // Temporarily disable extra effects to debug texture issue
        // renderEventHorizon(entity, partialTick, poseStack, bufferSource, packedLight);
        // renderVoidCenter(entity, partialTick, poseStack, bufferSource, packedLight);
        // renderParticles(entity, partialTick, poseStack, bufferSource);
        
        // Render speech bubble
        speechBubbleRenderer.tick();
        speechBubbleRenderer.render(entity, poseStack, bufferSource, packedLight);
    }
    
    private void renderEventHorizon(CosmoEntity entity, float partialTick, 
                                   PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        
        // Event horizon ring effect with reality distortion
        float time = entity.tickCount + partialTick;
        float rotation = time * 2.0f;
        float pulseScale = 1.0f + (float)Math.sin(time * 0.05f) * 0.1f;
        
        poseStack.scale(pulseScale, pulseScale, pulseScale);
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(rotation));
        
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(TEXTURE));
        Matrix4f matrix = poseStack.last().pose();
        
        // Get mood colors - temporarily use fixed colors for debugging
        // int[] colors = CosmoAnimationController.getParticleColors(entity);
        float r = 0.5f; // colors[0] / 255.0f;
        float g = 0.3f; // colors[1] / 255.0f;
        float b = 0.8f; // colors[2] / 255.0f;
        
        // Render distortion ring segments
        int segments = 16;
        float radius = 0.6f;
        for (int i = 0; i < segments; i++) {
            float angle = (float)(i * Math.PI * 2.0 / segments);
            float nextAngle = (float)((i + 1) * Math.PI * 2.0 / segments);
            
            // Add wave distortion
            float distortion = (float)Math.sin(time * 0.1f + angle * 3) * 0.1f;
            float r1 = radius + distortion;
            float r2 = radius + (float)Math.sin(time * 0.1f + nextAngle * 3) * 0.1f;
            
            float x1 = (float)Math.cos(angle) * r1;
            float z1 = (float)Math.sin(angle) * r1;
            float x2 = (float)Math.cos(nextAngle) * r2;
            float z2 = (float)Math.sin(nextAngle) * r2;
            
            float alpha = 0.3f + (float)Math.sin(time * 0.1f + angle) * 0.2f;
            
            // Render quad
            consumer.vertex(matrix, x1, -0.1f, z1).color(r, g, b, alpha).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 1, 0).endVertex();
            consumer.vertex(matrix, x2, -0.1f, z2).color(r, g, b, alpha).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 1, 0).endVertex();
            consumer.vertex(matrix, x2, 0.1f, z2).color(r, g, b, alpha * 0.5f).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 1, 0).endVertex();
            consumer.vertex(matrix, x1, 0.1f, z1).color(r, g, b, alpha * 0.5f).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 1, 0).endVertex();
        }
        
        poseStack.popPose();
    }
    
    private void renderVoidCenter(CosmoEntity entity, float partialTick, 
                                 PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        // Void center with reality warping effect
        float time = entity.tickCount + partialTick;
        
        poseStack.pushPose();
        
        // Pulsing void scale
        float voidScale = 0.2f + (float)Math.sin(time * 0.03f) * 0.05f;
        poseStack.scale(voidScale, voidScale, voidScale);
        
        // Counter-rotate for stability effect
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-time));
        poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(time * 0.7f));
        
        // Use translucent emissive instead of end portal to avoid random blocks
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(TEXTURE));
        Matrix4f matrix = poseStack.last().pose();
        
        // Simple void cube with distortion
        float size = 1.0f;
        float warp = (float)Math.sin(time * 0.05f) * 0.1f;
        
        // Front face
        consumer.vertex(matrix, -size - warp, -size, size).color(0.1f, 0.0f, 0.2f, 0.9f).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 0, 1).endVertex();
        consumer.vertex(matrix, size + warp, -size, size).color(0.1f, 0.0f, 0.2f, 0.9f).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 0, 1).endVertex();
        consumer.vertex(matrix, size, size + warp, size).color(0.0f, 0.0f, 0.1f, 0.9f).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 0, 1).endVertex();
        consumer.vertex(matrix, -size, size - warp, size).color(0.0f, 0.0f, 0.1f, 0.9f).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 0, 1).endVertex();
        
        poseStack.popPose();
    }
    
    private void renderParticles(CosmoEntity entity, float partialTick, 
                               PoseStack poseStack, MultiBufferSource bufferSource) {
        // Particle rendering handled by particle system
    }
    
    
    @Override
    public RenderType getRenderType(CosmoEntity animatable, ResourceLocation texture, 
                                   @Nullable MultiBufferSource bufferSource, float partialTick) {
        // Use cutout for solid rendering without transparency
        return RenderType.entityCutoutNoCull(texture);
    }
    
    @Override
    public int getPackedOverlay(CosmoEntity entity, float u) {
        // Use default overlay
        return super.getPackedOverlay(entity, u);
    }
}