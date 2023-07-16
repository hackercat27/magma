package ca.hackercat.magma.io;

import org.lwjgl.glfw.GLFWKeyCallback;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.*;

public class Keyboard {

    public static final GLFWKeyCallback keyCallback;

    private Keyboard() {}

    private static final int KEY_COUNT = GLFW_KEY_LAST;

    private static boolean[] held = new boolean[KEY_COUNT];
    private static boolean[] pressed = new boolean[KEY_COUNT];
    private static boolean[] pressedBuffer = new boolean[KEY_COUNT];

    private static Window window;

    static {
        keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scanCode, int action, int mods) {
                if (action == GLFW_RELEASE) {
                    release(key);
                }
                if (action == GLFW_PRESS) {
                    press(key);
                }
            }
        };
    }

    private static void press(int key) {
        if (key > KEY_COUNT || key < 0)
            return;

        held[key] = true;
        pressedBuffer[key] = true;
    }
    private static void release(int key) {
        if (key > KEY_COUNT || key < 0)
            return;

        held[key] = false;
    }
    public static void update() {
        System.arraycopy(pressedBuffer, 0, pressed, 0, pressed.length);
        Arrays.fill(pressedBuffer, false);
    }

    public static boolean isKeyHeld(int key) {
        if (key > KEY_COUNT || key < 0)
            return false;

        return held[key];
    }
    public static boolean isKeyPressed(int key) {
        if (key > KEY_COUNT || key < 0)
            return false;

        return pressed[key];
    }

    public static void close() {
        keyCallback.free();
    }

    public static void setWindow(Window window) {
        Keyboard.window = window;
    }
}
