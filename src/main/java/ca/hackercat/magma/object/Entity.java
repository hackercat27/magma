package ca.hackercat.magma.object;

import ca.hackercat.magma.core.Mesh;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public abstract class Entity extends Drawable {

    protected Mesh model;
    protected Vector3f position = new Vector3f(0, 0, 0);
    protected Vector3f velocity = new Vector3f(0, 0, 0);
    protected Quaternionf rotation = new Quaternionf().identity();
    protected Vector3f scale = new Vector3f(1, 1, 1);

    @Override
    public void close() {
        if (model != null) {
            model.close();
        }
    }

    public Vector3f getPosition() {
        return position;
    }
    public Quaternionf getRotation() {
        return rotation;
    }
    public Vector3f getScale() {
        return scale;
    }
    public Vector3f getVelocity() {
        return velocity;
    }
    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }
}
