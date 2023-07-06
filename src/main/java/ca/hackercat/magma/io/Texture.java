package ca.hackercat.magma.io;

import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture {

    private String filePath;
    private int id;
    private int width, height;

    public Texture(int width, int height) {
        this.width = width;
        this.height = height;

        id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, id);

//        ByteBuffer b = MemoryUtil.memAlloc(width * height * 4);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, 0);
//        MemoryUtil.memFree(b);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    }

    public Texture(String filePath) {
        this.filePath = filePath;

        id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, id);

        // wrap image in both x and y coords (s and t)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);


        try {
            byte[] data = FileUtils.getInputStream(filePath).readAllBytes();

            ByteBuffer inBuffer = BufferUtils.createByteBuffer(data.length);
            inBuffer.put(data).flip();
            ByteBuffer image = stbi_load_from_memory(inBuffer, width, height, channels, 0);

            if (image != null) {
                // something something mipmap levels go here
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0), 0,
                        GL_RGBA, GL_UNSIGNED_BYTE, image);
                stbi_image_free(image);

                this.width = width.get(0);
                this.height = height.get(0);
            }
            return;
        } catch (IOException e) {}
        System.err.println("couldn't load image '" + filePath + "'");
    }

    public void cleanup() {
        glDeleteTextures(id);
    }

    public int getID() {
        return id;
    }
}
