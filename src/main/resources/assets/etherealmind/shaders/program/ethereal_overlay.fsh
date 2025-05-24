#version 150

uniform sampler2D DiffuseSampler;
uniform float Intensity;
uniform float Time;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 color = texture(DiffuseSampler, texCoord);
    
    // Add ethereal blue tint
    vec3 tint = vec3(0.4, 0.6, 1.0);
    color.rgb = mix(color.rgb, color.rgb * tint, Intensity * 0.3);
    
    // Add vignette effect
    vec2 coord = (texCoord - 0.5) * 2.0;
    float vignette = 1.0 - dot(coord, coord) * 0.25;
    color.rgb *= vignette;
    
    // Add subtle scan lines
    float scanline = sin(texCoord.y * 800.0 + Time * 10.0) * 0.04;
    color.rgb += scanline * Intensity;
    
    fragColor = color;
}