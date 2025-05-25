package com.astrolabs.etherealmind.client;

import com.astrolabs.etherealmind.client.gui.DimensionalStorageScreen;
import com.astrolabs.etherealmind.client.gui.CosmoCompanionScreen;
import com.astrolabs.etherealmind.client.particle.CosmicParticle;
import com.astrolabs.etherealmind.client.particle.RealityRippleParticle;
import com.astrolabs.etherealmind.client.renderer.AdvancedCosmoRenderer;
import com.astrolabs.etherealmind.common.CommonProxy;
import com.astrolabs.etherealmind.common.registry.EntityRegistry;
import com.astrolabs.etherealmind.common.registry.MenuRegistry;
import com.astrolabs.etherealmind.common.registry.ParticleRegistry;
import com.astrolabs.etherealmind.common.storage.DimensionalStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientProxy extends CommonProxy {
    
    @Override
    public void clientInit() {
        // Register entity renderers - use advanced renderer for geometric model
        EntityRenderers.register(EntityRegistry.COSMO.get(), AdvancedCosmoRenderer::new);
        
        // Register menu screens - use new companion screen
        MenuScreens.register(MenuRegistry.DIMENSIONAL_STORAGE_MENU.get(), CosmoCompanionScreen::new);
    }
    
    @SubscribeEvent
    public void registerParticles(RegisterParticleProvidersEvent event) {
        Minecraft.getInstance().particleEngine.register(ParticleRegistry.COSMIC_PARTICLE.get(), CosmicParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(ParticleRegistry.REALITY_RIPPLE.get(), RealityRippleParticle.Provider::new);
    }
    
    // Removed - now handled by menu system
}