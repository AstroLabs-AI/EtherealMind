package com.astrolabs.etherealmind.common.registry;

import com.astrolabs.etherealmind.EtherealMind;
import com.astrolabs.etherealmind.common.item.CosmoSpawnItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistry {
    private static final DeferredRegister<Item> ITEMS = 
            DeferredRegister.create(ForgeRegistries.ITEMS, EtherealMind.MOD_ID);
    
    public static final RegistryObject<Item> COSMO_CORE = ITEMS.register("cosmo_core",
            () -> new Item(new Item.Properties().stacksTo(1).fireResistant()));
    
    public static final RegistryObject<Item> VOID_CRYSTAL = ITEMS.register("void_crystal",
            () -> new Item(new Item.Properties()));
    
    public static final RegistryObject<Item> DIMENSIONAL_SHARD = ITEMS.register("dimensional_shard",
            () -> new Item(new Item.Properties()));
    
    public static final RegistryObject<Item> COSMO_SPAWN_EGG = ITEMS.register("cosmo_spawn_egg",
            () -> new CosmoSpawnItem(new Item.Properties().stacksTo(1)));
    
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}