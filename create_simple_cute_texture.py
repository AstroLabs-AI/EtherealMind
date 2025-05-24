#!/usr/bin/env python3
"""
Create a simple but cute texture for basic COSMO model
"""

from PIL import Image, ImageDraw

def create_simple_cute_texture():
    # Create 64x64 texture for simple model
    size = 64
    img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Color palette - cute and friendly
    body_color = (130, 180, 220, 255)      # Soft blue
    face_color = (40, 60, 80, 255)         # Dark blue face
    eye_white = (240, 240, 250, 255)       # White
    iris_color = (100, 200, 255, 255)       # Cyan
    led_color = (0, 255, 255, 255)          # Bright cyan
    
    # Body texture (main cube faces)
    # Each face is 16x16 in the standard cube layout
    
    # Top row: front, right, back, left faces
    faces_y = 16
    for i in range(4):
        x = i * 16
        # Fill face with body color
        draw.rectangle([(x, faces_y), (x + 16, faces_y + 16)], fill=body_color)
        
        # Add some detail to each face
        if i == 0:  # Front face - add cute face
            # Face background
            draw.rectangle([(x + 2, faces_y + 2), (x + 14, faces_y + 14)], fill=face_color)
            
            # Eyes
            # Left eye
            draw.ellipse([(x + 4, faces_y + 5), (x + 7, faces_y + 8)], fill=eye_white)
            draw.ellipse([(x + 5, faces_y + 6), (x + 6, faces_y + 7)], fill=iris_color)
            
            # Right eye
            draw.ellipse([(x + 9, faces_y + 5), (x + 12, faces_y + 8)], fill=eye_white)
            draw.ellipse([(x + 10, faces_y + 6), (x + 11, faces_y + 7)], fill=iris_color)
            
            # Cute smile
            draw.arc([(x + 5, faces_y + 8), (x + 11, faces_y + 12)], start=0, end=180, fill=led_color, width=1)
        else:
            # Add LED dots on other faces
            draw.ellipse([(x + 7, faces_y + 7), (x + 9, faces_y + 9)], fill=led_color)
    
    # Top face (at 16, 0)
    draw.rectangle([(16, 0), (32, 16)], fill=(150, 200, 240, 255))
    # Add antenna base
    draw.rectangle([(22, 6), (26, 10)], fill=led_color)
    
    # Bottom face (at 16, 32)
    draw.rectangle([(16, 32), (32, 48)], fill=(90, 140, 180, 255))
    # Add engine ports
    for px, py in [(20, 36), (28, 36), (20, 44), (28, 44)]:
        draw.ellipse([(px-2, py-2), (px+2, py+2)], fill=led_color)
    
    # Arms (small sections)
    # Left arm
    draw.rectangle([(0, 20), (4, 28)], fill=body_color)
    draw.rectangle([(1, 22), (3, 26)], fill=led_color)
    
    # Right arm
    draw.rectangle([(60, 20), (64, 28)], fill=body_color)
    draw.rectangle([(61, 22), (63, 26)], fill=led_color)
    
    # Antenna on top
    draw.rectangle([(48, 0), (52, 8)], fill=body_color)
    draw.ellipse([(48, 0), (52, 4)], fill=led_color)
    
    # Save the texture
    img.save('/workspaces/EtherealMind/src/main/resources/assets/etherealmind/textures/entity/cosmo_cute_fixed.png')
    print("Created simple cute texture")

if __name__ == "__main__":
    create_simple_cute_texture()