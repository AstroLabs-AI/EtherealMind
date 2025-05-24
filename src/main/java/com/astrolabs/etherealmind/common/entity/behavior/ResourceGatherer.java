package com.astrolabs.etherealmind.common.entity.behavior;

import com.astrolabs.etherealmind.common.entity.CosmoEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.stream.Collectors;

public class ResourceGatherer {
    private final CosmoEntity cosmo;
    
    // Gathering settings
    private boolean autoCollectEnabled = true;
    private boolean cropHarvestEnabled = false;
    private boolean experienceCollectEnabled = true;
    private boolean sortItemsEnabled = true;
    
    // Gathering state
    private int gatherCooldown = 0;
    private int harvestCooldown = 0;
    private final Set<BlockPos> recentlyHarvested = new HashSet<>();
    
    // Gathering ranges
    private static final double ITEM_COLLECT_RANGE = 8.0;
    private static final int CROP_HARVEST_RANGE = 6;
    private static final double XP_COLLECT_RANGE = 10.0;
    
    // Statistics
    private int itemsCollected = 0;
    private int cropsHarvested = 0;
    private int xpCollected = 0;
    
    public ResourceGatherer(CosmoEntity cosmo) {
        this.cosmo = cosmo;
    }
    
    public void tick() {
        if (cosmo.level().isClientSide) return;
        
        Player owner = cosmo.getBoundPlayer();
        if (owner == null) return;
        
        // Update cooldowns
        if (gatherCooldown > 0) gatherCooldown--;
        if (harvestCooldown > 0) harvestCooldown--;
        
        // Clean up old harvest positions
        if (cosmo.tickCount % 200 == 0) {
            recentlyHarvested.clear();
        }
        
        // Perform gathering tasks
        if (autoCollectEnabled && gatherCooldown == 0) {
            collectNearbyItems(owner);
        }
        
        if (cropHarvestEnabled && harvestCooldown == 0 && cosmo.getLevel() >= 3) {
            harvestNearbyCrops(owner);
        }
        
        if (experienceCollectEnabled) {
            collectExperience(owner);
        }
        
        // Periodic status update
        if (cosmo.tickCount % 1200 == 0 && itemsCollected > 0) {
            cosmo.showSpeechBubble("Collected " + itemsCollected + " items so far! 📦");
            cosmo.showEmote("✨");
        }
    }
    
    private void collectNearbyItems(Player owner) {
        Level level = cosmo.level();
        Vec3 pos = cosmo.position();
        
        // Find items to collect
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class,
            AABB.ofSize(pos, ITEM_COLLECT_RANGE * 2, ITEM_COLLECT_RANGE, ITEM_COLLECT_RANGE * 2))
            .stream()
            .filter(item -> !item.hasPickUpDelay())
            .filter(item -> item.distanceToSqr(cosmo) <= ITEM_COLLECT_RANGE * ITEM_COLLECT_RANGE)
            .sorted(Comparator.comparing(item -> item.distanceToSqr(cosmo)))
            .collect(Collectors.toList());
            
        if (items.isEmpty()) return;
        
