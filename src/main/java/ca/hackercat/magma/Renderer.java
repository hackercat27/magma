package ca.hackercat.magma;

import ca.hackercat.magma.core.Mesh;
import ca.hackercat.magma.core.Shader;
import ca.hackercat.magma.object.Camera;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public abstract class Renderer {

    public abstract void setColor(Vector3f color);

    public abstract void fillCuboid(Vector3f position, Vector3f scale);
    public abstract void drawCuboid(Vector3f position, Vector3f scale);
    public void draw(Mesh mesh, Shader shader, Vector3f pos, Quaternionf rotation) {
        draw(mesh, shader, pos, rotation, 1f);
    }
    public void draw(Mesh mesh, Shader shader, Vector3f pos, Quaternionf rotation, float scale) {
        draw(mesh, shader, pos, rotation, new Vector3f(scale, scale, scale));
    }
    public abstract void draw(Mesh mesh, Shader shader, Vector3f pos, Quaternionf rotation, Vector3f scale);

    public abstract void drawString(String str, Vector3f pos, Quaternionf rotation);
    public abstract Camera getCamera();

}
