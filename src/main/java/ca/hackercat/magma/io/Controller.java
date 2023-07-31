package ca.hackercat.magma.io;

import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFW.*;

public class Controller {

    private static final int size = GLFW_GAMEPAD_BUTTON_LAST;
    private static final boolean[] held = new boolean[size];
    private static final boolean[] heldBuffer = new boolean[size];

    public static void update() {
        System.arraycopy(heldBuffer, 0, held, 0, size);

        ByteBuffer b = glfwGetJoystickButtons(GLFW_JOYSTICK_1);

        if (b == null) {
            return;
        }

        for (int i = held.length - 1; i >= 0; i--) {
            heldBuffer[i] = b.get() != 0;
        }


    }

    public static boolean isButtonHeld(int button) {
        return held[button];
    }

}
