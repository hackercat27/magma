package ca.hackercat.magma.io;

import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.*;

public class Mouse {

    public static final GLFWCursorPosCallback posCallback;
    public static final GLFWMouseButtonCallback buttonCallback;
    public static final GLFWScrollCallback scrollCallback;

    private static final int BUTTON_COUNT = GLFW_MOUSE_BUTTON_LAST;

    private static boolean[] held = new boolean[BUTTON_COUNT];
    private static boolean[] pressed = new boolean[BUTTON_COUNT];
    private static boolean[] pressedBuffer = new boolean[BUTTON_COUNT];

    private static double x, y, lastX, lastY;
    private static double xScroll, yScroll, lastXScroll, lastYScroll;
    private static boolean grabbed = false;
    private static Window window;

    static {
        posCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xPos, double yPos) {
                x = xPos;
                y = yPos;
            }
        };
        buttonCallback = new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                if (action == GLFW_PRESS) {
                    press(button);
                }
                if (action == GLFW_RELEASE) {
                    release(button);
                }
            }
        };
        scrollCallback = new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xOffset, double yOffset) {
                xScroll += xOffset;
                yScroll += yOffset;
            }
        };
    }

    private static void press(int button) {
        held[button] = true;
        pressedBuffer[button] = true;
    }
    private static void release(int button) {
        held[button] = false;
    }

    public static void update() {
        System.arraycopy(pressedBuffer, 0, pressed, 0, pressed.length);
        Arrays.fill(pressedBuffer, false);
        lastX = x;
        lastY = y;
        lastXScroll = xScroll;
        lastYScroll = yScroll;
        if (grabbed) {
            glfwSetInputMode(window.getGLFWWindow(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
            glfwSetInputMode(window.getGLFWWindow(), GLFW_RAW_MOUSE_MOTION, GLFW_TRUE);
        }
        else {
            glfwSetInputMode(window.getGLFWWindow(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            glfwSetInputMode(window.getGLFWWindow(), GLFW_RAW_MOUSE_MOTION, GLFW_FALSE);
        }
    }


    public static boolean isButtonHeld(int button) {
        return held[button];
    }
    public static boolean isButtonPressed(int button) {
        return pressed[button];
    }

    public static double getX() {
        return x;
    }
    public static double getY() {
        return y;
    }
    public static double getDX() {
        return x - lastX;
    }
    public static double getDY() {
        return y - lastY;
    }
    public static double getDYScroll() {
        return yScroll - lastYScroll;
    }
    public static double getDXScroll() {
        return xScroll - lastXScroll;
    }

    public static void close() {
        posCallback.free();
        buttonCallback.free();
        scrollCallback.free();
    }

    public static boolean isGrabbed() {
        return grabbed;
    }

    public static void setGrabbed(boolean grabbed) {
        Mouse.grabbed = grabbed;
    }

    public static void setWindow(Window window) {
        Mouse.window = window;
    }
}
