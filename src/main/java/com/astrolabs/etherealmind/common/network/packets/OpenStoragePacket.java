package com.astrolabs.etherealmind.common.network.packets;

import com.astrolabs.etherealmind.common.entity.CosmoEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

public class OpenStoragePacket {
    private final UUID cosmoId;
    
    public OpenStoragePacket(UUID cosmoId) {
        this.cosmoId = cosmoId;
    }
    
    public static void encode(OpenStoragePacket packet, FriendlyByteBuf buf) {
        buf.writeUUID(packet.cosmoId);
    }
    
    public static OpenStoragePacket decode(FriendlyByteBuf buf) {
        return new OpenStoragePacket(buf.readUUID());
    }
    
    public static void handle(OpenStoragePacket packet, NetworkEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player != null) {
                CosmoEntity cosmo = CosmoEntity.getCosmoForPlayer(player);
                if (cosmo != null && cosmo.getUUID().equals(packet.cosmoId)) {
                    // TODO: Open storage GUI
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}