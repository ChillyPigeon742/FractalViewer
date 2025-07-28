package net.alek.fractalviewer.render;

import net.alek.fractalviewer.ui.CreateWindow;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL46.*;

public class RenderLoop {

    public static void render() {
        glClear(GL_COLOR_BUFFER_BIT);

        glUseProgram(RenderTriangle.shaderProgram);
        glBindVertexArray(RenderTriangle.vao);
        glUniform2f(RenderTriangle.resolutionLoc, (float) CreateWindow.width, (float) CreateWindow.height);
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);
        glUseProgram(0);
        glfwSwapBuffers(CreateWindow.window);
    }
}