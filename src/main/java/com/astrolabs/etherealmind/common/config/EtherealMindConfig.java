package com.astrolabs.etherealmind.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = "etherealmind", bus = Mod.EventBusSubscriber.Bus.MOD)
public class EtherealMindConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    
    // AI Configuration
    public static final ForgeConfigSpec.ConfigValue<String> OPENAI_API_KEY;
    public static final ForgeConfigSpec.BooleanValue ENABLE_AI_CHAT;
    public static final ForgeConfigSpec.ConfigValue<String> AI_MODEL;
    public static final ForgeConfigSpec.IntValue MAX_CONVERSATION_HISTORY;
    public static final ForgeConfigSpec.IntValue API_TIMEOUT_SECONDS;
    public static final ForgeConfigSpec.DoubleValue AI_TEMPERATURE;
    public static final ForgeConfigSpec.IntValue MAX_TOKENS;
    
    // Feature toggles
    public static final ForgeConfigSpec.BooleanValue ENABLE_DIMENSIONAL_STORAGE;
    public static final ForgeConfigSpec.BooleanValue ENABLE_REALITY_WARPING;
    public static final ForgeConfigSpec.BooleanValue ENABLE_COMBAT_ASSISTANT;
    public static final ForgeConfigSpec.BooleanValue ENABLE_RESOURCE_GATHERING;
    
    // Performance settings
    public static final ForgeConfigSpec.IntValue PARTICLE_DENSITY;
    public static final ForgeConfigSpec.BooleanValue ENABLE_SHADERS;
    public static final ForgeConfigSpec.BooleanValue ENABLE_SPEECH_BUBBLES;
    
    static {
        BUILDER.comment("EtherealMind Configuration").push("general");
        
        BUILDER.comment("AI Integration Settings").push("ai");
        
        OPENAI_API_KEY = BUILDER
            .comment("OpenAI API Key for ChatGPT integration",
                    "Get your API key from https://platform.openai.com/api-keys",
                    "Leave empty to disable AI chat features")
            .define("openaiApiKey", "");
            
        ENABLE_AI_CHAT = BUILDER
            .comment("Enable AI-powered natural language chat with COSMO")
            .define("enableAIChat", true);
            
        AI_MODEL = BUILDER
            .comment("OpenAI model to use",
                    "Options: gpt-3.5-turbo, gpt-4, gpt-4-turbo-preview")
            .define("aiModel", "gpt-3.5-turbo");
            
        MAX_CONVERSATION_HISTORY = BUILDER
            .comment("Maximum number of messages to keep in conversation history")
            .defineInRange("maxConversationHistory", 20, 5, 100);
            
        API_TIMEOUT_SECONDS = BUILDER
            .comment("API request timeout in seconds")
            .defineInRange("apiTimeoutSeconds", 30, 10, 120);
            
        AI_TEMPERATURE = BUILDER
            .comment("AI creativity/randomness (0.0 = focused, 1.0 = creative)")
            .defineInRange("aiTemperature", 0.7, 0.0, 1.0);
            
        MAX_TOKENS = BUILDER
            .comment("Maximum tokens per AI response")
            .defineInRange("maxTokens", 150, 50, 500);
            
        BUILDER.pop();
        
        BUILDER.comment("Feature Toggles").push("features");
        
        ENABLE_DIMENSIONAL_STORAGE = BUILDER
            .comment("Enable COSMO's dimensional storage abilities")
            .define("enableDimensionalStorage", true);
            
        ENABLE_REALITY_WARPING = BUILDER
            .comment("Enable reality warping visual effects")
            .define("enableRealityWarping", true);
            
        ENABLE_COMBAT_ASSISTANT = BUILDER
            .comment("Enable COSMO's combat assistance features")
            .define("enableCombatAssistant", true);
            
        ENABLE_RESOURCE_GATHERING = BUILDER
            .comment("Enable COSMO's resource gathering abilities")
            .define("enableResourceGathering", true);
            
        BUILDER.pop();
        
        BUILDER.comment("Performance Settings").push("performance");
        
        PARTICLE_DENSITY = BUILDER
            .comment("Particle effect density (1-10, lower = less particles)")
            .defineInRange("particleDensity", 5, 1, 10);
            
        ENABLE_SHADERS = BUILDER
            .comment("Enable custom shader effects")
            .define("enableShaders", true);
            
        ENABLE_SPEECH_BUBBLES = BUILDER
            .comment("Enable speech bubble animations")
            .define("enableSpeechBubbles", true);
            
        BUILDER.pop();
        BUILDER.pop();
    }
    
    public static final ForgeConfigSpec SPEC = BUILDER.build();
    
    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        // Validate API key if AI chat is enabled
        if (ENABLE_AI_CHAT.get() && OPENAI_API_KEY.get().isEmpty()) {
            System.out.println("[EtherealMind] AI chat is enabled but no API key is configured!");
        }
    }
}