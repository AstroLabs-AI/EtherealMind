package com.astrolabs.etherealmind.common.network.packets;

import com.astrolabs.etherealmind.client.gui.DimensionalStorageScreen;
import com.astrolabs.etherealmind.common.entity.CosmoEntity;
import com.astrolabs.etherealmind.common.menu.DimensionalStorageMenu;
import com.astrolabs.etherealmind.common.registry.MenuRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

public class OpenStoragePacket {
    private final int entityId;
    
    public OpenStoragePacket(int entityId) {
        this.entityId = entityId;
    }
    
    public static void encode(OpenStoragePacket packet, FriendlyByteBuf buf) {
        buf.writeVarInt(packet.entityId);
    }
    
    public static OpenStoragePacket decode(FriendlyByteBuf buf) {
        return new OpenStoragePacket(buf.readVarInt());
    }
    
    public static void handle(OpenStoragePacket packet, NetworkEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player != null && player.level() != null) {
                // Server-side: Open the menu
                Entity entity = player.level().getEntity(packet.entityId);
                if (entity instanceof CosmoEntity cosmo) {
                    NetworkHooks.openScreen(player, new SimpleMenuProvider(
                        (containerId, playerInventory, p) -> new DimensionalStorageMenu(containerId, playerInventory, cosmo.getStorage()),
                        Component.translatable("gui.etherealmind.dimensional_storage")
                    ), buf -> buf.writeVarInt(packet.entityId));
                }
            } else {
                // Client-side: This packet should only be sent to server
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                    // Client receives the menu opening through vanilla mechanics
                });
            }
        });
        ctx.setPacketHandled(true);
    }
    
    // Static method to send packet from server to open storage
    public static void sendToClient(ServerPlayer player, CosmoEntity cosmo) {
        NetworkHooks.openScreen(player, new SimpleMenuProvider(
            (containerId, playerInventory, p) -> new DimensionalStorageMenu(containerId, playerInventory, cosmo.getStorage()),
            Component.translatable("gui.etherealmind.dimensional_storage")
        ), buf -> buf.writeVarInt(cosmo.getId()));
    }
}