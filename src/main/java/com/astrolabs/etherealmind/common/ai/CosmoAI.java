package com.astrolabs.etherealmind.common.ai;

import com.astrolabs.etherealmind.common.entity.CosmoEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CosmoAI {
    private final CosmoEntity cosmo;
    private final PersonalityMatrix personality;
    private final MemoryBank memories;
    private final Random random = new Random();
    
    // Learning data
    private final Map<String, Integer> playerActions = new HashMap<>();
    private String favoriteActivity = "exploring";
    private int trustLevel = 0;
    
    public CosmoAI(CosmoEntity cosmo) {
        this.cosmo = cosmo;
        this.personality = new PersonalityMatrix();
        this.memories = new MemoryBank();
    }
    
    public void update() {
        // Update mood based on recent events
        updateMood();
        
        // Adjust personality based on playtime
        long playtime = cosmo.getPlaytime();
        personality.evolve(playtime);
        
        // Energy management
        float currentEnergy = cosmo.getEnergyLevel();
        if (currentEnergy < 0.3f) {
            cosmo.setMood("tired");
        }
    }
    
    private void updateMood() {
        // Mood calculation based on various factors
        if (cosmo.getBoundPlayer() != null) {
            Player player = cosmo.getBoundPlayer();
            double distance = cosmo.distanceToSqr(player);
            
            if (distance < 10) {
                adjustMood("happy", 0.1f);
            } else if (distance > 100) {
                adjustMood("lonely", 0.05f);
            }
        }
    }
    
    private void adjustMood(String targetMood, float weight) {
        // Smooth mood transitions
        String currentMood = cosmo.getMood();
        if (!currentMood.equals(targetMood) && random.nextFloat() < weight) {
            cosmo.setMood(targetMood);
        }
    }
    
    public void onFirstMeeting(Player player) {
        cosmo.setMood("excited");
        memories.addMemory("first_meeting", player.getName().getString());
        // TODO: Send greeting message to player
    }
    
    public void onInteraction(Player player) {
        trustLevel++;
        cosmo.setMood("happy");
        personality.increaseHelpfulness();
    }
    
    public void onStrangerInteraction(Player player) {
        cosmo.setMood("cautious");
        // TODO: Send message that COSMO is bound to another player
    }
    
    public void observePlayerAction(String action) {
        playerActions.merge(action, 1, Integer::sum);
        
        // Update favorite activity
        playerActions.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .ifPresent(entry -> favoriteActivity = entry.getKey());
    }
    
    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.put("Personality", personality.save());
        tag.put("Memories", memories.save());
        tag.putInt("TrustLevel", trustLevel);
        tag.putString("FavoriteActivity", favoriteActivity);
        
        // Save player actions
        CompoundTag actionsTag = new CompoundTag();
        playerActions.forEach(actionsTag::putInt);
        tag.put("PlayerActions", actionsTag);
        
        return tag;
    }
    
    public void load(CompoundTag tag) {
        personality.load(tag.getCompound("Personality"));
        memories.load(tag.getCompound("Memories"));
        trustLevel = tag.getInt("TrustLevel");
        favoriteActivity = tag.getString("FavoriteActivity");
        
        // Load player actions
        CompoundTag actionsTag = tag.getCompound("PlayerActions");
        playerActions.clear();
        for (String key : actionsTag.getAllKeys()) {
            playerActions.put(key, actionsTag.getInt(key));
        }
    }
    
    public static class PersonalityMatrix {
        private float curiosity = 0.7f;
        private float playfulness = 0.6f;
        private float helpfulness = 0.8f;
        private float wisdom = 0.2f;
        
        public void evolve(long playtime) {
            // Wisdom increases with playtime
            wisdom = Math.min(1.0f, 0.2f + (playtime / 360000f)); // Max at 5 hours
            
            // Other traits fluctuate
            curiosity = 0.7f + (float)Math.sin(playtime * 0.0001) * 0.2f;
        }
        
        public void increaseHelpfulness() {
            helpfulness = Math.min(1.0f, helpfulness + 0.01f);
        }
        
        public CompoundTag save() {
            CompoundTag tag = new CompoundTag();
            tag.putFloat("Curiosity", curiosity);
            tag.putFloat("Playfulness", playfulness);
            tag.putFloat("Helpfulness", helpfulness);
            tag.putFloat("Wisdom", wisdom);
            return tag;
        }
        
        public void load(CompoundTag tag) {
            curiosity = tag.getFloat("Curiosity");
            playfulness = tag.getFloat("Playfulness");
            helpfulness = tag.getFloat("Helpfulness");
            wisdom = tag.getFloat("Wisdom");
        }
    }
    
    public static class MemoryBank {
        private final Map<String, String> memories = new HashMap<>();
        
        public void addMemory(String key, String value) {
            memories.put(key, value);
        }
        
        public String getMemory(String key) {
            return memories.getOrDefault(key, "");
        }
        
        public CompoundTag save() {
            CompoundTag tag = new CompoundTag();
            memories.forEach(tag::putString);
            return tag;
        }
        
        public void load(CompoundTag tag) {
            memories.clear();
            for (String key : tag.getAllKeys()) {
                memories.put(key, tag.getString(key));
            }
        }
    }
}