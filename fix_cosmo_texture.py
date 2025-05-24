#!/usr/bin/env python3
"""
Fix COSMO texture to match the cute model UV mapping
"""

from PIL import Image, ImageDraw
import json

def create_fixed_cosmo_texture():
    # Create a 64x64 texture (standard for simple models)
    size = 64
    img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Define colors
    body_color = (100, 150, 200, 255)  # Light blue metallic
    dark_blue = (50, 100, 150, 255)    # Darker blue for shading
    light_blue = (150, 200, 250, 255)  # Light blue for highlights
    led_color = (0, 255, 255, 255)     # Cyan for LEDs
    panel_color = (80, 120, 160, 255)  # Panel color
    
    # Body texture mapping (based on standard cube UV layout)
    # The model uses a simple cube layout with each face taking up specific regions
    
    # Main body - fill the center area
    draw.rectangle([0, 0, size, size], fill=body_color)
    
    # Add panel lines for robotic look
    panel_width = 2
    # Vertical panels
    for x in range(8, size, 8):
        draw.line([(x, 0), (x, size)], fill=dark_blue, width=panel_width)
    # Horizontal panels  
    for y in range(8, size, 8):
        draw.line([(0, y), (size, y)], fill=dark_blue, width=panel_width)
    
    # Face area (front of the cube) - typically in the center
    face_x, face_y = 16, 16
    face_size = 16
    
    # Face background
    draw.rectangle([face_x, face_y, face_x + face_size, face_y + face_size], 
                   fill=light_blue)
    
    # Camera eye (centered)
    eye_x = face_x + face_size // 2
    eye_y = face_y + face_size // 2 - 2
    eye_radius = 5
    
    # Outer ring
    draw.ellipse([eye_x - eye_radius, eye_y - eye_radius, 
                  eye_x + eye_radius, eye_y + eye_radius], 
                 fill=dark_blue)
    # Inner lens
    draw.ellipse([eye_x - eye_radius + 2, eye_y - eye_radius + 2, 
                  eye_x + eye_radius - 2, eye_y + eye_radius - 2], 
                 fill=led_color)
    # Center dot
    draw.ellipse([eye_x - 1, eye_y - 1, eye_x + 1, eye_y + 1], 
                 fill=(255, 255, 255, 255))
    
    # LED indicators on body
    led_positions = [
        (8, 8), (24, 8), (40, 8), (56, 8),
        (8, 56), (24, 56), (40, 56), (56, 56)
    ]
    for x, y in led_positions:
        draw.ellipse([x-2, y-2, x+2, y+2], fill=led_color)
    
    # Antenna area (top of texture)
    antenna_x, antenna_y = 24, 4
    antenna_width = 16
    antenna_height = 8
    draw.rectangle([antenna_x, antenna_y, antenna_x + antenna_width, antenna_y + antenna_height],
                   fill=panel_color)
    # Antenna tip
    draw.ellipse([antenna_x + antenna_width//2 - 3, antenna_y - 2, 
                  antenna_x + antenna_width//2 + 3, antenna_y + 2], 
                 fill=led_color)
    
    # Arms (sides of texture)
    # Left arm
    draw.rectangle([0, 20, 8, 44], fill=panel_color)
    # Right arm  
    draw.rectangle([56, 20, 64, 44], fill=panel_color)
    
    # Add some shading/highlights for depth
    # Top highlight
    for y in range(4):
        alpha = int(255 * (4 - y) / 4)
        draw.line([(0, y), (size, y)], fill=(255, 255, 255, alpha // 2))
    
    # Bottom shadow
    for y in range(size - 4, size):
        alpha = int(255 * (y - (size - 4)) / 4)
        draw.line([(0, y), (size, y)], fill=(0, 0, 0, alpha // 3))
    
    # Save the texture
    img.save('/workspaces/EtherealMind/src/main/resources/assets/etherealmind/textures/entity/cosmo_cute.png')
    print("Created fixed COSMO texture at cosmo_cute.png")
    
    # Also create a backup
    img.save('/workspaces/EtherealMind/src/main/resources/assets/etherealmind/textures/entity/cosmo_cute_backup.png')

if __name__ == "__main__":
    create_fixed_cosmo_texture()