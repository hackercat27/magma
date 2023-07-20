package ca.hackercat.magma.core;

import ca.hackercat.logging.Logger;
import ca.hackercat.magma.io.FileUtils;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;

public class Shader {

    // make these available so that if programs using this
    // engine want to use them they don't need to be in memory twice
    public static final Shader DEFAULT_SHADER = new Shader("DEFAULT_SHADER", "/assets/shaders/default.vsh", "/assets/shaders/default.fsh");
    public static final Shader COLOR_SHADER = new Shader("COLOR_SHADER", "/assets/shaders/color.vsh", "/assets/shaders/color.fsh");

    private static final Logger LOGGER = Logger.get(Shader.class);

    private String vertexSource, fragmentSource;
    private String vertexPath, fragmentPath;
    private String name;
    private int vertexID, fragmentID, programID;


    public Shader(String vertexPath, String fragmentPath) {
        this(null, vertexPath, fragmentPath);
    }
    public Shader(String name, String vertexPath, String fragmentPath) {
        this.fragmentPath = fragmentPath;
        this.vertexSource = FileUtils.getContents(vertexPath);
        this.fragmentSource = FileUtils.getContents(fragmentPath);
        this.vertexPath = vertexPath;

        if (name == null) {
            this.name = this.toString();
        }
        else {
            this.name = name;
        }

        create();
    }

    private void create() {
        programID = glCreateProgram();
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexID, vertexSource);
        glCompileShader(vertexID);

        if (glGetShaderi(vertexID, GL_COMPILE_STATUS) == GL_FALSE) {
            LOGGER.error(vertexPath + " couldn't compile\n"
                    + glGetShaderInfoLog(vertexID));
            return;
        }

        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentID, fragmentSource);
        glCompileShader(fragmentID);
        if (glGetShaderi(fragmentID, GL_COMPILE_STATUS) == GL_FALSE) {
            LOGGER.error(fragmentPath + " couldn't compile\n"
                    + glGetShaderInfoLog(fragmentID));
            return;
        }

        glAttachShader(programID, vertexID);
        glAttachShader(programID, fragmentID);

        glLinkProgram(programID);
        if (glGetProgrami(programID, GL_LINK_STATUS) == GL_FALSE) {
            LOGGER.error("Shader " + name + " initialization error - Couldn't link program\n" + glGetProgramInfoLog(programID));
            return;
        }
        glValidateProgram(programID);
        if (glGetProgrami(programID, GL_VALIDATE_STATUS) == GL_FALSE) {
            LOGGER.error("Shader " + name + " initialization error Program is invalid\n" + glGetProgramInfoLog(programID));
            return;
        }

//        glDeleteShader(vertexID);
//        glDeleteShader(fragmentID);
    }

    public int getUniformLocation(String name) {
        return glGetUniformLocation(programID, name);
    }

    public void setUniform(String name, float value) {
        glUniform1f(getUniformLocation(name), value);
    }
    public void setUniform(String name, int value) {
        glUniform1i(getUniformLocation(name), value);
    }
    public void setUniform(String name, boolean value) {
        glUniform1i(getUniformLocation(name), value? 1 : 0);
    }
    public void setUniform(String name, Vector2f value) {
        glUniform2f(getUniformLocation(name), value.x(), value.y());
    }
    public void setUniform(String name, Vector3f value) {
        glUniform3f(getUniformLocation(name), value.x(), value.y(), value.z());
    }
    public void setUniform(String name, Vector4f value) {
        glUniform4f(getUniformLocation(name), value.x(), value.y(), value.z(), value.w());
    }
    public void setUniform(String name, Matrix4f value) {
        FloatBuffer matBuffer = MemoryUtil.memAllocFloat(16);
        value.get(matBuffer);
        glUniformMatrix4fv(getUniformLocation(name), false, matBuffer);

        MemoryUtil.memFree(matBuffer);
    }

    public void bind() {
        glUseProgram(programID);
    }
    public void unbind() {
        glUseProgram(0);
    }
    public void close() {
        glDetachShader(programID, vertexID);
        glDetachShader(programID, fragmentID);
        glDeleteShader(vertexID);
        glDeleteShader(fragmentID);
        glDeleteProgram(programID);
    }
}
