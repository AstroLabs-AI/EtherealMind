package com.astrolabs.etherealmind.client.model;

import com.astrolabs.etherealmind.EtherealMind;
import com.astrolabs.etherealmind.client.renderer.CosmoAnimationController;
import com.astrolabs.etherealmind.common.entity.CosmoEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CosmoModel extends GeoModel<CosmoEntity> {
    // Legacy models (kept for compatibility)
    private static final ResourceLocation SIMPLE_MODEL = new ResourceLocation(EtherealMind.MOD_ID, "geo/cosmo_simple.geo.json");
    private static final ResourceLocation DETAILED_MODEL = new ResourceLocation(EtherealMind.MOD_ID, "geo/cosmo_detailed.geo.json");
    private static final ResourceLocation CUTE_MODEL = new ResourceLocation(EtherealMind.MOD_ID, "geo/cosmo_cute.geo.json");
    
    // Advanced model resources
    private static final ResourceLocation ADVANCED_MODEL = new ResourceLocation(EtherealMind.MOD_ID, "geo/cosmo_advanced_cute.geo.json");
    private static final ResourceLocation ADVANCED_TEXTURE = new ResourceLocation(EtherealMind.MOD_ID, "textures/entity/cosmo_advanced_cute.png");
    private static final ResourceLocation ADVANCED_ANIMATION = new ResourceLocation(EtherealMind.MOD_ID, "animations/cosmo_advanced.animation.json");
    
    // Legacy textures
    private static final ResourceLocation TEXTURE = new ResourceLocation(EtherealMind.MOD_ID, "textures/entity/cosmo.png");
    private static final ResourceLocation CUTE_TEXTURE = new ResourceLocation(EtherealMind.MOD_ID, "textures/entity/cosmo_cute.png");
    private static final ResourceLocation SIMPLE_ANIMATION = new ResourceLocation(EtherealMind.MOD_ID, "animations/cosmo.animation.json");
    private static final ResourceLocation CUTE_ANIMATION = new ResourceLocation(EtherealMind.MOD_ID, "animations/cosmo_cute.animation.json");
    private static final ResourceLocation DETAILED_ANIMATION = new ResourceLocation(EtherealMind.MOD_ID, "animations/cosmo_detailed.animation.json");
    
    @Override
    public ResourceLocation getModelResource(CosmoEntity object) {
        // Use improved model with 3D arms and smaller size
        ResourceLocation improvedModel = new ResourceLocation(EtherealMind.MOD_ID, "geo/cosmo_improved.geo.json");
        EtherealMind.LOGGER.info("Loading COSMO model: " + improvedModel.toString());
        return improvedModel;
    }
    
    @Override
    public ResourceLocation getTextureResource(CosmoEntity object) {
        // Use the improved texture with 3D arm textures
        ResourceLocation improvedTexture = new ResourceLocation(EtherealMind.MOD_ID, "textures/entity/cosmo_improved.png");
        EtherealMind.LOGGER.info("Loading COSMO texture: " + improvedTexture.toString());
        return improvedTexture;
    }
    
    @Override
    public ResourceLocation getAnimationResource(CosmoEntity animatable) {
        // Use cute animations that match the model
        return CUTE_ANIMATION;
    }
    
    private boolean useDetailedModel() {
        // TODO: Add client config option for model detail level
        // For now, use simple model to avoid issues
        return false;
    }
}