# EtherealMind API Documentation

Developer documentation for integrating with EtherealMind.

## Table of Contents
1. [Getting Started](#getting-started)
2. [Core API](#core-api)
3. [COSMO Interface](#cosmo-interface)
4. [Storage API](#storage-api)
5. [AI System API](#ai-system-api)
6. [Events](#events)
7. [Examples](#examples)

## Getting Started

### Maven/Gradle Setup

#### Maven
```xml
<dependency>
    <groupId>com.astrolabs</groupId>
    <artifactId>etherealmind-api</artifactId>
    <version>1.0.0</version>
    <scope>provided</scope>
</dependency>
```

#### Gradle
```gradle
dependencies {
    compileOnly 'com.astrolabs:etherealmind-api:1.0.0'
}
```

### Basic Usage
```java
import com.astrolabs.etherealmind.api.EtherealMindAPI;
import com.astrolabs.etherealmind.api.ICosmo;

public class MyMod {
    public void onPlayerInteract(Player player) {
        ICosmo cosmo = EtherealMindAPI.getCosmo(player);
        if (cosmo != null) {
            cosmo.openStorage();
        }
    }
}
```

## Core API

### EtherealMindAPI Class
Main entry point for the API.

```java
public class EtherealMindAPI {
    /**
     * Get a player's COSMO companion
     * @param player The player
     * @return The COSMO instance or null
     */
    @Nullable
    public static ICosmo getCosmo(Player player);
    
    /**
     * Check if a player has a COSMO
     * @param player The player
     * @return true if player has COSMO
     */
    public static boolean hasCosmo(Player player);
    
    /**
     * Register a custom AI behavior
     * @param behavior The behavior to register
     */
    public static void registerBehavior(IAIBehavior behavior);
    
    /**
     * Register a storage category
     * @param category The category to register
     */
    public static void registerCategory(IStorageCategory category);
}
```

## COSMO Interface

### ICosmo Interface
```java
public interface ICosmo {
    /**
     * Get the bonded player
     * @return The player this COSMO is bonded to
     */
    Player getBondedPlayer();
    
    /**
     * Get current trust level
     * @return Trust level (0-5)
     */
    int getTrustLevel();
    
    /**
     * Add trust points
     * @param amount Amount to add
     */
    void addTrust(float amount);
    
    /**
     * Get current mood
     * @return Current mood state
     */
    CosmoMood getMood();
    
    /**
     * Set mood
     * @param mood New mood state
     */
    void setMood(CosmoMood mood);
    
    /**
     * Get the AI brain
     * @return The AI system
     */
    ICosmoAI getAI();
    
    /**
     * Get dimensional storage
     * @return The storage system
     */
    IDimensionalStorage getStorage();
    
    /**
     * Open storage for player
     */
    void openStorage();
    
    /**
     * Get entity
     * @return The entity instance
     */
    LivingEntity getEntity();
}
```

### CosmoMood Enum
```java
public enum CosmoMood {
    HAPPY(0x00FF00),
    CURIOUS(0x0000FF),
    PLAYFUL(0xFF00FF),
    PROTECTIVE(0xFF0000),
    NEUTRAL(0xFFFFFF);
    
    private final int particleColor;
    
    public int getParticleColor() {
        return particleColor;
    }
}
```

## Storage API

### IDimensionalStorage Interface
```java
public interface IDimensionalStorage {
    /**
     * Get total pages
     * @return Number of pages
     */
    int getTotalPages();
    
    /**
     * Get items on a page
     * @param page Page number (0-indexed)
     * @return List of items on the page
     */
    List<ItemStack> getPage(int page);
    
    /**
     * Add item to storage
     * @param stack Item to add
     * @return Remainder that couldn't fit
     */
    ItemStack addItem(ItemStack stack);
    
    /**
     * Search storage
     * @param query Search query
     * @return Matching items
     */
    List<ItemStack> search(String query);
    
    /**
     * Get quick access items
     * @return Quick access bar contents
     */
    ItemStack[] getQuickAccess();
    
    /**
     * Set quick access slot
     * @param slot Slot index (0-8)
     * @param stack Item to set
     */
    void setQuickAccess(int slot, ItemStack stack);
    
    /**
     * Register storage listener
     * @param listener The listener
     */
    void addListener(IStorageListener listener);
}
```

### IStorageListener Interface
```java
public interface IStorageListener {
    /**
     * Called when an item is added
     * @param stack The item added
     * @param page Page it was added to
     * @param slot Slot it was added to
     */
    void onItemAdded(ItemStack stack, int page, int slot);
    
    /**
     * Called when an item is removed
     * @param stack The item removed
     * @param page Page it was removed from
     * @param slot Slot it was removed from
     */
    void onItemRemoved(ItemStack stack, int page, int slot);
    
    /**
     * Called when storage is reorganized
     */
    void onStorageReorganized();
}
```

## AI System API

### ICosmoAI Interface
```java
public interface ICosmoAI {
    /**
     * Get personality matrix
     * @return The personality system
     */
    IPersonalityMatrix getPersonality();
    
    /**
     * Get memory bank
     * @return The memory system
     */
    IMemoryBank getMemory();
    
    /**
     * Add custom behavior
     * @param behavior Behavior to add
     * @param priority Priority (higher = more important)
     */
    void addBehavior(IAIBehavior behavior, int priority);
    
    /**
     * Remove custom behavior
     * @param behavior Behavior to remove
     */
    void removeBehavior(IAIBehavior behavior);
    
    /**
     * Force AI update
     */
    void forceUpdate();
}
```

### IPersonalityMatrix Interface
```java
public interface IPersonalityMatrix {
    /**
     * Get trait value
     * @param trait The trait
     * @return Value (0-100)
     */
    float getTrait(PersonalityTrait trait);
    
    /**
     * Set trait value
     * @param trait The trait
     * @param value New value (0-100)
     */
    void setTrait(PersonalityTrait trait, float value);
    
    /**
     * Get all traits
     * @return Map of traits to values
     */
    Map<PersonalityTrait, Float> getAllTraits();
}
```

### Custom AI Behavior
```java
public abstract class IAIBehavior {
    /**
     * Check if behavior should run
     * @param cosmo The COSMO instance
     * @return true if should execute
     */
    public abstract boolean shouldExecute(ICosmo cosmo);
    
    /**
     * Execute the behavior
     * @param cosmo The COSMO instance
     */
    public abstract void execute(ICosmo cosmo);
    
    /**
     * Get behavior name
     * @return Unique identifier
     */
    public abstract String getName();
}
```

## Events

### CosmoEvents
```java
@Mod.EventBusSubscriber(modid = "yourmod")
public class MyEventHandler {
    
    @SubscribeEvent
    public static void onCosmoSpawn(CosmoSpawnEvent event) {
        ICosmo cosmo = event.getCosmo();
        Player player = event.getPlayer();
        // Handle spawn
    }
    
    @SubscribeEvent
    public static void onStorageOpen(StorageOpenEvent event) {
        if (event.isCancelable() && shouldPrevent(event.getPlayer())) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public static void onTrustChange(TrustChangeEvent event) {
        float oldTrust = event.getOldTrust();
        float newTrust = event.getNewTrust();
        // React to trust changes
    }
    
    @SubscribeEvent
    public static void onMoodChange(MoodChangeEvent event) {
        CosmoMood oldMood = event.getOldMood();
        CosmoMood newMood = event.getNewMood();
        // React to mood changes
    }
}
```

### Event List
- `CosmoSpawnEvent` - When COSMO is spawned
- `CosmoDeathEvent` - When COSMO dies
- `StorageOpenEvent` - When storage is opened
- `StorageCloseEvent` - When storage is closed
- `ItemStoredEvent` - When item is stored
- `ItemRetrievedEvent` - When item is retrieved
- `TrustChangeEvent` - When trust level changes
- `MoodChangeEvent` - When mood changes
- `AIDecisionEvent` - When AI makes a decision

## Examples

### Custom Storage Category
```java
public class GemCategory implements IStorageCategory {
    @Override
    public String getName() {
        return "gems";
    }
    
    @Override
    public String getDisplayName() {
        return "Precious Gems";
    }
    
    @Override
    public boolean matches(ItemStack stack) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        return id != null && id.getPath().contains("gem");
    }
    
    @Override
    public int getPriority() {
        return 100; // Higher priority than default
    }
}

// Register in mod initialization
EtherealMindAPI.registerCategory(new GemCategory());
```

### Custom AI Behavior
```java
public class GreetingBehavior extends IAIBehavior {
    private long lastGreeting = 0;
    
    @Override
    public String getName() {
        return "greeting_behavior";
    }
    
    @Override
    public boolean shouldExecute(ICosmo cosmo) {
        long currentTime = cosmo.getEntity().level().getGameTime();
        return currentTime - lastGreeting > 24000; // Once per day
    }
    
    @Override
    public void execute(ICosmo cosmo) {
        Player player = cosmo.getBondedPlayer();
        if (player != null) {
            // Spawn happy particles
            cosmo.setMood(CosmoMood.HAPPY);
            
            // Send greeting message
            player.sendSystemMessage(
                Component.literal("COSMO is happy to see you!")
            );
            
            lastGreeting = cosmo.getEntity().level().getGameTime();
        }
    }
}
```

### Storage Integration
```java
public class AutoSorter {
    public void sortPlayerInventory(Player player) {
        ICosmo cosmo = EtherealMindAPI.getCosmo(player);
        if (cosmo == null || cosmo.getTrustLevel() < 3) {
            return;
        }
        
        IDimensionalStorage storage = cosmo.getStorage();
        
        // Move items from player inventory to storage
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && shouldStore(stack)) {
                ItemStack remainder = storage.addItem(stack);
                player.getInventory().setItem(i, remainder);
            }
        }
    }
    
    private boolean shouldStore(ItemStack stack) {
        // Custom logic for what to store
        return !isEssentialItem(stack);
    }
}
```

---

*For more examples, see our [GitHub repository](https://github.com/AstroLabs-AI/EtherealMind/tree/main/examples)*