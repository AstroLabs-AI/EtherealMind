package com.astrolabs.etherealmind.common.network.packets;

import com.astrolabs.etherealmind.client.ClientProxy;
import com.astrolabs.etherealmind.common.entity.CosmoEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

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
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                Minecraft mc = Minecraft.getInstance();
                if (mc.level != null) {
                    Entity entity = mc.level.getEntity(packet.entityId);
                    if (entity instanceof CosmoEntity cosmo) {
                        ClientProxy.openDimensionalStorage(cosmo.getStorage());
                    }
                }
            });
        });
        ctx.setPacketHandled(true);
    }
}