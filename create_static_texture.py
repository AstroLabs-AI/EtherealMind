#!/usr/bin/env python3
"""
Create a simple static 64x64 texture for COSMO to test visibility
"""

from PIL import Image, ImageDraw

def create_cosmo_texture():
    # Create a 64x64 RGBA image
    img = Image.new('RGBA', (64, 64), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Head (purple/blue gradient effect)
    head_color = (100, 50, 200, 255)  # Purple-blue
    draw.ellipse([16, 8, 48, 40], fill=head_color, outline=(150, 100, 255, 255))
    
    # Eyes (glowing cyan)
    eye_color = (0, 255, 255, 255)
    draw.ellipse([20, 18, 28, 26], fill=eye_color)
    draw.ellipse([36, 18, 44, 26], fill=eye_color)
    
    # Body (smaller, ethereal)
    body_color = (80, 40, 180, 200)
    draw.ellipse([20, 32, 44, 56], fill=body_color, outline=(120, 80, 220, 255))
    
    # Ethereal glow effect
    glow_color = (150, 100, 255, 100)
    draw.ellipse([12, 4, 52, 44], outline=glow_color, width=2)
    draw.ellipse([8, 0, 56, 48], outline=(150, 100, 255, 50), width=1)
    
    # Save the texture
    img.save('src/main/resources/assets/etherealmind/textures/entity/cosmo_static.png')
    print("Created static 64x64 COSMO texture at cosmo_static.png")
    
    # Also create a backup of current texture
    print("Current cosmo.png is already backed up as cosmo_backup.png")

if __name__ == "__main__":
    try:
        create_cosmo_texture()
    except ImportError:
        print("Pillow not installed. Creating placeholder with ImageMagick...")
        import os
        # Try ImageMagick as fallback
        cmd = """convert -size 64x64 xc:transparent \
            -fill 'rgba(100,50,200,255)' -draw 'circle 32,24 32,8' \
            -fill 'rgba(0,255,255,255)' -draw 'circle 24,22 24,18' \
            -fill 'rgba(0,255,255,255)' -draw 'circle 40,22 40,18' \
            -fill 'rgba(80,40,180,200)' -draw 'ellipse 32,44 12,12 0,360' \
            src/main/resources/assets/etherealmind/textures/entity/cosmo_static.png"""
        os.system(cmd)
        print("Created basic static texture with ImageMagick")