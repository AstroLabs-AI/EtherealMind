#!/bin/bash
# Apply texture visibility fix for COSMO

echo "Applying COSMO texture visibility fix..."

# 1. Switch to simple model (64x64 texture support)
sed -i 's|"geo/cosmo.geo.json"|"geo/cosmo_simple.geo.json"|g' src/main/java/com/astrolabs/etherealmind/client/model/CosmoModel.java

# 2. Use static texture
cp src/main/resources/assets/etherealmind/textures/entity/cosmo_static.png src/main/resources/assets/etherealmind/textures/entity/cosmo.png

# 3. Remove animation metadata (temporarily)
if [ -f "src/main/resources/assets/etherealmind/textures/entity/cosmo.png.mcmeta" ]; then
    mv src/main/resources/assets/etherealmind/textures/entity/cosmo.png.mcmeta src/main/resources/assets/etherealmind/textures/entity/cosmo.png.mcmeta.disabled
fi

echo "Fix applied! Changes made:"
echo "- Switched to simple model (cosmo_simple.geo.json) that supports 64x64 textures"
echo "- Replaced animated texture with static 64x64 texture"
echo "- Disabled texture animation metadata"
echo ""
echo "To revert these changes, run: ./revert_texture_fix.sh"