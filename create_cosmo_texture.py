#!/usr/bin/env python3
"""
Create COSMO texture with proper UV mapping for the cosmo_cute model.
UV Layout:
- Body: 0,0 to 96,48 (main body faces)
- Body glow: 0,48 to 104,100
- Eye components: 96,0 to 128,24
- Arms: 0,24 to 40,36
- Antenna: 120,0 to 128,8
"""

from PIL import Image, ImageDraw
import numpy as np

def create_cosmo_texture():
    # Create 128x128 RGBA texture
    img = Image.new('RGBA', (128, 128), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Color palette
    body_color = (60, 80, 120, 255)  # Dark blue-gray metallic
    body_highlight = (80, 100, 140, 255)  # Lighter metallic
    panel_color = (40, 60, 100, 255)  # Darker panel color
    led_blue = (100, 180, 255, 255)  # Bright LED blue
    led_glow = (150, 200, 255, 255)  # LED glow color
    eye_base = (30, 30, 40, 255)  # Dark eye socket
    iris_color = (0, 150, 255, 255)  # Bright blue iris
    pupil_color = (0, 0, 0, 255)  # Black pupil
    antenna_color = (180, 180, 200, 255)  # Silver antenna
    
    # Body main cube (6 faces, each 24x24)
    # Front face (0,0 to 24,24) - Main front with panel details
    draw.rectangle([0, 0, 24, 24], fill=body_color)
    # Panel lines
    draw.rectangle([2, 2, 22, 6], fill=panel_color)  # Top panel
    draw.rectangle([2, 18, 22, 22], fill=panel_color)  # Bottom panel
    # LED indicators
    draw.ellipse([4, 8, 8, 12], fill=led_blue)
    draw.ellipse([16, 8, 20, 12], fill=led_blue)
    draw.ellipse([10, 14, 14, 18], fill=led_glow)
    
    # Right face (24,0 to 48,24)
    draw.rectangle([24, 0, 48, 24], fill=body_color)
    # Side panel
    draw.rectangle([26, 4, 46, 20], fill=panel_color)
    # Ventilation lines
    for i in range(3):
        y = 8 + i * 4
        draw.rectangle([28, y, 44, y+2], fill=body_highlight)
    
    # Back face (48,0 to 72,24)
    draw.rectangle([48, 0, 72, 24], fill=body_color)
    # Back panel with tech pattern
    draw.rectangle([50, 2, 70, 22], fill=panel_color)
    # Power indicator
    draw.ellipse([58, 10, 62, 14], fill=led_glow)
    
    # Left face (72,0 to 96,24)
    draw.rectangle([72, 0, 96, 24], fill=body_color)
    # Side panel (mirror of right)
    draw.rectangle([74, 4, 94, 20], fill=panel_color)
    # Ventilation lines
    for i in range(3):
        y = 8 + i * 4
        draw.rectangle([76, y, 92, y+2], fill=body_highlight)
    
    # Top face (24,24 to 48,48)
    draw.rectangle([24, 24, 48, 48], fill=body_highlight)
    # Central processing unit design
    draw.rectangle([28, 28, 44, 44], fill=panel_color)
    draw.rectangle([32, 32, 40, 40], fill=led_blue)
    
    # Bottom face (48,24 to 72,48)
    draw.rectangle([48, 24, 72, 48], fill=body_color)
    # Bottom details
    draw.rectangle([52, 28, 68, 44], fill=panel_color)
    
    # Arms (0,24 to 40,36)
    # Right arm
    draw.rectangle([0, 24, 20, 32], fill=body_color)
    draw.rectangle([2, 26, 18, 30], fill=panel_color)
    # Left arm
    draw.rectangle([20, 24, 40, 32], fill=body_color)
    draw.rectangle([22, 26, 38, 30], fill=panel_color)
    
    # Body glow layer (0,48 to 104,100) - Semi-transparent glow effect
    # This creates a subtle holographic effect around the body
    glow_alpha = 80
    # Front glow
    draw.rectangle([0, 48, 26, 74], fill=(*led_glow[:3], glow_alpha))
    # Right glow
    draw.rectangle([26, 48, 52, 74], fill=(*led_glow[:3], glow_alpha))
    # Back glow
    draw.rectangle([52, 48, 78, 74], fill=(*led_glow[:3], glow_alpha))
    # Left glow
    draw.rectangle([78, 48, 104, 74], fill=(*led_glow[:3], glow_alpha))
    # Top glow
    draw.rectangle([26, 74, 52, 100], fill=(*led_glow[:3], glow_alpha))
    # Bottom glow
    draw.rectangle([52, 74, 78, 100], fill=(*led_glow[:3], glow_alpha))
    
    # Eye components (96,0 to 128,24)
    # Eye base/socket (96,0 to 108,12)
    draw.rectangle([96, 0, 108, 12], fill=eye_base)
    # Eye rim
    draw.rectangle([97, 1, 107, 11], fill=body_color)
    draw.rectangle([98, 2, 106, 10], fill=eye_base)
    
    # Iris (96,16 to 104,24)
    draw.ellipse([96, 16, 104, 24], fill=iris_color)
    # Iris detail
    draw.ellipse([97, 17, 103, 23], fill=(0, 120, 220, 255))
    
    # Pupil (114,16 to 116,18)
    draw.ellipse([114, 16, 116, 18], fill=pupil_color)
    
    # Antenna (120,0 to 128,8)
    draw.rectangle([120, 0, 128, 6], fill=antenna_color)
    # Antenna tip
    draw.ellipse([122, 6, 126, 8], fill=led_blue)
    
    # Add some texture detail to main body surfaces
    pixels = np.array(img)
    
    # Add subtle noise to body areas for metallic texture
    body_areas = [
        (0, 0, 24, 24),    # Front
        (24, 0, 48, 24),   # Right
        (48, 0, 72, 24),   # Back
        (72, 0, 96, 24),   # Left
    ]
    
    for x1, y1, x2, y2 in body_areas:
        for x in range(x1, x2):
            for y in range(y1, y2):
                if pixels[y, x, 3] > 0:  # If pixel is not transparent
                    # Add subtle variation
                    variation = np.random.randint(-10, 10)
                    for c in range(3):  # RGB channels
                        new_val = pixels[y, x, c] + variation
                        pixels[y, x, c] = max(0, min(255, new_val))
    
    # Convert back to PIL Image
    img = Image.fromarray(pixels.astype('uint8'), 'RGBA')
    
    # Save the texture
    img.save('/workspaces/EtherealMind/src/main/resources/assets/etherealmind/textures/entity/cosmo.png')
    print("COSMO texture created successfully!")
    
    # Also create a debug version with UV grid overlay
    debug_img = img.copy()
    debug_draw = ImageDraw.Draw(debug_img)
    
    # Draw UV region boundaries in red
    debug_color = (255, 0, 0, 128)
    # Body faces
    for i in range(4):
        x = i * 24
        debug_draw.rectangle([x, 0, x+24, 24], outline=debug_color, width=1)
    # Top/bottom faces
    debug_draw.rectangle([24, 24, 48, 48], outline=debug_color, width=1)
    debug_draw.rectangle([48, 24, 72, 48], outline=debug_color, width=1)
    # Glow regions
    debug_draw.rectangle([0, 48, 104, 100], outline=debug_color, width=1)
    # Eye region
    debug_draw.rectangle([96, 0, 128, 24], outline=debug_color, width=1)
    # Arms region
    debug_draw.rectangle([0, 24, 40, 36], outline=debug_color, width=1)
    
    debug_img.save('/workspaces/EtherealMind/cosmo_texture_debug.png')
    print("Debug texture with UV grid saved as cosmo_texture_debug.png")

if __name__ == "__main__":
    create_cosmo_texture()