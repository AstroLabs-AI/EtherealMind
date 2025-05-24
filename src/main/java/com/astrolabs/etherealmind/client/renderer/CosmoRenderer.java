package com.astrolabs.etherealmind.client.renderer;

import com.astrolabs.etherealmind.EtherealMind;
import com.astrolabs.etherealmind.client.model.CosmoModel;
import com.astrolabs.etherealmind.common.entity.CosmoEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CosmoRenderer extends GeoEntityRenderer<CosmoEntity> {
    private static final ResourceLocation TEXTURE = EtherealMind.id("textures/entity/cosmo.png");
    
    public CosmoRenderer(EntityRendererProvider.Context context) {
        super(context, new CosmoModel());
        this.shadowRadius = 0.0f; // No shadow for ethereal being
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
        // Render main entity
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        
        // Render additional layers
        renderEventHorizon(entity, partialTick, poseStack, bufferSource, packedLight);
        renderVoidCenter(entity, partialTick, poseStack, bufferSource, packedLight);
        renderParticles(entity, partialTick, poseStack, bufferSource);
    }
    
    private void renderEventHorizon(CosmoEntity entity, float partialTick, 
                                   PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        
        // Rotation animation
        float rotation = (entity.tickCount + partialTick) * 2.0f;
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(rotation));
        
        // TODO: Render event horizon ring
        
        poseStack.popPose();
    }
    
    private void renderVoidCenter(CosmoEntity entity, float partialTick, 
                                 PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        // TODO: Render void center with shader effects
    }
    
    private void renderParticles(CosmoEntity entity, float partialTick, 
                               PoseStack poseStack, MultiBufferSource bufferSource) {
        // Particle rendering handled by particle system
    }
    
    @Override
    public ResourceLocation getTextureLocation(CosmoEntity entity) {
        return TEXTURE;
    }
    
    @Override
    public RenderType getRenderType(CosmoEntity animatable, ResourceLocation texture, 
                                   MultiBufferSource bufferSource, float partialTick) {
        // Use emissive render type for glowing effect
        return RenderType.entityTranslucentEmissive(texture);
    }
    
    @Override
    public int getPackedOverlay(CosmoEntity entity, float u) {
        // Make entity glow
        return 0xF000F0;
    }
}