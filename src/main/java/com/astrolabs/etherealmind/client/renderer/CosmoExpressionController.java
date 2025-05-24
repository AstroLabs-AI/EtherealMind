package com.astrolabs.etherealmind.client.renderer;

import com.astrolabs.etherealmind.common.entity.CosmoEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public class CosmoExpressionController {
    
    public enum Expression {
        NORMAL("^_^", "normal"),
        HAPPY(":D", "happy"),
        EXCITED(":D", "excited"),
        THINKING(".-.", "thinking"),
        SLEEPY("z_z", "sleepy"),
        CONFUSED("@_@", "confused"),
        LOVE("♥‿♥", "love"),
        SURPRISED(":o", "surprised"),
        SHY(">.<", "shy");
        
        private final String emoticon;
        private final String animationName;
        
        Expression(String emoticon, String animationName) {
            this.emoticon = emoticon;
            this.animationName = animationName;
        }
        
        public String getEmoticon() {
            return emoticon;
        }
        
        public String getAnimationName() {
            return animationName;
        }
    }
    
    private static final Map<String, Expression> moodToExpression = new HashMap<>();
    static {
        moodToExpression.put("happy", Expression.HAPPY);
        moodToExpression.put("excited", Expression.EXCITED);
        moodToExpression.put("sleepy", Expression.SLEEPY);
        moodToExpression.put("confused", Expression.CONFUSED);
        moodToExpression.put("curious", Expression.THINKING);
        moodToExpression.put("love", Expression.LOVE);
        moodToExpression.put("alert", Expression.SURPRISED);
        moodToExpression.put("sad", Expression.SHY);
    }
    
    private Expression currentExpression = Expression.NORMAL;
    private Expression targetExpression = Expression.NORMAL;
    private float transitionProgress = 1.0f;
    private long lastUpdateTime = 0;
    
    // Interaction tracking
    private long lastPetTime = 0;
    private long lastFeedTime = 0;
    private long lastPlayTime = 0;
    private int consecutiveHappyEvents = 0;
    
    public void update(CosmoEntity entity) {
        long currentTime = System.currentTimeMillis();
        
        // Check for special conditions that override mood
        Expression newExpression = determineExpression(entity, currentTime);
        
        if (newExpression != targetExpression) {
            targetExpression = newExpression;
            transitionProgress = 0.0f;
        }
        
        // Smooth transition between expressions
        if (transitionProgress < 1.0f) {
            float deltaTime = (currentTime - lastUpdateTime) / 1000.0f;
            transitionProgress = Math.min(1.0f, transitionProgress + deltaTime * 2.0f);
            
            if (transitionProgress >= 1.0f) {
                currentExpression = targetExpression;
            }
        }
        
        lastUpdateTime = currentTime;
    }
    
    private Expression determineExpression(CosmoEntity entity, long currentTime) {
        // Check for recent interactions
        if (currentTime - lastPetTime < 3000) {
            return Expression.LOVE;
        }
        
        if (currentTime - lastFeedTime < 5000) {
            return Expression.HAPPY;
        }
        
        if (currentTime - lastPlayTime < 10000) {
            return Expression.EXCITED;
        }
        
        // Check entity state
        Player player = entity.getBoundPlayer();
        if (player != null) {
            double distance = entity.distanceTo(player);
            
            // Very close = shy/love
            if (distance < 2.0) {
                return consecutiveHappyEvents > 3 ? Expression.LOVE : Expression.SHY;
            }
            
            // Being looked at
            Vec3 lookVec = player.getLookAngle();
            Vec3 toEntity = entity.position().subtract(player.position()).normalize();
            double dot = lookVec.dot(toEntity);
            
            if (dot > 0.95) {
                return Expression.SHY;
            }
        }
        
        // Check time of day
        if (entity.level().isNight() && entity.getRandom().nextFloat() < 0.1f) {
            return Expression.SLEEPY;
        }
        
        // Default to mood-based expression
        String mood = entity.getMood();
        return moodToExpression.getOrDefault(mood, Expression.NORMAL);
    }
    
    public void onPetted() {
        lastPetTime = System.currentTimeMillis();
        consecutiveHappyEvents++;
    }
    
    public void onFed() {
        lastFeedTime = System.currentTimeMillis();
        consecutiveHappyEvents++;
    }
    
    public void onPlayed() {
        lastPlayTime = System.currentTimeMillis();
        consecutiveHappyEvents++;
    }
    
    public Expression getCurrentExpression() {
        return currentExpression;
    }
    
    public float getTransitionProgress() {
        return transitionProgress;
    }
    
    public String getEmoticon() {
        if (transitionProgress < 1.0f) {
            // Show transition emoticon
            return "...";
        }
        return currentExpression.getEmoticon();
    }
    
    public boolean shouldShowBlush() {
        return currentExpression == Expression.LOVE || 
               currentExpression == Expression.SHY ||
               (currentExpression == Expression.HAPPY && consecutiveHappyEvents > 2);
    }
    
    public float getBlushIntensity() {
        if (currentExpression == Expression.LOVE) return 1.0f;
        if (currentExpression == Expression.SHY) return 0.8f;
        if (currentExpression == Expression.HAPPY) return 0.5f;
        return 0.0f;
    }
}