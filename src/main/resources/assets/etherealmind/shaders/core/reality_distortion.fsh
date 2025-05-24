#version 150

uniform sampler2D Sampler0;
uniform sampler2D DepthBuffer;

uniform float GameTime;
uniform vec2 ScreenSize;

in vec2 texCoord;
in vec4 vertexColor;
in vec2 distortionCoord;

out vec4 fragColor;

vec2 distort(vec2 coord, float time) {
    float strength = 0.05;
    vec2 offset = vec2(
        sin(coord.y * 10.0 + time * 2.0) * strength,
        cos(coord.x * 10.0 + time * 2.0) * strength
    );
    return coord + offset;
}

void main() {
    // Calculate distortion based on time and position
    float time = GameTime * 1000.0;
    vec2 distortedCoord = distort(texCoord, time);
    
    // Sample the texture with distortion
    vec4 color = texture(Sampler0, distortedCoord);
    
    if (color.a < 0.01) {
        discard;
    }
    
    // Apply vertex color
    color *= vertexColor;
    
    // Add chromatic aberration effect
    float aberrationStrength = 0.002;
    vec2 redOffset = vec2(aberrationStrength, 0.0);
    vec2 blueOffset = vec2(-aberrationStrength, 0.0);
    
    float r = texture(Sampler0, distortedCoord + redOffset).r;
    float g = color.g;
    float b = texture(Sampler0, distortedCoord + blueOffset).b;
    
    color.rgb = vec3(r, g, b);
    
    // Add ethereal glow
    float glowFactor = sin(time * 0.001) * 0.5 + 0.5;
    color.rgb += vec3(0.2, 0.3, 0.5) * glowFactor * 0.3;
    
    fragColor = color;
}