# Ethereal Mind - Technical Architecture

## Core Components

### 1. Entity System Architecture
```java
public class CosmoEntity extends MobEntity implements IAnimatable {
    private final AnimationFactory factory = new AnimationFactory(this);
    private final CosmoAI brain;
    private final DimensionalStorage storage;
    private final ParticleController particles;
    private UUID boundPlayer;
    
    // Singleton per player
    private static final Map<UUID, CosmoEntity> COSMOS = new HashMap<>();
}
```

### 2. Rendering Pipeline
```java
public class CosmoRenderer extends MobEntityRenderer<CosmoEntity> {
    private final EventHorizonLayer eventHorizon;
    private final VoidCenterLayer voidCenter;
    private final ParticleEmitterLayer particles;
    private final EnvironmentalEffectLayer effects;
    
    @Override
    public void render(CosmoEntity entity, float yaw, float partialTicks, 
                      MatrixStack matrices, VertexConsumerProvider vertices, int light) {
        // Multi-layer rendering with shaders
        renderEventHorizon(entity, matrices, vertices);
        renderVoidCenter(entity, matrices, vertices);
        renderParticles(entity, partialTicks);
        applyEnvironmentalEffects(entity, matrices);
    }
}
```

### 3. Storage System Design
```java
public class DimensionalStorage {
    private final StorageNode root;
    private final Map<String, Category> categories;
    private final SearchEngine searchEngine;
    private final AICategorizor categorizer;
    
    public class StorageNode {
        private final UUID id;
        private final ItemStack[] items;
        private final Map<String, Object> metadata;
        private final List<StorageNode> subNodes;
    }
}
```

### 4. AI System Architecture
```java
public class CosmoAI {
    private final NeuralNetwork personalityNet;
    private final PatternRecognizer patterns;
    private final DialogueGenerator dialogue;
    private final EmotionEngine emotions;
    
    public class PersonalityMatrix {
        float curiosity;
        float playfulness;
        float helpfulness;
        float wisdom;
        Map<String, Float> traits;
    }
}
```

### 5. Utility Framework
```java
public interface ICosmoUtility {
    String getId();
    void initialize(CosmoEntity cosmo);
    void render(GuiGraphics graphics);
    void tick();
    CompoundTag serialize();
    void deserialize(CompoundTag tag);
}

public class UtilityRegistry {
    private final Map<String, ICosmoUtility> utilities;
    
    public void register(ICosmoUtility utility) {
        utilities.put(utility.getId(), utility);
    }
}
```

### 6. Network Protocol
```java
public class CosmoNetwork {
    // Client -> Server
    public static class RequestStoragePacket {
        UUID cosmoId;
        StorageAction action;
        ItemStack item;
    }
    
    // Server -> Client
    public static class SyncCosmoPacket {
        UUID cosmoId;
        CosmoData data;
        ParticleState particles;
    }
    
    // Player -> Player (via COSMO)
    public static class VoidMailPacket {
        UUID sender;
        UUID recipient;
        ItemStack[] items;
        String message;
    }
}
```

### 7. Shader System
```glsl
// vertex shader
#version 150

in vec3 Position;
in vec2 UV0;
in vec4 Color;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 CosmoPos;
uniform float Time;

out vec2 texCoord;
out vec4 vertexColor;
out float distortionStrength;

void main() {
    vec3 pos = Position;
    float dist = length(pos - CosmoPos);
    
    // Gravitational distortion
    if (dist < 3.0) {
        float strength = 1.0 - (dist / 3.0);
        pos += normalize(CosmoPos - pos) * strength * 0.2 * sin(Time);
    }
    
    gl_Position = ProjMat * ModelViewMat * vec4(pos, 1.0);
    texCoord = UV0;
    vertexColor = Color;
    distortionStrength = 1.0 - (dist / 3.0);
}
```

### 8. Data Persistence
```java
public class CosmoDataManager {
    private static final String DATA_KEY = "ethereal_mind:cosmo_data";
    
    public void save(ServerPlayerEntity player) {
        CompoundTag tag = new CompoundTag();
        CosmoEntity cosmo = getCosmo(player);
        
        tag.put("personality", cosmo.getAI().serialize());
        tag.put("storage", cosmo.getStorage().serialize());
        tag.put("memories", cosmo.getMemories().serialize());
        tag.putLong("playtime", cosmo.getPlaytime());
        
        player.getPersistentData().put(DATA_KEY, tag);
    }
}
```

### 9. Performance Optimizations
```java
public class CosmoOptimizer {
    // LOD System
    public int getParticleCount(double distance) {
        if (distance > 64) return 0;
        if (distance > 32) return 5;
        if (distance > 16) return 15;
        return 30;
    }
    
    // Culling
    public boolean shouldRender(CosmoEntity cosmo, Frustum frustum) {
        return frustum.isVisible(cosmo.getBoundingBox().expand(3.0));
    }
    
    // Update throttling
    public boolean shouldUpdateAI(CosmoEntity cosmo) {
        return cosmo.age % 20 == 0; // Update AI every second
    }
}
```

### 10. API Design
```java
public interface IEtherealMindAPI {
    // Entity access
    CosmoEntity getCosmo(PlayerEntity player);
    
    // Storage access
    IStorage getStorage(PlayerEntity player);
    boolean addToStorage(PlayerEntity player, ItemStack stack);
    
    // Utility registration
    void registerUtility(ICosmoUtility utility);
    
    // AI interaction
    void teachCosmo(PlayerEntity player, String knowledge);
    void addDialogue(String context, String[] responses);
    
    // Events
    void onCosmoEvent(Consumer<CosmoEvent> handler);
}
```

## Database Schema (for web features)
```sql
-- Player profiles
CREATE TABLE player_profiles (
    uuid VARCHAR(36) PRIMARY KEY,
    playtime BIGINT,
    cosmo_level INT,
    personality_data JSON
);

-- Shared designs
CREATE TABLE shared_designs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    player_uuid VARCHAR(36),
    design_name VARCHAR(100),
    schematic_data BLOB,
    tags JSON,
    likes INT DEFAULT 0
);

-- COSMO network messages
CREATE TABLE void_mail (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sender_uuid VARCHAR(36),
    recipient_uuid VARCHAR(36),
    message TEXT,
    items JSON,
    sent_time TIMESTAMP,
    read BOOLEAN DEFAULT FALSE
);