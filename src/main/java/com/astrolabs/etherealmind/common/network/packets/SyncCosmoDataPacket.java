package com.astrolabs.etherealmind.common.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

public class SyncCosmoDataPacket {
    private final UUID cosmoId;
    private final String mood;
    private final float energyLevel;
    
    public SyncCosmoDataPacket(UUID cosmoId, String mood, float energyLevel) {
        this.cosmoId = cosmoId;
        this.mood = mood;
        this.energyLevel = energyLevel;
    }
    
    public static void encode(SyncCosmoDataPacket packet, FriendlyByteBuf buf) {
        buf.writeUUID(packet.cosmoId);
        buf.writeUtf(packet.mood);
        buf.writeFloat(packet.energyLevel);
    }
    
    public static SyncCosmoDataPacket decode(FriendlyByteBuf buf) {
        return new SyncCosmoDataPacket(buf.readUUID(), buf.readUtf(), buf.readFloat());
    }
    
    public static void handle(SyncCosmoDataPacket packet, NetworkEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            // TODO: Update client-side COSMO data
        });
        ctx.setPacketHandled(true);
    }
}