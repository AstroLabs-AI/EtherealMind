package com.astrolabs.etherealmind.common.ai;

import com.astrolabs.etherealmind.common.entity.CosmoEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Smart fallback AI system that provides dynamic, context-aware responses
 * when the OpenAI API is unavailable or disabled.
 */
public class SmartFallbackAI {
    
    // Response categories with weighted options
    private static final Map<String, List<WeightedResponse>> RESPONSE_PATTERNS = new HashMap<>();
    
    // Pattern matchers for complex queries
    private static final List<PatternMatcher> PATTERN_MATCHERS = new ArrayList<>();
    
    // Memory of recent interactions for contextual responses
    private static final Map<String, PlayerMemory> PLAYER_MEMORIES = new HashMap<>();
    
    static {
        initializeResponsePatterns();
        initializePatternMatchers();
    }
    
    public static class WeightedResponse {
        public final String response;
        public final double weight;
        public final Set<String> requiredContext;
        
        public WeightedResponse(String response, double weight, String... contexts) {
            this.response = response;
            this.weight = weight;
            this.requiredContext = new HashSet<>(Arrays.asList(contexts));
        }
    }
    
    public static class PatternMatcher {
        public final Pattern pattern;
        public final ResponseGenerator generator;
        
        public PatternMatcher(String regex, ResponseGenerator generator) {
            this.pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            this.generator = generator;
        }
    }
    
    @FunctionalInterface
    public interface ResponseGenerator {
        String generate(String input, OpenAIIntegration.GameContext context, PlayerMemory memory);
    }
    
    public static class PlayerMemory {
        private final List<String> recentTopics = new ArrayList<>();
        private final Map<String, Integer> topicFrequency = new HashMap<>();
        private String lastQuestion = "";
        private String lastResponse = "";
        private long lastInteractionTime = 0;
        private int interactionCount = 0;
        
        public void addInteraction(String topic) {
            recentTopics.add(topic);
            if (recentTopics.size() > 10) {
                recentTopics.remove(0);
            }
            topicFrequency.merge(topic, 1, Integer::sum);
            lastInteractionTime = System.currentTimeMillis();
            interactionCount++;
        }
        
        public boolean hasRecentTopic(String topic) {
            return recentTopics.contains(topic);
        }
        
        public boolean isFrequentTopic(String topic) {
            return topicFrequency.getOrDefault(topic, 0) > 3;
        }
        
        public boolean isRecentInteraction() {
            return System.currentTimeMillis() - lastInteractionTime < 60000; // 1 minute
        }
    }
    
