package com.astrolabs.etherealmind.common.storage;

import com.astrolabs.etherealmind.common.entity.CosmoEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import java.util.*;

public class DimensionalStorage {
    private final CosmoEntity cosmo;
    private final Map<String, StorageCategory> categories = new HashMap<>();
    private final ItemStackHandler quickAccess = new ItemStackHandler(9);
    private final List<StoragePage> pages = new ArrayList<>();
    private final SearchEngine searchEngine = new SearchEngine();
    
    // Storage configuration
    private static final int SLOTS_PER_PAGE = 54;
    private static final int MAX_PAGES = 64;
    
    public DimensionalStorage(CosmoEntity cosmo) {
        this.cosmo = cosmo;
        initializeCategories();
        
        // Initialize with one page
        pages.add(new StoragePage());
    }
    
    private void initializeCategories() {
        categories.put("building", new StorageCategory("Building Materials", "building_block"));
        categories.put("tools", new StorageCategory("Tools & Weapons", "tool", "weapon"));
        categories.put("food", new StorageCategory("Food & Potions", "food", "potion"));
        categories.put("valuables", new StorageCategory("Valuables", "valuable", "rare"));
        categories.put("misc", new StorageCategory("Miscellaneous"));
    }
    
    public boolean addItem(ItemStack stack) {
        if (stack.isEmpty()) return false;
        
        // Try quick access first if it's a frequently used item
        if (isFrequentlyUsed(stack)) {
            ItemStack remainder = insertIntoHandler(quickAccess, stack);
            if (remainder.isEmpty()) return true;
            stack = remainder;
        }
        
        // Find appropriate category
        StorageCategory category = categorizeItem(stack);
        
        // Try to add to existing pages
        for (StoragePage page : pages) {
            ItemStack remainder = page.addItem(stack);
            if (remainder.isEmpty()) {
                return true;
            }
            stack = remainder;
        }
        
        // Create new page if needed
        if (pages.size() < MAX_PAGES) {
            StoragePage newPage = new StoragePage();
            ItemStack remainder = newPage.addItem(stack);
            pages.add(newPage);
            return remainder.isEmpty();
        }
        
        return false;
    }
    
    private ItemStack insertIntoHandler(ItemStackHandler handler, ItemStack stack) {
        ItemStack remainder = stack.copy();
        for (int i = 0; i < handler.getSlots(); i++) {
            remainder = handler.insertItem(i, remainder, false);
            if (remainder.isEmpty()) break;
        }
        return remainder;
    }
    
    private boolean isFrequentlyUsed(ItemStack stack) {
        // TODO: Track usage frequency
        return false;
    }
    
    private StorageCategory categorizeItem(ItemStack stack) {
        // AI-based categorization
        for (Map.Entry<String, StorageCategory> entry : categories.entrySet()) {
            if (entry.getValue().matches(stack)) {
                return entry.getValue();
            }
        }
        return categories.get("misc");
    }
    
    
    public ItemStack getStackInSlot(int slot) {
        int pageIndex = slot / SLOTS_PER_PAGE;
        int slotIndex = slot % SLOTS_PER_PAGE;
        
        if (pageIndex < pages.size()) {
            StoragePage page = pages.get(pageIndex);
            return page.getItems().getStackInSlot(slotIndex);
        }
        return ItemStack.EMPTY;
    }
    
    public ItemStack getQuickAccessItem(int slot) {
        if (slot >= 0 && slot < quickAccess.getSlots()) {
            return quickAccess.getStackInSlot(slot);
        }
        return ItemStack.EMPTY;
    }
    
    public List<ItemStack> search(String query) {
        return searchEngine.search(this, query);
    }
    
    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        
        // Save quick access
        tag.put("QuickAccess", quickAccess.serializeNBT());
        
        // Save pages
        ListTag pagesTag = new ListTag();
        for (StoragePage page : pages) {
            pagesTag.add(page.save());
        }
        tag.put("Pages", pagesTag);
        
        // Save categories
        CompoundTag categoriesTag = new CompoundTag();
        categories.forEach((key, category) -> categoriesTag.put(key, category.save()));
        tag.put("Categories", categoriesTag);
        
