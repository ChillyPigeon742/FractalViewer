package net.alek.fractalviewer.ui;

import net.alek.fractalviewer.ui.components.FractalCanvas;

import javax.swing.*;

public class CreateWindow {
    private static final JFrame window = new JFrame();
    private static FractalCanvas fractalCanvas;

    public static void createWindow() {
        JFrame frame = new JFrame("Fractal Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 720);

        try {
            fractalCanvas = new FractalCanvas();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        fractalCanvas.initGL();
        fractalCanvas.paintGL();
        frame.add(fractalCanvas);

        frame.setVisible(true);
        fractalCanvas.requestFocusInWindow();
    }

    public static JFrame getWindow() {
        return window;
    }

    public static FractalCanvas getFractalCanvas() {
        return fractalCanvas;
    }
}
