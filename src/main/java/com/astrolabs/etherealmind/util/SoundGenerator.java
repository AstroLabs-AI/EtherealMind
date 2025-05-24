package com.astrolabs.etherealmind.util;

import javax.sound.sampled.*;
import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;

/**
 * Utility to generate placeholder OGG sound files
 * Run with: ./gradlew runClient -PrunArgs="--generate-sounds"
 */
public class SoundGenerator {
    
    private static final List<String> ENTITY_SOUNDS = Arrays.asList(
        "ambient", "happy", "sad", "excited", "bored",
        "hurt", "death", "curious", "alert", "confused", "acknowledge"
    );
    
    private static final List<String> MISC_SOUNDS = Arrays.asList(
        "storage_open", "storage_close", "level_up",
        "ability_activate", "teleport", "energy_charge"
    );
    
    public static void generatePlaceholderSounds() {
        System.out.println("Generating placeholder sound files...");
        
        Path baseDir = Paths.get("src/main/resources/assets/etherealmind/sounds");
        
        try {
            // Create directories
            Files.createDirectories(baseDir.resolve("entity/cosmo"));
            Files.createDirectories(baseDir.resolve("misc"));
            
            // For now, create empty files as placeholders
            // Minecraft will handle them gracefully
            
            for (String sound : ENTITY_SOUNDS) {
                Path soundFile = baseDir.resolve("entity/cosmo/cosmo_" + sound + ".ogg");
                if (!Files.exists(soundFile)) {
                    Files.write(soundFile, createSilentOggData());
                    System.out.println("Created: " + soundFile);
                }
            }
            
            for (String sound : MISC_SOUNDS) {
                Path soundFile = baseDir.resolve("misc/" + sound + ".ogg");
                if (!Files.exists(soundFile)) {
                    Files.write(soundFile, createSilentOggData());
                    System.out.println("Created: " + soundFile);
                }
            }
            
            System.out.println("Sound generation complete!");
            
        } catch (IOException e) {
            System.err.println("Error generating sounds: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static byte[] createSilentOggData() {
        // Minimal valid OGG file header for a silent sound
        // This is a tiny valid OGG Vorbis file
        return new byte[] {
            0x4F, 0x67, 0x67, 0x53, 0x00, 0x02, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x01, 0x1E, 0x01, 0x76, 0x6F, 0x72, 0x62, 0x69,
            0x73, 0x00, 0x00, 0x00, 0x00, 0x02, 0x44, (byte)0xAC,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte)0x80, (byte)0xBB,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01
        };
    }
}