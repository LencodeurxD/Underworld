#version 150

uniform sampler2D Sampler0;
uniform float Time;

in vec2 texCoord0;

out vec4 fragColor;

void main() {
    float alpha = texture(Sampler0, texCoord0).a;
    float colorShift = 0.5 + 0.5 * sin(Time * 0.35);
    vec3 deepViolet = vec3(0.11, 0.008, 0.20);
    vec3 deepBlue = vec3(0.006, 0.035, 0.20);
    vec3 darkColor = mix(deepViolet, deepBlue, colorShift);
    fragColor = vec4(darkColor, alpha);
}
