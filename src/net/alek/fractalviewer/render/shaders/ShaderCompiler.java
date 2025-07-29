package net.alek.fractalviewer.render.shaders;

import net.alek.fractalviewer.data.asset.Shaders;
import net.alek.fractalviewer.transfer.event.type.Event;
import net.alek.fractalviewer.transfer.event.type.SubscribeMethod;
import net.alek.fractalviewer.transfer.request.type.Request;
import net.alek.fractalviewer.transfer.request.payload.ShaderProgramPayload;

import static org.lwjgl.opengl.GL46.*;

public class ShaderCompiler {
    public static int shaderProgram;

    static {
        Event.COMPILE_SHADERS.subscribe(SubscribeMethod.SYNC, ignored -> compileShaders());
        Request.GET_SHADER_PROGRAM.handle(ShaderCompiler::getShaderProgram);
    }

    private static void compileShaders() {
        Shaders shaders = (Shaders) Request.GET_SHADER_SOURCE.request().await().get();

        String vertexShaderSource = shaders.vertexShaderSource();
        String fragmentShaderSource = shaders.fragmentShaderSource();

        createShaderProgram(vertexShaderSource, fragmentShaderSource);
    }

    private static void createShaderProgram(String vertexShaderSource, String fragmentShaderSource) {
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vertexShaderSource);
        glCompileShader(vertexShader);
        checkCompileErrors(vertexShader, "VERTEX");

        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, fragmentShaderSource);
        glCompileShader(fragmentShader);
        checkCompileErrors(fragmentShader, "FRAGMENT");

        int program = glCreateProgram();
        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);
        glLinkProgram(program);
        checkCompileErrors(program, "PROGRAM");

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        shaderProgram = program;
    }

    private static ShaderProgramPayload getShaderProgram() {
        return new ShaderProgramPayload(shaderProgram);
    }

    private static void checkCompileErrors(int shader, String type) {
        int success;
        if (type.equals("PROGRAM")) {
            success = glGetProgrami(shader, GL_LINK_STATUS);
            if (success == GL_FALSE) {
                String infoLog = glGetProgramInfoLog(shader);
                System.err.println("PROGRAM LINKING ERROR:\n" + infoLog);
            }
        } else {
            success = glGetShaderi(shader, GL_COMPILE_STATUS);
            if (success == GL_FALSE) {
                String infoLog = glGetShaderInfoLog(shader);
                System.err.println("SHADER COMPILATION ERROR (" + type + "):\n" + infoLog);
            }
        }
    }
}
