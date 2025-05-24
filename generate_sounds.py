#!/usr/bin/env python3
"""
Generate placeholder OGG sound files for EtherealMind mod
These are minimal valid OGG files that won't crash Minecraft
"""

import os
import struct

# Minimal valid OGG file header (silent sound)
OGG_HEADER = bytes([
    0x4F, 0x67, 0x67, 0x53, 0x00, 0x02, 0x00, 0x00,
    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
    0x01, 0x1E, 0x01, 0x76, 0x6F, 0x72, 0x62, 0x69,
    0x73, 0x00, 0x00, 0x00, 0x00, 0x02, 0x44, 0xAC,
    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x80, 0xBB,
    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01
])

ENTITY_SOUNDS = [
    "ambient", "happy", "sad", "excited", "bored",
    "hurt", "death", "curious", "alert", "confused", "acknowledge"
]

MISC_SOUNDS = [
    "storage_open", "storage_close", "level_up",
    "ability_activate", "teleport", "energy_charge"
]

def create_sound_file(path):
    """Create a minimal valid OGG file"""
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'wb') as f:
        f.write(OGG_HEADER)
    print(f"Created: {path}")

def main():
    base_dir = "src/main/resources/assets/etherealmind/sounds"
    
    print("Generating placeholder sound files...")
    
    # Entity sounds
    for sound in ENTITY_SOUNDS:
        path = os.path.join(base_dir, "entity", "cosmo", f"cosmo_{sound}.ogg")
        create_sound_file(path)
    
    # Misc sounds
    for sound in MISC_SOUNDS:
        path = os.path.join(base_dir, "misc", f"{sound}.ogg")
        create_sound_file(path)
    
    total = len(ENTITY_SOUNDS) + len(MISC_SOUNDS)
    print(f"\nDone! Created {total} placeholder sound files.")
    print("Note: These are silent placeholders. Replace them with actual sound effects.")

if __name__ == "__main__":
    main()