from PIL import Image, ImageDraw

# Create a 256x256 GUI texture (256x222 used, rest for texture atlas)
img = Image.new('RGBA', (256, 256), (0, 0, 0, 0))
draw = ImageDraw.Draw(img)

# GUI dimensions
GUI_WIDTH = 256
GUI_HEIGHT = 222

# Main GUI background - more standard Minecraft style
# Outer frame
draw.rectangle([0, 0, GUI_WIDTH-1, GUI_HEIGHT-1], fill=(198, 198, 198, 255), outline=(0, 0, 0, 255), width=2)

# Inner dark background
draw.rectangle([3, 3, GUI_WIDTH-4, GUI_HEIGHT-4], fill=(139, 139, 139, 255))

# Title bar area
draw.rectangle([3, 3, GUI_WIDTH-4, 20], fill=(79, 79, 79, 255))

# Chat area frame (left side)
CHAT_X = 8
CHAT_Y = 50
CHAT_WIDTH = 160
CHAT_HEIGHT = 100

# Chat area border
draw.rectangle([CHAT_X-2, CHAT_Y-2, CHAT_X+CHAT_WIDTH+1, CHAT_Y+CHAT_HEIGHT+1], 
               fill=(0, 0, 0, 255))
# Chat area background (will be overlaid with transparency in game)
draw.rectangle([CHAT_X, CHAT_Y, CHAT_X+CHAT_WIDTH, CHAT_Y+CHAT_HEIGHT], 
               fill=(40, 40, 40, 255))

# Stats panel area (right side)
STATS_X = 176
STATS_Y = 8
STATS_WIDTH = 72
STATS_HEIGHT = 80

# Stats panel border
draw.rectangle([STATS_X-2, STATS_Y-2, STATS_X+STATS_WIDTH+1, STATS_Y+STATS_HEIGHT+1], 
               fill=(0, 0, 0, 255))
# Stats panel background
draw.rectangle([STATS_X, STATS_Y, STATS_X+STATS_WIDTH, STATS_Y+STATS_HEIGHT], 
               fill=(60, 60, 60, 255))

# Input area frame
INPUT_Y = 154
draw.rectangle([CHAT_X-2, INPUT_Y-2, CHAT_X+CHAT_WIDTH+1, INPUT_Y+20], 
               fill=(0, 0, 0, 255))
draw.rectangle([CHAT_X, INPUT_Y, CHAT_X+CHAT_WIDTH, INPUT_Y+18], 
               fill=(0, 0, 0, 255))

# Player inventory area (bottom)
INV_Y = 176
# Standard MC inventory background
draw.rectangle([7, INV_Y-1, 248, GUI_HEIGHT-4], fill=(139, 139, 139, 255), outline=(0, 0, 0, 255))

# Inventory slots (9x4 + 9 hotbar)
for row in range(3):  # Main inventory
    for col in range(9):
        x = 8 + col * 18
        y = INV_Y + row * 18
        # Slot shadow
        draw.rectangle([x-1, y-1, x+17, y+17], fill=(55, 55, 55, 255))
        # Slot background
        draw.rectangle([x, y, x+16, y+16], fill=(139, 139, 139, 255))

# Hotbar slots
for col in range(9):
    x = 8 + col * 18
    y = INV_Y + 58
    # Slot shadow
    draw.rectangle([x-1, y-1, x+17, y+17], fill=(55, 55, 55, 255))
    # Slot background
    draw.rectangle([x, y, x+16, y+16], fill=(139, 139, 139, 255))

# Button areas (right side)
# Storage button area
draw.rectangle([STATS_X-1, 94, STATS_X+STATS_WIDTH, 116], fill=(0, 0, 0, 255))
draw.rectangle([STATS_X, 95, STATS_X+STATS_WIDTH-1, 115], fill=(100, 100, 100, 255))

# Abilities button area
draw.rectangle([STATS_X-1, 119, STATS_X+STATS_WIDTH, 141], fill=(0, 0, 0, 255))
draw.rectangle([STATS_X, 120, STATS_X+STATS_WIDTH-1, 140], fill=(100, 100, 100, 255))

# Mode indicator area
draw.rectangle([STATS_X-1, 144, STATS_X+STATS_WIDTH, 158], fill=(79, 79, 79, 255))

# Decorative tech lines
for y in range(25, 45, 4):
    draw.line([(8, y), (168, y)], fill=(60, 60, 80, 100), width=1)

# Corner tech details
# Top left
draw.polygon([(8, 25), (25, 25), (8, 42)], fill=(100, 150, 200, 100))
# Top right
draw.polygon([(168, 25), (151, 25), (168, 42)], fill=(100, 150, 200, 100))

# Save the texture
img.save('src/main/resources/assets/etherealmind/textures/gui/cosmo_companion.png')
print("Created fixed companion GUI texture!")