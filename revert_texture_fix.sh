#!/bin/bash
# Revert texture visibility fix for COSMO

echo "Reverting COSMO texture changes..."

# 1. Switch back to original model
sed -i 's|"geo/cosmo_simple.geo.json"|"geo/cosmo.geo.json"|g' src/main/java/com/astrolabs/etherealmind/client/model/CosmoModel.java

# 2. Restore animated texture
if [ -f "src/main/resources/assets/etherealmind/textures/entity/cosmo_backup.png" ]; then
    cp src/main/resources/assets/etherealmind/textures/entity/cosmo_backup.png src/main/resources/assets/etherealmind/textures/entity/cosmo.png
fi

# 3. Re-enable animation metadata
if [ -f "src/main/resources/assets/etherealmind/textures/entity/cosmo.png.mcmeta.disabled" ]; then
    mv src/main/resources/assets/etherealmind/textures/entity/cosmo.png.mcmeta.disabled src/main/resources/assets/etherealmind/textures/entity/cosmo.png.mcmeta
fi

echo "Reverted to original texture setup!"