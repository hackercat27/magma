package ca.hackercat.magma.object;

import ca.hackercat.magma.Renderer;

public abstract class Drawable {
    protected int layer;
    protected boolean forRemoval;

    public abstract void update(float deltaTime);
    public abstract void draw(Renderer r);
    public abstract void close();

    public int getLayer() {
        return layer;
    }
    public boolean isForRemoval() {
        return forRemoval;
    }
}
