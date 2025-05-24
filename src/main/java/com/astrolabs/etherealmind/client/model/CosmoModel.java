package com.astrolabs.etherealmind.client.model;

import com.astrolabs.etherealmind.EtherealMind;
import com.astrolabs.etherealmind.client.renderer.CosmoAnimationController;
import com.astrolabs.etherealmind.common.entity.CosmoEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CosmoModel extends GeoModel<CosmoEntity> {
    private static final ResourceLocation SIMPLE_MODEL = new ResourceLocation(EtherealMind.MOD_ID, "geo/cosmo_simple.geo.json");
    private static final ResourceLocation DETAILED_MODEL = new ResourceLocation(EtherealMind.MOD_ID, "geo/cosmo_detailed.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(EtherealMind.MOD_ID, "textures/entity/cosmo.png");
    private static final ResourceLocation SIMPLE_ANIMATION = new ResourceLocation(EtherealMind.MOD_ID, "animations/cosmo.animation.json");
    private static final ResourceLocation DETAILED_ANIMATION = new ResourceLocation(EtherealMind.MOD_ID, "animations/cosmo_detailed.animation.json");
    
    @Override
    public ResourceLocation getModelResource(CosmoEntity object) {
        // Use detailed model for higher level COSMOs
        if (object.getLevel() >= 3 || useDetailedModel()) {
            return DETAILED_MODEL;
        }
        return SIMPLE_MODEL;
    }
    
    @Override
    public ResourceLocation getTextureResource(CosmoEntity object) {
        // Always use the main texture to ensure proper loading
        return TEXTURE;
    }
    
    @Override
    public ResourceLocation getAnimationResource(CosmoEntity animatable) {
        // Use detailed animations for higher level COSMOs
        if (animatable.getLevel() >= 3 || useDetailedModel()) {
            return DETAILED_ANIMATION;
        }
        return SIMPLE_ANIMATION;
    }
    
    private boolean useDetailedModel() {
        // TODO: Add client config option for model detail level
        // For now, use simple model to avoid issues
        return false;
    }
}