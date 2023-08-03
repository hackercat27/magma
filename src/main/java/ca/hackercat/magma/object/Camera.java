package ca.hackercat.magma.object;

import ca.hackercat.magma.Renderer;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Math;

public abstract class Camera extends Drawable {

    protected Vector3f position = new Vector3f();
    protected Quaternionf rotation = new Quaternionf();
    protected float fov = Math.toRadians(90);


    public Vector3f getPosition() {
        return position;
    }
    public Quaternionf getRotation() {
        return rotation;
    }
    public float getFOV() {
        return fov;
    }
    public void setFOV(float fov) {
        this.fov = fov;
    }

    @Override
    public void draw(Renderer r) {}

    @Override
    public void close() {}

}
