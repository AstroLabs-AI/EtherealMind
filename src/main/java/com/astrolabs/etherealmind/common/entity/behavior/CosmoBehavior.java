package com.astrolabs.etherealmind.common.entity.behavior;

import com.astrolabs.etherealmind.common.entity.CosmoEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class CosmoBehavior {
    private final CosmoEntity cosmo;
    private BehaviorState currentState = BehaviorState.FOLLOWING;
    private BlockPos guardPosition = null;
    private BlockPos patrolCenter = null;
    private int patrolRadius = 10;
    private int stateTimer = 0;
    
    // Visual feedback
    private String lastCommand = "";
    private int feedbackTimer = 0;
    
    public enum BehaviorState {
        FOLLOWING("Following", "♥"),
        STAYING("Staying", "⏸"),
        GUARDING("Guarding", "🛡"),
        PATROLLING("Patrolling", "🔄"),
        COMING("Coming", "→"),
        IDLE("Idle", "💤");
        
        private final String displayName;
        private final String icon;
        
        BehaviorState(String displayName, String icon) {
            this.displayName = displayName;
            this.icon = icon;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public String getIcon() {
            return icon;
        }
    }
    
    public CosmoBehavior(CosmoEntity cosmo) {
        this.cosmo = cosmo;
    }
    
    public void tick() {
        if (cosmo.level().isClientSide) return;
        
        // Update timers
        if (feedbackTimer > 0) feedbackTimer--;
        if (stateTimer > 0) stateTimer--;
        
        Player owner = cosmo.getBoundPlayer();
        if (owner == null) {
            currentState = BehaviorState.IDLE;
            return;
        }
        
        // Execute behavior based on current state
        switch (currentState) {
            case FOLLOWING:
                executeFollowing(owner);
                break;
            case STAYING:
                executeStaying();
                break;
            case GUARDING:
                executeGuarding();
                break;
            case PATROLLING:
                executePatrolling();
                break;
            case COMING:
                executeComing(owner);
                break;
            case IDLE:
                executeIdle();
                break;
        }
        
        // Visual feedback every 10 ticks
        if (cosmo.tickCount % 10 == 0) {
            showStateParticles();
        }
    }
    
    private void executeFollowing(Player owner) {
        // Already handled in CosmoEntity tick, but we can enhance it
        double distance = cosmo.distanceToSqr(owner);
        
        // Adjust follow distance based on activity
        if (distance > 144) { // More than 12 blocks
            teleportToPlayer(owner);
        } else if (distance > 25) { // More than 5 blocks
            moveTowardsPlayer(owner);
        }
    }
    
    private void executeStaying() {
        // Stay in place - disable following
        cosmo.getNavigation().stop();
        
        // Face the owner if nearby
        Player owner = cosmo.getBoundPlayer();
        if (owner != null && cosmo.distanceToSqr(owner) < 100) {
            cosmo.getLookControl().setLookAt(owner);
        }
    }
    
    private void executeGuarding() {
        if (guardPosition == null) {
            guardPosition = cosmo.blockPosition();
        }
        
        // Return to guard position if too far
        double distance = cosmo.distanceToSqr(guardPosition.getX(), guardPosition.getY(), guardPosition.getZ());
        if (distance > 9) { // More than 3 blocks
            cosmo.getNavigation().moveTo(guardPosition.getX() + 0.5, guardPosition.getY(), guardPosition.getZ() + 0.5, 1.0);
        }
        
        // Look around for threats
        if (stateTimer <= 0) {
            stateTimer = 100; // Look around every 5 seconds
            // TODO: Implement threat detection
        }
    }
    
    private void executePatrolling() {
        if (patrolCenter == null) {
            patrolCenter = cosmo.blockPosition();
        }
        
        // Simple patrol pattern
        if (cosmo.getNavigation().isDone()) {
            // Pick random position within patrol radius
            BlockPos target = patrolCenter.offset(
                cosmo.getRandom().nextInt(patrolRadius * 2) - patrolRadius,
                0,
                cosmo.getRandom().nextInt(patrolRadius * 2) - patrolRadius
            );
            
            cosmo.getNavigation().moveTo(target.getX() + 0.5, target.getY(), target.getZ() + 0.5, 0.8);
        }
    }
    
    private void executeComing(Player owner) {
        // Move directly to owner
        cosmo.getNavigation().moveTo(owner, 1.2);
        
        // Switch to following when close enough
        if (cosmo.distanceToSqr(owner) < 4) {
            setState(BehaviorState.FOLLOWING);
        }
    }
    
    private void executeIdle() {
        // Gentle floating animation handled elsewhere
        // Occasionally look at interesting things
        if (stateTimer <= 0) {
            stateTimer = 100 + cosmo.getRandom().nextInt(100);
            // TODO: Look at nearby entities or items
        }
    }
    
    // Command processing
    public void processCommand(String command, Player sender) {
        if (!cosmo.getBoundPlayer().equals(sender)) {
            return; // Only owner can command
        }
        
        String lowerCommand = command.toLowerCase();
        lastCommand = command;
        feedbackTimer = 60; // 3 seconds
        
        // Parse commands
        if (lowerCommand.contains("follow")) {
            setState(BehaviorState.FOLLOWING);
            cosmo.playAcknowledgeSound();
            cosmo.showEmote("❤️");
            showFeedback("Following you! " + BehaviorState.FOLLOWING.getIcon());
            
        } else if (lowerCommand.contains("stay") || lowerCommand.contains("wait")) {
            setState(BehaviorState.STAYING);
            cosmo.playAcknowledgeSound();
            cosmo.showEmote("✋");
            showFeedback("Staying here! " + BehaviorState.STAYING.getIcon());
            
        } else if (lowerCommand.contains("come") || lowerCommand.contains("here")) {
            setState(BehaviorState.COMING);
            cosmo.playAcknowledgeSound();
            cosmo.showEmote("🏃");
            showFeedback("Coming! " + BehaviorState.COMING.getIcon());
            
        } else if (lowerCommand.contains("guard") || lowerCommand.contains("protect")) {
            setState(BehaviorState.GUARDING);
            guardPosition = cosmo.blockPosition();
            cosmo.playAcknowledgeSound();
            cosmo.showEmote("🛡️");
            showFeedback("Guarding this area! " + BehaviorState.GUARDING.getIcon());
            
        } else if (lowerCommand.contains("patrol")) {
            setState(BehaviorState.PATROLLING);
            patrolCenter = cosmo.blockPosition();
            
            // Try to parse radius
            String[] parts = command.split(" ");
            for (int i = 0; i < parts.length - 1; i++) {
                if (parts[i].equalsIgnoreCase("patrol")) {
                    try {
                        patrolRadius = Integer.parseInt(parts[i + 1]);
                        patrolRadius = Math.max(5, Math.min(20, patrolRadius)); // Clamp 5-20
                    } catch (NumberFormatException ignored) {}
                    break;
                }
            }
            
            cosmo.playAcknowledgeSound();
            cosmo.showEmote("🔄");
            showFeedback("Patrolling area (radius: " + patrolRadius + ")! " + BehaviorState.PATROLLING.getIcon());
            
        } else {
            // Unknown command
            cosmo.playConfusedSound();
            cosmo.showEmote("❓");
            showFeedback("I don't understand... ?");
        }
    }
    
    private void showFeedback(String message) {
        // Send feedback to player
        Player owner = cosmo.getBoundPlayer();
        if (owner != null) {
            owner.displayClientMessage(
                net.minecraft.network.chat.Component.literal("§d[COSMO] §f" + message), 
                true
            );
        }
        
        // Also show as speech bubble
        cosmo.showSpeechBubble(message);
    }
    
    public void setState(BehaviorState state) {
        if (this.currentState != state) {
            this.currentState = state;
            this.stateTimer = 0;
            
            // Clear state-specific data when switching
            if (state != BehaviorState.GUARDING) {
                guardPosition = null;
            }
            if (state != BehaviorState.PATROLLING) {
                patrolCenter = null;
            }
        }
    }
    
    public BehaviorState getCurrentState() {
        return currentState;
    }
    
    public String getStateDisplay() {
        return currentState.getDisplayName() + " " + currentState.getIcon();
    }
    
    public boolean hasFeedback() {
        return feedbackTimer > 0;
    }
    
    public String getLastCommand() {
        return lastCommand;
    }
    
    // Save/Load
    public void save(CompoundTag tag) {
        tag.putString("BehaviorState", currentState.name());
        tag.putString("LastCommand", lastCommand);
        tag.putInt("PatrolRadius", patrolRadius);
        
        if (guardPosition != null) {
            tag.putInt("GuardX", guardPosition.getX());
            tag.putInt("GuardY", guardPosition.getY());
            tag.putInt("GuardZ", guardPosition.getZ());
        }
        
        if (patrolCenter != null) {
            tag.putInt("PatrolX", patrolCenter.getX());
            tag.putInt("PatrolY", patrolCenter.getY());
            tag.putInt("PatrolZ", patrolCenter.getZ());
        }
    }
    
    public void load(CompoundTag tag) {
        try {
            currentState = BehaviorState.valueOf(tag.getString("BehaviorState"));
        } catch (Exception e) {
            currentState = BehaviorState.FOLLOWING;
        }
        
        lastCommand = tag.getString("LastCommand");
        patrolRadius = tag.getInt("PatrolRadius");
        
        if (tag.contains("GuardX")) {
            guardPosition = new BlockPos(
                tag.getInt("GuardX"),
                tag.getInt("GuardY"),
                tag.getInt("GuardZ")
            );
        }
        
        if (tag.contains("PatrolX")) {
            patrolCenter = new BlockPos(
                tag.getInt("PatrolX"),
                tag.getInt("PatrolY"),
                tag.getInt("PatrolZ")
            );
        }
    }
    
    private void teleportToPlayer(Player owner) {
        if (owner != null) {
            Vec3 targetPos = owner.position().add(
                cosmo.getRandom().nextGaussian() * 2,
                2,
                cosmo.getRandom().nextGaussian() * 2
            );
            cosmo.teleportTo(targetPos.x, targetPos.y, targetPos.z);
        }
    }
    
    private void moveTowardsPlayer(Player owner) {
        if (owner != null) {
            Vec3 direction = owner.position().subtract(cosmo.position()).normalize();
            cosmo.setDeltaMovement(direction.scale(0.3));
        }
    }
    
    private void showStateParticles() {
        if (!(cosmo.level() instanceof ServerLevel serverLevel)) return;
        
        Vec3 pos = cosmo.position();
        
        switch (currentState) {
            case FOLLOWING -> {
                // Heart particles
                serverLevel.sendParticles(ParticleTypes.HEART,
                    pos.x, pos.y + 1.5, pos.z,
                    1, 0.2, 0.2, 0.2, 0.0);
            }
            case GUARDING -> {
                // Shield-like particles
                serverLevel.sendParticles(ParticleTypes.ENCHANTED_HIT,
                    pos.x, pos.y + 1, pos.z,
                    3, 0.5, 0.5, 0.5, 0.0);
            }
            case PATROLLING -> {
                // Moving particles
                serverLevel.sendParticles(ParticleTypes.END_ROD,
                    pos.x, pos.y + 1, pos.z,
                    2, 0.3, 0.3, 0.3, 0.02);
            }
            case STAYING -> {
                // Stationary particles
                serverLevel.sendParticles(ParticleTypes.SNOWFLAKE,
                    pos.x, pos.y + 1.2, pos.z,
                    1, 0.1, 0.1, 0.1, 0.0);
            }
            case COMING -> {
                // Speedy particles
                serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK,
                    pos.x, pos.y + 1, pos.z,
                    1, 0.2, 0.2, 0.2, 0.0);
            }
            case IDLE -> {
                // Gentle ambient particles
                serverLevel.sendParticles(ParticleTypes.END_ROD,
                    pos.x, pos.y + 1.5, pos.z,
                    1, 0.5, 0.5, 0.5, 0.0);
            }
        }
    }
}