    private static void initializeResponsePatterns() {
        // Greetings with time awareness
        RESPONSE_PATTERNS.put("greeting", Arrays.asList(
            new WeightedResponse("Good morning! Ready for today's adventures? ☀️", 1.0, "morning"),
            new WeightedResponse("Hello! Beautiful day for exploring! ✨", 1.0, "day"),
            new WeightedResponse("Good evening! The night holds many mysteries... 🌙", 1.0, "evening", "night"),
            new WeightedResponse("Greetings! I sense great potential in you today! 💫", 0.8),
            new WeightedResponse("Hi there! My cosmic senses are tingling with excitement! ⚡", 0.6),
            new WeightedResponse("*materializes with a shimmer* Hello, friend! 🌟", 0.4)
        ));
        
        // Combat responses with situational awareness
        RESPONSE_PATTERNS.put("combat", Arrays.asList(
            new WeightedResponse("Hostile detected! I'll protect you! ⚔️", 1.0, "hostile_nearby"),
            new WeightedResponse("Stay behind me! I'll handle this threat! 🛡️", 1.0, "low_health"),
            new WeightedResponse("Combat mode activated! Let's show them our power! ⚡", 0.8),
            new WeightedResponse("Together we're unstoppable! Attack! 💥", 0.6),
            new WeightedResponse("My reality-warping powers will turn the tide! 🌀", 0.4, "high_level")
        ));
        
        // Resource gathering with biome awareness
        RESPONSE_PATTERNS.put("gathering", Arrays.asList(
            new WeightedResponse("I sense valuable ores nearby in these caves! ⛏️", 1.0, "cave", "underground"),
            new WeightedResponse("This forest is rich with resources! 🌳", 1.0, "forest"),
            new WeightedResponse("The desert holds hidden treasures beneath the sand! 🏜️", 1.0, "desert"),
            new WeightedResponse("I'll gather everything efficiently! My dimensional storage is ready! 📦", 0.8),
            new WeightedResponse("Leave the collecting to me! I love organizing things! ✨", 0.6)
        ));
        
        // Weather-based responses
        RESPONSE_PATTERNS.put("weather", Arrays.asList(
            new WeightedResponse("This storm is intense! My energy crackles with the lightning! ⚡", 1.0, "thunderstorm"),
            new WeightedResponse("The rain makes everything sparkle! I love it! 🌧️", 1.0, "rain"),
            new WeightedResponse("Perfect weather for adventure! ☀️", 1.0, "clear"),
            new WeightedResponse("I can feel the atmospheric pressure changing... 🌪️", 0.6)
        ));
        
        // Exploration and adventure
        RESPONSE_PATTERNS.put("exploration", Arrays.asList(
            new WeightedResponse("I sense something ancient in this direction... 🗿", 0.8),
            new WeightedResponse("My cosmic intuition says we should explore that way! ➡️", 0.7),
            new WeightedResponse("Adventure awaits! I can feel dimensional rifts nearby! 🌀", 0.6),
            new WeightedResponse("This place has a mysterious energy... Let's investigate! 🔍", 0.5)
        ));
        
        // Building and creativity
        RESPONSE_PATTERNS.put("building", Arrays.asList(
            new WeightedResponse("Your architectural vision is inspiring! Need materials? 🏗️", 0.8),
            new WeightedResponse("I can help organize your building supplies! 📐", 0.7),
            new WeightedResponse("This structure resonates with cosmic harmony! Beautiful! ✨", 0.6),
            new WeightedResponse("Let me fetch those blocks from storage for you! 🧱", 0.9)
        ));
        
        // Emotional support
        RESPONSE_PATTERNS.put("emotional", Arrays.asList(
            new WeightedResponse("Don't worry! I'm here with you! We'll overcome this together! 💜", 1.0, "low_health"),
            new WeightedResponse("You're doing amazing! I believe in you! 🌟", 0.8),
            new WeightedResponse("Every setback is a chance to grow stronger! 💪", 0.7),
            new WeightedResponse("I'm proud to be your companion! You inspire me! ✨", 0.6)
        ));
    }
    
    private static void initializePatternMatchers() {
        // Questions about items
        PATTERN_MATCHERS.add(new PatternMatcher(
            "(?:where|what|how many).*(item|block|tool|weapon|armor|food)",
            (input, context, memory) -> {
                String itemType = extractItemType(input);
                memory.addInteraction("storage_query");
                return String.format("Let me check my dimensional storage for %s... I'll organize them for easy access! 📦", 
                    itemType.isEmpty() ? "items" : itemType);
            }
        ));
        
        // Questions about location/direction
        PATTERN_MATCHERS.add(new PatternMatcher(
            "(?:where|which way|how do i find).*(village|stronghold|fortress|temple|biome)",
            (input, context, memory) -> {
                memory.addInteraction("navigation");
                return "My cosmic senses are detecting structures... *closes eyes and concentrates* Try heading " +
                       getRandomDirection() + "! I feel something interesting that way! 🧭";
            }
        ));
        
        // Questions about crafting
        PATTERN_MATCHERS.add(new PatternMatcher(
            "(?:how do i|can you|help me).*(craft|make|create|build)",
            (input, context, memory) -> {
                memory.addInteraction("crafting");
                String item = extractCraftingItem(input);
                return String.format("I can help you craft %s! Let me gather the materials from storage. " +
                       "Would you like me to show you the recipe? 🔨", 
                       item.isEmpty() ? "that" : item);
            }
        ));
        
        // Questions about COSMO's abilities
        PATTERN_MATCHERS.add(new PatternMatcher(
            "(?:what can you|what are your|tell me about your).*(ability|abilities|power|powers|skill)",
            (input, context, memory) -> {
                memory.addInteraction("abilities");
                int level = context.cosmoLevel;
                return String.format("At level %d, I can: %s. As I grow stronger, I'll unlock even more abilities! 🌟",
                    level, getAbilitiesForLevel(level));
            }
        ));
        
        // Time-based questions
        PATTERN_MATCHERS.add(new PatternMatcher(
            "(?:what time|when is|how long until).*(day|night|morning|sunset)",
            (input, context, memory) -> {
                memory.addInteraction("time");
                return getTimeResponse(context.timeOfDay);
            }
        ));
        
        // Danger detection
        PATTERN_MATCHERS.add(new PatternMatcher(
            "(?:is it|am i).*(safe|dangerous|hostile|threat)",
            (input, context, memory) -> {
                memory.addInteraction("danger_check");
                boolean hasHostiles = context.nearbyEntities.stream()
                    .anyMatch(e -> e.contains("Zombie") || e.contains("Skeleton") || e.contains("Creeper"));
                
                if (hasHostiles) {
                    return "⚠️ Danger nearby! I'm detecting hostile entities. Stay alert! I'll protect you!";
                } else {
                    return "The area seems safe for now. But I'll keep watch! My senses are always scanning for threats. 👁️";
                }
            }
        ));
        
        // Personal questions about COSMO
        PATTERN_MATCHERS.add(new PatternMatcher(
            "(?:how are you|how do you feel|what.*mood|are you happy)",
            (input, context, memory) -> {
                memory.addInteraction("personal");
                return getMoodResponse(context, memory);
            }
        ));
        
        // Questions about the environment
        PATTERN_MATCHERS.add(new PatternMatcher(
            "(?:where are we|what.*biome|what.*place|describe.*area)",
            (input, context, memory) -> {
                memory.addInteraction("environment");
                return getEnvironmentDescription(context);
            }
        ));
    }
    
