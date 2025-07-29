package net.alek.fractalviewer.read.asset;

import net.alek.fractalviewer.data.asset.Shaders;
import net.alek.fractalviewer.render.FractalData;
import net.alek.fractalviewer.transfer.event.type.Event;
import net.alek.fractalviewer.transfer.event.type.SubscribeMethod;
import net.alek.fractalviewer.transfer.request.type.Request;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class ShaderLoader {
    private static Shaders shaders;

    static {
        Request.GET_SHADER_SOURCE.handle(ShaderLoader::getShaders);
        Event.LOAD_GAME.subscribe(SubscribeMethod.SYNC, ignored -> loadShaders());
        Event.UNLOAD_GAME.subscribe(SubscribeMethod.SYNC, ignored -> unloadShaders());
    }

    private static String loadShaderSource(String filepath) {
        try {
            Path file = Path.of(Objects.requireNonNull(
                    FractalData.class.getResource(filepath)).toURI());
            return new String(Files.readAllBytes(file));
        } catch (IOException | URISyntaxException e) {
            System.err.println("Failed to load shader file: " + filepath);
            System.exit(-1);
            return null;
        }
    }

    private static void loadShaders() {
        shaders = new Shaders(
                loadShaderSource("/assets/fractalviewer/shaders/vertex.glsl"),
                loadShaderSource("/assets/fractalviewer/shaders/fragment.glsl")
        );
    }

    private static void unloadShaders() {
        shaders = null;
    }

    private static Shaders getShaders() {
        return shaders;
    }
}
