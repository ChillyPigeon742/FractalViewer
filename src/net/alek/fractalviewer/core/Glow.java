package net.alek.fractalviewer.core;

import net.alek.fractalviewer.core.log.LogType;
import net.alek.fractalviewer.transfer.event.payload.LogPayload;
import net.alek.fractalviewer.transfer.request.payload.WindowDataPayload;
import net.alek.fractalviewer.transfer.event.type.Event;
import net.alek.fractalviewer.transfer.event.type.SubscribeMethod;
import net.alek.fractalviewer.transfer.request.type.Request;
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
        Request.GET_WINDOW_DATA.handle(Glow::getWindowData);
    }

    private static void createWindow() {
        initGLFW();
        createGLFWWindow();
        setupCallbacks();
        Event.LOG.publish(new LogPayload(LogType.DEBUG, "OpenGL Version: " + glGetString(GL_VERSION)));
        Event.LOAD_SHADER_SOURCE.publish().await();
        Event.COMPILE_SHADERS.publish().await();
        Event.GENERATE_FRACTAL_DATA.publish().await();
        Event.UPLOAD_FRACTAL_DATA.publish().await();

        Event.MARK_DRAW_DIRTY.publish();
        Event.INITIALIZE_DRAW_CYCLE.publish();
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

        assert vidmode != null;
        int xpos = (vidmode.width() - width) / 2;
        int ypos = (vidmode.height() - height) / 2;
        glfwSetWindowPos(window, xpos, ypos);
        glfwShowWindow(window);

        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        glfwSwapInterval(1);
        glViewport(0, 0, width, height);
    }

    private static void setupCallbacks() {
        glfwSetWindowSizeCallback(window, (win, widt, heigh) -> {
            glViewport(0, 0, widt, heigh);

            width = widt;
            height = heigh;
            Event.MARK_DRAW_DIRTY.publish();
        });

        glfwSetWindowCloseCallback(window, win -> glfwSetWindowShouldClose(win, true));
    }

    private static WindowDataPayload getWindowData(){
        return new WindowDataPayload(window, width, height);
    }

    private static void cleanup() {
        glfwDestroyWindow(window);
        glfwTerminate();
    }
}
