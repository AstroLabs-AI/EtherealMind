# Getting Started with Ethereal Mind Development

## Prerequisites
- Java 17 or higher
- Minecraft Development environment (IntelliJ IDEA recommended)
- Git
- Gradle 8.0+

## Quick Start

### 1. Choose Your Mod Loader
```bash
# For Fabric (Recommended)
git clone https://github.com/yourusername/EtherealMind.git
cd EtherealMind
./gradlew genEclipseRuns  # or genIntellijRuns

# For Forge
git clone -b forge https://github.com/yourusername/EtherealMind.git
cd EtherealMind
./gradlew genEclipseRuns
```

### 2. Project Structure Setup
```bash
# Create initial directory structure
mkdir -p src/main/java/com/astrolabs/etherealmind/{common,client,server}
mkdir -p src/main/resources/assets/etherealmind/{textures,models,sounds}
mkdir -p src/main/resources/data/etherealmind/{recipes,loot_tables,advancements}
```

### 3. Initial Dependencies (build.gradle)
```gradle
dependencies {
    // Fabric
    modImplementation "net.fabricmc.fabric-api:fabric-api:${fabric_version}"
    modImplementation "com.terraformersmc:modmenu:${modmenu_version}"
    modImplementation "me.shedaniel.cloth:cloth-config-fabric:${cloth_config_version}"
    modImplementation "software.bernie.geckolib:geckolib-fabric-${minecraft_version}:${geckolib_version}"
    
    // Common libraries
    implementation "com.google.code.gson:gson:2.10.1"
    implementation "org.apache.commons:commons-lang3:3.12.0"
}
```

## Development Workflow

### Week 1-2: Foundation
1. Set up basic mod structure
2. Create COSMO entity class
3. Implement entity spawning/binding
4. Basic renderer setup

### Week 3-4: Visual System
1. Implement particle systems
2. Create shader effects
3. Add animation controllers
4. Environmental effects

### Week 5-6: Storage System
1. Design storage data structure
2. Create GUI screens
3. Implement search functionality
4. Add categorization system

### Week 7-8: Core Utilities
1. Calculator implementation
2. Building assistant basics
3. Utility framework
4. Configuration system

### Week 9-10: AI System
1. Personality framework
2. Learning algorithms
3. Dialogue system
4. Memory management

## Key Development Files

### Main Mod Class
```java
// src/main/java/com/astrolabs/etherealmind/EtherealMind.java
@Mod("etherealmind")
public class EtherealMind {
    public static final String MOD_ID = "etherealmind";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    public EtherealMind() {
        // Register systems
        EntityRegistry.register();
        NetworkRegistry.register();
        UtilityRegistry.register();
    }
}
```

### COSMO Entity Registration
```java
// src/main/java/com/astrolabs/etherealmind/common/registry/EntityRegistry.java
public class EntityRegistry {
    public static final EntityType<CosmoEntity> COSMO = Registry.register(
        Registries.ENTITY_TYPE,
        new Identifier(EtherealMind.MOD_ID, "cosmo"),
        FabricEntityTypeBuilder.create(SpawnGroup.MISC, CosmoEntity::new)
            .dimensions(EntityDimensions.fixed(1.0f, 1.0f))
            .trackRangeBlocks(80)
            .trackedUpdateRate(3)
            .build()
    );
}
```

## Testing During Development

### 1. Create Test World
```
/summon etherealmind:cosmo ~ ~ ~
/etherealmind bind @p
```

### 2. Debug Commands
```
/etherealmind debug particles
/etherealmind debug storage
/etherealmind debug ai
```

### 3. Performance Monitoring
```java
@Mixin(MinecraftClient.class)
public class PerformanceDebugMixin {
    @Inject(method = "render", at = @At("HEAD"))
    private void logPerformance(CallbackInfo ci) {
        if (EtherealMindConfig.debugMode) {
            // Log frame time, particle count, etc.
        }
    }
}
```

## Common Development Tasks

### Adding a New Utility
1. Create utility class implementing `ICosmoUtility`
2. Register in `UtilityRegistry`
3. Add GUI components
4. Create keybind if needed

### Adding Particle Effects
1. Create particle type in `ParticleRegistry`
2. Implement particle behavior
3. Add to particle controller
4. Configure in COSMO renderer

### Implementing AI Behaviors
1. Add behavior to `CosmoAI`
2. Create learning patterns
3. Add dialogue contexts
4. Test personality evolution

## Resources
- [Fabric Wiki](https://fabricmc.net/wiki/)
- [GeckoLib Documentation](https://github.com/bernie-g/geckolib/wiki)
- [Minecraft Wiki - Modding](https://minecraft.wiki/w/Mods)
- Project Discord: [Coming Soon]

## Next Steps
1. Set up your development environment
2. Run the example mod to ensure everything works
3. Start with Phase 1 from the implementation plan
4. Join our Discord for support and collaboration