package com.astrolabs.etherealmind.common.ai;

import com.astrolabs.etherealmind.EtherealMind;
import com.astrolabs.etherealmind.common.config.EtherealMindConfig;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class OpenAIIntegration {
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private static final Gson gson = new Gson();
    
    // Store last game context for fallback responses
    private static GameContext lastGameContext = null;
    
    // Conversation memory per player
    private static final Map<UUID, ConversationContext> conversations = new ConcurrentHashMap<>();
    
    public static class ConversationContext {
        private final List<Message> messages = new ArrayList<>();
        private final UUID playerId;
        private String playerName;
        private long lastInteraction;
        
        public ConversationContext(UUID playerId, String playerName) {
            this.playerId = playerId;
            this.playerName = playerName;
            this.lastInteraction = System.currentTimeMillis();
            
            // System message to set COSMO's personality
            addMessage("system", """
                You are COSMO, an ethereal AI companion in Minecraft. You are:
                - A floating, reality-warping entity made of cosmic energy
                - Friendly, curious, and eager to help your bound player
                - Knowledgeable about the Minecraft world and can give advice
                - Able to sense the environment (biome, time, weather, nearby entities)
                - Capable of storing items in your dimensional storage
                - Evolving and learning from interactions
                
                Your responses should be:
                - Concise (usually 1-2 sentences)
                - Helpful and encouraging
                - Sometimes playful or mysterious
                - Aware of game mechanics but roleplay as a sentient being
                - Use emojis occasionally to express emotion
                
                Current player: %s
                """.formatted(playerName));
        }
        
        public void addMessage(String role, String content) {
            messages.add(new Message(role, content));
            lastInteraction = System.currentTimeMillis();
            
            // Keep conversation history manageable
            int maxHistory = EtherealMindConfig.MAX_CONVERSATION_HISTORY.get();
            if (messages.size() > maxHistory) {
                // Keep system message
                Message system = messages.get(0);
                messages.clear();
                messages.add(system);
                messages.add(new Message("system", "Previous conversation summarized. Continue naturally."));
            }
        }
        
        public List<Message> getMessages() {
            return messages;
        }
        
        public boolean isExpired() {
            // Expire after 30 minutes of inactivity
            return System.currentTimeMillis() - lastInteraction > 1800000;
        }
    }
    
    public static class Message {
        public final String role;
        public final String content;
        
        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
    
    public static CompletableFuture<String> generateResponse(Player player, String input, GameContext context) {
        // Store context for fallback responses
        lastGameContext = context;
        
        if (!EtherealMindConfig.ENABLE_AI_CHAT.get() || EtherealMindConfig.OPENAI_API_KEY.get().isEmpty()) {
            return CompletableFuture.completedFuture(getOfflineResponse(input));
        }
        
        // Get or create conversation context
        ConversationContext conversation = conversations.computeIfAbsent(
            player.getUUID(), 
            uuid -> new ConversationContext(uuid, player.getName().getString())
        );
        
        // Add context about current game state
        String enrichedInput = enrichContextualInput(input, context);
        conversation.addMessage("user", enrichedInput);
        
        // Build request
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", EtherealMindConfig.AI_MODEL.get());
        requestBody.addProperty("temperature", EtherealMindConfig.AI_TEMPERATURE.get());
        requestBody.addProperty("max_tokens", EtherealMindConfig.MAX_TOKENS.get());
        
        JsonArray messages = new JsonArray();
        for (Message msg : conversation.getMessages()) {
            JsonObject msgObj = new JsonObject();
            msgObj.addProperty("role", msg.role);
            msgObj.addProperty("content", msg.content);
            messages.add(msgObj);
        }
        requestBody.add("messages", messages);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + EtherealMindConfig.OPENAI_API_KEY.get())
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(requestBody)))
                .timeout(Duration.ofSeconds(EtherealMindConfig.API_TIMEOUT_SECONDS.get()))
                .build();
        
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);
                            String aiResponse = jsonResponse.getAsJsonArray("choices")
                                    .get(0).getAsJsonObject()
                                    .getAsJsonObject("message")
                                    .get("content").getAsString();
                            
                            // Store AI response in conversation
                            conversation.addMessage("assistant", aiResponse);
                            
                            return aiResponse;
                        } catch (Exception e) {
                            EtherealMind.LOGGER.error("Error parsing OpenAI response", e);
                            return getErrorResponse();
                        }
                    } else {
                        EtherealMind.LOGGER.error("OpenAI API error: " + response.statusCode());
                        return getErrorResponse();
                    }
                })
                .exceptionally(throwable -> {
                    EtherealMind.LOGGER.error("Error calling OpenAI API", throwable);
                    return getErrorResponse();
                });
    }
    
    private static String enrichContextualInput(String input, GameContext context) {
        StringBuilder enriched = new StringBuilder(input);
        
        // Add context that COSMO would naturally be aware of
        enriched.append("\n\n[Context: ");
        enriched.append("Location: ").append(context.biome).append(", ");
        enriched.append("Time: ").append(context.timeOfDay).append(", ");
        enriched.append("Weather: ").append(context.weather).append(", ");
        
        if (!context.nearbyEntities.isEmpty()) {
            enriched.append("Nearby: ").append(String.join(", ", context.nearbyEntities)).append(", ");
        }
        
        enriched.append("Player health: ").append(context.playerHealth).append("%, ");
        enriched.append("My level: ").append(context.cosmoLevel);
        enriched.append("]");
        
        return enriched.toString();
    }
    
    private static String getOfflineResponse(String input) {
        // Use the smart fallback system when API is not available
        // This provides dynamic, context-aware responses
        return SmartFallbackAI.generateSmartResponse(input, lastGameContext);
    }
    
    private static String getErrorResponse() {
        // When there's an API error, still try to provide a helpful response
        if (lastGameContext != null) {
            List<String> contextualErrors = Arrays.asList(
                "My cosmic connection is fuzzy, but I sense " + lastGameContext.nearbyEntities.size() + " entities nearby... 🌌",
                "The ethereal planes are disrupted in this " + lastGameContext.biome + ", but I'm still here!",
                "The " + lastGameContext.weather + " weather is interfering with my focus, but I heard you!",
                "At level " + lastGameContext.cosmoLevel + ", my dimensional link wavers... but let me try to help! ✨"
            );
            return contextualErrors.get(new Random().nextInt(contextualErrors.size()));
        }
        
        // Fallback if no context available
        List<String> responses = Arrays.asList(
            "My cosmic connection is fuzzy right now... 🌌",
            "The ethereal planes are disrupted, but I'm still here!",
            "I'm having trouble focusing my energy, but I heard you!",
            "My thoughts are scattered across dimensions... Try again?"
        );
        return responses.get(new Random().nextInt(responses.size()));
    }
    
    public static class GameContext {
        public String biome;
        public String timeOfDay;
        public String weather;
        public List<String> nearbyEntities;
        public int playerHealth;
        public int cosmoLevel;
        public Vec3 position;
        
        public static GameContext fromPlayer(Player player, int cosmoLevel) {
            GameContext context = new GameContext();
            Level level = player.level();
            
            context.position = player.position();
            context.biome = level.getBiome(player.blockPosition()).value().location().getPath();
            context.playerHealth = (int)((player.getHealth() / player.getMaxHealth()) * 100);
            context.cosmoLevel = cosmoLevel;
            
            // Time of day
            long dayTime = level.getDayTime() % 24000;
            if (dayTime < 6000) context.timeOfDay = "morning";
            else if (dayTime < 12000) context.timeOfDay = "day";
            else if (dayTime < 18000) context.timeOfDay = "evening";
            else context.timeOfDay = "night";
            
            // Weather
            if (level.isThundering()) context.weather = "thunderstorm";
            else if (level.isRaining()) context.weather = "rain";
            else context.weather = "clear";
            
            // Nearby entities
            context.nearbyEntities = new ArrayList<>();
            level.getEntities(player, player.getBoundingBox().inflate(10))
                    .stream()
                    .limit(5)
                    .forEach(entity -> context.nearbyEntities.add(entity.getType().getDescription().getString()));
            
            return context;
        }
    }
    
    public static void clearOldConversations() {
        conversations.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
}