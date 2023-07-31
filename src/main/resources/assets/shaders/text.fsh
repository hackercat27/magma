#version 330 core

in vec2 pass_TextureUV;
in vec3 pass_Normal;
in vec3 toLight;
in vec3 surfaceNormal;

out vec4 fragColor;

uniform sampler2D tex;

void main() {
    vec4 col = texture(tex, pass_TextureUV);
    if (col.a == 0.0) {
        discard;
    }
    fragColor = col;
}

