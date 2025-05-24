package com.astrolabs.etherealmind.client.gui;

import com.astrolabs.etherealmind.EtherealMind;
import com.astrolabs.etherealmind.common.menu.DimensionalStorageMenu;
import com.astrolabs.etherealmind.common.network.NetworkHandler;
import com.astrolabs.etherealmind.common.network.packets.StorageActionPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DimensionalStorageScreen extends AbstractContainerScreen<DimensionalStorageMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(EtherealMind.MOD_ID, "textures/gui/dimensional_storage.png");
    
    private EditBox searchBox;
    private Button prevPageButton;
    private Button nextPageButton;
    private Button[] categoryButtons;
    
    // Category filter
    private String currentCategory = "All";
    
    public DimensionalStorageScreen(DimensionalStorageMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 276;
        this.imageHeight = 266; // Increased to accommodate player inventory
    }
    
    @Override
    protected void init() {
        super.init();
        
        // Search box
        this.searchBox = new EditBox(this.font, leftPos + 8, topPos + 8, 120, 20, Component.translatable("gui.etherealmind.search"));
        this.searchBox.setMaxLength(50);
        this.searchBox.setResponder(this::onSearchChanged);
        this.addRenderableWidget(this.searchBox);
        
        // Page navigation
        this.prevPageButton = Button.builder(Component.literal("<"), (button) -> {
            if (menu.getCurrentPage() > 0) {
                changePage(menu.getCurrentPage() - 1);
            }
        }).bounds(leftPos + 8, topPos + 145, 20, 20).build();
        
        this.nextPageButton = Button.builder(Component.literal(">"), (button) -> {
            if (menu.getCurrentPage() < menu.getStorage().getTotalPages() - 1) {
                changePage(menu.getCurrentPage() + 1);
            }
        }).bounds(leftPos + imageWidth - 28, topPos + 145, 20, 20).build();
        
        this.addRenderableWidget(prevPageButton);
        this.addRenderableWidget(nextPageButton);
        
        // Category buttons
        String[] categories = {"All", "Blocks", "Items", "Tools", "Food", "Combat"};
        categoryButtons = new Button[categories.length];
        
        for (int i = 0; i < categories.length; i++) {
            final String category = categories[i];
            categoryButtons[i] = Button.builder(Component.literal(category), (button) -> {
                setCategory(category);
            }).bounds(leftPos + imageWidth - 55, topPos + 30 + (i * 22), 50, 20).build();
            this.addRenderableWidget(categoryButtons[i]);
        }
        
        updateButtons();
    }
    
    private void changePage(int newPage) {
        // Send packet to server to change page
        NetworkHandler.sendToServer(new StorageActionPacket(
            StorageActionPacket.Action.CHANGE_PAGE, 
            newPage, 
            -1, 
            -1
        ));
        menu.setPage(newPage);
        updateButtons();
    }
    
    private void setCategory(String category) {
        this.currentCategory = category;
        // Send packet to server to filter by category
        NetworkHandler.sendToServer(new StorageActionPacket(
            StorageActionPacket.Action.SET_CATEGORY,
            -1,
            -1,
            -1,
            category
        ));
        updateCategoryButtons();
    }
    
    private void onSearchChanged(String text) {
        // Send search query to server
        NetworkHandler.sendToServer(new StorageActionPacket(
            StorageActionPacket.Action.SEARCH,
            -1,
            -1,
            -1,
            text
        ));
    }
    
    private void updateButtons() {
        prevPageButton.active = menu.getCurrentPage() > 0;
        nextPageButton.active = menu.getCurrentPage() < menu.getStorage().getTotalPages() - 1;
    }
    
    private void updateCategoryButtons() {
        for (int i = 0; i < categoryButtons.length; i++) {
            categoryButtons[i].active = !categoryButtons[i].getMessage().getString().equals(currentCategory);
        }
    }
    
    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }
    
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
        
        // Render page indicator
        String pageText = String.format("Page %d/%d", menu.getCurrentPage() + 1, menu.getStorage().getTotalPages());
        graphics.drawString(this.font, pageText, leftPos + imageWidth / 2 - font.width(pageText) / 2, topPos + 150, 0xFFFFFF, false);
        
        // Render storage info
        int totalItems = menu.getStorage().getTotalItemCount();
        int maxItems = menu.getStorage().getTotalSlots();
        String storageText = String.format("Storage: %d/%d", totalItems, maxItems);
        graphics.drawString(this.font, storageText, leftPos + 135, topPos + 12, 0xAAAAAA, false);
        
        // Render category indicator
        graphics.drawString(this.font, "Category: " + currentCategory, leftPos + imageWidth - 120, topPos + 12, 0xAAAAAA, false);
    }
    
    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        // Title
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
        
        // Quick Access label
        graphics.drawString(this.font, "Quick Access", 8, 162, 4210752, false);
        
        // Player inventory label
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY + 10, 4210752, false);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.searchBox.isFocused()) {
            return this.searchBox.keyPressed(keyCode, scanCode, modifiers);
        }
        
        // Quick access hotkeys (1-9)
        if (keyCode >= 49 && keyCode <= 57) { // Keys 1-9
            int slot = keyCode - 49;
            NetworkHandler.sendToServer(new StorageActionPacket(
                StorageActionPacket.Action.QUICK_ACCESS_HOTKEY,
                slot,
                -1,
                -1
            ));
            return true;
        }
        
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}