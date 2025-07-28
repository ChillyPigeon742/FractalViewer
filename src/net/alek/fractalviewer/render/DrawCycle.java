package net.alek.fractalviewer.render;

import net.alek.fractalviewer.transfer.request.Request;
import net.alek.fractalviewer.transfer.request.payload.ShaderProgramPayload;
import net.alek.fractalviewer.ui.CreateWindow;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL46.*;

public class DrawCycle {
    private static boolean redraw = true;
    private static final long window;
    private static final int width;
    private static final int height;

    private static void initalizeDrawCycle(w) {
        ShaderProgramPayload windowData = (ShaderProgramPayload) Request.GET_WINDOW_DATA.request().await().get();
        window = windowData.window();
         = windowData.window();
        window = windowData.window();

        while (!glfwWindowShouldClose(window)) {
            if (redraw) {
                glfwPollEvents();
                DrawCycle.render();
                redraw = false;
            } else {
                glfwWaitEvents();
            }
        }
    }

     private static void render() {
        glClear(GL_COLOR_BUFFER_BIT);

        glUseProgram(RenderFractal.shaderProgram);
        glBindVertexArray(RenderFractal.vao);

        float aspectRatio = (float) CreateWindow.width / (float) CreateWindow.height;
        glUniform2f(RenderFractal.resolutionLoc, (float) CreateWindow.width, (float) CreateWindow.height);
        glUniform1f(RenderFractal.invMaxIterLoc, 1.0f / 50.0f);
        glUniform1f(RenderFractal.aspectRatioLoc, aspectRatio);

        glDrawArrays(GL_TRIANGLES, 0, 3);

        glBindVertexArray(0);
        glUseProgram(0);

        glfwSwapBuffers(CreateWindow.window);
        redraw = false;
    }

    private static void markDirty() {
        redraw = true;
    }
}