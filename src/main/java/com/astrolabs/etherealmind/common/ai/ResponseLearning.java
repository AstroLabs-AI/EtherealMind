package com.astrolabs.etherealmind.common.ai;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.server.MinecraftServer;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks successful response patterns and learns from player feedback
 * to improve the fallback AI system over time.
 */
public class ResponseLearning {
    
    private static final String LEARNING_FILE = "etherealmind_ai_learning.json";
    private static final Gson GSON = new Gson();
    
    // Track response effectiveness
    private static final Map<String, ResponseMetrics> responseMetrics = new ConcurrentHashMap<>();
    
    // Track query patterns and their successful responses
    private static final Map<String, List<SuccessfulResponse>> queryPatterns = new ConcurrentHashMap<>();
    
    public static class ResponseMetrics {
        public int timesUsed = 0;
        public int positiveReactions = 0;
        public int negativeReactions = 0;
        public long lastUsed = System.currentTimeMillis();
        
        public double getEffectiveness() {
            int total = positiveReactions + negativeReactions;
            if (total == 0) return 0.5; // Neutral if no feedback
            return (double) positiveReactions / total;
        }
    }
    
    public static class SuccessfulResponse {
        public final String query;
        public final String response;
        public final String context;
        public final long timestamp;
        
        public SuccessfulResponse(String query, String response, String context) {
            this.query = query;
            this.response = response;
            this.context = context;
            this.timestamp = System.currentTimeMillis();
        }
    }
    
    /**
     * Record that a response was used
     */
    public static void recordResponseUsed(String response) {
        ResponseMetrics metrics = responseMetrics.computeIfAbsent(response, k -> new ResponseMetrics());
        metrics.timesUsed++;
        metrics.lastUsed = System.currentTimeMillis();
    }
    
    /**
     * Record player feedback about a response
     */
    public static void recordFeedback(String lastResponse, boolean positive, String originalQuery) {
        ResponseMetrics metrics = responseMetrics.get(lastResponse);
        if (metrics != null) {
            if (positive) {
                metrics.positiveReactions++;
                
                // Store as successful pattern
                String queryPattern = extractPattern(originalQuery);
                queryPatterns.computeIfAbsent(queryPattern, k -> new ArrayList<>())
                    .add(new SuccessfulResponse(originalQuery, lastResponse, ""));
                
                // Keep only recent successful responses
                List<SuccessfulResponse> patterns = queryPatterns.get(queryPattern);
                if (patterns.size() > 10) {
                    patterns.remove(0);
                }
            } else {
                metrics.negativeReactions++;
            }
        }
    }
    
    /**
     * Get the best response for a query based on learned patterns
     */
    public static String getLearnedResponse(String query) {
        String pattern = extractPattern(query);
        List<SuccessfulResponse> successful = queryPatterns.get(pattern);
        
        if (successful != null && !successful.isEmpty()) {
            // Return a successful response, preferring recent ones
            SuccessfulResponse best = successful.get(successful.size() - 1);
            
            // Slightly modify the response to avoid repetition
            return modifyResponse(best.response);
        }
        
        return null;
    }
    
    /**
     * Check if a response has been effective
     */
    public static boolean isEffectiveResponse(String response) {
        ResponseMetrics metrics = responseMetrics.get(response);
        if (metrics == null) return true; // Unknown responses are neutral
        
        return metrics.getEffectiveness() >= 0.4; // 40% positive threshold
    }
    
    /**
     * Extract a pattern from a query for matching
     */
    private static String extractPattern(String query) {
        String lower = query.toLowerCase();
        
        // Remove specific words to create a pattern
        String[] removeWords = {"the", "a", "an", "is", "are", "was", "were", "please", "can", "you"};
        for (String word : removeWords) {
            lower = lower.replace(" " + word + " ", " ");
        }
        
        // Extract key words
        String[] keywords = lower.split("\\s+");
        Arrays.sort(keywords);
        
        // Return first 3 keywords as pattern
        return String.join("_", Arrays.copyOf(keywords, Math.min(3, keywords.length)));
    }
    
    /**
     * Slightly modify a response to add variation
     */
    private static String modifyResponse(String response) {
        // Add variation prefixes occasionally
        if (Math.random() < 0.3) {
            String[] prefixes = {"Ah yes! ", "Right! ", "Of course! ", "Indeed! "};
            return prefixes[new Random().nextInt(prefixes.length)] + response;
        }
        
        // Sometimes add suffixes
        if (Math.random() < 0.2) {
            String[] suffixes = {" Hope that helps!", " Let me know if you need more!", " I'm here for you!"};
            return response + suffixes[new Random().nextInt(suffixes.length)];
        }
        
        return response;
    }
    
    /**
     * Save learning data to disk
     */
    public static void saveLearningData(MinecraftServer server) {
        try {
            Path worldPath = server.getWorldPath(net.minecraft.world.level.storage.LevelResource.ROOT);
            Path filePath = worldPath.resolve(LEARNING_FILE);
            
            Map<String, Object> data = new HashMap<>();
            data.put("metrics", responseMetrics);
            data.put("patterns", queryPatterns);
            
            String json = GSON.toJson(data);
            Files.write(filePath, json.getBytes());
        } catch (IOException e) {
            // Silently fail - learning is optional
        }
    }
    
    /**
     * Load learning data from disk
     */
    public static void loadLearningData(MinecraftServer server) {
        try {
            Path worldPath = server.getWorldPath(net.minecraft.world.level.storage.LevelResource.ROOT);
            Path filePath = worldPath.resolve(LEARNING_FILE);
            
            if (Files.exists(filePath)) {
                String json = new String(Files.readAllBytes(filePath));
                Type dataType = new TypeToken<Map<String, Object>>(){}.getType();
                Map<String, Object> data = GSON.fromJson(json, dataType);
                
                // Restore metrics and patterns
                if (data.containsKey("metrics")) {
                    responseMetrics.clear();
                    responseMetrics.putAll((Map<String, ResponseMetrics>) data.get("metrics"));
                }
                
                if (data.containsKey("patterns")) {
                    queryPatterns.clear();
                    queryPatterns.putAll((Map<String, List<SuccessfulResponse>>) data.get("patterns"));
                }
            }
        } catch (Exception e) {
            // Silently fail - learning is optional
        }
    }
    
    /**
     * Clean up old learning data
     */
    public static void cleanupOldData() {
        long cutoff = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000); // 30 days
        
        // Remove old metrics
        responseMetrics.entrySet().removeIf(entry -> entry.getValue().lastUsed < cutoff);
        
        // Remove old patterns
        queryPatterns.values().forEach(list -> 
            list.removeIf(response -> response.timestamp < cutoff)
        );
        
        // Remove empty pattern lists
        queryPatterns.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }
}