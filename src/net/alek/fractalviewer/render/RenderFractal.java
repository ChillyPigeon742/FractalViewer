package net.alek.fractalviewer.render;

import static org.lwjgl.opengl.GL46.*;

import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class RenderFractal {

    private static final float[] vertices = {
            -1.0f, -1.0f,
            3.0f, -1.0f,
            -1.0f,  3.0f
    };

    public static int vao;
    public static int vbo;
    public static int shaderProgram;
    public static int resolutionLoc;
    public static int invMaxIterLoc;
    public static int aspectRatioLoc;

    public static void initializeGLData(){
        vao = glGenVertexArrays();
        vbo = glGenBuffers();
        shaderProgram = createShaderProgram();
        resolutionLoc = glGetUniformLocation(shaderProgram, "u_resolution");
        invMaxIterLoc = glGetUniformLocation(shaderProgram, "u_invMaxIter");
        aspectRatioLoc = glGetUniformLocation(shaderProgram, "u_aspectRatio");
    }

    public static void renderFractal() {
        glBindVertexArray(vao);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
        vertexBuffer.put(vertices).flip();
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        glBindVertexArray(0);

        glClearColor(0.1f, 0.1f, 0.1f, 1f);
    }

    public static String loadShaderSource(String filepath) {
        try {
            Path file = Path.of(Objects.requireNonNull(
                    RenderFractal.class.getResource(filepath)).toURI());
            return new String(Files.readAllBytes(file));
        } catch (IOException | URISyntaxException e) {
            System.err.println("Failed to load shader file: " + filepath);
            System.exit(-1);
            return null;
        }
    }


}