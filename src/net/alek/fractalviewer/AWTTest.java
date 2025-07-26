package net.alek.fractalviewer;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.*;
import org.lwjgl.opengl.awt.AWTGLCanvas;
import org.lwjgl.opengl.awt.GLData;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.Serial;

import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.opengl.GL46.*;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class AWTTest {
    public static void main(String[] args) {
        JFrame frame = new JFrame("AWT test");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setPreferredSize(new Dimension(600, 600));

        GLData data = new GLData();
        data.majorVersion = 4;
        data.minorVersion = 3;
        data.profile = GLData.Profile.CORE;
        data.samples = 4;
        data.doubleBuffer = true;

        AWTGLCanvas canvas;
        frame.add(canvas = new AWTGLCanvas(data) {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public void initGL() {
                System.out.println("OpenGL version: " + glGetString(GL_VERSION));
                createCapabilities();
                glClearColor(0.3f, 0.4f, 0.5f, 1);
            }

            @Override
            public void paintGL() {
                glClearColor(0.2f, 0.2f, 0.8f, 1.0f);
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                swapBuffers();
            }
        }, BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
        frame.transferFocus();

        Runnable renderLoop = new Runnable() {
            @Override
            public void run() {
                if (!canvas.isValid()) {
                    setCapabilities(null);
                    return;
                }
                canvas.render();
                SwingUtilities.invokeLater(this);
            }
        };
        SwingUtilities.invokeLater(renderLoop);
    }
}