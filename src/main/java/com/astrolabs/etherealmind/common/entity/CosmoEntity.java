package com.astrolabs.etherealmind.common.entity;

import com.astrolabs.etherealmind.common.ai.CosmoAI;
import com.astrolabs.etherealmind.common.storage.DimensionalStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
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
    
    // Player binding
    private UUID boundPlayerUUID;
    private Player boundPlayer;
    
    // Singleton management
    private static final Map<UUID, CosmoEntity> COSMOS = new HashMap<>();
    
    // State tracking
    private long playtime = 0;
    private float rotationSpeed = 2.0f;
    private int particleTimer = 0;
    
    public CosmoEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.brain = new CosmoAI(this);
        this.storage = new DimensionalStorage(this);
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
            }
        }
        
        // Hovering animation
        if (!this.onGround()) {
            Vec3 velocity = getDeltaMovement();
            double hoverOffset = Math.sin(tickCount * 0.1) * 0.05;
            setDeltaMovement(velocity.x, hoverOffset, velocity.z);
        }
        
        // Follow bound player
        if (boundPlayer != null && boundPlayer.isAlive()) {
            double distance = distanceToSqr(boundPlayer);
            if (distance > 100) { // More than 10 blocks away
                teleportToPlayer();
            } else if (distance > 25) { // More than 5 blocks away
                moveTowardsPlayer();
            }
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
                // Open interaction menu
                openInteractionMenu(player);
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
        // Will implement GUI opening
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
        compound.put("Brain", brain.save());
        compound.put("Storage", storage.save());
    }
    
    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("BoundPlayer")) {
            boundPlayerUUID = compound.getUUID("BoundPlayer");
        }
        playtime = compound.getLong("Playtime");
        brain.load(compound.getCompound("Brain"));
        storage.load(compound.getCompound("Storage"));
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
}