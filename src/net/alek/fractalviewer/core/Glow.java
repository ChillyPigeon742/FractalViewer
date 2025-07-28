package net.alek.fractalviewer.core;

import net.alek.fractalviewer.render.RenderFractal;
import net.alek.fractalviewer.render.DrawCycle;
import net.alek.fractalviewer.transfer.event.type.Event;
import net.alek.fractalviewer.transfer.event.type.SubscribeMethod;
import net.alek.fractalviewer.transfer.request.payload.ShaderProgramPayload;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL46.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Glow {
    private static long window;
    private static int width = 800;
    private static int height = 600;

    static {
        Event.INIT_GUI.subscribe(SubscribeMethod.SYNC, ignored -> createWindow());
    }

    public static void createWindow() {
        initGLFW();
        createGLFWWindow();
        setupCallbacks();
        RenderFractal.renderFractal();

        System.out.println("OpenGL Version: " + glGetString(GL_VERSION));

        while (!glfwWindowShouldClose(window)) {
            if (redraw) {
                glfwPollEvents();
                DrawCycle.render();
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
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
    }

    private static void createGLFWWindow() {
        window = glfwCreateWindow(width, height, "Fractal Viewer", NULL, NULL);
        if (window == NULL) {
            glfwTerminate();
            throw new RuntimeException("Failed to create GLFW window");
        }
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        int xpos = (vidmode.width() - width) / 2;
        int ypos = (vidmode.height() - height) / 2;
        glfwSetWindowPos(window, xpos, ypos);
        glfwShowWindow(window);

        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        RenderFractal.initializeData();

        glfwSwapInterval(1);
        glViewport(0, 0, width, height);
        redraw = true;
    }

    private static void setupCallbacks() {
        glfwSetWindowSizeCallback(window, (win, wid, heigh) -> {
            glViewport(0, 0, wid, heigh);

            width = wid;
            height = heigh;
            redraw = true;
        });

        glfwSetWindowCloseCallback(window, win -> glfwSetWindowShouldClose(win, true));
    }

    private static ShaderProgramPayload getWindowData(){
        return new ShaderProgramPayload(window, width, height);
    }

    private static void cleanup() {
        glfwDestroyWindow(window);
        glfwTerminate();
    }
}
