#!/usr/bin/env python3
"""
Create texture for improved COSMO model with 3D arms
"""

from PIL import Image, ImageDraw

def create_improved_texture():
    # Create 64x64 texture to match the model
    size = 64
    img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Color palette
    body_color = (130, 180, 220, 255)      # Soft blue
    body_dark = (90, 140, 180, 255)        # Darker blue for shading
    body_light = (170, 210, 240, 255)      # Light blue for highlights
    face_color = (40, 60, 80, 255)         # Dark blue face
    eye_white = (240, 240, 250, 255)       # White
    iris_color = (100, 200, 255, 255)       # Cyan
    pupil_color = (20, 40, 60, 255)         # Dark
    led_color = (0, 255, 255, 255)         # Bright cyan LED
    
    # Body texture (0,0 to 64,32) - each face is 16x16
    # Front face (0,0)
    draw.rectangle([(0, 0), (16, 16)], fill=body_color)
    # Add some panel details
    draw.line([(0, 4), (16, 4)], fill=body_dark, width=1)
    draw.line([(0, 8), (16, 8)], fill=body_dark, width=1)
    draw.line([(0, 12), (16, 12)], fill=body_dark, width=1)
    # Add LED
    draw.ellipse([(7, 7), (9, 9)], fill=led_color)
    
    # Right face (16,0)
    draw.rectangle([(16, 0), (32, 16)], fill=body_color)
    for y in range(2, 14, 4):
        draw.rectangle([(18, y), (30, y+2)], fill=body_dark)
    
    # Back face (32,0)
    draw.rectangle([(32, 0), (48, 16)], fill=body_color)
    # Tech pattern
    for x in range(35, 45, 5):
        for y in range(3, 13, 5):
            draw.ellipse([(x, y), (x+2, y+2)], fill=led_color)
    
    # Left face (48,0)
    draw.rectangle([(48, 0), (64, 16)], fill=body_color)
    for y in range(2, 14, 4):
        draw.rectangle([(50, y), (62, y+2)], fill=body_dark)
    
    # Top face (16,16)
    draw.rectangle([(16, 16), (32, 32)], fill=body_light)
    draw.ellipse([(20, 20), (28, 28)], fill=led_color)
    draw.ellipse([(22, 22), (26, 26)], fill=body_light)
    
    # Bottom face (32,16)
    draw.rectangle([(32, 16), (48, 32)], fill=body_dark)
    for x, y in [(36, 20), (44, 20), (36, 28), (44, 28)]:
        draw.ellipse([(x-2, y-2), (x+2, y+2)], fill=led_color)
    
    # Antenna (0,32) - 2x4
    draw.rectangle([(0, 32), (2, 36)], fill=body_color)
    draw.rectangle([(2, 32), (4, 36)], fill=body_dark)
    draw.rectangle([(4, 32), (6, 36)], fill=body_color)
    draw.rectangle([(6, 32), (8, 36)], fill=body_dark)
    
    # Antenna tip (8,32) - 4x4 for each face
    for i in range(4):
        x = 8 + i * 4
        draw.rectangle([(x, 32), (x+4, 36)], fill=led_color)
        draw.ellipse([(x+1, 33), (x+3, 35)], fill=(255, 255, 255, 200))
    
    # Left arm (0,36) - 4x8 for each face (6 faces for 3D)
    # Front
    draw.rectangle([(0, 36), (4, 44)], fill=body_color)
    draw.line([(2, 36), (2, 44)], fill=body_dark, width=1)
    # Right side
    draw.rectangle([(4, 36), (8, 44)], fill=body_dark)
    # Back
    draw.rectangle([(8, 36), (12, 44)], fill=body_color)
    # Left side
    draw.rectangle([(12, 36), (16, 44)], fill=body_light)
    # Top
    draw.rectangle([(4, 48), (8, 52)], fill=body_light)
    # Bottom
    draw.rectangle([(8, 48), (12, 52)], fill=body_dark)
    
    # Left hand (16,36) - 4x4 for each face
    for i in range(6):
        x = 16 + i * 4
        if i < 4:
            draw.rectangle([(x, 36), (x+4, 40)], fill=body_light)
            draw.ellipse([(x+1, 37), (x+3, 39)], fill=led_color)
        else:
            draw.rectangle([(x-8, 40), (x-4, 44)], fill=body_light)
    
    # Right arm (32,36) - same pattern as left
    # Front
    draw.rectangle([(32, 36), (36, 44)], fill=body_color)
    draw.line([(34, 36), (34, 44)], fill=body_dark, width=1)
    # Left side
    draw.rectangle([(36, 36), (40, 44)], fill=body_light)
    # Back
    draw.rectangle([(40, 36), (44, 44)], fill=body_color)
    # Right side
    draw.rectangle([(44, 36), (48, 44)], fill=body_dark)
    # Top
    draw.rectangle([(36, 48), (40, 52)], fill=body_light)
    # Bottom
    draw.rectangle([(40, 48), (44, 52)], fill=body_dark)
    
    # Right hand (48,36)
    for i in range(6):
        x = 48 + i * 4
        if i < 4 and x < 64:
            draw.rectangle([(x, 36), (x+4, 40)], fill=body_light)
            draw.ellipse([(x+1, 37), (x+3, 39)], fill=led_color)
        elif x-8 >= 48:
            draw.rectangle([(x-8, 40), (x-4, 44)], fill=body_light)
    
    # Face plate (0,48) - 12x12
    draw.rectangle([(0, 48), (12, 60)], fill=face_color)
    # Add border
    draw.rectangle([(0, 48), (12, 60)], outline=led_color, width=1)
    # Cute smile
    draw.arc([(2, 54), (10, 58)], start=0, end=180, fill=led_color, width=1)
    
    # Left eye (12,48) - 4x4
    draw.rectangle([(12, 48), (16, 52)], fill=eye_white)
    draw.ellipse([(12, 48), (16, 52)], fill=eye_white)
    draw.ellipse([(13, 49), (15, 51)], fill=iris_color)
    draw.ellipse([(13.5, 49.5), (14.5, 50.5)], fill=pupil_color)
    
    # Right eye (16,48) - 4x4
    draw.rectangle([(16, 48), (20, 52)], fill=eye_white)
    draw.ellipse([(16, 48), (20, 52)], fill=eye_white)
    draw.ellipse([(17, 49), (19, 51)], fill=iris_color)
    draw.ellipse([(17.5, 49.5), (18.5, 50.5)], fill=pupil_color)
    
    # Save texture
    img.save('/workspaces/EtherealMind/src/main/resources/assets/etherealmind/textures/entity/cosmo_improved.png')
    print("Created improved COSMO texture")

if __name__ == "__main__":
    create_improved_texture()