#version 150

in vec3 Position;
in vec4 Color;
in vec2 UV0;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 ChunkOffset;
uniform float GameTime;

out vec2 texCoord;
out vec4 vertexColor;
out vec2 distortionCoord;

void main() {
    vec3 pos = Position + ChunkOffset;
    gl_Position = ProjMat * ModelViewMat * vec4(pos, 1.0);
    
    texCoord = UV0;
    vertexColor = Color;
    
    // Calculate distortion coordinates for warping effect
    vec4 viewPos = ModelViewMat * vec4(pos, 1.0);
    distortionCoord = viewPos.xy / viewPos.w;
    distortionCoord = distortionCoord * 0.5 + 0.5;
}