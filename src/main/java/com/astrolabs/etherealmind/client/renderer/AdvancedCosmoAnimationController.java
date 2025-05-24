package com.astrolabs.etherealmind.client.renderer;

import com.astrolabs.etherealmind.common.entity.CosmoEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import java.util.Random;

public class AdvancedCosmoAnimationController {
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.cosmo.idle_float");
    private static final RawAnimation MOVE_ANIM = RawAnimation.begin().thenLoop("animation.cosmo.move");
    private static final RawAnimation WAVE_ANIM = RawAnimation.begin().thenPlay("animation.cosmo.wave_hello");
    private static final RawAnimation THUMBS_UP_ANIM = RawAnimation.begin().thenPlay("animation.cosmo.thumbs_up");
    private static final RawAnimation THINKING_ANIM = RawAnimation.begin().thenPlay("animation.cosmo.thinking");
    private static final RawAnimation EXCITED_ANIM = RawAnimation.begin().thenPlay("animation.cosmo.excited_jump");
    private static final RawAnimation SAD_ANIM = RawAnimation.begin().thenPlay("animation.cosmo.sad");
    private static final RawAnimation SCANNING_ANIM = RawAnimation.begin().thenLoop("animation.cosmo.scanning");
    private static final RawAnimation STORAGE_ANIM = RawAnimation.begin().thenPlay("animation.cosmo.storage_access");
    private static final RawAnimation COMBAT_ANIM = RawAnimation.begin().thenPlay("animation.cosmo.combat_mode");
    private static final RawAnimation TELEPORT_ANIM = RawAnimation.begin().thenPlay("animation.cosmo.teleport_charge");
    private static final RawAnimation BLINK_ANIM = RawAnimation.begin().thenPlay("animation.cosmo.blink");
    private static final RawAnimation HAPPY_ANIM = RawAnimation.begin().thenLoop("animation.cosmo.happy");
    
    private final Random random = new Random();
    private long lastBlinkTime = 0;
    private long lastGestureTime = 0;
    private long lastInteractionTime = 0;
    
    public static <T extends GeoEntity> PlayState handleAnimations(AnimationState<T> state) {
        if (!(state.getAnimatable() instanceof CosmoEntity cosmo)) {
            return PlayState.STOP;
        }
        
        AnimationController<T> controller = state.getController();
        AdvancedCosmoAnimationController advController = new AdvancedCosmoAnimationController();
        
        // Handle blinking
        advController.handleBlinking(cosmo, controller, state);
        
        // Handle eye tracking
        advController.handleEyeTracking(cosmo);
        
        // Priority-based animation selection
        if (cosmo.isTeleporting()) {
            controller.setAnimation(TELEPORT_ANIM);
            return PlayState.CONTINUE;
        }
        
        if (cosmo.getCombatAssistant().isCombatModeEnabled()) {
            controller.setAnimation(COMBAT_ANIM);
            return PlayState.CONTINUE;
        }
        
        if (cosmo.isAccessingStorage()) {
            controller.setAnimation(STORAGE_ANIM);
            return PlayState.CONTINUE;
        }
        
        if (cosmo.isScanning()) {
            controller.setAnimation(SCANNING_ANIM);
            return PlayState.CONTINUE;
        }
        
        // Mood-based animations
        String mood = cosmo.getMood();
        switch (mood) {
            case "excited" -> {
                if (advController.shouldPlayGesture()) {
                    controller.setAnimation(EXCITED_ANIM);
                    return PlayState.CONTINUE;
                }
            }
            case "sad" -> {
                controller.setAnimation(SAD_ANIM);
                return PlayState.CONTINUE;
            }
            case "happy" -> {
                if (advController.shouldPlayGesture() && advController.random.nextBoolean()) {
                    controller.setAnimation(THUMBS_UP_ANIM);
                } else {
                    controller.setAnimation(HAPPY_ANIM);
                }
                return PlayState.CONTINUE;
            }
            case "curious" -> {
                if (advController.shouldPlayGesture()) {
                    controller.setAnimation(THINKING_ANIM);
                    return PlayState.CONTINUE;
                }
            }
        }
        
        // Movement-based animations
        Vec3 velocity = cosmo.getDeltaMovement();
        boolean isMoving = velocity.horizontalDistance() > 0.01;
        
        if (isMoving) {
            controller.setAnimation(MOVE_ANIM);
        } else {
            // Idle with occasional gestures
            if (advController.shouldPlayIdleGesture(cosmo)) {
                int gesture = advController.random.nextInt(3);
                switch (gesture) {
                    case 0 -> controller.setAnimation(WAVE_ANIM);
                    case 1 -> controller.setAnimation(THINKING_ANIM);
                    case 2 -> controller.setAnimation(THUMBS_UP_ANIM);
                }
            } else {
                controller.setAnimation(IDLE_ANIM);
            }
        }
        
        return PlayState.CONTINUE;
    }
    
