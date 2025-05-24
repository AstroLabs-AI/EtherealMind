package com.astrolabs.etherealmind.client.renderer;

import com.astrolabs.etherealmind.EtherealMind;
import com.astrolabs.etherealmind.common.entity.CosmoEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class CosmoGlowLayer extends GeoRenderLayer<CosmoEntity> {
    private static final ResourceLocation GLOW_TEXTURE = new ResourceLocation(EtherealMind.MOD_ID, "textures/entity/cosmo.png");
    
    public CosmoGlowLayer(GeoRenderer<CosmoEntity> entityRenderer) {
        super(entityRenderer);
    }
    
    @Override
    public void render(PoseStack poseStack, CosmoEntity entity, BakedGeoModel bakedModel, 
                      RenderType renderType, MultiBufferSource bufferSource, 
                      VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        // Get mood-based color
        int[] colors = CosmoAnimationController.getParticleColors(entity);
        float r = colors[0] / 255.0f;
        float g = colors[1] / 255.0f;
        float b = colors[2] / 255.0f;
        
        // Pulsing glow effect
        float time = entity.tickCount + partialTick;
        float glowIntensity = 0.5f + (float)Math.sin(time * 0.05f) * 0.3f;
        
        // Render with emissive glow
        RenderType glowRenderType = RenderType.entityTranslucentEmissive(GLOW_TEXTURE);
        VertexConsumer glowBuffer = bufferSource.getBuffer(glowRenderType);
        
        // Render the model with glow
        getRenderer().reRender(bakedModel, poseStack, bufferSource, entity, glowRenderType, 
                               glowBuffer, partialTick, 15728880, // Full brightness
                               packedOverlay, r * glowIntensity, g * glowIntensity, b * glowIntensity, 1.0f);
    }
}