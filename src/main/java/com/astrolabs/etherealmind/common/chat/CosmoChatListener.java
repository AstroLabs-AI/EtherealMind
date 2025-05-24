package com.astrolabs.etherealmind.common.chat;

import com.astrolabs.etherealmind.common.entity.CosmoEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CosmoChatListener {
    
    private static final String[] COSMO_NAMES = {
        "cosmo", "COSMO", "Cosmo"
    };
    
    private static final String[] GREETING_TRIGGERS = {
        "hello", "hi", "hey", "greetings", "howdy"
    };
    
    private static final String[] STATUS_TRIGGERS = {
        "how are you", "status", "what's up", "how's it going"
    };
    
    private static final String[] HELP_TRIGGERS = {
        "help", "what can you do", "commands", "abilities"
    };
    
    @SubscribeEvent
    public static void onServerChat(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();
        String message = event.getMessage().getString();
        
        // Check if message is directed at COSMO
        if (!isMessageForCosmo(message)) {
            return;
        }
        
        // Get player's COSMO
        CosmoEntity cosmo = CosmoEntity.getCosmoForPlayer(player);
        if (cosmo == null) {
            return;
        }
        
        // Process the message
        processMessage(cosmo, player, message);
    }
    
    private static boolean isMessageForCosmo(String message) {
        String lowerMessage = message.toLowerCase();
        for (String name : COSMO_NAMES) {
            if (lowerMessage.contains(name.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    
    private static void processMessage(CosmoEntity cosmo, ServerPlayer player, String message) {
        String lowerMessage = message.toLowerCase();
        
        // Remove COSMO's name from the message for easier parsing
        for (String name : COSMO_NAMES) {
            lowerMessage = lowerMessage.replace(name.toLowerCase(), "").trim();
        }
        
        // Check for greetings
        if (containsAny(lowerMessage, GREETING_TRIGGERS)) {
            respondGreeting(cosmo, player);
            return;
        }
        
        // Check for status request
        if (containsAny(lowerMessage, STATUS_TRIGGERS)) {
            respondStatus(cosmo, player);
            return;
        }
        
        // Check for help request
        if (containsAny(lowerMessage, HELP_TRIGGERS)) {
            respondHelp(cosmo, player);
            return;
        }
        
        // Check for fetch/bring commands
        if (lowerMessage.contains("bring") || lowerMessage.contains("fetch") || 
            lowerMessage.contains("get") || lowerMessage.contains("give")) {
            processFetchCommand(cosmo, player, lowerMessage);
            return;
        }
        
        // Check for combat commands
        if (lowerMessage.contains("combat") || lowerMessage.contains("fight") || 
            lowerMessage.contains("protect") || lowerMessage.contains("defend")) {
            processCombatCommand(cosmo, player, lowerMessage);
            return;
        }
        
        // Check for gathering commands
        if (lowerMessage.contains("collect") || lowerMessage.contains("gather") || 
            lowerMessage.contains("harvest") || lowerMessage.contains("farm")) {
            cosmo.getResourceGatherer().processGatherCommand(lowerMessage, player);
            return;
        }
        
        // Otherwise, treat as behavior command
        cosmo.getBehavior().processCommand(lowerMessage, player);
    }
    
    private static boolean containsAny(String message, String[] triggers) {
        for (String trigger : triggers) {
            if (message.contains(trigger)) {
                return true;
            }
        }
        return false;
    }
    
    private static void respondGreeting(CosmoEntity cosmo, ServerPlayer player) {
        String[] greetings = {
            "Hello, " + player.getName().getString() + "! ✨",
            "Hi there! Ready to help! 💫",
            "Greetings, my friend! 🌟",
            "Hey! Nice to see you! ✨"
        };
        
        String response = greetings[player.getRandom().nextInt(greetings.length)];
        sendCosmoMessage(player, response);
        cosmo.showSpeechBubble(response);
        cosmo.showEmote("👋");
        cosmo.playHappySound();
    }
    
    private static void respondStatus(CosmoEntity cosmo, ServerPlayer player) {
        int level = cosmo.getLevel();
        String mood = cosmo.getMood();
        String behavior = cosmo.getBehavior().getStateDisplay();
        
        String response = String.format(
            "I'm level %d and feeling %s! Currently: %s 🌟",
            level, mood, behavior
        );
        
        sendCosmoMessage(player, response);
        cosmo.showSpeechBubble(response);
        
        // Add environmental awareness
        String envComment = cosmo.getAwareness().getEnvironmentalComment();
        sendCosmoMessage(player, envComment);
    }
    
    private static void respondHelp(CosmoEntity cosmo, ServerPlayer player) {
        sendCosmoMessage(player, "I can help you with:");
        sendCosmoMessage(player, "§6Movement:");
        sendCosmoMessage(player, "§7• follow me §f- I'll stay close");
        sendCosmoMessage(player, "§7• stay/wait §f- I'll wait here");
        sendCosmoMessage(player, "§7• come here §f- I'll come to you");
        sendCosmoMessage(player, "§7• guard §f- I'll protect this spot");
        sendCosmoMessage(player, "§6Items:");
        sendCosmoMessage(player, "§7• bring <item> §f- Fetch from storage");
        sendCosmoMessage(player, "§7• bring food/tools §f- Fetch by category");
        sendCosmoMessage(player, "§6Combat (Lv5+):");
        sendCosmoMessage(player, "§7• combat on/off §f- Toggle assistance");
        sendCosmoMessage(player, "§7• defend me §f- Defensive mode");
        sendCosmoMessage(player, "§6Gathering:");
        sendCosmoMessage(player, "§7• collect items §f- Auto-collect drops");
        sendCosmoMessage(player, "§7• harvest crops §f- Farm helper (Lv3+)");
        cosmo.showSpeechBubble("How can I help? 🤔");
    }
    
    private static void processFetchCommand(CosmoEntity cosmo, ServerPlayer player, String message) {
        // Extract item name from message
        String itemName = extractItemName(message);
        
        if (itemName.isEmpty()) {
            String response = "What would you like me to bring? 🤔";
            sendCosmoMessage(player, response);
            cosmo.showSpeechBubble(response);
            cosmo.showEmote("❓");
            return;
        }
        
        // Try to find and deliver the item
        boolean found = cosmo.getAbilities().fetchItem(itemName, player);
        
        if (found) {
            String response = "Here's your " + itemName + "! 📦";
            sendCosmoMessage(player, response);
            cosmo.showSpeechBubble(response);
            cosmo.showEmote("📦");
            cosmo.playHappySound();
        } else {
            String response = "I couldn't find any " + itemName + " in storage... 😔";
            sendCosmoMessage(player, response);
            cosmo.showSpeechBubble(response);
            cosmo.showEmote("🔍");
            cosmo.playConfusedSound();
        }
    }
    
    private static String extractItemName(String message) {
        // Remove common words to find the item name
        String[] removeWords = {"bring", "fetch", "get", "give", "me", "some", "a", "an", "the", "please"};
        
        String cleaned = message;
        for (String word : removeWords) {
            cleaned = cleaned.replace(word + " ", "").replace(" " + word, "");
        }
        
        return cleaned.trim();
    }
    
    private static void processCombatCommand(CosmoEntity cosmo, ServerPlayer player, String message) {
        if (message.contains("enable") || message.contains("on") || message.contains("activate")) {
            cosmo.getCombatAssistant().setCombatModeEnabled(true);
            if (message.contains("defensive") || message.contains("defend")) {
                cosmo.getCombatAssistant().setDefensiveMode(true);
            } else if (message.contains("offensive") || message.contains("attack")) {
                cosmo.getCombatAssistant().setDefensiveMode(false);
            }
        } else if (message.contains("disable") || message.contains("off") || message.contains("stop")) {
            cosmo.getCombatAssistant().setCombatModeEnabled(false);
        } else {
            // Toggle combat mode
            boolean enabled = !cosmo.getCombatAssistant().isCombatModeEnabled();
            cosmo.getCombatAssistant().setCombatModeEnabled(enabled);
        }
    }
    
    private static void sendCosmoMessage(ServerPlayer player, String message) {
        player.displayClientMessage(
            net.minecraft.network.chat.Component.literal("§d[COSMO] §f" + message),
            false
        );
    }
}