package com.astrolabs.etherealmind.common.registry;

import com.astrolabs.etherealmind.EtherealMind;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SoundRegistry {
    private static final DeferredRegister<SoundEvent> SOUNDS = 
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, EtherealMind.MOD_ID);
    
    public static final RegistryObject<SoundEvent> COSMO_AMBIENT = registerSound("cosmo_ambient");
    public static final RegistryObject<SoundEvent> COSMO_HAPPY = registerSound("cosmo_happy");
    public static final RegistryObject<SoundEvent> COSMO_CURIOUS = registerSound("cosmo_curious");
    public static final RegistryObject<SoundEvent> COSMO_ALERT = registerSound("cosmo_alert");
    public static final RegistryObject<SoundEvent> VOID_PORTAL_OPEN = registerSound("void_portal_open");
    public static final RegistryObject<SoundEvent> DIMENSIONAL_SHIFT = registerSound("dimensional_shift");
    
    private static RegistryObject<SoundEvent> registerSound(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(EtherealMind.id(name)));
    }
    
    public static void register(IEventBus eventBus) {
        SOUNDS.register(eventBus);
    }
}