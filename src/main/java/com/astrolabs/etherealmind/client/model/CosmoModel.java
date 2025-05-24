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
        // Always use cute model now
        return new ResourceLocation(EtherealMind.MOD_ID, "geo/cosmo_cute.geo.json");
    }
    
    @Override
    public ResourceLocation getTextureResource(CosmoEntity object) {
        // Use cute texture
        return new ResourceLocation(EtherealMind.MOD_ID, "textures/entity/cosmo_cute.png");
    }
    
    @Override
    public ResourceLocation getAnimationResource(CosmoEntity animatable) {
        // Use cute animations
        return new ResourceLocation(EtherealMind.MOD_ID, "animations/cosmo_cute.animation.json");
    }
    
    private boolean useDetailedModel() {
        // TODO: Add client config option for model detail level
        // For now, use simple model to avoid issues
        return false;
    }
}