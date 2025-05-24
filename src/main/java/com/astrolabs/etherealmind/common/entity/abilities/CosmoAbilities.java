package com.astrolabs.etherealmind.common.entity.abilities;

import com.astrolabs.etherealmind.common.entity.CosmoEntity;
import com.astrolabs.etherealmind.common.storage.DimensionalStorage;
import com.astrolabs.etherealmind.common.storage.ItemCategorizer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CosmoAbilities {
    private final CosmoEntity cosmo;
    
    // Ability cooldowns (in ticks)
    private int itemMagnetCooldown = 0;
    private int autoDepositCooldown = 0;
    private int teleportHomeCooldown = 0;
    private int healingAuraCooldown = 0;
    
    // Ability settings
    private boolean itemMagnetEnabled = true;
    private boolean autoDepositEnabled = false;
    private boolean healingAuraEnabled = false;
    private float magnetRange = 8.0f;
    
    // Home position for teleportation
    private BlockPos homePos = null;
    
    // Cooldowns for fetch commands
    private final Map<String, Long> cooldowns = new HashMap<>();
    
    public CosmoAbilities(CosmoEntity cosmo) {
        this.cosmo = cosmo;
    }
    
    public void tick() {
        // Update cooldowns
        if (itemMagnetCooldown > 0) itemMagnetCooldown--;
        if (autoDepositCooldown > 0) autoDepositCooldown--;
        if (teleportHomeCooldown > 0) teleportHomeCooldown--;
        if (healingAuraCooldown > 0) healingAuraCooldown--;
        
        Player owner = cosmo.getBoundPlayer();
        if (owner == null || cosmo.level().isClientSide) return;
        
        // Execute abilities based on COSMO's level
        int level = cosmo.getLevel();
        
        if (level >= 1 && itemMagnetEnabled && itemMagnetCooldown == 0) {
            performItemMagnet(owner);
        }
        
        if (level >= 3 && autoDepositEnabled && autoDepositCooldown == 0) {
            performAutoDeposit(owner);
        }
        
        if (level >= 5 && healingAuraEnabled && healingAuraCooldown == 0) {
            performHealingAura(owner);
        }
    }
    
    // Level 1 Ability: Item Magnet
    private void performItemMagnet(Player owner) {
        Level level = cosmo.level();
        Vec3 pos = cosmo.position();
        
        // Find items and experience orbs
        AABB searchBox = new AABB(pos.x - magnetRange, pos.y - magnetRange, pos.z - magnetRange,
                                  pos.x + magnetRange, pos.y + magnetRange, pos.z + magnetRange);
        
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, searchBox);
        List<ExperienceOrb> xpOrbs = level.getEntitiesOfClass(ExperienceOrb.class, searchBox);
        
        // Pull items toward player
        for (ItemEntity item : items) {
            if (item.hasPickUpDelay()) continue;
            
            Vec3 motion = owner.position().subtract(item.position()).normalize().scale(0.3);
            item.setDeltaMovement(motion);
            
            // Sparkle effect
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.END_ROD, 
                    item.getX(), item.getY() + 0.5, item.getZ(), 
                    1, 0, 0, 0, 0.02);
            }
        }
        
        // Pull XP orbs
        for (ExperienceOrb xp : xpOrbs) {
            Vec3 motion = owner.position().subtract(xp.position()).normalize().scale(0.4);
            xp.setDeltaMovement(motion);
        }
        
        if (!items.isEmpty() || !xpOrbs.isEmpty()) {
            itemMagnetCooldown = 5; // Small cooldown to prevent lag
        }
    }
    
    // Level 3 Ability: Auto Deposit
    private void performAutoDeposit(Player owner) {
        // Check for items near the player
        AABB pickupBox = owner.getBoundingBox().inflate(2.0);
        List<ItemEntity> nearbyItems = owner.level().getEntitiesOfClass(ItemEntity.class, pickupBox);
        
        for (ItemEntity itemEntity : nearbyItems) {
            if (itemEntity.hasPickUpDelay()) continue;
            
            ItemStack stack = itemEntity.getItem();
            ItemStack remaining = cosmo.getStorage().insertItem(stack, false);
            
            if (remaining.isEmpty()) {
                // Item fully deposited
                itemEntity.discard();
                
                // Effect
                if (owner.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.PORTAL,
                        itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(),
                        10, 0.2, 0.2, 0.2, 0.05);
                }
                
                // Sound
                owner.level().playSound(null, itemEntity.blockPosition(), 
                    SoundEvents.ENDERMAN_TELEPORT, SoundSource.NEUTRAL, 0.5f, 1.5f);
            } else if (remaining.getCount() < stack.getCount()) {
                // Partially deposited
                itemEntity.setItem(remaining);
            }
        }
        
        if (!nearbyItems.isEmpty()) {
            autoDepositCooldown = 10;
        }
    }
    
    // Level 5 Ability: Healing Aura
    private void performHealingAura(Player owner) {
        if (owner.getHealth() < owner.getMaxHealth()) {
            owner.heal(0.5f); // Heal half heart every second
            
            // Healing particles
            if (owner.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.HEART,
                    owner.getX(), owner.getY() + 1, owner.getZ(),
                    2, 0.5, 0.5, 0.5, 0.1);
            }
            
            healingAuraCooldown = 20; // Once per second
        }
    }
    
    // Level 7 Ability: Teleport Home
    public void teleportHome(Player owner) {
        if (teleportHomeCooldown > 0 || homePos == null) return;
        
        // Teleport effect at start position
        if (owner.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL,
                owner.getX(), owner.getY() + 1, owner.getZ(),
                50, 0.5, 1, 0.5, 0.1);
        }
        
        // Teleport
        owner.teleportTo(homePos.getX() + 0.5, homePos.getY(), homePos.getZ() + 0.5);
        
        // Teleport effect at destination
        if (owner.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL,
                homePos.getX() + 0.5, homePos.getY() + 1, homePos.getZ() + 0.5,
                50, 0.5, 1, 0.5, 0.1);
        }
        
        // Sound
        owner.level().playSound(null, owner.blockPosition(), 
            SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
        
        teleportHomeCooldown = 1200; // 1 minute cooldown
    }
    
    // Setters for abilities
    public void setItemMagnetEnabled(boolean enabled) {
        this.itemMagnetEnabled = enabled;
    }
    
    public void setAutoDepositEnabled(boolean enabled) {
        this.autoDepositEnabled = enabled;
    }
    
    public void setHealingAuraEnabled(boolean enabled) {
        this.healingAuraEnabled = enabled;
    }
    
    public void setHomePos(BlockPos pos) {
        this.homePos = pos;
    }
    
    public void setMagnetRange(float range) {
        this.magnetRange = Math.min(16.0f, Math.max(1.0f, range));
    }
    
    // Getters
    public boolean isItemMagnetEnabled() { return itemMagnetEnabled; }
    public boolean isAutoDepositEnabled() { return autoDepositEnabled; }
    public boolean isHealingAuraEnabled() { return healingAuraEnabled; }
    public BlockPos getHomePos() { return homePos; }
    public float getMagnetRange() { return magnetRange; }
    
    public int getTeleportHomeCooldown() { return teleportHomeCooldown; }
    
    // Fetch item ability for chat commands
    public boolean fetchItem(String itemName, Player owner) {
        if (owner == null) return false;
        
        // Check cooldown
        if (cooldowns.getOrDefault("fetch", 0L) > System.currentTimeMillis()) {
            return false;
        }
        
        // Check if it's a category request
        ItemCategorizer.ItemCategory category = ItemCategorizer.getCategoryFromAlias(itemName);
        if (category != null) {
            return fetchItemByCategory(category, owner);
        }
        
        // Search for item in COSMO's storage
        DimensionalStorage storage = cosmo.getStorage();
        ItemStack foundItem = ItemStack.EMPTY;
        
        // Search through storage pages
        for (int page = 0; page < storage.getTotalPages(); page++) {
            for (int slot = 0; slot < 54; slot++) {
                ItemStack stack = storage.getItem(page, slot);
                if (!stack.isEmpty() && stack.getHoverName().getString().toLowerCase().contains(itemName.toLowerCase())) {
                    foundItem = stack.copy();
                    storage.setItem(page, slot, ItemStack.EMPTY);
                    break;
                }
            }
            if (!foundItem.isEmpty()) break;
        }
        
        // If found, give to player
        if (!foundItem.isEmpty()) {
            if (!owner.addItem(foundItem)) {
                // Drop item if inventory full
                owner.drop(foundItem, false);
            }
            cooldowns.put("fetch", System.currentTimeMillis() + 3000); // 3 second cooldown
            return true;
        }
        
        return false;
    }
    
    // Fetch items by category
    private boolean fetchItemByCategory(ItemCategorizer.ItemCategory category, Player owner) {
        DimensionalStorage storage = cosmo.getStorage();
        List<ItemStack> categoryItems = new ArrayList<>();
        
        // Find all items in the category
        for (int page = 0; page < storage.getTotalPages(); page++) {
            for (int slot = 0; slot < 54; slot++) {
                ItemStack stack = storage.getItem(page, slot);
                if (!stack.isEmpty() && ItemCategorizer.itemMatchesCategory(stack, category)) {
                    categoryItems.add(stack.copy());
                    storage.setItem(page, slot, ItemStack.EMPTY);
                    break; // Just fetch one item for now
                }
            }
            if (!categoryItems.isEmpty()) break;
        }
        
        // Give items to player
        if (!categoryItems.isEmpty()) {
            for (ItemStack item : categoryItems) {
                if (!owner.addItem(item)) {
                    owner.drop(item, false);
                }
            }
            cooldowns.put("fetch", System.currentTimeMillis() + 3000);
            
            // Show what was fetched
            if (cosmo.level() instanceof ServerLevel serverLevel) {
                cosmo.showSpeechBubble("Found " + category.getName() + "! " + category.getIcon());
            }
            
            return true;
        }
        
        return false;
    }
}