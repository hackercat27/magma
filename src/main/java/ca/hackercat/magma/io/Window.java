package ca.hackercat.magma.io;

import ca.hackercat.logging.Logger;
import ca.hackercat.magma.MagmaEngine;
import ca.hackercat.magma.io.FileUtils;
import ca.hackercat.magma.io.Keyboard;
import ca.hackercat.magma.io.Mouse;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static ca.hackercat.logging.Logger.LOGGER;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

public class Window {

    private enum State {
        WINDOWED,
        WINDOWED_BORDERLESS,
        FULLSCREEN
    }

    private State state = State.WINDOWED;

    private final int initialWidth;
    private final int initialHeight;
    private int width;
    private int height;
    private String title;
    private GLFWVidMode videoMode;

    private long window;

    private GLFWWindowSizeCallback sizeCallback;
    private GLFWWindowCloseCallback closeCallback;

    private long renderTimeMillis = System.currentTimeMillis();
    private long lastRenderTimeMillis = System.currentTimeMillis() - 10L;

    private MagmaEngine engine;

    public Window(MagmaEngine engine, int width, int height, String title) {
        this.engine = engine;
        this.width = width;
        this.height = height;
        this.initialWidth = width;
        this.initialHeight = height;
        this.title = title;

        sizeCallback = new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                setWidth(width);
                setHeight(height);
            }
        };
        closeCallback = new GLFWWindowCloseCallback() {
            @Override
            public void invoke(long l) {
                engine.halt();
            }
        };
        create();
    }
    private void setWidth(int width) {
        this.width = width;
    }
    private void setHeight(int height) {
        this.height = height;
    }

    private void create() {
        boolean init = glfwInit();
        if (!init) {
            System.err.println("error initializing glfw");
            return;
        }

        window = glfwCreateWindow(width, height, title, 0, 0);

        if (window == 0) {
            System.err.println("window was not created");
            return;
        }

        videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (videoMode == null) {
            System.err.println("glfw video mode == null!");
            return;
        }
        glfwSetWindowPos(window, (videoMode.width() - width) / 2, (videoMode.height() - height) / 2);


        glfwShowWindow(window);

        glfwMakeContextCurrent(window);
        // swapInterval == vsync, 0 == off, 1 == on
        glfwSwapInterval(1);
        createCallbacks();
        GL.createCapabilities();

        // load icon
//        InputStream is = FileUtils.getInputStream("/icon.png");
//        if (is != null) {
//            IntBuffer width = BufferUtils.createIntBuffer(1);
//            IntBuffer height = BufferUtils.createIntBuffer(1);
//            IntBuffer channels = BufferUtils.createIntBuffer(1);
//
//            try {
//                byte[] data  = is.readAllBytes();
//
//            ByteBuffer inBuffer = BufferUtils.createByteBuffer(data.length);
//            inBuffer.put(data).flip();
//            ByteBuffer image = stbi_load_from_memory(inBuffer, width, height, channels, 0);
//
//            if (image != null) {
//                glfwSetWindowIcon(window, new GLFWImage.Buffer(image));
//                stbi_image_free(image);
//
////                this.width = width.get(0);
////                this.height = height.get(0);
//            }
//
//
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }

    }

    private void createCallbacks() {
        glfwSetWindowSizeCallback(window, sizeCallback);
        glfwSetWindowCloseCallback(window, closeCallback);
        glfwSetKeyCallback(window, Keyboard.keyCallback);
        glfwSetCursorPosCallback(window, Mouse.posCallback);
        glfwSetMouseButtonCallback(window, Mouse.buttonCallback);
        glfwSetScrollCallback(window, Mouse.scrollCallback);
    }

    public void update() {
        if (Keyboard.isKeyPressed(GLFW_KEY_F11)) {
            if (state == State.WINDOWED) {
                setFullscreen();
            }
            else {
                setWindowed();
            }
        }

        glfwPollEvents();
    }

    public void swapBuffers() {
        glfwSwapBuffers(window);
    }


    public void close() {
        sizeCallback.free();
        closeCallback.free();
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    public void setFullscreen() {
        if (state == State.FULLSCREEN)
            return;
        state = State.FULLSCREEN;

        long monitor = glfwGetPrimaryMonitor();

        int width = videoMode.width();
        int height = videoMode.height();
        int rate = videoMode.refreshRate();

        glfwSetWindowMonitor(window, monitor, 0, 0, width, height, rate);
        LOGGER.log("Set to fullscreen");
    }
    public void setWindowedFullscreen() {

    }
    public void setWindowed() {
        if (state == State.WINDOWED)
            return;
        state = State.WINDOWED;

        int width = videoMode.width();
        int height = videoMode.height();

        glfwSetWindowMonitor(window, 0, (width - initialWidth) / 2, (height - initialHeight) / 2, initialWidth, initialHeight, 0);
//        glfwSetWindowPos(window, (width - initialWidth) / 2, (height - initialHeight) / 2);.
        LOGGER.log("Set to windowed");
    }


    public float getFPS() {
        return 1000f / (renderTimeMillis - lastRenderTimeMillis);
    }
    public long getLastFrameDurationMillis() {
        return renderTimeMillis - lastRenderTimeMillis;
    }

    public void setRenderTimeMillis(long renderTimeMillis) {
        this.renderTimeMillis = renderTimeMillis;
    }

    public void setLastRenderTimeMillis(long lastRenderTimeMillis) {
        this.lastRenderTimeMillis = lastRenderTimeMillis;
    }

    public long getRenderTimeMillis() {
        return renderTimeMillis;
    }

    public long getLastRenderTimeMillis() {
        return lastRenderTimeMillis;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public long getGLFWWindow() {
        return window;
    }

}
