package net.alek.fractalviewer.ui;

import net.alek.fractalviewer.render.RenderLoop;
import net.alek.fractalviewer.render.RenderTriangle;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL46.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static net.alek.fractalviewer.render.RenderTriangle.shaderProgram;

public class CreateWindow {
    public static long window;
    public static int width = 800;
    public static int height = 600;
    public static boolean redraw = true;

    public static void createWindow() {
        initGLFW();
        createGLFWWindow();
        setupCallbacks();
        initOpenGL();
        RenderTriangle.renderTriangle();
        System.out.println("OpenGL Version: " + glGetString(GL_VERSION));

        while (!glfwWindowShouldClose(window)) {
            if (redraw) {
                glfwPollEvents();
                RenderLoop.render();
                redraw = false;
            } else {
                glfwWaitEvents();
            }
        }

        cleanup();
    }

    private static void initGLFW() {
        if (!glfwInit()) throw new IllegalStateException("Failed to initialize GLFW");

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        glfwWindowHint(GLFW_SAMPLES, 4);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
    }

    private static void createGLFWWindow() {
        window = glfwCreateWindow(width, height, "Fractal Viewer", NULL, NULL);
        if (window == NULL) {
            glfwTerminate();
            throw new RuntimeException("Failed to create GLFW window");
        }

        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        glViewport(0, 0, width, height);
    }

    private static void setupCallbacks() {
        glfwSetWindowSizeCallback(window, (win, wid, heigh) -> {
            glViewport(0, 0, wid, heigh);
            updateProjectionMatrix(wid, heigh);

            width = wid;
            height = heigh;
            redraw = true;
        });

        glfwSetWindowCloseCallback(window, win -> glfwSetWindowShouldClose(win, true));
    }

    private static void initOpenGL() {
        glEnable(GL_DEBUG_OUTPUT);

        int[] width = new int[1];
        int[] height = new int[1];
        glfwGetWindowSize(window, width, height);
        updateProjectionMatrix(width[0], height[0]);
    }

    private static void updateProjectionMatrix(int width, int height) {
        float aspect = (float) width / height;
        Matrix4f projection = new Matrix4f().ortho2D(-aspect, aspect, -1f, 1f);

        int projectionLoc = glGetUniformLocation(shaderProgram, "projection");

        glUseProgram(shaderProgram);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = stack.mallocFloat(16);
            projection.get(fb);
            glUniformMatrix4fv(projectionLoc, false, fb);
        }

        glUseProgram(0);
    }

    private static void cleanup() {
        glDeleteBuffers(RenderTriangle.vbo);
        glDeleteVertexArrays(RenderTriangle.vao);

        glfwDestroyWindow(window);
        glfwTerminate();
        System.exit(0);
    }
}