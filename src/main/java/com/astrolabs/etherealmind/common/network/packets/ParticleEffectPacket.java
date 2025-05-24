package com.astrolabs.etherealmind.common.network.packets;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

public class ParticleEffectPacket {
    private final Vec3 position;
    private final int particleType;
    private final int count;
    
    public ParticleEffectPacket(Vec3 position, int particleType, int count) {
        this.position = position;
        this.particleType = particleType;
        this.count = count;
    }
    
    public static void encode(ParticleEffectPacket packet, FriendlyByteBuf buf) {
        buf.writeDouble(packet.position.x);
        buf.writeDouble(packet.position.y);
        buf.writeDouble(packet.position.z);
        buf.writeInt(packet.particleType);
        buf.writeInt(packet.count);
    }
    
    public static ParticleEffectPacket decode(FriendlyByteBuf buf) {
        return new ParticleEffectPacket(
            new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()),
            buf.readInt(),
            buf.readInt()
        );
    }
    
    public static void handle(ParticleEffectPacket packet, NetworkEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            // TODO: Spawn particles on client
        });
        ctx.setPacketHandled(true);
    }
}