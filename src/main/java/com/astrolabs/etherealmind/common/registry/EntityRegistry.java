package com.astrolabs.etherealmind.common.registry;

import com.astrolabs.etherealmind.EtherealMind;
import com.astrolabs.etherealmind.common.entity.CosmoEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityRegistry {
    private static final DeferredRegister<EntityType<?>> ENTITIES = 
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, EtherealMind.MOD_ID);
    
    public static final RegistryObject<EntityType<CosmoEntity>> COSMO = ENTITIES.register("cosmo",
            () -> EntityType.Builder.<CosmoEntity>of(CosmoEntity::new, MobCategory.MISC)
                    .sized(1.0f, 1.0f)
                    .clientTrackingRange(80)
                    .updateInterval(3)
                    .build("cosmo"));
    
    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}