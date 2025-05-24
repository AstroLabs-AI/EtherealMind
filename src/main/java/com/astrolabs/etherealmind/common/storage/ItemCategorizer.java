package com.astrolabs.etherealmind.common.storage;

import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.tags.ItemTags;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ItemCategorizer {
    
    public enum ItemCategory {
        FOOD("food", "🍖"),
        TOOLS("tools", "⚒️"),
        WEAPONS("weapons", "⚔️"),
        ARMOR("armor", "🛡️"),
        BLOCKS("blocks", "🧱"),
        MATERIALS("materials", "🔧"),
        VALUABLES("valuables", "💎"),
        POTIONS("potions", "🧪"),
        MISC("misc", "📦");
        
        private final String name;
        private final String icon;
        
        ItemCategory(String name, String icon) {
            this.name = name;
            this.icon = icon;
        }
        
        public String getName() { return name; }
        public String getIcon() { return icon; }
    }
    
    private static final Map<String, ItemCategory> categoryAliases = new HashMap<>();
    static {
        // Food aliases
        categoryAliases.put("food", ItemCategory.FOOD);
        categoryAliases.put("eat", ItemCategory.FOOD);
        categoryAliases.put("edible", ItemCategory.FOOD);
        categoryAliases.put("hungry", ItemCategory.FOOD);
        categoryAliases.put("meal", ItemCategory.FOOD);
        
        // Tool aliases
        categoryAliases.put("tool", ItemCategory.TOOLS);
        categoryAliases.put("tools", ItemCategory.TOOLS);
        categoryAliases.put("equipment", ItemCategory.TOOLS);
        categoryAliases.put("pickaxe", ItemCategory.TOOLS);
        categoryAliases.put("axe", ItemCategory.TOOLS);
        categoryAliases.put("shovel", ItemCategory.TOOLS);
        categoryAliases.put("hoe", ItemCategory.TOOLS);
        
        // Weapon aliases
        categoryAliases.put("weapon", ItemCategory.WEAPONS);
        categoryAliases.put("weapons", ItemCategory.WEAPONS);
        categoryAliases.put("sword", ItemCategory.WEAPONS);
        categoryAliases.put("bow", ItemCategory.WEAPONS);
        categoryAliases.put("combat", ItemCategory.WEAPONS);
        
        // Armor aliases
        categoryAliases.put("armor", ItemCategory.ARMOR);
        categoryAliases.put("armour", ItemCategory.ARMOR);
        categoryAliases.put("protection", ItemCategory.ARMOR);
        categoryAliases.put("helmet", ItemCategory.ARMOR);
        categoryAliases.put("chestplate", ItemCategory.ARMOR);
        categoryAliases.put("leggings", ItemCategory.ARMOR);
        categoryAliases.put("boots", ItemCategory.ARMOR);
        
        // Block aliases
        categoryAliases.put("block", ItemCategory.BLOCKS);
        categoryAliases.put("blocks", ItemCategory.BLOCKS);
        categoryAliases.put("building", ItemCategory.BLOCKS);
        categoryAliases.put("construction", ItemCategory.BLOCKS);
        
        // Material aliases
        categoryAliases.put("material", ItemCategory.MATERIALS);
        categoryAliases.put("materials", ItemCategory.MATERIALS);
        categoryAliases.put("resource", ItemCategory.MATERIALS);
        categoryAliases.put("resources", ItemCategory.MATERIALS);
        categoryAliases.put("ingredient", ItemCategory.MATERIALS);
        
        // Valuable aliases
        categoryAliases.put("valuable", ItemCategory.VALUABLES);
        categoryAliases.put("valuables", ItemCategory.VALUABLES);
        categoryAliases.put("precious", ItemCategory.VALUABLES);
        categoryAliases.put("diamond", ItemCategory.VALUABLES);
        categoryAliases.put("emerald", ItemCategory.VALUABLES);
        categoryAliases.put("gold", ItemCategory.VALUABLES);
        categoryAliases.put("netherite", ItemCategory.VALUABLES);
        
        // Potion aliases
        categoryAliases.put("potion", ItemCategory.POTIONS);
        categoryAliases.put("potions", ItemCategory.POTIONS);
        categoryAliases.put("brew", ItemCategory.POTIONS);
        categoryAliases.put("effect", ItemCategory.POTIONS);
    }
    
    public static ItemCategory categorizeItem(ItemStack stack) {
        if (stack.isEmpty()) return ItemCategory.MISC;
        
        Item item = stack.getItem();
        
        // Food items
        if (item.isEdible() || item instanceof BowlFoodItem) {
            return ItemCategory.FOOD;
        }
        
        // Tools
        if (item instanceof TieredItem || item instanceof ShearsItem || 
            item instanceof FishingRodItem || item instanceof FlintAndSteelItem) {
            if (item instanceof SwordItem || item instanceof TridentItem) {
                return ItemCategory.WEAPONS;
            }
            return ItemCategory.TOOLS;
        }
        
        // Weapons
        if (item instanceof BowItem || item instanceof CrossbowItem || 
            item instanceof SwordItem || item instanceof TridentItem) {
            return ItemCategory.WEAPONS;
        }
        
        // Armor
        if (item instanceof ArmorItem || item instanceof ElytraItem || 
            item instanceof ShieldItem) {
            return ItemCategory.ARMOR;
        }
        
        // Potions
        if (item instanceof PotionItem || item instanceof SplashPotionItem || 
            item instanceof LingeringPotionItem) {
            return ItemCategory.POTIONS;
        }
        
        // Blocks
        if (item instanceof BlockItem) {
            return ItemCategory.BLOCKS;
        }
        
        // Valuables (by item name)
        String itemName = item.toString().toLowerCase();
        if (itemName.contains("diamond") || itemName.contains("emerald") || 
            itemName.contains("gold") || itemName.contains("netherite") ||
            itemName.contains("ancient_debris")) {
            return ItemCategory.VALUABLES;
        }
        
        // Default to materials for non-block items
        return ItemCategory.MATERIALS;
    }
    
    public static ItemCategory getCategoryFromAlias(String alias) {
        return categoryAliases.getOrDefault(alias.toLowerCase(), null);
    }
    
    public static boolean itemMatchesCategory(ItemStack stack, ItemCategory category) {
        return categorizeItem(stack) == category;
    }
    
    public static Set<ItemStack> filterByCategory(Iterable<ItemStack> items, ItemCategory category) {
        Set<ItemStack> filtered = new HashSet<>();
        for (ItemStack stack : items) {
            if (itemMatchesCategory(stack, category)) {
                filtered.add(stack);
            }
        }
        return filtered;
    }
    
    public static String getCategoryDescription(ItemCategory category) {
        switch (category) {
            case FOOD:
                return "Food items that restore hunger";
            case TOOLS:
                return "Tools for mining, farming, and crafting";
            case WEAPONS:
                return "Weapons for combat";
            case ARMOR:
                return "Protective gear and equipment";
            case BLOCKS:
                return "Building blocks and decorations";
            case MATERIALS:
                return "Crafting materials and resources";
            case VALUABLES:
                return "Rare and valuable items";
            case POTIONS:
                return "Potions and magical brews";
            default:
                return "Miscellaneous items";
        }
    }
}