package com.astrolabs.etherealmind.client.model;

import com.astrolabs.etherealmind.EtherealMind;
import com.astrolabs.etherealmind.common.entity.CosmoEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CosmoModel extends GeoModel<CosmoEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(EtherealMind.MOD_ID, "geo/cosmo_simple.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(EtherealMind.MOD_ID, "textures/entity/cosmo.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(EtherealMind.MOD_ID, "animations/cosmo.animation.json");
    
    @Override
    public ResourceLocation getModelResource(CosmoEntity object) {
        return MODEL;
    }
    
    @Override
    public ResourceLocation getTextureResource(CosmoEntity object) {
        return TEXTURE;
    }
    
    @Override
    public ResourceLocation getAnimationResource(CosmoEntity animatable) {
        return ANIMATION;
    }
}