        return tag;
    }
    
    public void load(CompoundTag tag) {
        // Load quick access
        quickAccess.deserializeNBT(tag.getCompound("QuickAccess"));
        
        // Load pages
        pages.clear();
        ListTag pagesTag = tag.getList("Pages", Tag.TAG_COMPOUND);
        for (int i = 0; i < pagesTag.size(); i++) {
            StoragePage page = new StoragePage();
            page.load(pagesTag.getCompound(i));
            pages.add(page);
        }
        
        // Load categories
        CompoundTag categoriesTag = tag.getCompound("Categories");
        for (String key : categoriesTag.getAllKeys()) {
            if (categories.containsKey(key)) {
                categories.get(key).load(categoriesTag.getCompound(key));
            }
        }
    }
    
    // Getters
    public ItemStackHandler getQuickAccess() {
        return quickAccess;
    }
    
    public List<StoragePage> getPages() {
        return pages;
    }
    
    public Map<String, StorageCategory> getCategories() {
        return categories;
    }
    
    // Methods for Menu integration
    public void setStackInSlot(int slot, ItemStack stack) {
        int pageIndex = slot / SLOTS_PER_PAGE;
        int slotIndex = slot % SLOTS_PER_PAGE;
        
        if (pageIndex < pages.size()) {
            StoragePage page = pages.get(pageIndex);
            page.getItems().setStackInSlot(slotIndex, stack);
        }
    }
    
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        int pageIndex = slot / SLOTS_PER_PAGE;
        int slotIndex = slot % SLOTS_PER_PAGE;
        
        if (pageIndex < pages.size()) {
            StoragePage page = pages.get(pageIndex);
            return page.getItems().extractItem(slotIndex, amount, simulate);
        }
        return ItemStack.EMPTY;
    }
    
    public boolean isItemValid(int slot, ItemStack stack) {
        return !stack.isEmpty();
    }
    
    public ItemStack insertItem(ItemStack stack, boolean simulate) {
        ItemStack remaining = stack.copy();
        
        // Try to insert into existing stacks first
        for (StoragePage page : pages) {
            for (int i = 0; i < page.getItems().getSlots(); i++) {
                remaining = page.getItems().insertItem(i, remaining, simulate);
                if (remaining.isEmpty()) {
                    return ItemStack.EMPTY;
                }
            }
        }
        
        // Add new pages if needed and allowed
        if (!remaining.isEmpty() && pages.size() < getMaxPages()) {
            if (!simulate) {
                pages.add(new StoragePage());
            }
            // Try inserting into the new page
            StoragePage newPage = pages.get(pages.size() - 1);
            for (int i = 0; i < newPage.getItems().getSlots(); i++) {
                remaining = newPage.getItems().insertItem(i, remaining, simulate);
                if (remaining.isEmpty()) {
                    return ItemStack.EMPTY;
                }
            }
        }
        
        return remaining;
    }
    
    public int getTotalItemCount() {
        int count = 0;
        for (StoragePage page : pages) {
            for (int i = 0; i < page.getItems().getSlots(); i++) {
                ItemStack stack = page.getItems().getStackInSlot(i);
                if (!stack.isEmpty()) {
                    count += stack.getCount();
                }
            }
        }
        return count;
    }
    
    public int getTotalSlots() {
        return getMaxPages() * SLOTS_PER_PAGE;
    }
    
    public int getTotalPages() {
        return pages.size();
    }
    
    public int getMaxPages() {
        // Base pages + level bonus
        int basePages = 8; // Start with 8 pages (432 slots)
        float multiplier = cosmo.getLevelSystem().getStorageMultiplier();
        int totalPages = (int)(basePages * multiplier);
        return Math.min(MAX_PAGES, totalPages);
    }
    
    public static class StoragePage {
        private final ItemStackHandler items = new ItemStackHandler(SLOTS_PER_PAGE);
        private final Map<String, Object> metadata = new HashMap<>();
        
        public ItemStack addItem(ItemStack stack) {
            return insertIntoHandler(items, stack);
        }
        
        private ItemStack insertIntoHandler(ItemStackHandler handler, ItemStack stack) {
            ItemStack remainder = stack.copy();
            for (int i = 0; i < handler.getSlots(); i++) {
                remainder = handler.insertItem(i, remainder, false);
                if (remainder.isEmpty()) break;
            }
            return remainder;
        }
        
        public CompoundTag save() {
            CompoundTag tag = new CompoundTag();
            tag.put("Items", items.serializeNBT());
            // Save metadata
            CompoundTag metaTag = new CompoundTag();
            metadata.forEach((key, value) -> {
                if (value instanceof String) {
                    metaTag.putString(key, (String) value);
                } else if (value instanceof Integer) {
                    metaTag.putInt(key, (Integer) value);
                }
            });
            tag.put("Metadata", metaTag);
            return tag;
        }
        
        public void load(CompoundTag tag) {
            items.deserializeNBT(tag.getCompound("Items"));
            // Load metadata
            metadata.clear();
            CompoundTag metaTag = tag.getCompound("Metadata");
            for (String key : metaTag.getAllKeys()) {
                metadata.put(key, metaTag.get(key));
            }
        }
        
        public ItemStackHandler getItems() {
            return items;
        }
    }
    
    public static class StorageCategory {
        private final String name;
        private final Set<String> tags;
        private int itemCount = 0;
        
        public StorageCategory(String name, String... tags) {
            this.name = name;
            this.tags = new HashSet<>(Arrays.asList(tags));
        }
        
        public boolean matches(ItemStack stack) {
            // TODO: Implement smart categorization
            return false;
        }
        
        public CompoundTag save() {
            CompoundTag tag = new CompoundTag();
            tag.putString("Name", name);
            tag.putInt("ItemCount", itemCount);
            ListTag tagsTag = new ListTag();
            tags.forEach(t -> {
                CompoundTag tagTag = new CompoundTag();
                tagTag.putString("Tag", t);
                tagsTag.add(tagTag);
            });
            tag.put("Tags", tagsTag);
            return tag;
        }
        
        public void load(CompoundTag tag) {
            itemCount = tag.getInt("ItemCount");
            tags.clear();
            ListTag tagsTag = tag.getList("Tags", Tag.TAG_COMPOUND);
            for (int i = 0; i < tagsTag.size(); i++) {
                tags.add(tagsTag.getCompound(i).getString("Tag"));
            }
        }
        
        public String getName() {
            return name;
        }
        
        public int getItemCount() {
            return itemCount;
        }
    }
    
    public static class SearchEngine {
        public List<ItemStack> search(DimensionalStorage storage, String query) {
            List<ItemStack> results = new ArrayList<>();
            
            // Search through all pages
            for (StoragePage page : storage.pages) {
                for (int i = 0; i < page.items.getSlots(); i++) {
                    ItemStack stack = page.items.getStackInSlot(i);
                    if (!stack.isEmpty() && matches(stack, query)) {
                        results.add(stack);
                    }
                }
            }
            
            return results;
        }
        
        private boolean matches(ItemStack stack, String query) {
            String itemName = stack.getDisplayName().getString().toLowerCase();
            return itemName.contains(query.toLowerCase());
        }
    }
}