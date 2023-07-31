#version 330 core

in vec3 position;
in vec2 textureUV;
in vec3 normal;

out vec2 pass_TextureUV;
out vec3 pass_Normal;
out vec3 toLight;
out vec3 surfaceNormal;

uniform mat4 transform;
uniform mat4 cameraTransform;
uniform mat4 projection;
uniform vec3 lightPos;
uniform int chararcter;

void main() {
    pass_TextureUV = (textureUV / 64.0);
    pass_Normal = normal;

    gl_Position = projection * cameraTransform * transform * vec4(position, 1.0);
}