#!/usr/bin/env python3
"""Create a detailed texture for the new COSMO 3D model"""

from PIL import Image, ImageDraw
import os

def create_detailed_texture():
    # Create 128x128 texture
    width, height = 128, 128
    img = Image.new('RGBA', (width, height), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Color palette
    primary_purple = (138, 43, 226)
    secondary_purple = (147, 112, 219)
    glow_cyan = (0, 255, 255)
    energy_pink = (255, 20, 147)
    dark_purple = (75, 0, 130)
    white = (255, 255, 255)
    
    # Main body texture (16x16 for each face at y=16)
    for x in range(0, 64, 16):
        # Gradient effect
        for i in range(16):
            for j in range(16):
                fade = 1.0 - (abs(i - 7.5) + abs(j - 7.5)) / 15.0
                r = int(primary_purple[0] * fade + dark_purple[0] * (1 - fade))
                g = int(primary_purple[1] * fade + dark_purple[1] * (1 - fade))
                b = int(primary_purple[2] * fade + dark_purple[2] * (1 - fade))
                draw.point((x + i, 16 + j), (r, g, b, 255))
    
    # Outer shell texture (20x20 for each face at y=52)
    for x in range(0, 80, 20):
        for i in range(20):
            for j in range(20):
                # Translucent effect with energy patterns
                dist = ((i - 10) ** 2 + (j - 10) ** 2) ** 0.5
                if dist < 10:
                    alpha = int(100 * (1 - dist / 10))
                    draw.point((x + i, 52 + j), (*secondary_purple, alpha))
    
    # Energy core (8x8 at y=80)
    for x in range(0, 32, 8):
        for i in range(8):
            for j in range(8):
                # Bright glowing core
                dist = ((i - 4) ** 2 + (j - 4) ** 2) ** 0.5
                if dist < 4:
                    intensity = 1.0 - dist / 4
                    r = int(glow_cyan[0] * intensity + energy_pink[0] * (1 - intensity))
                    g = int(glow_cyan[1] * intensity + energy_pink[1] * (1 - intensity))
                    b = int(glow_cyan[2] * intensity + energy_pink[2] * (1 - intensity))
                    draw.point((x + i, 80 + j), (r, g, b, 255))
    
    # Eyes (4x4 at y=80)
    # Left eye at x=34
    for i in range(4):
        for j in range(4):
            if i in [1, 2] and j in [1, 2]:
                draw.point((34 + i, 80 + j), (*glow_cyan, 255))
            else:
                draw.point((34 + i, 80 + j), (*white, 200))
    
    # Right eye at x=46
    for i in range(4):
        for j in range(4):
            if i in [1, 2] and j in [1, 2]:
                draw.point((46 + i, 80 + j), (*glow_cyan, 255))
            else:
                draw.point((46 + i, 80 + j), (*white, 200))
    
    # Antenna tips (4x4)
    for base_x in [68, 92]:
        for i in range(4):
            for j in range(4):
                # Glowing antenna orbs
                dist = ((i - 2) ** 2 + (j - 2) ** 2) ** 0.5
                if dist < 2:
                    draw.point((base_x + i, 80 + j), (*energy_pink, 255))
    
    # Orbital rings texture (24x2 at y=94)
    for i in range(24):
        for j in range(2):
            # Energy flow pattern
            intensity = (i / 24.0) * 255
            draw.point((2 + i, 94 + j), (int(intensity), int(intensity * 0.5), 255, 200))
            draw.point((2 + i, 98 + j), (255, int(intensity * 0.5), int(intensity), 200))
    
    # Save texture
    output_path = 'src/main/resources/assets/etherealmind/textures/entity/cosmo_detailed.png'
    os.makedirs(os.path.dirname(output_path), exist_ok=True)
    img.save(output_path)
    print(f"Created detailed texture: {output_path}")
    
    # Create animation metadata
    mcmeta = {
        "animation": {
            "frametime": 4,
            "frames": [0, 1, 2, 3, 4, 5, 6, 7]
        }
    }
    
    import json
    with open(output_path + '.mcmeta', 'w') as f:
        json.dump(mcmeta, f, indent=2)
    print(f"Created animation metadata: {output_path}.mcmeta")

if __name__ == "__main__":
    create_detailed_texture()