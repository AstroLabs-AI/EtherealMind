package com.astrolabs.etherealmind.client.gui;

import com.astrolabs.etherealmind.EtherealMind;
import com.astrolabs.etherealmind.common.storage.DimensionalStorage;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DimensionalStorageScreen extends Screen {
    private static final ResourceLocation TEXTURE = new ResourceLocation(EtherealMind.MOD_ID, "textures/gui/dimensional_storage.png");
    private static final int GUI_WIDTH = 276;
    private static final int GUI_HEIGHT = 230;
    
    private final DimensionalStorage storage;
    private final Player player;
    
    private int currentPage = 0;
    private EditBox searchBox;
    private Button prevPageButton;
    private Button nextPageButton;
    private Button[] categoryButtons;
    
    private int leftPos;
    private int topPos;
    
    public DimensionalStorageScreen(DimensionalStorage storage, Player player) {
        super(Component.translatable("gui.etherealmind.dimensional_storage"));
        this.storage = storage;
        this.player = player;
    }
    
    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - GUI_WIDTH) / 2;
        this.topPos = (this.height - GUI_HEIGHT) / 2;
        
        // Search box
        this.searchBox = new EditBox(this.font, leftPos + 8, topPos + 8, 120, 20, Component.translatable("gui.etherealmind.search"));
        this.searchBox.setMaxLength(50);
        this.addRenderableWidget(this.searchBox);
        
        // Page navigation
        this.prevPageButton = Button.builder(Component.literal("<"), (button) -> {
            if (currentPage > 0) {
                currentPage--;
                updateButtons();
            }
        }).bounds(leftPos + 8, topPos + GUI_HEIGHT - 25, 20, 20).build();
        
        this.nextPageButton = Button.builder(Component.literal(">"), (button) -> {
            if (currentPage < storage.getTotalPages() - 1) {
                currentPage++;
                updateButtons();
            }
        }).bounds(leftPos + GUI_WIDTH - 28, topPos + GUI_HEIGHT - 25, 20, 20).build();
        
        this.addRenderableWidget(prevPageButton);
        this.addRenderableWidget(nextPageButton);
        
        // Category buttons
        String[] categories = {"All", "Blocks", "Items", "Tools", "Food", "Combat"};
        categoryButtons = new Button[categories.length];
        
        for (int i = 0; i < categories.length; i++) {
            final String category = categories[i];
            categoryButtons[i] = Button.builder(Component.literal(category), (button) -> {
                // TODO: Implement category filtering
            }).bounds(leftPos + GUI_WIDTH - 50, topPos + 30 + (i * 22), 45, 20).build();
            this.addRenderableWidget(categoryButtons[i]);
        }
        
        updateButtons();
    }
    
    private void updateButtons() {
        prevPageButton.active = currentPage > 0;
        nextPageButton.active = currentPage < storage.getTotalPages() - 1;
    }
    
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        
        // Render GUI background
        RenderSystem.setShaderTexture(0, TEXTURE);
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, GUI_WIDTH, GUI_HEIGHT);
        
        // Render page indicator
        String pageText = String.format("Page %d/%d", currentPage + 1, storage.getTotalPages());
        graphics.drawString(this.font, pageText, leftPos + GUI_WIDTH / 2 - font.width(pageText) / 2, topPos + GUI_HEIGHT - 20, 0xFFFFFF);
        
        // Render storage slots
        renderStorageSlots(graphics, mouseX, mouseY);
        
        // Render quick access bar
        renderQuickAccessBar(graphics, mouseX, mouseY);
        
        super.render(graphics, mouseX, mouseY, partialTick);
        
        // Render tooltips
        renderTooltips(graphics, mouseX, mouseY);
    }
    
    private void renderStorageSlots(GuiGraphics graphics, int mouseX, int mouseY) {
        int slotsPerRow = 9;
        int rows = 6;
        int slotSize = 18;
        int startX = leftPos + 8;
        int startY = topPos + 35;
        
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < slotsPerRow; col++) {
                int slotIndex = (currentPage * 54) + (row * slotsPerRow) + col;
                if (slotIndex < storage.getTotalSlots()) {
                    int x = startX + col * slotSize;
                    int y = startY + row * slotSize;
                    
                    // Render slot background
                    graphics.blit(TEXTURE, x, y, 276, 0, 18, 18);
                    
                    // Render item if present
                    ItemStack stack = storage.getStackInSlot(slotIndex);
                    if (!stack.isEmpty()) {
                        graphics.renderItem(stack, x + 1, y + 1);
                        graphics.renderItemDecorations(font, stack, x + 1, y + 1);
                    }
                }
            }
        }
    }
    
    private void renderQuickAccessBar(GuiGraphics graphics, int mouseX, int mouseY) {
        int barX = leftPos + 8;
        int barY = topPos + 155;
        
        // Render quick access label
        graphics.drawString(font, "Quick Access", barX, barY - 10, 0xFFFFFF);
        
        for (int i = 0; i < 9; i++) {
            int x = barX + i * 18;
            
            // Render slot background with highlight
            graphics.blit(TEXTURE, x, barY, 276, 18, 18, 18);
            
            ItemStack stack = storage.getQuickAccessItem(i);
            if (!stack.isEmpty()) {
                graphics.renderItem(stack, x + 1, barY + 1);
                graphics.renderItemDecorations(font, stack, x + 1, barY + 1);
            }
        }
    }
    
    private void renderTooltips(GuiGraphics graphics, int mouseX, int mouseY) {
        // Render item tooltips for storage slots
        int slotsPerRow = 9;
        int rows = 6;
        int slotSize = 18;
        int startX = leftPos + 8;
        int startY = topPos + 35;
        
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < slotsPerRow; col++) {
                int x = startX + col * slotSize;
                int y = startY + row * slotSize;
                
                if (mouseX >= x && mouseX < x + 18 && mouseY >= y && mouseY < y + 18) {
                    int slotIndex = (currentPage * 54) + (row * slotsPerRow) + col;
                    if (slotIndex < storage.getTotalSlots()) {
                        ItemStack stack = storage.getStackInSlot(slotIndex);
                        if (!stack.isEmpty()) {
                            graphics.renderTooltip(font, stack, mouseX, mouseY);
                        }
                    }
                }
            }
        }
        
        // Render tooltips for quick access bar
        int barX = leftPos + 8;
        int barY = topPos + 155;
        
        for (int i = 0; i < 9; i++) {
            int x = barX + i * 18;
            
            if (mouseX >= x && mouseX < x + 18 && mouseY >= barY && mouseY < barY + 18) {
                ItemStack stack = storage.getQuickAccessItem(i);
                if (!stack.isEmpty()) {
                    graphics.renderTooltip(font, stack, mouseX, mouseY);
                }
            }
        }
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Handle slot clicks
        int slotsPerRow = 9;
        int rows = 6;
        int slotSize = 18;
        int startX = leftPos + 8;
        int startY = topPos + 35;
        
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < slotsPerRow; col++) {
                int x = startX + col * slotSize;
                int y = startY + row * slotSize;
                
                if (mouseX >= x && mouseX < x + 18 && mouseY >= y && mouseY < y + 18) {
                    int slotIndex = (currentPage * 54) + (row * slotsPerRow) + col;
                    if (slotIndex < storage.getTotalSlots()) {
                        handleSlotClick(slotIndex, button);
                        return true;
                    }
                }
            }
        }
        
        // Handle quick access clicks
        int barX = leftPos + 8;
        int barY = topPos + 155;
        
        for (int i = 0; i < 9; i++) {
            int x = barX + i * 18;
            
            if (mouseX >= x && mouseX < x + 18 && mouseY >= barY && mouseY < barY + 18) {
                handleQuickAccessClick(i, button);
                return true;
            }
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    private void handleSlotClick(int slotIndex, int button) {
        // TODO: Send packet to server to handle storage interaction
        // For now, just log the click
        System.out.println("Clicked storage slot " + slotIndex + " with button " + button);
    }
    
    private void handleQuickAccessClick(int slot, int button) {
        // TODO: Send packet to server to handle quick access interaction
        System.out.println("Clicked quick access slot " + slot + " with button " + button);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}