    private void handleBlinking(CosmoEntity cosmo, AnimationController<?> controller, AnimationState<?> state) {
        long currentTime = cosmo.level().getGameTime();
        
        // Blink every 3-7 seconds
        if (currentTime - lastBlinkTime > 60 + random.nextInt(80)) {
            lastBlinkTime = currentTime;
            // Queue blink animation to play over current animation
            controller.tryTriggerAnimation("animation.cosmo.blink");
        }
    }
    
    private void handleEyeTracking(CosmoEntity cosmo) {
        // Find nearest player
        Player nearestPlayer = cosmo.level().getNearestPlayer(cosmo, 16.0);
        if (nearestPlayer != null) {
            // Calculate look direction
            Vec3 toPlayer = nearestPlayer.getEyePosition().subtract(cosmo.getEyePosition());
            double distance = toPlayer.length();
            
            if (distance < 16.0) {
                // Update eye bone positions for tracking
                // This would be done through custom bone manipulation
                float yaw = (float)Math.atan2(toPlayer.x, toPlayer.z);
                float pitch = (float)Math.asin(toPlayer.y / distance);
                
                // Store in entity for renderer to use
                cosmo.setEyeTarget(yaw, pitch);
            }
        }
    }
    
    private boolean shouldPlayGesture() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastGestureTime > 5000) { // 5 second cooldown
            lastGestureTime = currentTime;
            return random.nextFloat() < 0.3f; // 30% chance
        }
        return false;
    }
    
    private boolean shouldPlayIdleGesture(CosmoEntity cosmo) {
        // More likely to gesture when player is nearby
        Player nearestPlayer = cosmo.level().getNearestPlayer(cosmo, 8.0);
        if (nearestPlayer != null) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastInteractionTime > 10000) { // 10 second cooldown
                lastInteractionTime = currentTime;
                return random.nextFloat() < 0.4f; // 40% chance when player nearby
            }
        }
        return random.nextFloat() < 0.05f; // 5% chance otherwise
    }
    
    // Animation registration is now handled in CosmoEntity.registerControllers()
    
    // Physics-based procedural animations
    public static void applyPhysicsAnimations(CosmoEntity entity) {
        // Antenna physics
        Vec3 velocity = entity.getDeltaMovement();
        float velocityMagnitude = (float)velocity.length();
        
        // Apply physics to antenna segments
        float antennaLag = velocityMagnitude * 10.0f;
        entity.setAntennaRotation(
            -antennaLag * (float)velocity.x,
            0,
            -antennaLag * (float)velocity.z
        );
        
        // Ear fin physics
        float earFlopAmount = Math.min(velocityMagnitude * 20.0f, 30.0f);
        entity.setEarFinRotation(0, 0, earFlopAmount);
        
        // Hover engine glow based on movement
        float engineGlow = 0.5f + Math.min(velocityMagnitude * 2.0f, 0.5f);
        entity.setEngineGlow(engineGlow);
    }
    
    // Smooth animation transitions
    public static class AnimationTransitionManager {
        private String currentAnimation = "";
        private String targetAnimation = "";
        private float transitionProgress = 0;
        private final float transitionSpeed = 0.1f;
        
        public void transitionTo(String newAnimation) {
            if (!newAnimation.equals(currentAnimation)) {
                targetAnimation = newAnimation;
                transitionProgress = 0;
            }
        }
        
        public void update() {
            if (!targetAnimation.isEmpty() && !targetAnimation.equals(currentAnimation)) {
                transitionProgress += transitionSpeed;
                if (transitionProgress >= 1.0f) {
                    currentAnimation = targetAnimation;
                    transitionProgress = 0;
                }
            }
        }
        
        public float getBlendWeight() {
            return transitionProgress;
        }
    }
}