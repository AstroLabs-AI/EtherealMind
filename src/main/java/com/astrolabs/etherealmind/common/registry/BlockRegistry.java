package com.astrolabs.etherealmind.common.registry;

import com.astrolabs.etherealmind.EtherealMind;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockRegistry {
    private static final DeferredRegister<Block> BLOCKS = 
            DeferredRegister.create(ForgeRegistries.BLOCKS, EtherealMind.MOD_ID);
    
    public static final RegistryObject<Block> VOID_STONE = BLOCKS.register("void_stone",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.OBSIDIAN)
                    .strength(50.0F, 1200.0F)
                    .lightLevel((state) -> 3)));
    
    public static final RegistryObject<Block> COSMIC_BEACON = BLOCKS.register("cosmic_beacon",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.BEACON)
                    .lightLevel((state) -> 15)));
    
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}