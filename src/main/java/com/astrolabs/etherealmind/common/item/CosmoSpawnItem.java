package com.astrolabs.etherealmind.common.item;

import com.astrolabs.etherealmind.common.entity.CosmoEntity;
import com.astrolabs.etherealmind.common.registry.EntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class CosmoSpawnItem extends Item {
    public CosmoSpawnItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel)) {
            return InteractionResult.SUCCESS;
        }
        
        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        ItemStack stack = context.getItemInHand();
        
        // Check if player already has a COSMO
        if (CosmoEntity.getCosmoForPlayer(context.getPlayer()) != null) {
            // Player already has a COSMO
            return InteractionResult.FAIL;
        }
        
        // Spawn COSMO
        CosmoEntity cosmo = EntityRegistry.COSMO.get().spawn(
                (ServerLevel) level, 
                stack, 
                context.getPlayer(), 
                pos, 
                MobSpawnType.SPAWN_EGG, 
                true, 
                false
        );
        
        if (cosmo != null) {
            cosmo.bindToPlayer(context.getPlayer());
            stack.shrink(1);
        }
        
        return InteractionResult.CONSUME;
    }
}