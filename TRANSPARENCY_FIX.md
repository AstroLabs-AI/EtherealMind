# COSMO Transparency Fix

## Issue
COSMO was appearing transparent/see-through instead of solid.

## Cause
The renderer was using `RenderType.entityTranslucent(texture)` which is meant for semi-transparent entities like ghosts.

## Solution
Changed to `RenderType.entityCutoutNoCull(texture)` which:
- Renders solid pixels as fully opaque
- Only makes truly transparent pixels (alpha = 0) invisible
- Perfect for entities with solid bodies and transparent areas

## Result
COSMO now appears as a solid, opaque AI companion with:
- Fully visible blue body
- No transparency issues
- Proper rendering of all features

The texture itself was fine - it was just the render type causing the transparency.