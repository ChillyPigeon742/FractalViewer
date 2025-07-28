package net.alek.fractalviewer.ui;

import net.alek.fractalviewer.render.RenderFractal;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL46.*;

public class CreateWindow {





    private static void cleanup() {
        glDeleteBuffers(RenderFractal.vbo);
        glDeleteVertexArrays(RenderFractal.vao);

        glfwDestroyWindow(window);
        glfwTerminate();
    }
}