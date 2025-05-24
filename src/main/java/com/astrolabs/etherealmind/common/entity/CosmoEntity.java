package com.astrolabs.etherealmind.common.entity;

import com.astrolabs.etherealmind.common.ai.CosmoAI;
import com.astrolabs.etherealmind.common.entity.abilities.CosmoAbilities;
import com.astrolabs.etherealmind.common.entity.behavior.CosmoBehavior;
import com.astrolabs.etherealmind.common.entity.behavior.CombatAssistant;
import com.astrolabs.etherealmind.common.entity.behavior.EnvironmentAwareness;
import com.astrolabs.etherealmind.common.entity.behavior.ResourceGatherer;
import com.astrolabs.etherealmind.common.network.NetworkHandler;
import com.astrolabs.etherealmind.common.network.packets.OpenStoragePacket;
import com.astrolabs.etherealmind.common.registry.SoundRegistry;
import com.astrolabs.etherealmind.common.storage.DimensionalStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CosmoEntity extends PathfinderMob implements GeoEntity {
    private static final EntityDataAccessor<String> MOOD = SynchedEntityData.defineId(CosmoEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Float> ENERGY_LEVEL = SynchedEntityData.defineId(CosmoEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> PARTICLE_STATE = SynchedEntityData.defineId(CosmoEntity.class, EntityDataSerializers.INT);
    
    // Core systems
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final CosmoAI brain;
    private final DimensionalStorage storage;
    private final CosmoLevel levelSystem;
    private final CosmoAbilities abilities;
    private final CosmoBehavior behavior;
    private final EnvironmentAwareness awareness;
    private final CombatAssistant combatAssistant;
    private final ResourceGatherer resourceGatherer;
    
    // Player binding
    private UUID boundPlayerUUID;
    private Player boundPlayer;
    
    // Singleton management
    private static final Map<UUID, CosmoEntity> COSMOS = new HashMap<>();
    
    // State tracking
    private long playtime = 0;
    private int interactionCount = 0;
    private float rotationSpeed = 2.0f;
    private int particleTimer = 0;
    
    // Speech bubble data
    private String speechBubbleMessage = "";
    private String currentEmote = "";
    private int speechBubbleTimer = 0;
    
    public CosmoEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.brain = new CosmoAI(this);
        this.storage = new DimensionalStorage(this);
        this.levelSystem = new CosmoLevel();
        this.abilities = new CosmoAbilities(this);
        this.behavior = new CosmoBehavior(this);
        this.awareness = new EnvironmentAwareness(this);
        this.combatAssistant = new CombatAssistant(this);
        this.resourceGatherer = new ResourceGatherer(this);
        this.setNoGravity(true);
        this.noPhysics = true;
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(MOOD, "curious");
        this.entityData.define(ENERGY_LEVEL, 1.0f);
        this.entityData.define(PARTICLE_STATE, 0);
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1000.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.5D)
                .add(Attributes.FLYING_SPEED, 0.8D)
                .add(Attributes.FOLLOW_RANGE, 64.0D);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // Update playtime
        if (!level().isClientSide && boundPlayer != null) {
            playtime++;
            
            // Update AI every second
            if (playtime % 20 == 0) {
                brain.update();
                
                // Gain XP over time (1 XP per minute)
                if (playtime % 1200 == 0) {
                    levelSystem.addExperience(1, boundPlayer);
                }
            }
            
            // Update abilities
            abilities.tick();
            
            // Update behavior
            behavior.tick();
            
            // Update environmental awareness
            awareness.tick();
            
            // Update combat assistant
            combatAssistant.tick();
            
            // Update resource gatherer
            resourceGatherer.tick();
            
            // Periodic environmental comments (every 30 seconds)
            if (playtime % 600 == 0 && random.nextFloat() < 0.3) {
                String comment = awareness.getEnvironmentalComment();
                showSpeechBubble(comment);
            }
            
            // Alert for dangers
            if (awareness.shouldAlertDanger() && playtime % 100 == 0) {
                String alert = awareness.getDangerAlert();
                if (!alert.isEmpty()) {
                    showSpeechBubble(alert);
                    playAlert();
                }
            }
        }
        
        // Update speech bubble timer (both client and server)
        if (speechBubbleTimer > 0) {
            speechBubbleTimer--;
            if (speechBubbleTimer == 0) {
                speechBubbleMessage = "";
                currentEmote = "";
            }
        }
        
        // Hovering animation
        if (!this.onGround()) {
            Vec3 velocity = getDeltaMovement();
            double hoverOffset = Math.sin(tickCount * 0.1) * 0.05;
            setDeltaMovement(velocity.x, hoverOffset, velocity.z);
        }
        
        // Particle effects
        updateParticles();
    }
    
    private void teleportToPlayer() {
        if (boundPlayer != null) {
            Vec3 targetPos = boundPlayer.position().add(
                random.nextGaussian() * 2,
                2,
                random.nextGaussian() * 2
            );
            teleportTo(targetPos.x, targetPos.y, targetPos.z);
        }
    }
    
    private void moveTowardsPlayer() {
        if (boundPlayer != null) {
            Vec3 direction = boundPlayer.position().subtract(position()).normalize();
            setDeltaMovement(direction.scale(0.3));
        }
    }
    
    private void updateParticles() {
        if (level().isClientSide && particleTimer++ % 2 == 0) {
            // Particle spawning will be handled by the renderer
            int state = getParticleState();
            // Different particle patterns based on state
        }
    }
    
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!level().isClientSide) {
            if (boundPlayerUUID == null) {
                // Bind to player
                bindToPlayer(player);
                brain.onFirstMeeting(player);
            } else if (player.getUUID().equals(boundPlayerUUID)) {
                // Count interactions for XP
                interactionCount++;
                if (interactionCount % 10 == 0) {
                    levelSystem.addExperience(5, player); // Bonus XP for interaction
                }
                
                // Sneak + interact = set home position
                if (player.isShiftKeyDown()) {
                    abilities.setHomePos(player.blockPosition());
                    player.displayClientMessage(
                        net.minecraft.network.chat.Component.literal("✨ Home position set!")
                            .withStyle(style -> style.withColor(0x00FFFF)), 
                        true
                    );
                } else {
                    // Open interaction menu
                    openInteractionMenu(player);
                }
            } else {
                // Not bound to this player
                brain.onStrangerInteraction(player);
            }
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }
    
    public void bindToPlayer(Player player) {
        this.boundPlayerUUID = player.getUUID();
        this.boundPlayer = player;
        COSMOS.put(player.getUUID(), this);
    }
    
    private void openInteractionMenu(Player player) {
        if (!level().isClientSide && player instanceof ServerPlayer serverPlayer) {
            // Open the storage menu using the new system
            OpenStoragePacket.sendToClient(serverPlayer, this);
        }
        brain.onInteraction(player);
    }
    
    @Override
    public boolean hurt(DamageSource source, float amount) {
        // COSMO cannot be hurt
        return false;
    }
    
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (boundPlayerUUID != null) {
            compound.putUUID("BoundPlayer", boundPlayerUUID);
        }
        compound.putLong("Playtime", playtime);
        compound.putInt("InteractionCount", interactionCount);
        compound.put("Brain", brain.save());
        compound.put("Storage", storage.save());
        compound.put("Level", levelSystem.save(new CompoundTag()));
        compound.put("Abilities", saveAbilities());
        behavior.save(compound);
    }
    
    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("BoundPlayer")) {
            boundPlayerUUID = compound.getUUID("BoundPlayer");
        }
        playtime = compound.getLong("Playtime");
        interactionCount = compound.getInt("InteractionCount");
        brain.load(compound.getCompound("Brain"));
        storage.load(compound.getCompound("Storage"));
        levelSystem.load(compound.getCompound("Level"));
        loadAbilities(compound.getCompound("Abilities"));
        behavior.load(compound);
    }
    
    // GeckoLib animation
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }
    
    private PlayState predicate(software.bernie.geckolib.core.animation.AnimationState<CosmoEntity> animationState) {
        animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }
    
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
    
    // Getters
    public String getMood() {
        return entityData.get(MOOD);
    }
    
    public void setMood(String mood) {
        entityData.set(MOOD, mood);
    }
    
    public float getEnergyLevel() {
        return entityData.get(ENERGY_LEVEL);
    }
    
    public void setEnergyLevel(float level) {
        entityData.set(ENERGY_LEVEL, level);
    }
    
    public int getParticleState() {
        return entityData.get(PARTICLE_STATE);
    }
    
    public void setParticleState(int state) {
        entityData.set(PARTICLE_STATE, state);
    }
    
    public CosmoAI getCosmoAI() {
        return brain;
    }
    
    public DimensionalStorage getStorage() {
        return storage;
    }
    
    public long getPlaytime() {
        return playtime;
    }
    
    @Nullable
    public Player getBoundPlayer() {
        return boundPlayer;
    }
    
    public static CosmoEntity getCosmoForPlayer(Player player) {
        return COSMOS.get(player.getUUID());
    }
    
    public int getLevel() {
        return levelSystem.getLevel();
    }
    
    public CosmoLevel getLevelSystem() {
        return levelSystem;
    }
    
    public CosmoAbilities getAbilities() {
        return abilities;
    }
    
    public CosmoBehavior getBehavior() {
        return behavior;
    }
    
    public void playHappySound() {
        this.playSound(SoundRegistry.COSMO_HAPPY.get(), 1.0F, 1.0F + (random.nextFloat() - random.nextFloat()) * 0.2F);
    }
    
    public void playConfusedSound() {
        this.playSound(SoundRegistry.COSMO_CONFUSED.get(), 1.0F, 1.0F + (random.nextFloat() - random.nextFloat()) * 0.2F);
    }
    
    public void playAcknowledgeSound() {
        this.playSound(SoundRegistry.COSMO_ACKNOWLEDGE.get(), 1.0F, 1.0F + (random.nextFloat() - random.nextFloat()) * 0.2F);
    }
    
    public void playAlert() {
        this.playSound(SoundRegistry.COSMO_ALERT.get(), 1.0F, 1.2F);
    }
    
    public void showSpeechBubble(String message) {
        this.speechBubbleMessage = message;
        this.speechBubbleTimer = 100; // 5 seconds
    }
    
    public void showEmote(String emote) {
        this.currentEmote = emote;
        this.speechBubbleTimer = 60; // 3 seconds for emotes
    }
    
    public String getSpeechBubbleMessage() {
        return speechBubbleTimer > 0 ? speechBubbleMessage : "";
    }
    
    public String getCurrentEmote() {
        return speechBubbleTimer > 0 ? currentEmote : "";
    }
    
    public int getSpeechBubbleTimer() {
        return speechBubbleTimer;
    }
    
    public EnvironmentAwareness getAwareness() {
        return awareness;
    }
    
    public CombatAssistant getCombatAssistant() {
        return combatAssistant;
    }
    
    public ResourceGatherer getResourceGatherer() {
        return resourceGatherer;
    }
    
    private CompoundTag saveAbilities() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("ItemMagnet", abilities.isItemMagnetEnabled());
        tag.putBoolean("AutoDeposit", abilities.isAutoDepositEnabled());
        tag.putBoolean("HealingAura", abilities.isHealingAuraEnabled());
        tag.putFloat("MagnetRange", abilities.getMagnetRange());
        if (abilities.getHomePos() != null) {
            tag.putInt("HomeX", abilities.getHomePos().getX());
            tag.putInt("HomeY", abilities.getHomePos().getY());
            tag.putInt("HomeZ", abilities.getHomePos().getZ());
        }
        return tag;
    }
    
    private void loadAbilities(CompoundTag tag) {
        abilities.setItemMagnetEnabled(tag.getBoolean("ItemMagnet"));
        abilities.setAutoDepositEnabled(tag.getBoolean("AutoDeposit"));
        abilities.setHealingAuraEnabled(tag.getBoolean("HealingAura"));
        abilities.setMagnetRange(tag.getFloat("MagnetRange"));
        if (tag.contains("HomeX")) {
            abilities.setHomePos(new net.minecraft.core.BlockPos(
                tag.getInt("HomeX"),
                tag.getInt("HomeY"),
                tag.getInt("HomeZ")
            ));
        }
    }
}