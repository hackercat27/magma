#version 330 core

in vec2 pass_TextureUV;
in vec3 pass_Normal;
in vec3 toLight;
in vec3 surfaceNormal;

out vec4 fragColor;

uniform sampler2D tex;

void main() {
    fragColor = texture(tex, pass_TextureUV);
}

