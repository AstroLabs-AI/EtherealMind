package com.astrolabs.etherealmind.client.gui;

import com.astrolabs.etherealmind.EtherealMind;
import com.astrolabs.etherealmind.common.entity.CosmoEntity;
import com.astrolabs.etherealmind.common.menu.DimensionalStorageMenu;
import com.astrolabs.etherealmind.common.network.NetworkHandler;
import com.astrolabs.etherealmind.common.network.packets.StorageActionPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

import java.util.ArrayList;
import java.util.List;

public class CosmoCompanionScreen extends AbstractContainerScreen<DimensionalStorageMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(EtherealMind.MOD_ID, "textures/gui/cosmo_companion.png");
    
    // GUI dimensions
    private static final int GUI_WIDTH = 256;
    private static final int GUI_HEIGHT = 222;
    
    // Chat area
    private static final int CHAT_X = 7;
    private static final int CHAT_Y = 32;
    private static final int CHAT_WIDTH = 162;
    private static final int CHAT_HEIGHT = 120;
    private static final int CHAT_LINE_HEIGHT = 10;
    private static final int MAX_CHAT_LINES = 9;
    
    // Stats panel
    private static final int STATS_X = 176;
    private static final int STATS_Y = 32;
    private static final int STATS_WIDTH = 72;
    private static final int STATS_HEIGHT = 120;
    
    // Input area
    private static final int INPUT_Y_OFFSET = 154;
    
    private final CosmoEntity cosmo;
    private EditBox chatInput;
    private Button sendButton;
    private Button chatButton;
    private Button storageButton;
    private Button abilitiesButton;
    
    // Chat history
    private final List<ChatMessage> chatHistory = new ArrayList<>();
    private int scrollOffset = 0;
    
    // View mode
    private enum ViewMode { CHAT, STORAGE, ABILITIES }
    private ViewMode currentView = ViewMode.CHAT;
    
    public CosmoCompanionScreen(DimensionalStorageMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = GUI_WIDTH;
        this.imageHeight = GUI_HEIGHT;
        this.inventoryLabelY = this.imageHeight - 94;
        this.cosmo = menu.getCosmo();
        
        // Hide vanilla labels
        this.titleLabelX = -1000;
        this.inventoryLabelX = -1000;
    }
    
    @Override
    protected void init() {
        super.init();
        
        // Calculate centered position
        int guiLeft = (this.width - this.imageWidth) / 2;
        int guiTop = (this.height - this.imageHeight) / 2;
        
        // Chat input box
        this.chatInput = new EditBox(this.font, 
            guiLeft + CHAT_X, 
            guiTop + INPUT_Y_OFFSET, 
            CHAT_WIDTH - 65, 
            18, 
            Component.literal(""));
        this.chatInput.setMaxLength(256);
        this.chatInput.setBordered(true);
        this.addRenderableWidget(this.chatInput);
        
        // Send button
        this.sendButton = Button.builder(
            Component.literal("Send"),
            button -> sendChatMessage()
        ).bounds(guiLeft + CHAT_X + CHAT_WIDTH - 60, guiTop + INPUT_Y_OFFSET, 60, 20).build();
        this.addRenderableWidget(this.sendButton);
        
        // Mode buttons (positioned to match texture)
        int buttonY = guiTop + 8;
        int buttonWidth = 50;
        int buttonHeight = 20;
        int buttonSpacing = 5;
        
        // Chat button (always visible but disabled when in chat mode)
        this.chatButton = Button.builder(
            Component.literal("CHAT"),
            button -> switchView(ViewMode.CHAT)
        ).bounds(guiLeft + 7, buttonY, buttonWidth, buttonHeight).build();
        this.chatButton.active = (currentView != ViewMode.CHAT);
        this.addRenderableWidget(this.chatButton);
        
        this.storageButton = Button.builder(
            Component.literal("STORAGE"),
            button -> switchView(ViewMode.STORAGE)
        ).bounds(guiLeft + 7 + buttonWidth + buttonSpacing, buttonY, buttonWidth + 10, buttonHeight).build();
        this.addRenderableWidget(this.storageButton);
        
        this.abilitiesButton = Button.builder(
            Component.literal("ABILITIES"),
            button -> switchView(ViewMode.ABILITIES)
        ).bounds(guiLeft + 7 + (buttonWidth * 2) + (buttonSpacing * 2) + 10, buttonY, buttonWidth + 10, buttonHeight).build();
        this.addRenderableWidget(this.abilitiesButton);
        
        // Initialize chat
        if (chatHistory.isEmpty()) {
            addChatMessage("COSMO", "Hello! I'm your robotic companion! 🤖");
            addChatMessage("COSMO", "How can I assist you today?");
        }
    }
    
    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        
        // Draw main background
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        
        // Draw current view content
        switch (currentView) {
            case CHAT -> renderChatView(graphics, x, y);
            case STORAGE -> renderStorageView(graphics, x, y);
            case ABILITIES -> renderAbilitiesView(graphics, x, y);
        }
    }
    
    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        // Title
        graphics.drawString(this.font, "COSMO AI Companion", 8, 6, 0x404040, false);
        
        // Stats panel
        renderStatsPanel(graphics);
        
        // Mode indicator
        String mode = "Mode: " + currentView.name();
        graphics.drawString(this.font, mode, STATS_X, 145, 0x404040, false);
    }
    
    private void renderChatView(GuiGraphics graphics, int guiX, int guiY) {
        // Chat background
        graphics.fill(guiX + CHAT_X, guiY + CHAT_Y, 
                     guiX + CHAT_X + CHAT_WIDTH, guiY + CHAT_Y + CHAT_HEIGHT, 
                     0xCC000000);
        
        // Render chat messages
        int y = guiY + CHAT_Y + CHAT_HEIGHT - 5;
        int visibleLines = 0;
        
        for (int i = chatHistory.size() - 1 - scrollOffset; i >= 0 && visibleLines < MAX_CHAT_LINES; i--) {
            if (i >= 0 && i < chatHistory.size()) {
                ChatMessage msg = chatHistory.get(i);
                
                // Split long messages into multiple lines
                List<String> lines = wrapText(msg.getFormattedMessage(), CHAT_WIDTH - 10);
                
                for (int j = lines.size() - 1; j >= 0 && visibleLines < MAX_CHAT_LINES; j--) {
                    y -= CHAT_LINE_HEIGHT;
                    graphics.drawString(this.font, lines.get(j), 
                                      guiX + CHAT_X + 5, y, msg.getColor(), false);
                    visibleLines++;
                }
            }
        }
    }
    
    private void renderStorageView(GuiGraphics graphics, int guiX, int guiY) {
        // Storage info
        graphics.drawString(this.font, "Dimensional Storage", guiX + CHAT_X, guiY + CHAT_Y, 0xFFFFFF, false);
        graphics.drawString(this.font, "Page " + (menu.getCurrentPage() + 1) + "/" + menu.getStorage().getTotalPages(), 
                          guiX + CHAT_X, guiY + CHAT_Y + 12, 0xAAAAAA, false);
        
        // Page navigation hint
        graphics.drawString(this.font, "Use scroll wheel to change pages", 
                          guiX + CHAT_X, guiY + CHAT_Y + 24, 0x888888, false);
    }
    
    private void renderAbilitiesView(GuiGraphics graphics, int guiX, int guiY) {
        graphics.drawString(this.font, "COSMO Abilities", guiX + CHAT_X, guiY + CHAT_Y, 0xFFFFFF, false);
        
        int y = guiY + CHAT_Y + 15;
        graphics.drawString(this.font, "• Item Magnet: " + (cosmo.getAbilities().isItemMagnetEnabled() ? "ON" : "OFF"), 
                          guiX + CHAT_X, y, 0xAAFFAA, false);
        y += 12;
        graphics.drawString(this.font, "• Auto Deposit: " + (cosmo.getAbilities().isAutoDepositEnabled() ? "ON" : "OFF"), 
                          guiX + CHAT_X, y, 0xAAAAFF, false);
        y += 12;
        graphics.drawString(this.font, "• Combat Assist: " + (cosmo.getCombatAssistant().isCombatModeEnabled() ? "ON" : "OFF"), 
                          guiX + CHAT_X, y, 0xFFAAAA, false);
    }
    
    private void renderStatsPanel(GuiGraphics graphics) {
        // Panel background
        graphics.fill(STATS_X, STATS_Y, STATS_X + STATS_WIDTH, STATS_Y + STATS_HEIGHT, 0xAA000000);
        
        // COSMO info
        graphics.drawString(this.font, "COSMO Stats", STATS_X + 2, STATS_Y + 2, 0xFFFFFF, false);
        
        if (cosmo != null) {
            int y = STATS_Y + 14;
            graphics.drawString(this.font, "Level: " + cosmo.getLevel(), STATS_X + 2, y, 0xAAFFAA, false);
            y += 10;
            graphics.drawString(this.font, "Mood: " + cosmo.getMood(), STATS_X + 2, y, 0xAAAAFF, false);
            y += 10;
            graphics.drawString(this.font, "Energy: " + (int)(cosmo.getEnergyLevel() * 100) + "%", STATS_X + 2, y, 0xFFFF88, false);
            y += 10;
            graphics.drawString(this.font, "State: " + cosmo.getBehavior().getCurrentState().getDisplayName(), STATS_X + 2, y, 0xFFAAAA, false);
        } else {
            graphics.drawString(this.font, "No data", STATS_X + 2, STATS_Y + 14, 0xFF8888, false);
        }
    }
    
    private void switchView(ViewMode newView) {
        currentView = newView;
        
        // Update button states
        chatButton.active = (newView != ViewMode.CHAT);
        storageButton.active = (newView != ViewMode.STORAGE);
        abilitiesButton.active = (newView != ViewMode.ABILITIES);
        
        // Show/hide chat input based on view
        chatInput.visible = (newView == ViewMode.CHAT);
        sendButton.visible = (newView == ViewMode.CHAT);
        
        if (newView == ViewMode.STORAGE) {
            // Request storage update from server
            NetworkHandler.INSTANCE.sendToServer(new StorageActionPacket(StorageActionPacket.Action.REQUEST_UPDATE, 0, 0, 0));
        }
    }
    
    private void sendChatMessage() {
        String message = chatInput.getValue().trim();
        if (!message.isEmpty()) {
            // Add player message
            addChatMessage("You", message);
            
            // Send to COSMO for processing
            if (minecraft != null && minecraft.player != null) {
                minecraft.player.connection.sendChat("cosmo " + message);
            }
            
            // Clear input
            chatInput.setValue("");
        }
    }
    
    private void addChatMessage(String sender, String message) {
        chatHistory.add(new ChatMessage(sender, message));
        
        // Limit chat history
        if (chatHistory.size() > 100) {
            chatHistory.remove(0);
        }
        
        // Auto-scroll to bottom
        scrollOffset = 0;
    }
    
    private List<String> wrapText(String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();
        
        for (String word : words) {
            if (font.width(currentLine + " " + word) > maxWidth) {
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                } else {
                    // Word is too long, force break it
                    lines.add(word);
                }
            } else {
                if (currentLine.length() > 0) currentLine.append(" ");
                currentLine.append(word);
            }
        }
        
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        
        return lines.isEmpty() ? List.of(text) : lines;
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (currentView == ViewMode.CHAT) {
            // Check if mouse is over chat area
            int guiLeft = (this.width - this.imageWidth) / 2;
            int guiTop = (this.height - this.imageHeight) / 2;
            
            if (mouseX >= guiLeft + CHAT_X && mouseX <= guiLeft + CHAT_X + CHAT_WIDTH &&
                mouseY >= guiTop + CHAT_Y && mouseY <= guiTop + CHAT_Y + CHAT_HEIGHT) {
                
                scrollOffset = Math.max(0, Math.min(chatHistory.size() - MAX_CHAT_LINES, 
                    scrollOffset - (int)delta));
                return true;
            }
        } else if (currentView == ViewMode.STORAGE) {
            // Change storage page
            int newPage = menu.getCurrentPage() + (delta > 0 ? -1 : 1);
            if (newPage >= 0 && newPage < menu.getStorage().getTotalPages()) {
                NetworkHandler.INSTANCE.sendToServer(new StorageActionPacket(StorageActionPacket.Action.CHANGE_PAGE, newPage, 0, 0));
            }
            return true;
        }
        
        return super.mouseScrolled(mouseX, mouseY, delta);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (chatInput.isFocused()) {
            if (keyCode == 257) { // Enter key
                sendChatMessage();
                return true;
            }
            return chatInput.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    // Control slot rendering based on view mode
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
    }
    
    @Override
    protected boolean isHovering(int x, int y, int width, int height, double mouseX, double mouseY) {
        // Disable hovering for storage slots when not in storage view
        if (currentView != ViewMode.STORAGE) {
            // Check if this is a storage slot area
            int slotX = x - this.leftPos;
            int slotY = y - this.topPos;
            if (slotX >= CHAT_X && slotX <= CHAT_X + CHAT_WIDTH && 
                slotY >= CHAT_Y && slotY <= CHAT_Y + CHAT_HEIGHT) {
                return false;
            }
        }
        return super.isHovering(x, y, width, height, mouseX, mouseY);
    }
    
    // Helper class for chat messages
    private static class ChatMessage {
        private final String sender;
        private final String message;
        private final long timestamp;
        
        public ChatMessage(String sender, String message) {
            this.sender = sender;
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }
        
        public String getFormattedMessage() {
            return String.format("[%s] %s", sender, message);
        }
        
        public int getColor() {
            return sender.equals("COSMO") ? 0xFF87CEEB : // Sky blue for COSMO
                   sender.equals("You") ? 0xFFFFFF : // White for player
                   0xAAAAAA; // Grey for others
        }
    }
}