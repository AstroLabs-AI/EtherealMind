package com.astrolabs.etherealmind.common.registry;

import com.astrolabs.etherealmind.EtherealMind;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ParticleRegistry {
    private static final DeferredRegister<ParticleType<?>> PARTICLES = 
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, EtherealMind.MOD_ID);
    
    public static final RegistryObject<SimpleParticleType> COSMIC_STAR = PARTICLES.register("cosmic_star",
            () -> new SimpleParticleType(false));
    
    public static final RegistryObject<SimpleParticleType> VOID_WISP = PARTICLES.register("void_wisp",
            () -> new SimpleParticleType(false));
    
    public static final RegistryObject<SimpleParticleType> DIMENSIONAL_SPARK = PARTICLES.register("dimensional_spark",
            () -> new SimpleParticleType(false));
    
    public static final RegistryObject<SimpleParticleType> GRAVITY_DISTORTION = PARTICLES.register("gravity_distortion",
            () -> new SimpleParticleType(false));
    
    public static final RegistryObject<SimpleParticleType> COSMIC_PARTICLE = PARTICLES.register("cosmic_particle",
            () -> new SimpleParticleType(false));
    
    public static final RegistryObject<SimpleParticleType> REALITY_RIPPLE = PARTICLES.register("reality_ripple",
            () -> new SimpleParticleType(false));
    
    public static void register(IEventBus eventBus) {
        PARTICLES.register(eventBus);
    }
}