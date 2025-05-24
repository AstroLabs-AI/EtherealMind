package com.astrolabs.etherealmind.common.network;

import com.astrolabs.etherealmind.EtherealMind;
import com.astrolabs.etherealmind.common.network.packets.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(EtherealMind.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    
    public static void register() {
        int id = 0;
        
        // Server to Client
        INSTANCE.messageBuilder(OpenStoragePacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(OpenStoragePacket::encode)
                .decoder(OpenStoragePacket::decode)
                .consumerMainThread((packet, ctx) -> {
                    ctx.get().enqueueWork(() -> OpenStoragePacket.handle(packet, ctx.get()));
                    ctx.get().setPacketHandled(true);
                })
                .add();
        
        INSTANCE.messageBuilder(StorageActionPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(StorageActionPacket::encode)
                .decoder(StorageActionPacket::decode)
                .consumerMainThread((packet, ctx) -> {
                    ctx.get().enqueueWork(() -> StorageActionPacket.handle(packet, ctx.get()));
                    ctx.get().setPacketHandled(true);
                })
                .add();
        
        // Server to Client
        INSTANCE.messageBuilder(SyncCosmoDataPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SyncCosmoDataPacket::encode)
                .decoder(SyncCosmoDataPacket::decode)
                .consumerMainThread((packet, ctx) -> {
                    ctx.get().enqueueWork(() -> SyncCosmoDataPacket.handle(packet, ctx.get()));
                    ctx.get().setPacketHandled(true);
                })
                .add();
        
        INSTANCE.messageBuilder(ParticleEffectPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ParticleEffectPacket::encode)
                .decoder(ParticleEffectPacket::decode)
                .consumerMainThread((packet, ctx) -> {
                    ctx.get().enqueueWork(() -> ParticleEffectPacket.handle(packet, ctx.get()));
                    ctx.get().setPacketHandled(true);
                })
                .add();
    }
    
    public static void sendToPlayer(Object msg, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }
    
    public static void sendToServer(Object msg) {
        INSTANCE.sendToServer(msg);
    }
    
    public static void sendToAllNear(Object msg, double x, double y, double z, double radius, ResourceLocation dimension) {
        INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(x, y, z, radius, net.minecraft.world.level.Level.OVERWORLD)), msg);
    }
}