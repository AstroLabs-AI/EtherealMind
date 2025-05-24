#!/bin/bash

# Create placeholder sound files for COSMO
# These are silent 1-second .ogg files to prevent crashes

# Install ffmpeg if not available
if ! command -v ffmpeg &> /dev/null; then
    echo "ffmpeg not found. Please install ffmpeg to generate sound files."
    echo "On Ubuntu/Debian: sudo apt-get install ffmpeg"
    echo "On macOS: brew install ffmpeg"
    exit 1
fi

# Entity sounds
ENTITY_SOUNDS=(
    "ambient"
    "happy"
    "sad"
    "excited"
    "bored"
    "hurt"
    "death"
    "curious"
    "alert"
    "confused"
    "acknowledge"
)

# Misc sounds
MISC_SOUNDS=(
    "storage_open"
    "storage_close"
    "level_up"
    "ability_activate"
    "teleport"
    "energy_charge"
)

echo "Creating placeholder sound files..."

# Create entity sounds
for sound in "${ENTITY_SOUNDS[@]}"; do
    output="src/main/resources/assets/etherealmind/sounds/entity/cosmo/cosmo_${sound}.ogg"
    echo "Creating $output"
    ffmpeg -f lavfi -i anullsrc=r=44100:cl=mono -t 1 -c:a libvorbis -q:a 0 "$output" -y 2>/dev/null
done

# Create misc sounds
for sound in "${MISC_SOUNDS[@]}"; do
    output="src/main/resources/assets/etherealmind/sounds/misc/${sound}.ogg"
    echo "Creating $output"
    ffmpeg -f lavfi -i anullsrc=r=44100:cl=mono -t 1 -c:a libvorbis -q:a 0 "$output" -y 2>/dev/null
done

echo "Done! Created $(ls -1 src/main/resources/assets/etherealmind/sounds/**/*.ogg 2>/dev/null | wc -l) placeholder sound files."
echo "Note: These are silent placeholders. Replace them with actual sound effects for the best experience."