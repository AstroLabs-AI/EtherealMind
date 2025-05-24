package com.astrolabs.etherealmind.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class CosmoLevel {
    private int level = 1;
    private int experience = 0;
    private int totalExperience = 0;
    
    // XP requirements per level
    private static final int[] XP_REQUIREMENTS = {
        0,      // Level 1
        100,    // Level 2
        300,    // Level 3
        600,    // Level 4
        1000,   // Level 5
        1500,   // Level 6
        2100,   // Level 7
        2800,   // Level 8
        3600,   // Level 9
        4500    // Level 10 (max)
    };
    
    // Abilities unlocked at each level
    private static final String[] LEVEL_UNLOCKS = {
        "Item Magnet",                    // Level 1
        "Increased Storage",              // Level 2
        "Auto-Deposit",                   // Level 3
        "Enhanced Magnet Range",          // Level 4
        "Healing Aura",                   // Level 5
        "Void Storage (2x capacity)",     // Level 6
        "Teleport Home",                  // Level 7
        "Reality Ripple",                 // Level 8
        "Quantum Storage",                // Level 9
        "Ethereal Mastery"                // Level 10
    };
    
    public void addExperience(int amount, Player player) {
        if (level >= 10) return; // Max level
        
        experience += amount;
        totalExperience += amount;
        
        // Check for level up
        while (level < 10 && experience >= getXpForNextLevel()) {
            experience -= getXpForNextLevel();
            level++;
            onLevelUp(player);
        }
    }
    
    private void onLevelUp(Player player) {
        // Notify player
        player.displayClientMessage(
            Component.literal("✨ COSMO reached Level " + level + "! ✨")
                .withStyle(style -> style.withColor(0xFF00FF).withBold(true)), 
            false
        );
        
        // Show unlock message
        if (level <= LEVEL_UNLOCKS.length) {
            player.displayClientMessage(
                Component.literal("Unlocked: " + LEVEL_UNLOCKS[level - 1])
                    .withStyle(style -> style.withColor(0xFFFF00)), 
                false
            );
        }
        
        // Play level up sound
        player.playSound(net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP, 1.0f, 1.0f);
    }
    
    public int getXpForNextLevel() {
        if (level >= 10) return 0;
        return XP_REQUIREMENTS[level];
    }
    
    public float getXpProgress() {
        if (level >= 10) return 1.0f;
        return (float) experience / getXpForNextLevel();
    }
    
    public int getLevel() {
        return level;
    }
    
    public int getExperience() {
        return experience;
    }
    
    public int getTotalExperience() {
        return totalExperience;
    }
    
    public CompoundTag save(CompoundTag tag) {
        tag.putInt("Level", level);
        tag.putInt("Experience", experience);
        tag.putInt("TotalExperience", totalExperience);
        return tag;
    }
    
    public void load(CompoundTag tag) {
        level = tag.getInt("Level");
        experience = tag.getInt("Experience");
        totalExperience = tag.getInt("TotalExperience");
    }
    
    // Get bonus multipliers based on level
    public float getStorageMultiplier() {
        if (level >= 9) return 4.0f;  // Quantum storage
        if (level >= 6) return 2.0f;  // Void storage
        if (level >= 2) return 1.5f;  // Increased storage
        return 1.0f;
    }
    
    public float getSpeedMultiplier() {
        return 1.0f + (level * 0.1f); // 10% faster per level
    }
    
    public float getParticleMultiplier() {
        return 1.0f + (level * 0.2f); // More particles at higher levels
    }
}