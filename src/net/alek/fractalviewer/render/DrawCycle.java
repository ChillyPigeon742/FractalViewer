package net.alek.fractalviewer.render;

import net.alek.fractalviewer.transfer.request.payload.WindowDataPayload;
import net.alek.fractalviewer.transfer.event.type.Event;
import net.alek.fractalviewer.transfer.event.type.SubscribeMethod;
import net.alek.fractalviewer.transfer.request.payload.FractalDataPayload;
import net.alek.fractalviewer.transfer.request.payload.ShaderProgramPayload;
import net.alek.fractalviewer.transfer.request.type.Request;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL46.*;

public class DrawCycle {
    private static boolean redraw = false;

    private static long window;
    private static int width;
    private static int height;

    private static int shaderProgram;

    private static int vao;
    private static int resolutionLoc;
    private static int invMaxIterLoc;
    private static int aspectRatioLoc;

    static {
        Event.INITIALIZE_DRAW_CYCLE.subscribe(SubscribeMethod.SYNC, ignored -> initializeDrawCycle());
        Event.MARK_DRAW_DIRTY.subscribe(SubscribeMethod.SYNC, ignored -> markDirty());
    }

    private static void refreshDrawData() {
        WindowDataPayload windowDataPayload =
                (WindowDataPayload) Request.GET_WINDOW_DATA.request().await().get();
        ShaderProgramPayload shaderProgramPayload =
                (ShaderProgramPayload) Request.GET_SHADER_PROGRAM.request().await().get();
        FractalDataPayload fractalDataPayload =
                (FractalDataPayload) Request.GET_FRACTAL_DATA.request().await().get();

        window = windowDataPayload.window();
        width = windowDataPayload.width();
        height = windowDataPayload.height();

        shaderProgram = shaderProgramPayload.shaderProgram();

        vao = fractalDataPayload.vao();
        resolutionLoc = fractalDataPayload.resolutionLoc();
        invMaxIterLoc = fractalDataPayload.invMaxIterLoc();
        aspectRatioLoc = fractalDataPayload.aspectRatioLoc();
    }

     private static void render() {
        refreshDrawData();
        glClear(GL_COLOR_BUFFER_BIT);

        glUseProgram(shaderProgram);
        glBindVertexArray(vao);

        float aspectRatio = (float) width / (float) height;
        glUniform2f(resolutionLoc, (float) width, (float) height);
        glUniform1f(invMaxIterLoc, 1.0f / 50.0f);
        glUniform1f(aspectRatioLoc, aspectRatio);

        glDrawArrays(GL_TRIANGLES, 0, 3);

        glBindVertexArray(0);
        glUseProgram(0);

        glfwSwapBuffers(window);
    }

    private static void markDirty() {
        redraw = true;
    }

    private static void initializeDrawCycle() {
        refreshDrawData();
        while (!glfwWindowShouldClose(window)) {
            glfwPollEvents();
            if (redraw) {
                render();
                redraw = false;
            }
        }
    }
}