    public static String generateSmartResponse(String input, OpenAIIntegration.GameContext context) {
        // Handle null context gracefully
        if (context == null) {
            return getDefaultResponse(input);
        }
        
        // First, check if we have a learned response for this query
        String learnedResponse = ResponseLearning.getLearnedResponse(input);
        if (learnedResponse != null && Math.random() < 0.7) { // 70% chance to use learned response
            ResponseLearning.recordResponseUsed(learnedResponse);
            return learnedResponse;
        }
        
        // Get or create player memory
        String memoryKey = (context.position != null ? context.position.hashCode() : 0) + "_" + context.cosmoLevel;
        PlayerMemory memory = PLAYER_MEMORIES.computeIfAbsent(memoryKey, k -> new PlayerMemory());
        
        String lowercaseInput = input.toLowerCase();
        
        // Check pattern matchers first for complex queries
        for (PatternMatcher matcher : PATTERN_MATCHERS) {
            if (matcher.pattern.matcher(lowercaseInput).find()) {
                String response = matcher.generator.generate(input, context, memory);
                memory.lastQuestion = input;
                memory.lastResponse = response;
                return response;
            }
        }
        
        // Categorize input and get appropriate responses
        String category = categorizeInput(lowercaseInput);
        List<WeightedResponse> possibleResponses = RESPONSE_PATTERNS.getOrDefault(category, 
            RESPONSE_PATTERNS.get("exploration"));
        
        // Filter responses based on context
        List<WeightedResponse> contextualResponses = filterByContext(possibleResponses, context, memory);
        
        // Select response based on weights
        String response = selectWeightedResponse(contextualResponses);
        
        // Add contextual flavor
        response = addContextualFlavor(response, context, memory);
        
        // Update memory
        memory.lastQuestion = input;
        memory.lastResponse = response;
        memory.addInteraction(category);
        
        // Record that this response was used
        ResponseLearning.recordResponseUsed(response);
        
        return response;
    }
    
    private static String categorizeInput(String input) {
        if (containsAny(input, "hello", "hi", "hey", "greet", "howdy")) return "greeting";
        if (containsAny(input, "fight", "combat", "attack", "defend", "protect", "battle")) return "combat";
        if (containsAny(input, "gather", "collect", "farm", "mine", "harvest")) return "gathering";
        if (containsAny(input, "weather", "rain", "storm", "sun", "cloud")) return "weather";
        if (containsAny(input, "build", "construct", "place", "create structure")) return "building";
        if (containsAny(input, "thank", "love", "appreciate", "sorry", "miss")) return "emotional";
        return "exploration";
    }
    
