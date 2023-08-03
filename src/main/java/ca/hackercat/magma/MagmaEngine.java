package ca.hackercat.magma;

import ca.hackercat.logging.Logger;
import ca.hackercat.magma.core.MagmaRenderer;
import ca.hackercat.magma.core.Shader;
import ca.hackercat.magma.io.Controller;
import ca.hackercat.magma.io.Keyboard;
import ca.hackercat.magma.io.Mouse;
import ca.hackercat.magma.io.Window;
import ca.hackercat.magma.object.Camera;
import ca.hackercat.magma.object.Drawable;
import ca.hackercat.magma.io.SoundEventManager;
import org.lwjgl.opengl.GLCapabilities;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MagmaEngine {

    private Logger LOGGER = Logger.get(MagmaEngine.class);

    private Window window;

    private boolean running;
    private MagmaRenderer renderer;

    private int counter;

    private final List<Drawable> objects = new ArrayList<>(0);

    public MagmaEngine(String title) {
        this(title, 852, 480);
    }
    public MagmaEngine(String title, int initialWidth, int initialHeight) {
        window = new Window(this, initialWidth, initialHeight, title);
        init();
    }

    private void init() {
        StringBuilder str = new StringBuilder();
        str.append("Properties:").append("\n");

        String[] properties = new String[] {
                "java.vendor",
                "java.version",
                "os.name",
                "os.version",
                "user.dir"
        };

        for (String property : properties) {
            str.append(property).append(" = ").append(System.getProperty(property)).append("\n");
        }

        LOGGER.log(str);

        GLCapabilities capabilities = window.getGLCapabilities();

        StringBuilder cap = new StringBuilder();

        for (Field field : GLCapabilities.class.getFields()) {

            Type type = field.getType();

            if (type.getTypeName().equals(boolean.class.getTypeName())) {

                try {
                    boolean b = field.getBoolean(capabilities);

                    if (b) {
                        cap.append(field.getName()).append("\n");
                    }

                } catch (IllegalAccessException e) {
                    LOGGER.error(e);
                }

            }

        }

        LOGGER.log(cap);


        Mouse.setWindow(window);
        Keyboard.setWindow(window);

        renderer = new MagmaRenderer(window);
    }

    public void start() {
        running = true;
        while (running) {
            update();
            render();

            window.swapBuffers();
        }
        close();
        LOGGER.log("Finished execution");
    }

    public void halt() {
        running = false;
    }

    private void update() {

        counter++;
        if (counter > 1000) {
            counter = 0;
            SoundEventManager.cleanManagers();
        }

        float deltaTime = (window.getRenderTimeMillis() - window.getLastRenderTimeMillis()) / 1000f;

        Keyboard.update();
        Mouse.update();
        Controller.update();
        window.update();

        // garbage collection
        List<Drawable> removableObjects = new ArrayList<>(0);
        for (int i = 0, size = objects.size(); i < size; i++) {
            Drawable object = objects.get(i);
            if (object.isForRemoval()) {
                removableObjects.add(object);
                object.close();
            }
        }
        objects.removeAll(removableObjects);

        for (int i = 0, size = objects.size(); i < size; i++) {
            Drawable object = objects.get(i);
            object.update(deltaTime);

            if (object instanceof Camera c) {
                renderer.camera = c;
            }
        }
    }

    private void render() {
        renderer.init();
        objects.sort(Comparator.comparingInt(Drawable::getLayer));


        long layer = objects.get(0).getLayer();
        renderer.clearDepthBuffer();
        for (int i = 0, size = objects.size(); i < size; i++) {
            Drawable object = objects.get(i);

            object.draw(renderer);

            if (layer < object.getLayer()) {
                layer = object.getLayer();
                renderer.clearDepthBuffer();
            }
        }

        window.setLastRenderTimeMillis(window.getRenderTimeMillis());
        window.setRenderTimeMillis(System.currentTimeMillis());
    }

    public void close() {
        LOGGER.log("Cleaning system resources");

        Keyboard.close();
        Mouse.close();

        for (Drawable object : objects) {
            object.close();
        }

        Shader.closeAllInstances();

        renderer.close();

        // this needs to be last because glfw is more important.
        // stop putting things after this you idiot
        window.close();
    }

    public void add(Drawable obj) {
        objects.add(obj);
    }

    public Window getWindow() {
        return window;
    }

    // probably a bad idea
    public List<Drawable> getObjects() {
        return objects;
    }
}
