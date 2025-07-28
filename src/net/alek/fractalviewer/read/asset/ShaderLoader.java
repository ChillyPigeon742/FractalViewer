package net.alek.fractalviewer.read.asset;

import net.alek.fractalviewer.render.RenderFractal;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class ShaderLoader {


    public static String loadShaderSource(String filepath) {
        try {
            Path file = Path.of(Objects.requireNonNull(
                    RenderFractal.class.getResource(filepath)).toURI());
            return new String(Files.readAllBytes(file));
        } catch (IOException | URISyntaxException e) {
            System.err.println("Failed to load shader file: " + filepath);
            System.exit(-1);
            return null;
        }
    }
}
