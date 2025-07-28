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
        glUniform1f(RenderTriangle.invMaxIterLoc, 1.0f / 100.0f);

        glDrawArrays(GL_TRIANGLES, 0, 3);

        glBindVertexArray(0);
        glUseProgram(0);

        glfwSwapBuffers(CreateWindow.window);
    }
}