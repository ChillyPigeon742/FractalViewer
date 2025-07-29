package net.alek.fractalviewer.render;

import net.alek.fractalviewer.transfer.event.payload.DrawDataPayload;
import net.alek.fractalviewer.transfer.request.payload.WindowDataPayload;
import net.alek.fractalviewer.transfer.event.type.Event;
import net.alek.fractalviewer.transfer.event.type.SubscribeMethod;
import net.alek.fractalviewer.transfer.request.payload.FractalDataPayload;
import net.alek.fractalviewer.transfer.request.payload.ShaderProgramPayload;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL46.*;

public class DrawCycle {
    private static volatile boolean redraw = true;

    private static long window;
    private static int width;
    private static int height;

    private static int shaderProgram;

    private static int vao;
    private static int resolutionLoc;
    private static int invMaxIterLoc;
    private static int aspectRatioLoc;

    static {
        Event.REFRESH_DRAW_DATA.subscribe(SubscribeMethod.SYNC, (DrawDataPayload data) ->
                refreshDrawData(data.windowData(), data.shaderProgramData(), data.fractalData()));

        Event.INITIALIZE_DRAW_CYCLE.subscribe(SubscribeMethod.SYNC, ignored -> initializeDrawCycle());
        Event.MARK_DRAW_DIRTY.subscribe(SubscribeMethod.SYNC, ignored -> markDirty());
    }

    private static void refreshDrawData(WindowDataPayload windowData,
                                        ShaderProgramPayload shaderProgramData,
                                        FractalDataPayload fractalData) {
        window = windowData.window();
        width = windowData.width();
        height = windowData.height();

        shaderProgram = shaderProgramData.shaderProgram();

        vao = fractalData.vao();
        resolutionLoc = fractalData.resolutionLoc();
        invMaxIterLoc = fractalData.invMaxIterLoc();
        aspectRatioLoc = fractalData.aspectRatioLoc();
    }

     private static void render() {
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
        System.out.println("df");
    }

    private static void initializeDrawCycle() {
        while (!glfwWindowShouldClose(window)) {
            glfwPollEvents();
            if (redraw) {
                render();
                redraw = false;
            }
        }
    }
}