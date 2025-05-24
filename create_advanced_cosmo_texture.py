#!/usr/bin/env python3
"""
Create advanced texture for COSMO with 256x256 resolution
"""

from PIL import Image, ImageDraw, ImageFilter
import numpy as np

def create_advanced_cosmo_texture():
    # Create main texture and emission map
    size = 256
    texture = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    emission = Image.new('RGBA', (size, size), (0, 0, 0, 255))
    draw_tex = ImageDraw.Draw(texture)
    draw_em = ImageDraw.Draw(emission)
    
    # Color palette
    body_base = (80, 120, 160, 255)      # Blue-gray metallic
    body_light = (120, 160, 200, 255)    # Light blue metallic
    body_dark = (40, 80, 120, 255)       # Dark blue metallic
    
    energy_color = (100, 200, 255, 255)   # Cyan energy
    energy_bright = (150, 230, 255, 255)  # Bright cyan
    led_color = (0, 255, 255, 255)        # Pure cyan LED
    
    panel_color = (60, 100, 140, 255)     # Panel blue
    panel_edge = (40, 70, 100, 255)       # Panel edge
    
    # Core textures (0,0 - 64,64)
    # Inner core - glowing energy
    for y in range(16):
        for x in range(16):
            dist = ((x-8)**2 + (y-8)**2)**0.5
            if dist < 8:
                intensity = 1 - (dist / 8)
                color = tuple(int(c * intensity) for c in energy_bright[:3]) + (255,)
                texture.putpixel((x, y), color)
                emission.putpixel((x, y), color)
    
    # Outer shell - semi-transparent with panel details
    draw_tex.rectangle([(64, 0), (124, 40)], fill=body_base)
    
    # Add panel lines
    for i in range(5):
        y = i * 8
        draw_tex.line([(64, y), (124, y)], fill=panel_edge, width=1)
    for i in range(8):
        x = 64 + i * 8
        draw_tex.line([(x, 0), (x, 40)], fill=panel_edge, width=1)
    
    # Energy rings (0,48 - 68,68)
    ring_colors = [energy_color, energy_bright, (200, 100, 255, 255)]
    for i, color in enumerate(ring_colors):
        y = 48 + i * 4
        draw_tex.rectangle([(0, y), (32, y + 2)], fill=color)
        draw_em.rectangle([(0, y), (32, y + 2)], fill=color)
    
    # Head textures (144,0 - 224,36)
    # Head base with gradient
    for y in range(16):
        for x in range(20):
            t = y / 16
            color = tuple(int(body_dark[i] * (1-t) + body_light[i] * t) for i in range(3)) + (255,)
            texture.putpixel((144 + x, y), color)
    
    # Face plate - holographic display
    draw_tex.rectangle([(0, 64), (16, 76)], fill=(20, 40, 60, 255))
    draw_tex.rectangle([(1, 65), (15, 75)], fill=(40, 80, 120, 255))
    
    # Scanline effect on face
    for y in range(65, 75, 2):
        draw_tex.line([(1, y), (15, y)], fill=energy_color, width=1)
        draw_em.line([(1, y), (15, y)], fill=(50, 100, 150, 255), width=1)
    
    # Eyes (16,64 - 32,68)
    # Left eye
    draw_tex.ellipse([(16, 64), (20, 68)], fill=(20, 20, 40, 255), outline=energy_color)
    draw_tex.ellipse([(17, 65), (19, 67)], fill=energy_bright)
    draw_em.ellipse([(17, 65), (19, 67)], fill=energy_bright)
    
    # Right eye
    draw_tex.ellipse([(24, 64), (28, 68)], fill=(20, 20, 40, 255), outline=energy_color)
    draw_tex.ellipse([(25, 65), (27, 67)], fill=energy_bright)
    draw_em.ellipse([(25, 65), (27, 67)], fill=energy_bright)
    
    # Antenna (32,64 - 48,72)
    # Base
    draw_tex.rectangle([(32, 64), (36, 68)], fill=panel_color)
    # Segments
    draw_tex.rectangle([(36, 64), (38, 70)], fill=body_base)
    draw_tex.rectangle([(38, 64), (40, 68)], fill=body_base)
    # Tip - glowing
    draw_tex.ellipse([(40, 64), (44, 68)], fill=led_color)
    draw_em.ellipse([(40, 64), (44, 68)], fill=led_color)
    
    # Ear fins (44,64 - 52,72)
    for x in range(4):
        for y in range(8):
            alpha = int(255 * (1 - x/4))
            color = energy_color[:3] + (alpha,)
            texture.putpixel((44 + x, 64 + y), color)
            if x < 2:
                emission.putpixel((44 + x, 64 + y), energy_color)
    
    # Arms (0,80 - 48,96)
    # Shoulder joints
    draw_tex.ellipse([(0, 80), (4, 84)], fill=body_dark)
    draw_tex.ellipse([(24, 80), (28, 84)], fill=body_dark)
    
    # Upper arms
    draw_tex.rectangle([(4, 80), (6, 88)], fill=body_base)
    draw_tex.rectangle([(28, 80), (30, 88)], fill=body_base)
    
    # Add mechanical details
    for y in range(81, 87, 2):
        draw_tex.line([(4, y), (6, y)], fill=panel_edge, width=1)
        draw_tex.line([(28, y), (30, y)], fill=panel_edge, width=1)
    
    # Hands with fingers
    # Palm
    draw_tex.rectangle([(10, 80), (14, 84)], fill=body_light)
    draw_tex.rectangle([(34, 80), (38, 84)], fill=body_light)
    
    # Fingers - detailed
    finger_positions = [(14, 80), (16, 80), (18, 80), (20, 80), (22, 80)]
    for x, y in finger_positions:
        draw_tex.rectangle([(x, y), (x+1, y+2)], fill=body_base)
        draw_tex.rectangle([(x+24, y), (x+25, y+2)], fill=body_base)
    
    # Hover engine (0,96 - 48,116)
    # Main engine
    engine_gradient = Image.new('RGBA', (12, 8), (0, 0, 0, 0))
    draw_engine = ImageDraw.Draw(engine_gradient)
    for y in range(8):
        intensity = 1 - (y / 8)
        color = tuple(int(energy_bright[i] * intensity) for i in range(3)) + (255,)
        draw_engine.line([(0, y), (12, y)], fill=color)
    texture.paste(engine_gradient, (0, 96))
    emission.paste(engine_gradient, (0, 96))
    
    # Stabilizer fins
    fin_positions = [(48, 96), (52, 96), (54, 96), (58, 96)]
    for x, y in fin_positions:
        draw_tex.rectangle([(x, y), (x+2, y+4)], fill=body_dark)
        draw_tex.line([(x, y), (x+2, y)], fill=led_color, width=1)
        draw_em.line([(x, y), (x+2, y)], fill=led_color, width=1)
    
    # Particle emitters
    emitter_positions = [(60, 96), (62, 96), (64, 96), (66, 96)]
    for x, y in emitter_positions:
        draw_tex.ellipse([(x, y), (x+2, y+2)], fill=energy_bright)
        draw_em.ellipse([(x, y), (x+2, y+2)], fill=energy_bright)
    
    # Add noise and details to main body areas
    noise = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    pixels = noise.load()
    for y in range(size):
        for x in range(size):
            if texture.getpixel((x, y))[3] > 0:  # If pixel is not transparent
                variation = np.random.randint(-10, 10)
                r, g, b, a = texture.getpixel((x, y))
                r = max(0, min(255, r + variation))
                g = max(0, min(255, g + variation))
                b = max(0, min(255, b + variation))
                pixels[x, y] = (r, g, b, a)
    
    # Blend noise
    texture = Image.alpha_composite(texture, noise)
    
    # Apply slight blur to emission map for glow effect
    emission = emission.filter(ImageFilter.GaussianBlur(radius=1))
    
    # Save textures
    texture.save('/workspaces/EtherealMind/src/main/resources/assets/etherealmind/textures/entity/cosmo_advanced.png')
    emission.save('/workspaces/EtherealMind/src/main/resources/assets/etherealmind/textures/entity/cosmo_advanced_emissive.png')
    
    print("Created advanced COSMO textures")

if __name__ == "__main__":
    create_advanced_cosmo_texture()