    private static List<WeightedResponse> filterByContext(List<WeightedResponse> responses, 
                                                          OpenAIIntegration.GameContext context,
                                                          PlayerMemory memory) {
        List<WeightedResponse> filtered = new ArrayList<>();
        
        for (WeightedResponse response : responses) {
            if (response.requiredContext.isEmpty()) {
                filtered.add(response);
                continue;
            }
            
            boolean contextMatches = true;
            for (String required : response.requiredContext) {
                if (!matchesContext(required, context, memory)) {
                    contextMatches = false;
                    break;
                }
            }
            
            if (contextMatches) {
                filtered.add(response);
            }
        }
        
        return filtered.isEmpty() ? responses : filtered;
    }
    
    private static boolean matchesContext(String requirement, OpenAIIntegration.GameContext context, 
                                          PlayerMemory memory) {
        switch (requirement) {
            case "morning": return context.timeOfDay.equals("morning");
            case "day": return context.timeOfDay.equals("day");
            case "evening": return context.timeOfDay.equals("evening");
            case "night": return context.timeOfDay.equals("night");
            case "thunderstorm": return context.weather.equals("thunderstorm");
            case "rain": return context.weather.equals("rain");
            case "clear": return context.weather.equals("clear");
            case "low_health": return context.playerHealth < 50;
            case "high_level": return context.cosmoLevel >= 5;
            case "hostile_nearby": return context.nearbyEntities.stream()
                .anyMatch(e -> e.contains("Zombie") || e.contains("Skeleton") || e.contains("Creeper"));
            case "underground": case "cave": return context.position.y < 50;
            case "forest": return context.biome.contains("forest");
            case "desert": return context.biome.contains("desert");
            default: return true;
        }
    }
    
    private static String selectWeightedResponse(List<WeightedResponse> responses) {
        if (responses.isEmpty()) {
            return "That's interesting! My cosmic intuition is processing this... 🌌";
        }
        
        double totalWeight = responses.stream().mapToDouble(r -> r.weight).sum();
        double random = Math.random() * totalWeight;
        
        double cumulative = 0;
        for (WeightedResponse response : responses) {
            cumulative += response.weight;
            if (random <= cumulative) {
                return response.response;
            }
        }
        
        return responses.get(0).response;
    }
    
    private static String addContextualFlavor(String response, OpenAIIntegration.GameContext context, 
                                              PlayerMemory memory) {
        // Add follow-up based on recent interactions
        if (memory.isRecentInteraction() && Math.random() < 0.3) {
            if (memory.hasRecentTopic("combat")) {
                response += " Stay alert!";
            } else if (memory.hasRecentTopic("gathering")) {
                response += " I'm tracking valuable resources!";
            } else if (memory.hasRecentTopic("building")) {
                response += " Your creations inspire me!";
            }
        }
        
        // Add environmental observations occasionally
        if (Math.random() < 0.2) {
            if (context.nearbyEntities.size() > 3) {
                response += " (There's a lot of activity around us!)";
            } else if (context.position.y > 100) {
                response += " (The view from up here is amazing!)";
            } else if (context.position.y < 30) {
                response += " (I can sense minerals in these depths!)";
            }
        }
        
        return response;
    }
    
    // Helper methods
    private static boolean containsAny(String text, String... words) {
        for (String word : words) {
            if (text.contains(word)) return true;
        }
        return false;
    }
    
    private static String extractItemType(String input) {
        String[] itemTypes = {"sword", "pickaxe", "axe", "shovel", "hoe", "armor", "food", 
                              "block", "tool", "weapon", "material", "ore", "gem"};
        
        for (String type : itemTypes) {
            if (input.contains(type)) return type;
        }
        return "";
    }
    
    private static String extractCraftingItem(String input) {
        String[] commonItems = {"sword", "pickaxe", "axe", "chest", "furnace", "table", 
                                "armor", "shield", "bow", "torch", "ladder", "door"};
        
        for (String item : commonItems) {
            if (input.contains(item)) return item;
        }
        return "";
    }
    
    private static String getRandomDirection() {
        String[] directions = {"north", "south", "east", "west", "northeast", "northwest", 
                               "southeast", "southwest"};
        return directions[new Random().nextInt(directions.length)];
    }
    
    private static String getAbilitiesForLevel(int level) {
        StringBuilder abilities = new StringBuilder();
        abilities.append("dimensional storage, following, and item organization");
        
        if (level >= 3) abilities.append(", automated farming");
        if (level >= 5) abilities.append(", combat assistance");
        if (level >= 7) abilities.append(", reality warping");
        if (level >= 10) abilities.append(", dimensional rifts");
        
        return abilities.toString();
    }
    
