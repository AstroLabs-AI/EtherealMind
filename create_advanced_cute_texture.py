#!/usr/bin/env python3
"""
Create a cute but advanced texture for COSMO
"""

from PIL import Image, ImageDraw
import math

def create_advanced_cute_texture():
    # Create 128x128 texture
    size = 128
    img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Color palette - cute robotic
    body_color = (130, 180, 220, 255)      # Soft blue
    body_light = (170, 210, 240, 255)      # Light blue
    body_dark = (90, 140, 180, 255)        # Darker blue
    
    eye_white = (240, 240, 250, 255)       # Slightly blue white
    iris_color = (100, 200, 255, 255)       # Bright cyan
    pupil_color = (20, 40, 60, 255)         # Dark blue
    
    led_color = (0, 255, 255, 255)         # Cyan LED
    panel_color = (110, 160, 200, 255)     # Panel blue
    
    # Body texture (0,0 to 128,64) - main body faces
    # Front face
    draw.rectangle([(0, 0), (32, 32)], fill=body_color)
    # Add cute rounded corners effect
    for corner in [(0,0), (28,0), (0,28), (28,28)]:
        draw.rectangle([corner, (corner[0]+3, corner[1]+3)], fill=body_light)
    
    # Right face
    draw.rectangle([(32, 0), (64, 32)], fill=body_color)
    # Add panel lines
    for y in range(4, 28, 8):
        draw.line([(32, y), (64, y)], fill=body_dark, width=1)
    
    # Back face
    draw.rectangle([(64, 0), (96, 32)], fill=body_color)
    # Add tech pattern
    for x in range(68, 92, 8):
        for y in range(4, 28, 8):
            draw.ellipse([(x, y), (x+4, y+4)], fill=led_color)
    
    # Left face
    draw.rectangle([(96, 0), (128, 32)], fill=body_color)
    # Add panel lines
    for y in range(4, 28, 8):
        draw.line([(96, y), (128, y)], fill=body_dark, width=1)
    
    # Top face
    draw.rectangle([(32, 32), (64, 64)], fill=body_light)
    # Circular pattern
    draw.ellipse([(40, 40), (56, 56)], fill=led_color)
    draw.ellipse([(44, 44), (52, 52)], fill=body_light)
    
    # Bottom face
    draw.rectangle([(64, 32), (96, 64)], fill=body_dark)
    # Thruster ports
    for pos in [(72, 40), (88, 40), (72, 56), (88, 56)]:
        draw.ellipse([pos, (pos[0]+8, pos[1]+8)], fill=led_color)
    
    # Face plate (64,64 - 88,88) - cute face
    draw.rectangle([(64, 64), (88, 88)], fill=(40, 60, 80, 255))
    # Add border
    draw.rectangle([(64, 64), (88, 88)], outline=led_color, width=2)
    
    # Eyes (88,64 - 96,80)
    # Left eye white
    draw.ellipse([(88, 64), (96, 72)], fill=eye_white)
    # Left iris
    draw.ellipse([(89, 65), (95, 71)], fill=iris_color)
    # Left pupil
    draw.ellipse([(91, 67), (93, 69)], fill=pupil_color)
    # Left eye shine
    draw.ellipse([(92, 66), (94, 68)], fill=(255, 255, 255, 255))
    
    # Right eye white
    draw.ellipse([(88, 72), (96, 80)], fill=eye_white)
    # Right iris
    draw.ellipse([(89, 73), (95, 79)], fill=iris_color)
    # Right pupil
    draw.ellipse([(91, 75), (93, 77)], fill=pupil_color)
    # Right eye shine
    draw.ellipse([(92, 74), (94, 76)], fill=(255, 255, 255, 255))
    
    # Cute mouth on face plate
    draw.arc([(70, 74), (82, 84)], start=0, end=180, fill=led_color, width=2)
    
    # Antenna (100,64 - 120,80)
    # Base
    draw.rectangle([(100, 64), (108, 68)], fill=panel_color)
    # Segment
    draw.rectangle([(108, 64), (112, 72)], fill=body_color)
    # Tip - glowing ball
    draw.ellipse([(112, 64), (120, 72)], fill=led_color)
    draw.ellipse([(114, 66), (118, 70)], fill=(255, 255, 255, 200))
    
    # Arms (0,80 - 24,96)
    # Left arm
    draw.rectangle([(0, 80), (6, 96)], fill=body_color)
    draw.line([(3, 80), (3, 96)], fill=body_dark, width=1)
    # Left hand
    draw.rectangle([(6, 80), (12, 86)], fill=panel_color)
    draw.ellipse([(7, 81), (11, 85)], fill=body_light)
    
    # Right arm  
    draw.rectangle([(12, 80), (18, 96)], fill=body_color)
    draw.line([(15, 80), (15, 96)], fill=body_dark, width=1)
    # Right hand
    draw.rectangle([(18, 80), (24, 86)], fill=panel_color)
    draw.ellipse([(19, 81), (23, 85)], fill=body_light)
    
    # Hover engine (0,96 - 64,112)
    gradient_height = 8
    for y in range(gradient_height):
        intensity = 1 - (y / gradient_height)
        color = tuple(int(led_color[i] * intensity) for i in range(3)) + (255,)
        draw.rectangle([(0, 96 + y), (64, 97 + y)], fill=color)
    
    # Add particle emitters
    for x in range(8, 56, 16):
        draw.ellipse([(x, 104), (x+8, 112)], fill=led_color)
        draw.ellipse([(x+2, 106), (x+6, 110)], fill=(255, 255, 255, 255))
    
    # Energy rings (64,88 - 104,92)
    draw.rectangle([(64, 88), (104, 90)], fill=led_color)
    draw.rectangle([(64, 90), (68, 92)], fill=led_color)
    
    # Add some noise/texture to body areas
    for y in range(32):
        for x in range(128):
            if img.getpixel((x, y))[3] > 0:  # If not transparent
                r, g, b, a = img.getpixel((x, y))
                # Add slight variation
                variation = ((x + y) % 4) - 2
                r = max(0, min(255, r + variation * 5))
                g = max(0, min(255, g + variation * 5))
                b = max(0, min(255, b + variation * 5))
                img.putpixel((x, y), (r, g, b, a))
    
    # Save texture
    img.save('/workspaces/EtherealMind/src/main/resources/assets/etherealmind/textures/entity/cosmo_advanced_cute.png')
    print("Created advanced cute COSMO texture")

if __name__ == "__main__":
    create_advanced_cute_texture()