        // Collect items
        int collected = 0;
        for (ItemEntity itemEntity : items) {
            ItemStack stack = itemEntity.getItem();
            
            // Try to store in COSMO's storage
            ItemStack remaining = cosmo.getStorage().insertItem(stack, false);
            
            if (remaining.isEmpty()) {
                // Fully collected
                itemEntity.discard();
                collected++;
                itemsCollected++;
                
                // Effect at item location
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.PORTAL,
                        itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(),
                        5, 0.1, 0.1, 0.1, 0.02);
                }
            } else if (remaining.getCount() < stack.getCount()) {
                // Partially collected
                itemEntity.setItem(remaining);
                collected++;
            }
            
            // Limit items per tick
            if (collected >= 5) break;
        }
        
        if (collected > 0) {
            // Visual feedback
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.END_ROD,
                    cosmo.getX(), cosmo.getY() + 1, cosmo.getZ(),
                    10, 0.3, 0.3, 0.3, 0.05);
            }
            
            // Sound
            cosmo.playSound(SoundEvents.ITEM_PICKUP, 0.5f, 1.2f);
            
            // Cooldown based on items collected
            gatherCooldown = 10 + collected * 2;
            
            // Occasional comment
            if (cosmo.getRandom().nextFloat() < 0.1f) {
                String[] comments = {
                    "Got it! 📦",
                    "Collected!",
                    "Stored safely! ✨",
                    "Another one for the collection!"
                };
                cosmo.showSpeechBubble(comments[cosmo.getRandom().nextInt(comments.length)]);
            }
        }
    }
    
    private void harvestNearbyCrops(Player owner) {
        Level level = cosmo.level();
        BlockPos cosmoPos = cosmo.blockPosition();
        
        // Find mature crops
        List<BlockPos> matureCrops = new ArrayList<>();
        
        for (int x = -CROP_HARVEST_RANGE; x <= CROP_HARVEST_RANGE; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -CROP_HARVEST_RANGE; z <= CROP_HARVEST_RANGE; z++) {
                    BlockPos checkPos = cosmoPos.offset(x, y, z);
                    
                    if (recentlyHarvested.contains(checkPos)) continue;
                    
                    BlockState state = level.getBlockState(checkPos);
                    Block block = state.getBlock();
                    
                    // Check if it's a mature crop
                    if (block instanceof CropBlock cropBlock) {
                        if (cropBlock.isMaxAge(state)) {
                            matureCrops.add(checkPos);
                        }
                    } else if (isMatureCrop(block, state)) {
                        matureCrops.add(checkPos);
                    }
                }
            }
        }
        
        if (matureCrops.isEmpty()) return;
        
        // Sort by distance
        matureCrops.sort(Comparator.comparing(pos -> pos.distSqr(cosmoPos)));
        
        // Harvest closest crop
        BlockPos harvestPos = matureCrops.get(0);
        BlockState harvestState = level.getBlockState(harvestPos);
        Block harvestBlock = harvestState.getBlock();
        
        if (level instanceof ServerLevel serverLevel) {
            // Break the crop
            List<ItemStack> drops = Block.getDrops(harvestState, serverLevel, harvestPos, null, owner, ItemStack.EMPTY);
            
            // Collect drops
            for (ItemStack drop : drops) {
                ItemStack remaining = cosmo.getStorage().insertItem(drop, false);
                if (!remaining.isEmpty()) {
                    // Drop what couldn't be stored
                    Block.popResource(level, harvestPos, remaining);
                }
            }
            
            // Replant if it's a crop
            if (harvestBlock instanceof CropBlock) {
                level.setBlock(harvestPos, harvestBlock.defaultBlockState(), 3);
                
                // Use a seed from storage if available
                ItemStack seed = getSeedForCrop(harvestBlock);
                if (!seed.isEmpty()) {
                    cosmo.getStorage().extractItem(seed, 1, false);
                }
            } else {
                level.setBlock(harvestPos, Blocks.AIR.defaultBlockState(), 3);
            }
            
            // Effects
            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                harvestPos.getX() + 0.5, harvestPos.getY() + 0.5, harvestPos.getZ() + 0.5,
                10, 0.3, 0.3, 0.3, 0.0);
                
            serverLevel.playSound(null, harvestPos,
                SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 0.5f, 1.0f);
        }
        
        recentlyHarvested.add(harvestPos);
        cropsHarvested++;
        harvestCooldown = 20; // 1 second
        
        if (cropsHarvested % 10 == 0) {
            cosmo.showSpeechBubble("Harvesting crops! 🌾");
            cosmo.showEmote("🌱");
        }
    }
    
    private boolean isMatureCrop(Block block, BlockState state) {
        // Check for other harvestable blocks
        if (block == Blocks.PUMPKIN || block == Blocks.MELON) {
            return true;
        }
        if (block == Blocks.SUGAR_CANE) {
            // Only harvest if there's another sugar cane below
            return state.getValue(net.minecraft.world.level.block.SugarCaneBlock.AGE) == 15;
        }
        if (block == Blocks.BAMBOO) {
            return state.getValue(net.minecraft.world.level.block.BambooStalkBlock.LEAVES) != 
                   net.minecraft.world.level.block.state.properties.BambooLeaves.NONE;
        }
        return false;
    }
    
    private ItemStack getSeedForCrop(Block crop) {
        if (crop == Blocks.WHEAT) return new ItemStack(Items.WHEAT_SEEDS);
        if (crop == Blocks.CARROTS) return new ItemStack(Items.CARROT);
        if (crop == Blocks.POTATOES) return new ItemStack(Items.POTATO);
        if (crop == Blocks.BEETROOTS) return new ItemStack(Items.BEETROOT_SEEDS);
        return ItemStack.EMPTY;
    }
    
    private void collectExperience(Player owner) {
        Level level = cosmo.level();
        Vec3 pos = cosmo.position();
        
        // Find XP orbs
        List<ExperienceOrb> orbs = level.getEntitiesOfClass(ExperienceOrb.class,
            AABB.ofSize(pos, XP_COLLECT_RANGE * 2, XP_COLLECT_RANGE, XP_COLLECT_RANGE * 2))
            .stream()
            .filter(orb -> orb.distanceToSqr(cosmo) <= XP_COLLECT_RANGE * XP_COLLECT_RANGE)
            .collect(Collectors.toList());
            
        for (ExperienceOrb orb : orbs) {
            // Pull XP to owner
            Vec3 motion = owner.position().subtract(orb.position()).normalize().scale(0.4);
            orb.setDeltaMovement(motion);
            
            // Visual trail
            if (level instanceof ServerLevel serverLevel && cosmo.tickCount % 2 == 0) {
                serverLevel.sendParticles(ParticleTypes.ENCHANT,
                    orb.getX(), orb.getY(), orb.getZ(),
                    2, 0.1, 0.1, 0.1, 0.0);
            }
            
            xpCollected++;
        }
    }
    
    // Command processing
    public void processGatherCommand(String command, Player player) {
        String lowerCommand = command.toLowerCase();
        
        if (lowerCommand.contains("collect") || lowerCommand.contains("gather")) {
            if (lowerCommand.contains("stop") || lowerCommand.contains("disable")) {
                setAutoCollectEnabled(false);
                cosmo.showSpeechBubble("Auto-collection disabled.");
            } else {
                setAutoCollectEnabled(true);
                cosmo.showSpeechBubble("I'll collect nearby items! 📦");
                cosmo.showEmote("🧲");
            }
        } else if (lowerCommand.contains("harvest") || lowerCommand.contains("farm")) {
            if (cosmo.getLevel() < 3) {
                cosmo.showSpeechBubble("I need to be level 3 to harvest crops!");
                return;
            }
            
            if (lowerCommand.contains("stop") || lowerCommand.contains("disable")) {
                setCropHarvestEnabled(false);
                cosmo.showSpeechBubble("Crop harvesting disabled.");
            } else {
                setCropHarvestEnabled(true);
                cosmo.showSpeechBubble("I'll help harvest crops! 🌾");
                cosmo.showEmote("🌱");
            }
        }
    }
    
    // Settings
    public void setAutoCollectEnabled(boolean enabled) {
        this.autoCollectEnabled = enabled;
    }
    
    public void setCropHarvestEnabled(boolean enabled) {
        this.cropHarvestEnabled = enabled;
    }
    
    public void setExperienceCollectEnabled(boolean enabled) {
        this.experienceCollectEnabled = enabled;
    }
    
    public void setSortItemsEnabled(boolean enabled) {
        this.sortItemsEnabled = enabled;
    }
    
    // Getters
    public boolean isAutoCollectEnabled() { return autoCollectEnabled; }
    public boolean isCropHarvestEnabled() { return cropHarvestEnabled; }
    public int getItemsCollected() { return itemsCollected; }
    public int getCropsHarvested() { return cropsHarvested; }
    public int getXpCollected() { return xpCollected; }
}