package com.astrolabs.etherealmind.common.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class StorageActionPacket {
    private final int action;
    
    public StorageActionPacket(int action) {
        this.action = action;
    }
    
    public static void encode(StorageActionPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.action);
    }
    
    public static StorageActionPacket decode(FriendlyByteBuf buf) {
        return new StorageActionPacket(buf.readInt());
    }
    
    public static void handle(StorageActionPacket packet, NetworkEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            // TODO: Handle storage actions
        });
        ctx.setPacketHandled(true);
    }
}