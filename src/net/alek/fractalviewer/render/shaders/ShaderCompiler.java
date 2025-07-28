package net.alek.fractalviewer.render.shaders;

import net.alek.fractalviewer.transfer.request.Request;
import net.alek.fractalviewer.transfer.request.payload.ShaderProgramPayload;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class ShaderCompiler {
    private static int shaderProgram;

    static {
        Request.GET_SHADER_PROGRAM.handle(() -> getShaderProgram());
    }

    private static void createShaderProgram() {
        String vertexSource = loadShaderSource("/assets/fractalviewer/shaders/vertex.glsl");
        String fragmentSource = loadShaderSource("/assets/fractalviewer/shaders/fragment.glsl");

        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vertexSource);
        glCompileShader(vertexShader);
        checkCompileErrors(vertexShader, "VERTEX");

        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, fragmentSource);
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