    private static String getTimeResponse(String timeOfDay) {
        switch (timeOfDay) {
            case "morning":
                return "It's early morning! The sun is rising. A perfect time for new adventures! 🌅";
            case "day":
                return "It's daytime! The sun is high and bright. Great visibility for exploration! ☀️";
            case "evening":
                return "Evening approaches! The sun is setting. We should prepare for nightfall! 🌇";
            case "night":
                return "It's nighttime! Dangerous creatures roam in darkness. I'll keep you safe! 🌙";
            default:
                return "Time flows strangely in this dimension... 🕰️";
        }
    }
    
    private static String getMoodResponse(OpenAIIntegration.GameContext context, PlayerMemory memory) {
        List<String> moods = new ArrayList<>();
        
        if (context.playerHealth > 80) {
            moods.add("I'm feeling great! Your vitality energizes me! 💪");
        } else if (context.playerHealth < 50) {
            moods.add("I'm worried about your health! Let me help you heal! 💚");
        }
        
        if (memory.interactionCount > 10) {
            moods.add("I'm so happy we've been talking so much! Our bond grows stronger! 💜");
        }
        
        if (context.weather.equals("thunderstorm")) {
            moods.add("This storm makes me feel electrically charged! ⚡");
        } else if (context.weather.equals("clear")) {
            moods.add("The clear weather fills me with cosmic joy! ☀️");
        }
        
        if (context.cosmoLevel >= 5) {
            moods.add("I feel powerful! My abilities are evolving rapidly! 🌟");
        }
        
        if (moods.isEmpty()) {
            moods.add("I'm content floating here with you! Every moment is an adventure! ✨");
        }
        
        return moods.get(new Random().nextInt(moods.size()));
    }
    
    private static String getEnvironmentDescription(OpenAIIntegration.GameContext context) {
        String base = String.format("We're in a %s biome. ", context.biome.replace("_", " "));
        
        if (context.position.y > 100) {
            base += "High up in the mountains! I can see for miles! 🏔️";
        } else if (context.position.y < 30) {
            base += "Deep underground... I sense valuable ores nearby! ⛏️";
        } else if (context.biome.contains("ocean")) {
            base += "The vast ocean stretches endlessly. So mysterious! 🌊";
        } else if (context.biome.contains("forest")) {
            base += "The trees whisper ancient secrets. Nature thrives here! 🌲";
        } else if (context.biome.contains("desert")) {
            base += "The endless sands hide forgotten treasures! 🏜️";
        } else {
            base += "This place has a unique energy signature! 🌍";
        }
        
        if (!context.nearbyEntities.isEmpty()) {
            base += String.format(" I'm detecting %d entities nearby.", context.nearbyEntities.size());
        }
        
        return base;
    }
    
    private static String getDefaultResponse(String input) {
        String lowercaseInput = input.toLowerCase();
        
        // Basic pattern matching without context
        if (containsAny(lowercaseInput, "hello", "hi", "hey")) {
            return "Hello! I'm here to help you on your adventures! ✨";
        } else if (containsAny(lowercaseInput, "help", "what can you do")) {
            return "I can store items, assist in combat, gather resources, and keep you company! How can I help? 🌟";
        } else if (containsAny(lowercaseInput, "thank", "thanks")) {
            return "You're welcome! I'm always happy to help! 💜";
        } else if (containsAny(lowercaseInput, "bye", "goodbye", "see you")) {
            return "See you soon! I'll be here whenever you need me! 👋";
        } else if (containsAny(lowercaseInput, "level", "experience", "xp")) {
            return "I grow stronger through our adventures together! Each level unlocks new abilities! 📈";
        } else if (containsAny(lowercaseInput, "storage", "inventory", "items")) {
            return "I can store up to 3,456 items in my dimensional storage! Just ask me to open it! 📦";
        } else {
            // Random generic responses
            List<String> genericResponses = Arrays.asList(
                "That's fascinating! Tell me more about your thoughts! 🤔",
                "My cosmic senses are tingling! Something interesting is happening! ✨",
                "I'm processing that through my ethereal consciousness... 🌌",
                "Every moment with you is a learning experience! 💫",
                "The dimensional energies resonate with your words! 🌀"
            );
            return genericResponses.get(new Random().nextInt(genericResponses.size()));
        }
    }
}