package ca.hackercat.magma.core;


import ca.hackercat.magma.Renderer;
import ca.hackercat.magma.io.Window;
import ca.hackercat.magma.object.Camera;
import ca.hackercat.magma.util.MagmaMath;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13C.glActiveTexture;
import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.glBindBuffer;
import static org.lwjgl.opengl.GL20C.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20C.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;

public class MagmaRenderer extends Renderer {

//    public Vector3f cameraPosition = new Vector3f();
//    public Quaternionf cameraRotation = new Quaternionf();
//    public float cameraFOV = Math.toRadians(80);
    private Vector3f color = new Vector3f(1, 1, 1);

    private Window window;
    private Vector3f lightPos = new Vector3f(0, 100, 0);

    public Camera camera = new Camera() {
        @Override
        public void update(float deltaTime) {}
    };

    private Mesh unitCube = new Mesh(
            new Vector3f[] {
                    new Vector3f(0, 0, 0),
                    new Vector3f(0, 0, 1),
                    new Vector3f(1, 0, 1),
                    new Vector3f(1, 0, 0),
                    new Vector3f(0, 1, 0),
                    new Vector3f(0, 1, 1),
                    new Vector3f(1, 1, 1),
                    new Vector3f(1, 1, 0)
            },
            null,
            null,
            new int[] {
                    0, 3, 2, 1,
                    4, 5, 6, 7,
                    0, 1, 5, 4,
                    1, 2, 6, 5,
                    2, 3, 7, 6,
                    3, 0, 4, 7
            }
    );

    private Mesh plane = new Mesh(
            new Vector3f[] {
                    new Vector3f(0, 0, 0),
                    new Vector3f(0, 1, 0),
                    new Vector3f(1, 1, 0),
                    new Vector3f(1, 0, 0)
            },
            new Vector2f[] {
                    new Vector2f(0, 0),
                    new Vector2f(0, 1),
                    new Vector2f(1, 1),
                    new Vector2f(1, 0)
            },
            new Vector3f[] {
                    new Vector3f(0, 0, 1),
                    new Vector3f(0, 0, 1),
                    new Vector3f(0, 0, 1),
                    new Vector3f(0, 0, 1)
            },
            new int[] {
                    0, 3, 2, 1
            }
    );

    public MagmaRenderer(Window window) {
        this.window = window;
    }

    private Matrix4f getCameraTransform() {
        return MagmaMath.getCameraTransformationMatrix(camera.getPosition(), camera.getRotation());
    }
    private Matrix4f getProjectionMatrix() {
        return MagmaMath.getProjectionMatrix(window, camera.getFOV());
    }

    @Override
    public void setColor(Vector3f color) {
        this.color = color;
    }

    @Override
    public void fillCuboid(Vector3f position, Vector3f scale) {
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        glEnable(GL_CULL_FACE);
        genericCuboid(position, scale);
    }

    @Override
    public void drawCuboid(Vector3f position, Vector3f scale) {
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        glLineWidth(1f);
        glDisable(GL_CULL_FACE);
        genericCuboid(position, scale);
    }

    private void genericCuboid(Vector3f position, Vector3f scale) {
        glEnable(GL_DEPTH_TEST);
        glBindVertexArray(unitCube.getVertexArray());
        glEnableVertexAttribArray(0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, unitCube.getIndexBuffer());

        Shader shader = Shader.COLOR_SHADER;
        shader.bind();
        shader.setUniform("transform", MagmaMath.getTransformationMatrix(position, new Quaternionf(), scale));
        shader.setUniform("cameraTransform", getCameraTransform());
        shader.setUniform("projection", getProjectionMatrix());
        shader.setUniform("color", color);

        glDrawElements(GL_QUADS, unitCube.getIndices().length, GL_UNSIGNED_INT, 0);

        shader.unbind();

        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
    }

    @Override
    public void draw(Mesh mesh, Shader shader, Vector3f pos, Quaternionf rotation, Vector3f scale) {
        if (mesh == null || pos == null || rotation == null || scale == null) {
            return;
        }
        if (shader == null) {
            shader = Shader.DEFAULT_SHADER;
        }

        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glBindVertexArray(mesh.getVertexArray());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mesh.getIndexBuffer());
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, mesh.getTextureID());
        glBindBuffer(GL_ARRAY_BUFFER, mesh.getNormalBuffer());

        shader.bind();

        shader.emptyQueue();

        shader.setUniform("transform", MagmaMath.getTransformationMatrix(pos, rotation, scale));
        shader.setUniform("cameraTransform", getCameraTransform());
        shader.setUniform("projection", getProjectionMatrix());
        shader.setUniform("lightPos", lightPos);

        glDrawElements(GL_TRIANGLES, mesh.getIndices().length, GL_UNSIGNED_INT, 0);

        shader.unbind();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);
    }

    @Override
    public void drawString(String str, Vector3f pos, Quaternionf rotation) {



    }

    @Override
    public Camera getCamera() {
        return camera;
    }

    public void init() {
        glViewport(0, 0, window.getWidth(), window.getHeight());
        glClear(GL_COLOR_BUFFER_BIT);
        glClearColor(0, 0, 0, 1);
    }

    public void clearDepthBuffer() {
        glClear(GL_DEPTH_BUFFER_BIT);
    }
    public void close() {
        unitCube.close();
    }
}