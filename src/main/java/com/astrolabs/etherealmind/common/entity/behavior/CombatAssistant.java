package com.astrolabs.etherealmind.common.entity.behavior;

import com.astrolabs.etherealmind.common.entity.CosmoEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CombatAssistant {
    private final CosmoEntity cosmo;
    
    // Combat settings
    private boolean combatModeEnabled = false;
    private boolean defensiveMode = true;
    private boolean targetMarkingEnabled = true;
    private boolean distractionEnabled = true;
    
    // Combat state
    private LivingEntity currentTarget = null;
    private int combatTimer = 0;
    private int healCooldown = 0;
    private int distractCooldown = 0;
    
    // Combat ranges
    private static final double COMBAT_RANGE = 16.0;
    private static final double HEAL_RANGE = 8.0;
    private static final double DISTRACT_RANGE = 10.0;
    
    public CombatAssistant(CosmoEntity cosmo) {
        this.cosmo = cosmo;
    }
    
    public void tick() {
        if (cosmo.level().isClientSide) return;
        
        Player owner = cosmo.getBoundPlayer();
        if (owner == null || !combatModeEnabled) return;
        
        // Update cooldowns
        if (healCooldown > 0) healCooldown--;
        if (distractCooldown > 0) distractCooldown--;
        if (combatTimer > 0) combatTimer--;
        
        // Check if owner is in combat
        if (isOwnerInCombat(owner)) {
            if (combatTimer == 0) {
                enterCombatMode(owner);
            }
            combatTimer = 100; // Reset combat timer
            
            // Perform combat assistance
            if (defensiveMode) {
                performDefensiveAssistance(owner);
            } else {
                performOffensiveAssistance(owner);
            }
        } else if (combatTimer == 0 && currentTarget != null) {
            exitCombatMode();
        }
    }
    
    private boolean isOwnerInCombat(Player owner) {
        // Check if owner is being targeted
        List<Monster> nearbyMonsters = owner.level().getEntitiesOfClass(Monster.class, 
            AABB.ofSize(owner.position(), COMBAT_RANGE * 2, COMBAT_RANGE, COMBAT_RANGE * 2));
            
        for (Monster monster : nearbyMonsters) {
            if (monster.getTarget() == owner) {
                return true;
            }
        }
        
        // Check if owner is targeting something
        if (owner.getLastHurtMob() != null && owner.getLastHurtMob().isAlive()) {
            return true;
        }
        
        // Check if owner has low health
        return owner.getHealth() < owner.getMaxHealth() * 0.5f;
    }
    
    private void enterCombatMode(Player owner) {
        cosmo.showSpeechBubble("Combat mode activated! 🗡️");
        cosmo.showEmote("⚔️");
        cosmo.playAlert();
        
        // Give owner brief strength boost
        owner.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, 0));
    }
    
    private void exitCombatMode() {
        cosmo.showSpeechBubble("Combat ended. You're safe now! ✨");
        cosmo.showEmote("✅");
        currentTarget = null;
    }
    
    private void performDefensiveAssistance(Player owner) {
        // Priority 1: Heal owner if needed
        if (shouldHealOwner(owner)) {
            healOwner(owner);
        }
        
        // Priority 2: Mark dangerous enemies
        if (targetMarkingEnabled) {
            markDangerousEnemies(owner);
        }
        
        // Priority 3: Distract enemies attacking owner
        if (distractionEnabled && distractCooldown == 0) {
            distractEnemies(owner);
        }
        
        // Priority 4: Defensive positioning
        positionDefensively(owner);
    }
    
    private void performOffensiveAssistance(Player owner) {
        // Find priority target
        LivingEntity target = findPriorityTarget(owner);
        if (target != null) {
            currentTarget = target;
            
            // Mark target
            markTarget(target);
            
            // Apply debuffs
            applyDebuffs(target);
        }
    }
    
    private boolean shouldHealOwner(Player owner) {
        return owner.getHealth() < owner.getMaxHealth() * 0.3f && 
               healCooldown == 0 && 
               cosmo.getLevel() >= 5;
    }
    
    private void healOwner(Player owner) {
        if (!(cosmo.level() instanceof ServerLevel serverLevel)) return;
        
        // Heal effect
        owner.heal(4.0f); // 2 hearts
        owner.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0));
        
        // Visual effects
        serverLevel.sendParticles(ParticleTypes.HEART,
            owner.getX(), owner.getY() + 1, owner.getZ(),
            10, 0.5, 0.5, 0.5, 0.0);
            
        serverLevel.sendParticles(ParticleTypes.END_ROD,
            cosmo.getX(), cosmo.getY() + 1, cosmo.getZ(),
            20, 0.3, 0.3, 0.3, 0.1);
            
        // Sound
        serverLevel.playSound(null, owner.blockPosition(),
            SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.5f, 1.2f);
            
        cosmo.showSpeechBubble("Healing you! 💚");
        cosmo.showEmote("✨");
        healCooldown = 200; // 10 seconds
    }
    
    private void markDangerousEnemies(Player owner) {
        List<Monster> dangers = owner.level().getEntitiesOfClass(Monster.class,
            AABB.ofSize(owner.position(), COMBAT_RANGE * 2, COMBAT_RANGE, COMBAT_RANGE * 2))
            .stream()
            .filter(m -> m.getTarget() == owner)
            .sorted(Comparator.comparing(m -> m.distanceToSqr(owner)))
            .limit(3)
            .collect(Collectors.toList());
            
        for (Monster danger : dangers) {
            markTarget(danger);
        }
    }
    
    private void markTarget(LivingEntity target) {
        if (!(cosmo.level() instanceof ServerLevel serverLevel)) return;
        
        // Glowing effect
        target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100, 0));
        
        // Particle marker
        serverLevel.sendParticles(ParticleTypes.FLAME,
            target.getX(), target.getY() + target.getBbHeight() + 0.5, target.getZ(),
            5, 0.1, 0.1, 0.1, 0.02);
    }
    
    private void distractEnemies(Player owner) {
        List<Monster> attackers = owner.level().getEntitiesOfClass(Monster.class,
            AABB.ofSize(owner.position(), DISTRACT_RANGE * 2, DISTRACT_RANGE, DISTRACT_RANGE * 2))
            .stream()
            .filter(m -> m.getTarget() == owner)
            .collect(Collectors.toList());
            
        if (!attackers.isEmpty() && cosmo.level() instanceof ServerLevel serverLevel) {
            // Create distraction
            Vec3 distractPos = cosmo.position().add(
                cosmo.getRandom().nextGaussian() * 3,
                0,
                cosmo.getRandom().nextGaussian() * 3
            );
            
            // Flashy particles
            serverLevel.sendParticles(ParticleTypes.FLASH,
                distractPos.x, distractPos.y + 1, distractPos.z,
                1, 0, 0, 0, 0);
                
            serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                distractPos.x, distractPos.y + 1, distractPos.z,
                30, 1, 1, 1, 0.1);
                
            // Loud sound
            serverLevel.playSound(null, cosmo.blockPosition(),
                SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.PLAYERS, 1.0f, 1.0f);
                
            // Brief confusion
            for (Monster attacker : attackers) {
                if (cosmo.getRandom().nextFloat() < 0.5f) {
                    attacker.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, 0));
                }
            }
            
            cosmo.showSpeechBubble("Hey! Over here! 🎆");
            distractCooldown = 120; // 6 seconds
        }
    }
    
    private void positionDefensively(Player owner) {
        // Position between owner and nearest threat
        Monster nearestThreat = owner.level().getEntitiesOfClass(Monster.class,
            AABB.ofSize(owner.position(), COMBAT_RANGE * 2, COMBAT_RANGE, COMBAT_RANGE * 2))
            .stream()
            .filter(m -> m.getTarget() == owner)
            .min(Comparator.comparing(m -> m.distanceToSqr(owner)))
            .orElse(null);
            
        if (nearestThreat != null) {
            Vec3 ownerPos = owner.position();
            Vec3 threatPos = nearestThreat.position();
            Vec3 betweenPos = ownerPos.add(threatPos.subtract(ownerPos).normalize().scale(3));
            
            cosmo.getNavigation().moveTo(betweenPos.x, betweenPos.y, betweenPos.z, 1.2);
        }
    }
    
    private LivingEntity findPriorityTarget(Player owner) {
        return owner.level().getEntitiesOfClass(Monster.class,
            AABB.ofSize(owner.position(), COMBAT_RANGE * 2, COMBAT_RANGE, COMBAT_RANGE * 2))
            .stream()
            .filter(m -> m.getTarget() == owner || owner.getLastHurtMob() == m)
            .min(Comparator.comparing(m -> m.getHealth()))
            .orElse(null);
    }
    
    private void applyDebuffs(LivingEntity target) {
        if (cosmo.getLevel() >= 7) {
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0));
        }
        if (cosmo.getLevel() >= 9) {
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 0));
        }
    }
    
    // Settings
    public void setCombatModeEnabled(boolean enabled) {
        this.combatModeEnabled = enabled;
        if (enabled) {
            cosmo.showSpeechBubble("Combat assistance enabled! ⚔️");
        } else {
            cosmo.showSpeechBubble("Combat assistance disabled.");
        }
    }
    
    public void setDefensiveMode(boolean defensive) {
        this.defensiveMode = defensive;
        cosmo.showSpeechBubble(defensive ? "Defensive mode! 🛡️" : "Offensive mode! ⚔️");
    }
    
    public boolean isCombatModeEnabled() { return combatModeEnabled; }
    public boolean isDefensiveMode() { return defensiveMode; }
    public boolean isInCombat() { return combatTimer > 0; }
}