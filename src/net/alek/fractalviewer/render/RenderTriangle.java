package net.alek.fractalviewer.render;

import static org.lwjgl.opengl.GL46.*;

import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class RenderTriangle {

    private static final float[] vertices = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            1.0f,  1.0f,
            -1.0f,  1.0f
    };

    private static final int[] indices = {
            0, 1, 2,
            2, 3, 0
    };

    // === GL OBJECTS ===
    public static int vao = glGenVertexArrays();
    public static int vbo = glGenBuffers();
    public static int ebo = glGenBuffers();
    public static int shaderProgram = createShaderProgram();
    static int resolutionLoc = glGetUniformLocation(shaderProgram, "u_resolution");

    // === INIT & UPLOAD DATA TO GPU ===
    public static void renderTriangle() {
        glBindVertexArray(vao);

        // Upload vertex data
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
        vertexBuffer.put(vertices).flip();
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Upload index data
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // Define vertex layout (2D pos)
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glBindVertexArray(0);

        // Clear screen background color (just once here for setup)
        glClearColor(0.1f, 0.1f, 0.1f, 1f);
    }

    // === SHADER LOADER ===
    public static String loadShaderSource(String filepath) {
        try {
            Path file = Path.of(Objects.requireNonNull(
                    RenderTriangle.class.getResource(filepath)).toURI());
            return new String(Files.readAllBytes(file));
        } catch (IOException | URISyntaxException e) {
            System.err.println("🚨 Failed to load shader file: " + filepath);
            System.exit(-1);
            return null;
        }
    }

    // === SHADER PROGRAM CREATOR ===
    private static int createShaderProgram() {
        // Load source code from files
        String vertexSource = loadShaderSource("/assets/fractalviewer/shaders/vertex.glsl");
        String fragmentSource = loadShaderSource("/assets/fractalviewer/shaders/fragment.glsl");

        // Compile vertex shader
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vertexSource);
        glCompileShader(vertexShader);
        checkCompileErrors(vertexShader, "VERTEX");

        // Compile fragment shader
        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, fragmentSource);
        glCompileShader(fragmentShader);
        checkCompileErrors(fragmentShader, "FRAGMENT");

        // Link shaders into a program
        int program = glCreateProgram();
        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);
        glLinkProgram(program);
        checkCompileErrors(program, "PROGRAM");

        // Cleanup shaders (they’re linked now)
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        return program;
    }

    // === DEBUG: CHECK SHADER COMPILE/LINK ERRORS ===
    private static void checkCompileErrors(int shader, String type) {
        int success;
        if (type.equals("PROGRAM")) {
            success = glGetProgrami(shader, GL_LINK_STATUS);
            if (success == GL_FALSE) {
                String infoLog = glGetProgramInfoLog(shader);
                System.err.println("💥 PROGRAM LINKING ERROR:\n" + infoLog);
            }
        } else {
            success = glGetShaderi(shader, GL_COMPILE_STATUS);
            if (success == GL_FALSE) {
                String infoLog = glGetShaderInfoLog(shader);
                System.err.println("💥 SHADER COMPILATION ERROR (" + type + "):\n" + infoLog);
            }
        }
    }
}