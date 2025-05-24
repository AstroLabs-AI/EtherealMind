#version 150

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform float GameTime;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;
in vec4 normal;
in vec4 glowColor;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0);
    if (color.a < 0.1) {
        discard;
    }
    
    // Base color with vertex coloring
    color *= vertexColor * ColorModulator;
    
    // Add ethereal glow effect
    float glowStrength = glowColor.a;
    vec3 glow = glowColor.rgb * glowStrength;
    
    // Pulsating rim light effect
    float rim = 1.0 - dot(normalize(normal.xyz), vec3(0.0, 0.0, 1.0));
    rim = pow(rim, 2.0);
    glow += vec3(0.5, 0.7, 1.0) * rim * (0.5 + 0.5 * sin(GameTime * 4000.0));
    
    // Combine base color with glow
    color.rgb += glow;
    
    // Apply fog
    float fogFactor = smoothstep(FogStart, FogEnd, vertexDistance);
    color.rgb = mix(color.rgb, FogColor.rgb, fogFactor * FogColor.a);
    
    fragColor = color;
}