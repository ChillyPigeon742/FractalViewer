package net.alek.fractalviewer.ui.components;

import org.lwjgl.opengl.awt.AWTGLCanvas;
import org.lwjgl.opengl.awt.GLData;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL11.*;

public class FractalCanvas extends AWTGLCanvas {

    public FractalCanvas() throws Exception {
        super(setupGLData());
    }

    private static GLData setupGLData() {
        GLData data = new GLData();
        data.majorVersion = 4;
        data.minorVersion = 6;
        data.profile = GLData.Profile.CORE;
        data.samples = 4;
        data.doubleBuffer = true;
        return data;
    }

    @Override
    public void initGL() {
        GL.createCapabilities();
        System.out.println("OpenGL Version: " + glGetString(GL_VERSION));
    }

    @Override
    public void paintGL() {
        glClearColor(0.2f, 0.2f, 0.8f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        swapBuffers(); // Important!
    }
}