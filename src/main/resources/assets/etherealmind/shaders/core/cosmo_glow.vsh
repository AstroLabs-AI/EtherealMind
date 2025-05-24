#version 150

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV2;
in vec3 Normal;

uniform sampler2D Sampler2;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 ChunkOffset;
uniform float GameTime;

out float vertexDistance;
out vec4 vertexColor;
out vec2 texCoord0;
out vec4 normal;
out vec4 glowColor;

void main() {
    vec3 pos = Position + ChunkOffset;
    
    // Add floating animation
    pos.y += sin(GameTime * 2000.0) * 0.1;
    
    gl_Position = ProjMat * ModelViewMat * vec4(pos, 1.0);

    vertexDistance = length((ModelViewMat * vec4(pos, 1.0)).xyz);
    vertexColor = Color * texelFetch(Sampler2, UV2 / 16, 0);
    texCoord0 = UV0;
    normal = ProjMat * ModelViewMat * vec4(Normal, 0.0);
    
    // Calculate glow intensity based on time
    float glowIntensity = 0.5 + 0.5 * sin(GameTime * 3000.0);
    glowColor = vec4(0.4, 0.6, 1.0, glowIntensity);
}