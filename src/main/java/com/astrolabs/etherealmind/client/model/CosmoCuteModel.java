package com.astrolabs.etherealmind.client.model;

import com.astrolabs.etherealmind.EtherealMind;
import com.astrolabs.etherealmind.common.entity.CosmoEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class CosmoCuteModel extends GeoModel<CosmoEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(EtherealMind.MOD_ID, "geo/cosmo_cute.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(EtherealMind.MOD_ID, "textures/entity/cosmo_cute.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(EtherealMind.MOD_ID, "animations/cosmo_cute.animation.json");
    
    @Override
    public ResourceLocation getModelResource(CosmoEntity entity) {
        return MODEL;
    }
    
    @Override
    public ResourceLocation getTextureResource(CosmoEntity entity) {
        return TEXTURE;
    }
    
    @Override
    public ResourceLocation getAnimationResource(CosmoEntity entity) {
        return ANIMATION;
    }
    
    @Override
    public void setCustomAnimations(CosmoEntity entity, long instanceId, AnimationState<CosmoEntity> animationState) {
        super.setCustomAnimations(entity, instanceId, animationState);
        
        CoreGeoBone head = getAnimationProcessor().getBone("body");
        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            
            // Make COSMO look at the player
            if (entity.getBoundPlayer() != null) {
                double d0 = entity.getBoundPlayer().getX() - entity.getX();
                double d1 = entity.getBoundPlayer().getY() - entity.getY();
                double d2 = entity.getBoundPlayer().getZ() - entity.getZ();
                
                float f = (float)(Mth.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
                float f1 = (float)(-(Mth.atan2(d1, Math.sqrt(d0 * d0 + d2 * d2)) * (180D / Math.PI)));
                
                head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
                head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            }
        }
        
        // Pupil tracking
        CoreGeoBone pupil = getAnimationProcessor().getBone("pupil");
        if (pupil != null && entity.getBoundPlayer() != null) {
            // Calculate where to look
            double dx = entity.getBoundPlayer().getX() - entity.getX();
            double dz = entity.getBoundPlayer().getZ() - entity.getZ();
            double angle = Math.atan2(dz, dx);
            
            // Move pupil slightly based on look direction
            float offsetX = (float)(Math.cos(angle) * 0.5);
            float offsetY = (float)(Math.sin(angle) * 0.5);
            
            pupil.setPosX(offsetX);
            pupil.setPosY(offsetY);
        }
        
        // Dynamic expression based on mood
        updateExpression(entity);
    }
    
    private void updateExpression(CosmoEntity entity) {
        String mood = entity.getMood();
        CoreGeoBone eyeBase = getAnimationProcessor().getBone("eye_base");
        CoreGeoBone sparkle1 = getAnimationProcessor().getBone("sparkle_1");
        CoreGeoBone sparkle2 = getAnimationProcessor().getBone("sparkle_2");
        CoreGeoBone sparkle3 = getAnimationProcessor().getBone("sparkle_3");
        CoreGeoBone sparkle4 = getAnimationProcessor().getBone("sparkle_4");
        
        if (eyeBase == null) return;
        
        // Reset to defaults
        eyeBase.setScaleY(1.0f);
        
        // Apply mood-specific expressions
        switch (mood) {
            case "happy" -> {
                // ^_^ expression - squint eyes
                eyeBase.setScaleY(0.3f);
                showSparkles(sparkle1, sparkle2, sparkle3, sparkle4);
            }
            case "excited" -> {
                // :D expression - wide open
                eyeBase.setScaleY(1.2f);
                showSparkles(sparkle1, sparkle2, sparkle3, sparkle4);
            }
            case "sleepy" -> {
                // -_- expression - half closed
                eyeBase.setScaleY(0.5f);
                hideSparkles(sparkle1, sparkle2, sparkle3, sparkle4);
            }
            case "confused" -> {
                // @_@ expression - slightly tilted
                eyeBase.setRotZ(0.1f);
                hideSparkles(sparkle1, sparkle2, sparkle3, sparkle4);
            }
            case "love" -> {
                // ♥‿♥ expression
                eyeBase.setScaleY(0.7f);
                showSparkles(sparkle1, sparkle2, sparkle3, sparkle4);
            }
            default -> {
                // Normal expression
                eyeBase.setScaleY(1.0f);
                hideSparkles(sparkle1, sparkle2, sparkle3, sparkle4);
            }
        }
    }
    
    private void showSparkles(CoreGeoBone... sparkles) {
        for (CoreGeoBone sparkle : sparkles) {
            if (sparkle != null) {
                sparkle.setHidden(false);
                // Add twinkle animation
                float time = System.currentTimeMillis() % 1000 / 1000f;
                sparkle.setScaleX(0.5f + 0.5f * Mth.sin(time * Mth.TWO_PI));
                sparkle.setScaleY(0.5f + 0.5f * Mth.sin(time * Mth.TWO_PI));
            }
        }
    }
    
    private void hideSparkles(CoreGeoBone... sparkles) {
        for (CoreGeoBone sparkle : sparkles) {
            if (sparkle != null) {
                sparkle.setHidden(true);
            }
        }
    }
}