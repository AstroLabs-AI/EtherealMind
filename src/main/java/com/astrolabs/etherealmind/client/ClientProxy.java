package com.astrolabs.etherealmind.client;

import com.astrolabs.etherealmind.client.gui.DimensionalStorageScreen;
import com.astrolabs.etherealmind.client.particle.CosmicParticle;
import com.astrolabs.etherealmind.client.particle.RealityRippleParticle;
import com.astrolabs.etherealmind.client.renderer.CosmoRenderer;
import com.astrolabs.etherealmind.common.CommonProxy;
import com.astrolabs.etherealmind.common.registry.EntityRegistry;
import com.astrolabs.etherealmind.common.registry.ParticleRegistry;
import com.astrolabs.etherealmind.common.storage.DimensionalStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientProxy extends CommonProxy {
    
    @Override
    public void clientInit() {
        // Register entity renderers
        EntityRenderers.register(EntityRegistry.COSMO.get(), CosmoRenderer::new);
    }
    
    @SubscribeEvent
    public void registerParticles(RegisterParticleProvidersEvent event) {
        Minecraft.getInstance().particleEngine.register(ParticleRegistry.COSMIC_PARTICLE.get(), CosmicParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(ParticleRegistry.REALITY_RIPPLE.get(), RealityRippleParticle.Provider::new);
    }
    
    public static void openDimensionalStorage(DimensionalStorage storage) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null) {
            minecraft.setScreen(new DimensionalStorageScreen(storage, minecraft.player));
        }
    }
}