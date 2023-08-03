package ca.hackercat.magma.object;

import ca.hackercat.magma.Renderer;
import ca.hackercat.magma.io.Keyboard;
import ca.hackercat.magma.io.Mouse;
import ca.hackercat.magma.util.MagmaMath;
import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class ControllableCamera extends Camera {

    private float normalFOV = Math.toRadians(70);
    private float zoomFOV = Math.toRadians(30);

    private float yaw, pitch;

    public void update(float deltaTime) {

        if (Keyboard.isKeyPressed(GLFW_KEY_ESCAPE)) {
            Mouse.setGrabbed(!Mouse.isGrabbed());
        }

        if (!Mouse.isGrabbed())
            return;

        final float speed = 1;
        final float mouseSensitivity = 0.01f;

        if (Keyboard.isKeyHeld(GLFW_KEY_W)) {
            position.z -= speed * Math.cos(yaw) * deltaTime;
            position.x -= speed * Math.sin(yaw) * deltaTime;
        }
        if (Keyboard.isKeyHeld(GLFW_KEY_S)) {
            position.z += speed * Math.cos(yaw) * deltaTime;
            position.x += speed * Math.sin(yaw) * deltaTime;
        }
        if (Keyboard.isKeyHeld(GLFW_KEY_A)) {
            position.z += speed * Math.sin(yaw) * deltaTime;
            position.x -= speed * Math.cos(yaw) * deltaTime;
        }
        if (Keyboard.isKeyHeld(GLFW_KEY_D)) {
            position.z -= speed * Math.sin(yaw) * deltaTime;
            position.x += speed * Math.cos(yaw) * deltaTime;
        }
        if (Keyboard.isKeyHeld(GLFW_KEY_SPACE)) {
            position.y += speed * deltaTime;
        }
        if (Keyboard.isKeyHeld(GLFW_KEY_LEFT_SHIFT)) {
            position.y -= speed * deltaTime;
        }

        yaw -= Mouse.getDX() * mouseSensitivity;
        pitch -= Mouse.getDY() * mouseSensitivity;
        pitch = (float) Math.max(Math.min(pitch, Math.PI / 2), -Math.PI / 2);

        rotation = new Quaternionf();

        Vector3f xRotationAxis = new Vector3f(1, 0, 0);
        rotation.rotateAxis(-pitch, xRotationAxis);
        Vector3f yRotationAxis = new Vector3f(0, 1, 0);
        rotation.rotateAxis(-yaw, yRotationAxis);

        float targetFOV;
        if (Keyboard.isKeyHeld(GLFW_KEY_Z)) {
            targetFOV = zoomFOV;
            zoomFOV += Math.toRadians(-Mouse.getDYScroll() * fov * 8);
        }
        else {
            targetFOV = normalFOV;
        }
        fov = MagmaMath.ease(fov, targetFOV, 0.1f);

    }

    @Override
    public void draw(Renderer r) {}

    @Override
    public void close() {}
}
