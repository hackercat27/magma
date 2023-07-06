#version 330 core

in vec3 position;

out vec2 pass_TextureUV;

uniform mat4 transform;
uniform mat4 cameraTransform;
uniform mat4 projection;

void main() {
    gl_Position = projection * cameraTransform * transform * vec4(position, 1.0);
}