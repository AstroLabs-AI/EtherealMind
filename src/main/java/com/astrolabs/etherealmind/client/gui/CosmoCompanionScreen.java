package com.astrolabs.etherealmind.client.gui;

import com.astrolabs.etherealmind.EtherealMind;
import com.astrolabs.etherealmind.common.entity.CosmoEntity;
import com.astrolabs.etherealmind.common.menu.DimensionalStorageMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CosmoCompanionScreen extends AbstractContainerScreen<DimensionalStorageMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(EtherealMind.MOD_ID, "textures/gui/cosmo_companion.png");
    private static final int CHAT_HEIGHT = 100;
    private static final int STATS_WIDTH = 80;
    
    private final CosmoEntity cosmo;
    private EditBox chatInput;
    private Button sendButton;
    private Button storageButton;
    private Button abilitiesButton;
    
    // Chat history
    private final java.util.List<String> chatHistory = new java.util.ArrayList<>();
    private int scrollOffset = 0;
    
    public CosmoCompanionScreen(DimensionalStorageMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 256;
        this.imageHeight = 200;
        this.cosmo = menu.getCosmo();
    }
    
    @Override
    protected void init() {
        super.init();
        
        // Chat input box
        this.chatInput = new EditBox(this.font, 
            leftPos + 8, topPos + imageHeight - 30, 
            imageWidth - 80, 20, 
            Component.literal(""));
        this.chatInput.setMaxLength(256);
        this.addRenderableWidget(this.chatInput);
        
        // Send button
        this.sendButton = Button.builder(
            Component.literal("Send"),
            button -> sendChatMessage()
        ).bounds(leftPos + imageWidth - 68, topPos + imageHeight - 30, 60, 20).build();
        this.addRenderableWidget(this.sendButton);
        
        // Storage access button
        this.storageButton = Button.builder(
            Component.literal("Storage"),
            button -> openStorageView()
        ).bounds(leftPos + imageWidth - 68, topPos + 40, 60, 20).build();
        this.addRenderableWidget(this.storageButton);
        
        // Abilities button
        this.abilitiesButton = Button.builder(
            Component.literal("Abilities"),
            button -> openAbilitiesView()
        ).bounds(leftPos + imageWidth - 68, topPos + 65, 60, 20).build();
        this.addRenderableWidget(this.abilitiesButton);
        
        // Add some initial chat messages
        addChatMessage("COSMO", "Hello! How can I help you today? ✨");
        addChatMessage("COSMO", "You can chat with me, access storage, or manage my abilities!");
    }
    
    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        
        // Draw main background
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        
        // Draw COSMO's portrait
        renderCosmoPortrait(graphics, leftPos + 8, topPos + 8);
        
        // Draw stats panel
        renderStatsPanel(graphics, leftPos + imageWidth - STATS_WIDTH - 8, topPos + 8);
        
        // Draw chat area
        renderChatArea(graphics, leftPos + 8, topPos + 50);
    }
    
    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        // Title
        graphics.drawString(this.font, "COSMO Companion Interface", 8, 6, 0x404040, false);
        
        // COSMO's name and level
        String info = String.format("%s - Level %d", cosmo.getName().getString(), cosmo.getLevel());
        graphics.drawString(this.font, info, 48, 18, 0x404040, false);
        
        // Mood indicator
        String mood = "Mood: " + cosmo.getMood();
        graphics.drawString(this.font, mood, 48, 28, 0x404040, false);
    }
    
    private void renderCosmoPortrait(GuiGraphics graphics, int x, int y) {
        // Draw a frame for COSMO's portrait
        graphics.fill(x - 1, y - 1, x + 33, y + 33, 0xFF333333);
        graphics.fill(x, y, x + 32, y + 32, 0xFF000000);
        
        // TODO: Render 3D COSMO model here
        // For now, draw a placeholder
        graphics.drawString(this.font, "COSMO", x + 4, y + 12, 0xFFAA00FF, false);
    }
    
    private void renderStatsPanel(GuiGraphics graphics, int x, int y) {
        // Background
        graphics.fill(x, y, x + STATS_WIDTH, y + 30, 0xAA000000);
        
        // Stats
        graphics.drawString(this.font, "Stats", x + 2, y + 2, 0xFFFFFF, false);
        graphics.drawString(this.font, "Trust: 100%", 
            x + 2, y + 12, 0xAAFFAA, false);
        graphics.drawString(this.font, "Energy: " + (int)(cosmo.getEnergyLevel() * 100) + "%", 
            x + 2, y + 22, 0xAAAAFF, false);
    }
    
    private void renderChatArea(GuiGraphics graphics, int x, int y) {
        // Chat background
        graphics.fill(x, y, x + imageWidth - STATS_WIDTH - 24, y + CHAT_HEIGHT, 0xAA000000);
        
        // Render chat messages
        int messageY = y + CHAT_HEIGHT - 12;
        for (int i = chatHistory.size() - 1 - scrollOffset; i >= 0 && messageY > y; i--) {
            if (i >= 0 && i < chatHistory.size()) {
                String message = chatHistory.get(i);
                int color = message.startsWith("[COSMO]") ? 0xFFAA00FF : 0xFFFFFF;
                graphics.drawString(this.font, message, x + 2, messageY, color, false);
                messageY -= 10;
            }
        }
    }
    
    private void sendChatMessage() {
        String message = chatInput.getValue().trim();
        if (!message.isEmpty()) {
            // Add player message
            addChatMessage("You", message);
            
            // Send to COSMO for processing
            // TODO: Send network packet to server for AI processing
            minecraft.player.connection.sendChat(cosmo.getName().getString() + " " + message);
            
            // Clear input
            chatInput.setValue("");
        }
    }
    
    private void addChatMessage(String sender, String message) {
        String formatted = String.format("[%s] %s", sender, message);
        chatHistory.add(formatted);
        
        // Limit chat history
        if (chatHistory.size() > 100) {
            chatHistory.remove(0);
        }
    }
    
    private void openStorageView() {
        // TODO: Switch to storage view
        addChatMessage("System", "Opening dimensional storage...");
    }
    
    private void openAbilitiesView() {
        // TODO: Switch to abilities management
        addChatMessage("System", "Opening abilities panel...");
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        // Handle chat scrolling
        if (mouseX >= leftPos + 8 && mouseX <= leftPos + imageWidth - STATS_WIDTH - 24 &&
            mouseY >= topPos + 50 && mouseY <= topPos + 50 + CHAT_HEIGHT) {
            
            scrollOffset = Math.max(0, Math.min(chatHistory.size() - 10, 
                scrollOffset - (int)delta));
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
}