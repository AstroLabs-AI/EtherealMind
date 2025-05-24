#!/usr/bin/env python3
"""
Create a proper 128x128 cute texture that matches the cosmo_cute.geo.json UV mapping
"""

from PIL import Image, ImageDraw

def create_proper_cute_texture():
    # Create 128x128 texture as expected by the model
    size = 128
    img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Color palette - cute and friendly
    body_color = (130, 180, 220, 255)      # Soft blue
    face_color = (40, 60, 80, 255)         # Dark blue face plate
    eye_white = (240, 240, 250, 255)       # White
    iris_color = (100, 200, 255, 255)       # Bright cyan
    pupil_color = (20, 40, 60, 255)        # Dark
    led_color = (0, 255, 255, 255)         # Bright cyan LED
    
    # Body faces - each face is 24x24 as per UV mapping
    # Front face (0,0) - with cute face
    draw.rectangle([(0, 0), (24, 24)], fill=body_color)
    # Face plate
    draw.rectangle([(2, 2), (22, 22)], fill=face_color)
    # Eyes
    draw.ellipse([(5, 6), (10, 11)], fill=eye_white)
    draw.ellipse([(14, 6), (19, 11)], fill=eye_white)
    # Pupils
    draw.ellipse([(6, 7), (9, 10)], fill=iris_color)
    draw.ellipse([(15, 7), (18, 10)], fill=iris_color)
    draw.ellipse([(7, 8), (8, 9)], fill=pupil_color)
    draw.ellipse([(16, 8), (17, 9)], fill=pupil_color)
    # Smile
    draw.arc([(7, 12), (17, 18)], start=0, end=180, fill=led_color, width=2)
    
    # Right face (24,0)
    draw.rectangle([(24, 0), (48, 24)], fill=body_color)
    for y in range(4, 20, 4):
        draw.line([(24, y), (48, y)], fill=(100, 150, 190, 255), width=1)
    
    # Back face (48,0)
    draw.rectangle([(48, 0), (72, 24)], fill=body_color)
    # Tech pattern
    for x in range(52, 68, 8):
        for y in range(4, 20, 8):
            draw.ellipse([(x, y), (x+4, y+4)], fill=led_color)
    
    # Left face (72,0)
    draw.rectangle([(72, 0), (96, 24)], fill=body_color)
    for y in range(4, 20, 4):
        draw.line([(72, y), (96, y)], fill=(100, 150, 190, 255), width=1)
    
    # Top face (24,24)
    draw.rectangle([(24, 24), (48, 48)], fill=(150, 200, 240, 255))
    # Central glow
    draw.ellipse([(30, 30), (42, 42)], fill=led_color)
    draw.ellipse([(32, 32), (40, 40)], fill=(150, 200, 240, 255))
    
    # Bottom face (48,24)
    draw.rectangle([(48, 24), (72, 48)], fill=(90, 140, 180, 255))
    # Engine ports
    for x, y in [(54, 30), (66, 30), (54, 42), (66, 42)]:
        draw.ellipse([(x-3, y-3), (x+3, y+3)], fill=led_color)
    
    # Antenna (120,0) - small 2x6 area
    draw.rectangle([(120, 0), (122, 6)], fill=body_color)
    draw.rectangle([(120, 0), (122, 2)], fill=led_color)
    
    # Antenna tip (120,8)
    draw.ellipse([(120, 8), (128, 16)], fill=led_color)
    draw.ellipse([(122, 10), (126, 14)], fill=(255, 255, 255, 200))
    
    # Arms (96,0) - in remaining space
    # Left arm base
    draw.rectangle([(96, 0), (100, 12)], fill=body_color)
    draw.line([(98, 0), (98, 12)], fill=(100, 150, 190, 255), width=1)
    
    # Right arm base
    draw.rectangle([(104, 0), (108, 12)], fill=body_color)
    draw.line([(106, 0), (106, 12)], fill=(100, 150, 190, 255), width=1)
    
    # Hands
    draw.ellipse([(96, 12), (100, 16)], fill=(150, 200, 240, 255))
    draw.ellipse([(104, 12), (108, 16)], fill=(150, 200, 240, 255))
    
    # Eye details for expression (0, 48) - extra eye frames
    # Happy eyes
    draw.ellipse([(0, 48), (8, 52)], fill=eye_white)
    draw.arc([(2, 49), (6, 51)], start=0, end=180, fill=iris_color, width=2)
    
    # Save the texture
    img.save('/workspaces/EtherealMind/src/main/resources/assets/etherealmind/textures/entity/cosmo_cute_proper.png')
    print("Created proper 128x128 cute texture")

if __name__ == "__main__":
    create_proper_cute_texture()