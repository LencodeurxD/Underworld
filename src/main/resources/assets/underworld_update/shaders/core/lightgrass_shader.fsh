#version 150

uniform sampler2D Sampler0;
uniform float Time;

in vec2 texCoord0;

out vec4 fragColor;

void main() {
    float alpha = texture(Sampler0, texCoord0).a;
    float colorShift = 0.5 + 0.5 * sin(Time * 0.35);
    vec3 paleYellow = vec3(1.0, 0.82, 0.42);
    vec3 palePink = vec3(1.0, 0.58, 0.82);
    vec3 lightTint = mix(paleYellow, palePink, colorShift);
    fragColor = vec4(lightTint, alpha);
}