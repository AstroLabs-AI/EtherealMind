package com.astrolabs.etherealmind.common.registry;

import com.astrolabs.etherealmind.EtherealMind;
import com.astrolabs.etherealmind.common.menu.DimensionalStorageMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MenuRegistry {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = 
        DeferredRegister.create(ForgeRegistries.MENU_TYPES, EtherealMind.MOD_ID);
    
    public static final RegistryObject<MenuType<DimensionalStorageMenu>> DIMENSIONAL_STORAGE_MENU =
        MENU_TYPES.register("dimensional_storage", 
            () -> IForgeMenuType.create(DimensionalStorageMenu::new));
    
    public static void register(IEventBus eventBus) {
        MENU_TYPES.register(eventBus);
    }
}