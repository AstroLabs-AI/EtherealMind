package com.astrolabs.etherealmind;

import com.astrolabs.etherealmind.client.ClientProxy;
import com.astrolabs.etherealmind.common.CommonProxy;
import com.astrolabs.etherealmind.common.entity.CosmoEntity;
import com.astrolabs.etherealmind.common.registry.*;
import com.astrolabs.etherealmind.common.network.NetworkHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(EtherealMind.MOD_ID)
public class EtherealMind {
    public static final String MOD_ID = "etherealmind";
    public static final Logger LOGGER = LogManager.getLogger();
    
    private static CommonProxy proxy;
    
    public EtherealMind() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        if (FMLEnvironment.dist.isClient()) {
            proxy = new ClientProxy();
        } else {
            proxy = new CommonProxy();
        }
        
        // Register all registries
        EntityRegistry.register(modEventBus);
        ItemRegistry.register(modEventBus);
        BlockRegistry.register(modEventBus);
        ParticleRegistry.register(modEventBus);
        SoundRegistry.register(modEventBus);
        
        // Register setup events
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::registerEntityAttributes);
        
        // Register ourselves for server and other game events
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    private void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(EntityRegistry.COSMO.get(), CosmoEntity.createAttributes().build());
    }
    
    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            NetworkHandler.register();
            proxy.init();
            LOGGER.info("Ethereal Mind: COSMO systems initializing...");
        });
    }
    
    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            proxy.clientInit();
            LOGGER.info("Ethereal Mind: Client systems online");
        });
    }
    
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Ethereal Mind: COSMO awakening on server...");
    }
    
    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
    
    public static CommonProxy getProxy() {
        return proxy;
    }
}