#!/usr/bin/env python3
from PIL import Image, ImageDraw
import os

# Create a 128x128 texture for the geometric model
width = 128
height = 128

# Create base image with transparency
img = Image.new('RGBA', (width, height), (0, 0, 0, 0))
draw = ImageDraw.Draw(img)

# Core body texture (0,0 to 96,48) - main sphere
# Front face
for x in range(24):
    for y in range(24):
        # Create gradient sphere effect
        dist_x = abs(x - 12) / 12.0
        dist_y = abs(y - 12) / 12.0
        dist = (dist_x**2 + dist_y**2)**0.5
        if dist <= 1.0:
            brightness = int(255 * (1.0 - dist * 0.3))
            color = (brightness, brightness//2 + 100, brightness)
            draw.point((x, y), color + (255,))

# Copy to other faces
front = img.crop((0, 0, 24, 24))
img.paste(front, (24, 0))  # Right
img.paste(front, (48, 0))  # Back
img.paste(front, (72, 0))  # Left
img.paste(front, (24, 24))  # Top
img.paste(front, (48, 24))  # Bottom

# Face screen (96,0 to 108,8) - digital display
for x in range(96, 108):
    for y in range(8):
        if (x - 96) % 3 == 0 or y % 3 == 0:
            draw.point((x, y), (0, 255, 200, 255))
        else:
            draw.point((x, y), (0, 150, 100, 200))

# Orbit ring texture (0,64 to 40,66) - glowing ring
for x in range(40):
    intensity = 255 if 5 < x < 35 else 150
    draw.rectangle((x, 64, x, 66), fill=(100, 200, 255, intensity))

# Tail textures (0,40 to 32,48) - energy trails
tail_colors = [
    (255, 100, 100),  # Red
    (100, 255, 100),  # Green
    (100, 100, 255),  # Blue
    (255, 255, 100),  # Yellow
]

for i, color in enumerate(tail_colors):
    base_x = i * 8
    for x in range(2):
        for y in range(6):
            brightness = 255 - (y * 40)
            tail_color = tuple(int(c * brightness / 255) for c in color)
            draw.rectangle((base_x + x, 40 + y, base_x + x, 40 + y), 
                          fill=tail_color + (255,))

# Save the texture
output_path = 'src/main/resources/assets/etherealmind/textures/entity/cosmo_geometric.png'
os.makedirs(os.path.dirname(output_path), exist_ok=True)
img.save(output_path, 'PNG')
print(f"Created geometric texture at {output_path}")