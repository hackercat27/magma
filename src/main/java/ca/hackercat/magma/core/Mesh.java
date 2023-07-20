package ca.hackercat.magma.core;

import ca.hackercat.logging.Logger;
import ca.hackercat.magma.io.Texture;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30C.*;

public class Mesh {

    private static final Logger LOG = Logger.get(Mesh.class);

    private Vector3f[] vertices;
    private Vector2f[] uvs;
    private Vector3f[] normals;
    private int[] indices;
    private Texture texture;

    private int vao;
    private int indexBuffer; // index
    private int positionBuffer; // position
    private int textureUVBuffer; // texture uvs
    private int normalBuffer; // normals

    public Mesh(Vector3f[] vertices, Vector2f[] uvs, Vector3f[] normals, int[] indices) {
        this.indices = indices;
        this.vertices = vertices;
        this.uvs = uvs;
        this.normals = normals;
        create();
    }
    public Mesh(Vector3f[] vertices, Vector2f[] uvs, Vector3f[] normals, int[] indices, Texture texture) {
        this.indices = indices;
        this.texture = texture;
        this.vertices = vertices;
        this.uvs = uvs;
        this.normals = normals;
        create();
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    private void create() {
        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        FloatBuffer positionBuffer = MemoryUtil.memAllocFloat(vertices.length * 3);
        float[] positionData = new float[vertices.length * 3];
        for (int i = 0; i < vertices.length; i++) {
            positionData[i * 3] = vertices[i].x();
            positionData[(i * 3) + 1] = vertices[i].y();
            positionData[(i * 3) + 2] = vertices[i].z();
        }
        positionBuffer.put(positionData).flip();
        this.positionBuffer = storeData(positionBuffer, 0, 3);
        MemoryUtil.memFree(positionBuffer);

        if (uvs != null) {
            FloatBuffer textureUVBuffer = MemoryUtil.memAllocFloat(uvs.length * 2);
            float[] textureData = new float[uvs.length * 2];
            for (int i = 0; i < uvs.length; i++) {
                textureData[i * 2] = uvs[i].x();
                textureData[(i * 2) + 1] = uvs[i].y();
            }
            textureUVBuffer.put(textureData).flip();
            this.textureUVBuffer = storeData(textureUVBuffer, 1, 2);
            MemoryUtil.memFree(textureUVBuffer);
        }

        if (normals != null) {

            FloatBuffer normalBuffer = MemoryUtil.memAllocFloat(normals.length * 3);
            float[] normalData = new float[normals.length * 3];
            for (int i = 0; i < normals.length; i++) {
                normalData[i * 3] = normals[i].x();
                normalData[(i * 3) + 1] = normals[i].y();
                normalData[(i * 3) + 2] = normals[i].z();
            }
            normalBuffer.put(normalData).flip();
            this.normalBuffer = storeData(normalBuffer, 2, 3);
            MemoryUtil.memFree(normalBuffer);
        }

        IntBuffer indexBuffer = MemoryUtil.memAllocInt(indices.length);
        indexBuffer.put(indices).flip();

        this.indexBuffer = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.indexBuffer);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        MemoryUtil.memFree(indexBuffer);

        glBindVertexArray(0);

        LOG.log(
                "Created mesh (" + this.positionBuffer + ", "
                + this.textureUVBuffer + ", "
                + this.normalBuffer + ", "
                + this.indexBuffer + ") ("
                + vertices.length + ", "
                + (uvs == null? "null" : uvs.length) + ", "
                + (normals == null? "null" : normals.length) + ", "
                + indices.length + ")"
        );
    }

    private int storeData(FloatBuffer buffer, int index, int size) {
        int bufferID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, bufferID);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        glVertexAttribPointer(index, size, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        return bufferID;
    }

    public void close() {
        glDeleteBuffers(positionBuffer);
        glDeleteBuffers(indexBuffer);
        glDeleteBuffers(textureUVBuffer);
        glDeleteBuffers(normalBuffer);

        if (texture != null) {
            texture.cleanup();
        }

        glDeleteVertexArrays(vao);
    }

    public int[] getIndices() {
        return indices;
    }

    public int getVertexArray() {
        return vao;
    }

    public int getPositionBuffer() {
        return positionBuffer;
    }

    public int getIndexBuffer() {
        return indexBuffer;
    }

    public int getTextureUVBuffer() {
        return textureUVBuffer;
    }

    public int getNormalBuffer() {
        return normalBuffer;
    }

    public int getTextureID() {
        if (texture == null)
            return 0;
        return texture.getID();
    }
}
