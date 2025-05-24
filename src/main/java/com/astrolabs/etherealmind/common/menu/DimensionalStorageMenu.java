package com.astrolabs.etherealmind.common.menu;

import com.astrolabs.etherealmind.common.entity.CosmoEntity;
import com.astrolabs.etherealmind.common.registry.MenuRegistry;
import com.astrolabs.etherealmind.common.storage.DimensionalStorage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class DimensionalStorageMenu extends AbstractContainerMenu {
    private DimensionalStorage storage;
    private final Player player;
    private CosmoEntity cosmo;
    private int currentPage = 0;
    
    // Storage layout constants
    private static final int STORAGE_ROWS = 6;
    private static final int STORAGE_COLS = 9;
    private static final int SLOTS_PER_PAGE = STORAGE_ROWS * STORAGE_COLS;
    private static final int QUICK_ACCESS_SLOTS = 9;
    
    // GUI positioning
    private static final int STORAGE_START_X = 8;
    private static final int STORAGE_START_Y = 35;
    private static final int QUICK_ACCESS_Y = 155;
    private static final int PLAYER_INV_Y = 180;
    
    // Client constructor
    public DimensionalStorageMenu(int containerId, Inventory playerInventory, FriendlyByteBuf data) {
        super(MenuRegistry.DIMENSIONAL_STORAGE_MENU.get(), containerId);
        this.player = playerInventory.player;
        
        // Read entity ID from buffer
        int entityId = data.readVarInt();
        if (player.level().getEntity(entityId) instanceof CosmoEntity entity) {
            this.cosmo = entity;
            this.storage = entity.getStorage();
        } else {
            // Create temporary storage for client
            this.storage = null;
            this.cosmo = null;
        }
        
        // Add all slots
        if (this.storage != null) {
            addStorageSlots();
            addQuickAccessSlots();
        }
        addPlayerInventory(playerInventory);
    }
    
    // Server constructor
    public DimensionalStorageMenu(int containerId, Inventory playerInventory, DimensionalStorage storage) {
        super(MenuRegistry.DIMENSIONAL_STORAGE_MENU.get(), containerId);
        this.storage = storage;
        this.player = playerInventory.player;
        this.cosmo = storage.getOwner();
        
        // Add storage slots for current page
        addStorageSlots();
        
        // Add quick access slots
        addQuickAccessSlots();
        
        // Add player inventory slots
        addPlayerInventory(playerInventory);
    }
    
    private void addStorageSlots() {
        // Clear existing storage slots when changing pages
        this.slots.removeIf(slot -> slot instanceof StorageSlot);
        
        if (storage == null) return; // Skip if storage not synced yet
        
        int startSlot = currentPage * SLOTS_PER_PAGE;
        
        for (int row = 0; row < STORAGE_ROWS; row++) {
            for (int col = 0; col < STORAGE_COLS; col++) {
                int slotIndex = startSlot + (row * STORAGE_COLS) + col;
                int x = STORAGE_START_X + col * 18;
                int y = STORAGE_START_Y + row * 18;
                
                if (slotIndex < storage.getTotalSlots()) {
                    this.addSlot(new StorageSlot(storage, slotIndex, x, y));
                }
            }
        }
    }
    
    private void addQuickAccessSlots() {
        if (storage == null) return; // Skip if storage not synced yet
        
        IItemHandler quickAccess = storage.getQuickAccess();
        
        for (int i = 0; i < QUICK_ACCESS_SLOTS; i++) {
            int x = STORAGE_START_X + i * 18;
            this.addSlot(new QuickAccessSlot(quickAccess, i, x, QUICK_ACCESS_Y));
        }
    }
    
    private void addPlayerInventory(Inventory playerInventory) {
        // Player inventory
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int x = STORAGE_START_X + col * 18;
                int y = PLAYER_INV_Y + row * 18;
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, x, y));
            }
        }
        
        // Player hotbar
        for (int col = 0; col < 9; col++) {
            int x = STORAGE_START_X + col * 18;
            int y = PLAYER_INV_Y + 58;
            this.addSlot(new Slot(playerInventory, col, x, y));
        }
    }
    
    public void setPage(int page) {
        if (page >= 0 && page < storage.getTotalPages()) {
            this.currentPage = page;
            // Recreate storage slots for new page
            addStorageSlots();
        }
    }
    
    public int getCurrentPage() {
        return currentPage;
    }
    
    public DimensionalStorage getStorage() {
        return storage;
    }
    
    public CosmoEntity getCosmo() {
        return cosmo;
    }
    
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack returnStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            returnStack = slotStack.copy();
            
            int storageSlotCount = SLOTS_PER_PAGE;
            int quickAccessStart = storageSlotCount;
            int quickAccessEnd = quickAccessStart + QUICK_ACCESS_SLOTS;
            int playerInvStart = quickAccessEnd;
            int playerInvEnd = playerInvStart + 36;
            
            // From storage to player inventory
            if (index < storageSlotCount) {
                if (!this.moveItemStackTo(slotStack, playerInvStart, playerInvEnd, true)) {
                    return ItemStack.EMPTY;
                }
            }
            // From quick access to player inventory
            else if (index >= quickAccessStart && index < quickAccessEnd) {
                if (!this.moveItemStackTo(slotStack, playerInvStart, playerInvEnd, true)) {
                    return ItemStack.EMPTY;
                }
            }
            // From player inventory to storage/quick access
            else if (index >= playerInvStart && index < playerInvEnd) {
                // Try quick access first for frequently used items
                if (!this.moveItemStackTo(slotStack, quickAccessStart, quickAccessEnd, false)) {
                    // Then try storage
                    if (!this.moveItemStackTo(slotStack, 0, storageSlotCount, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }
            
            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            
            if (slotStack.getCount() == returnStack.getCount()) {
                return ItemStack.EMPTY;
            }
            
            slot.onTake(player, slotStack);
        }
        
        return returnStack;
    }
    
    @Override
    public boolean stillValid(Player player) {
        return this.player == player && storage != null;
    }
    
    public void setStorage(DimensionalStorage storage) {
        this.storage = storage;
        if (storage != null && this.slots.stream().noneMatch(slot -> slot instanceof StorageSlot)) {
            // Add storage slots if they haven't been added yet
            addStorageSlots();
            addQuickAccessSlots();
        }
    }
    
    // Custom slot for storage
    public static class StorageSlot extends Slot {
        private final DimensionalStorage storage;
        private final int storageIndex;
        
        public StorageSlot(DimensionalStorage storage, int index, int x, int y) {
            super(new DummyContainer(), index, x, y);
            this.storage = storage;
            this.storageIndex = index;
        }
        
        @Override
        public ItemStack getItem() {
            return storage.getStackInSlot(storageIndex);
        }
        
        @Override
        public void set(ItemStack stack) {
            storage.setStackInSlot(storageIndex, stack);
            this.setChanged();
        }
        
        @Override
        public ItemStack remove(int amount) {
            ItemStack stack = storage.extractItem(storageIndex, amount, false);
            this.setChanged();
            return stack;
        }
        
        @Override
        public boolean mayPlace(ItemStack stack) {
            return storage.isItemValid(storageIndex, stack);
        }
        
        @Override
        public int getMaxStackSize() {
            return 64;
        }
    }
    
    // Custom slot for quick access
    public static class QuickAccessSlot extends SlotItemHandler {
        public QuickAccessSlot(IItemHandler itemHandler, int index, int x, int y) {
            super(itemHandler, index, x, y);
        }
        
        @Override
        public int getMaxStackSize() {
            return 64;
        }
    }
    
    // Dummy container to satisfy Slot's requirement for a non-null container
    private static class DummyContainer implements net.minecraft.world.Container {
        @Override
        public int getContainerSize() {
            return 0;
        }
        
        @Override
        public boolean isEmpty() {
            return true;
        }
        
        @Override
        public ItemStack getItem(int index) {
            return ItemStack.EMPTY;
        }
        
        @Override
        public ItemStack removeItem(int index, int count) {
            return ItemStack.EMPTY;
        }
        
        @Override
        public ItemStack removeItemNoUpdate(int index) {
            return ItemStack.EMPTY;
        }
        
        @Override
        public void setItem(int index, ItemStack stack) {
        }
        
        @Override
        public void setChanged() {
        }
        
        @Override
        public boolean stillValid(Player player) {
            return true;
        }
        
        @Override
        public void clearContent() {
        }
    }
}