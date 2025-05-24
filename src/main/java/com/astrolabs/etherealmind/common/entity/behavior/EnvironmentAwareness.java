package com.astrolabs.etherealmind.common.entity.behavior;

import com.astrolabs.etherealmind.common.entity.CosmoEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.stream.Collectors;

public class EnvironmentAwareness {
    private final CosmoEntity cosmo;
    
    // Environmental data
    private long dayTime;
    private boolean isRaining;
    private boolean isThundering;
    private boolean isDaytime;
    private int lightLevel;
    private List<LivingEntity> nearbyHostiles;
    private List<Player> nearbyPlayers;
    private String currentBiome;
    private int updateTimer = 0;
    
    // Awareness ranges
    private static final double HOSTILE_DETECTION_RANGE = 16.0;
    private static final double PLAYER_DETECTION_RANGE = 32.0;
    private static final int UPDATE_INTERVAL = 20; // Update every second
    
    public EnvironmentAwareness(CosmoEntity cosmo) {
        this.cosmo = cosmo;
    }
    
    public void tick() {
        if (cosmo.level().isClientSide) return;
        
        updateTimer++;
        if (updateTimer >= UPDATE_INTERVAL) {
            updateTimer = 0;
            updateEnvironmentalData();
        }
    }
    
    private void updateEnvironmentalData() {
        Level level = cosmo.level();
        BlockPos pos = cosmo.blockPosition();
        
        // Time and weather
        dayTime = level.getDayTime();
        isDaytime = level.isDay();
        isRaining = level.isRaining();
        isThundering = level.isThundering();
        
        // Light level
        lightLevel = level.getBrightness(LightLayer.BLOCK, pos);
        
        // Nearby entities
        AABB searchBox = AABB.ofSize(cosmo.position(), 
            HOSTILE_DETECTION_RANGE * 2, 
            HOSTILE_DETECTION_RANGE * 2, 
            HOSTILE_DETECTION_RANGE * 2);
            
        nearbyHostiles = level.getEntitiesOfClass(LivingEntity.class, searchBox)
            .stream()
            .filter(entity -> entity instanceof Monster)
            .filter(entity -> entity.distanceToSqr(cosmo) <= HOSTILE_DETECTION_RANGE * HOSTILE_DETECTION_RANGE)
            .collect(Collectors.toList());
            
        AABB playerBox = AABB.ofSize(cosmo.position(),
            PLAYER_DETECTION_RANGE * 2,
            PLAYER_DETECTION_RANGE * 2,
            PLAYER_DETECTION_RANGE * 2);
            
        nearbyPlayers = level.getEntitiesOfClass(Player.class, playerBox)
            .stream()
            .filter(player -> player.distanceToSqr(cosmo) <= PLAYER_DETECTION_RANGE * PLAYER_DETECTION_RANGE)
            .collect(Collectors.toList());
            
        // Biome
        currentBiome = level.getBiome(pos).value().toString();
    }
    
    // Context-aware responses based on environment
    public String getEnvironmentalComment() {
        // Weather comments
        if (isThundering) {
            return getRandomComment(new String[]{
                "The storm is intense! ⛈️",
                "Lightning makes me nervous...",
                "We should find shelter!"
            });
        } else if (isRaining) {
            return getRandomComment(new String[]{
                "It's raining! 🌧️",
                "I love the sound of rain.",
                "Everything looks so fresh!"
            });
        }
        
        // Time-based comments
        if (!isDaytime) {
            if (nearbyHostiles != null && !nearbyHostiles.isEmpty()) {
                return getRandomComment(new String[]{
                    "Monsters nearby! Be careful! 👹",
                    "I sense danger... " + nearbyHostiles.size() + " hostiles!",
                    "Stay alert, it's dangerous at night!"
                });
            } else {
                return getRandomComment(new String[]{
                    "The stars are beautiful tonight ✨",
                    "It's so peaceful at night.",
                    "I'll keep watch while you sleep."
                });
            }
        }
        
        // Light level comments
        if (lightLevel < 7) {
            return getRandomComment(new String[]{
                "It's quite dark here... 🌑",
                "Maybe we need some torches?",
                "Monsters could spawn in this darkness!"
            });
        }
        
        // Biome-specific comments
        if (currentBiome != null) {
            if (currentBiome.contains("desert")) {
                return "This desert heat is intense! 🏜️";
            } else if (currentBiome.contains("snow") || currentBiome.contains("frozen")) {
                return "Brrr, it's cold here! ❄️";
            } else if (currentBiome.contains("jungle")) {
                return "So many trees and vines! 🌴";
            } else if (currentBiome.contains("ocean")) {
                return "I love the ocean breeze! 🌊";
            }
        }
        
        // Default pleasant comments
        return getRandomComment(new String[]{
            "What a lovely day for adventure!",
            "I'm happy to be with you! 😊",
            "Let's explore together!"
        });
    }
    
    private String getRandomComment(String[] comments) {
        return comments[cosmo.getRandom().nextInt(comments.length)];
    }
    
    // Alert system for dangers
    public boolean shouldAlertDanger() {
        return nearbyHostiles != null && !nearbyHostiles.isEmpty() && !isDaytime;
    }
    
    public String getDangerAlert() {
        if (nearbyHostiles == null || nearbyHostiles.isEmpty()) {
            return "";
        }
        
        int count = nearbyHostiles.size();
        Entity closest = nearbyHostiles.get(0);
        double distance = closest.distanceTo(cosmo);
        
        if (distance < 8) {
            return "DANGER! " + closest.getName().getString() + " very close! 🚨";
        } else {
            return count + " hostile" + (count > 1 ? "s" : "") + " detected nearby! ⚠️";
        }
    }
    
    // Getters for environmental data
    public boolean isDaytime() { return isDaytime; }
    public boolean isRaining() { return isRaining; }
    public boolean isThundering() { return isThundering; }
    public int getLightLevel() { return lightLevel; }
    public List<LivingEntity> getNearbyHostiles() { return nearbyHostiles; }
    public List<Player> getNearbyPlayers() { return nearbyPlayers; }
    public String getCurrentBiome() { return currentBiome; }
    
    // Special awareness methods
    public boolean isOwnerInDanger() {
        Player owner = cosmo.getBoundPlayer();
        if (owner == null) return false;
        
        // Check if owner is low on health
        if (owner.getHealth() < owner.getMaxHealth() * 0.3f) {
            return true;
        }
        
        // Check if hostile mobs are near the owner
        if (nearbyHostiles != null) {
            for (LivingEntity hostile : nearbyHostiles) {
                if (hostile instanceof net.minecraft.world.entity.Mob mob && mob.getTarget() == owner) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public boolean shouldSuggestSleep() {
        return !isDaytime && nearbyHostiles != null && nearbyHostiles.isEmpty();
    }
}