package com.astrolabs.etherealmind.common.ai;

import net.minecraft.world.phys.Vec3;
import java.util.Arrays;

/**
 * Test class to demonstrate the enhanced fallback AI system
 */
public class FallbackAITest {
    
    public static void main(String[] args) {
        System.out.println("=== EtherealMind Enhanced Fallback AI Test ===\n");
        
        // Create test contexts
        OpenAIIntegration.GameContext morningForest = createContext("forest", "morning", "clear", 80, 3);
        OpenAIIntegration.GameContext nightCave = createContext("cave", "night", "clear", 40, 5);
        OpenAIIntegration.GameContext stormyOcean = createContext("ocean", "day", "thunderstorm", 60, 7);
        
        // Test various queries
        testQuery("Hello COSMO!", morningForest);
        testQuery("What can you do?", morningForest);
        testQuery("I'm looking for diamonds", nightCave);
        testQuery("Help! There are zombies nearby!", nightCave);
        testQuery("How's the weather?", stormyOcean);
        testQuery("Where should I build my house?", morningForest);
        testQuery("I need food", morningForest);
        testQuery("What time is it?", nightCave);
        testQuery("Tell me about this place", stormyOcean);
        testQuery("I'm feeling scared", nightCave);
        
        // Test pattern matching
        System.out.println("\n=== Pattern Matching Tests ===\n");
        testQuery("How do I craft a diamond sword?", morningForest);
        testQuery("Where can I find a village?", morningForest);
        testQuery("Is it safe here?", nightCave);
        testQuery("How are you feeling today?", morningForest);
        
        // Test contextual responses
        System.out.println("\n=== Contextual Response Tests ===\n");
        morningForest.nearbyEntities.add("Cow");
        morningForest.nearbyEntities.add("Sheep");
        testQuery("What's around us?", morningForest);
        
        nightCave.nearbyEntities.add("Zombie");
        nightCave.nearbyEntities.add("Skeleton");
        testQuery("Am I safe?", nightCave);
        
        // Test memory and learning
        System.out.println("\n=== Memory and Context Tests ===\n");
        testQuery("I love mining!", nightCave);
        testQuery("Any tips for mining?", nightCave);
        testQuery("Thanks COSMO!", nightCave);
    }
    
    private static void testQuery(String query, OpenAIIntegration.GameContext context) {
        System.out.println("Player: " + query);
        String response = SmartFallbackAI.generateSmartResponse(query, context);
        System.out.println("COSMO: " + response);
        System.out.println("Context: " + contextSummary(context));
        System.out.println();
    }
    
    private static OpenAIIntegration.GameContext createContext(String biome, String time, String weather, int health, int level) {
        OpenAIIntegration.GameContext context = new OpenAIIntegration.GameContext();
        context.biome = biome;
        context.timeOfDay = time;
        context.weather = weather;
        context.playerHealth = health;
        context.cosmoLevel = level;
        context.position = new Vec3(100, 64, 100);
        context.nearbyEntities = new java.util.ArrayList<>();
        return context;
    }
    
    private static String contextSummary(OpenAIIntegration.GameContext context) {
        return String.format("[%s, %s, %s, Health:%d%%, Level:%d, Entities:%d]",
            context.biome, context.timeOfDay, context.weather, 
            context.playerHealth, context.cosmoLevel, context.nearbyEntities.size());
    }
}