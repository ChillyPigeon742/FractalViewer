package net.alek.fractalviewer.render;

import static org.lwjgl.opengl.GL46.*;

import net.alek.fractalviewer.transfer.event.type.Event;
import net.alek.fractalviewer.transfer.event.type.SubscribeMethod;
import net.alek.fractalviewer.transfer.request.type.Request;
import net.alek.fractalviewer.transfer.request.payload.FractalDataPayload;
import net.alek.fractalviewer.transfer.request.payload.ShaderProgramPayload;

import org.lwjgl.BufferUtils;
import java.nio.FloatBuffer;

public class FractalData {
    private static final float[] vertices = {
            -1.0f, -1.0f,
            3.0f, -1.0f,
            -1.0f,  3.0f
    };
    private static int vao;
    private static int vbo;
    private static int resolutionLoc;
    private static int invMaxIterLoc;
    private static int aspectRatioLoc;

    static {
        Event.GENERATE_FRACTAL_DATA.subscribe(SubscribeMethod.SYNC, ignored -> generateGLData());
        Event.UPLOAD_FRACTAL_DATA.subscribe(SubscribeMethod.SYNC, ignored -> uploadGLData());
        Event.UNLOAD_GAME.subscribe(SubscribeMethod.SYNC, ignored -> cleanupGLData());

        Request.GET_FRACTAL_DATA.handle(FractalData::getFractalData);
    }

    private static FractalDataPayload getFractalData() {
        return new FractalDataPayload(vao, vbo, resolutionLoc, invMaxIterLoc, aspectRatioLoc);
    }

    public static void generateGLData(){
        vao = glGenVertexArrays();
        vbo = glGenBuffers();

        ShaderProgramPayload shaderProgramPayload =
                (ShaderProgramPayload) Request.GET_SHADER_PROGRAM.request().await().get();
        int shaderProgram = shaderProgramPayload.shaderProgram();

        resolutionLoc = glGetUniformLocation(shaderProgram, "u_resolution");
        invMaxIterLoc = glGetUniformLocation(shaderProgram, "u_invMaxIter");
        aspectRatioLoc = glGetUniformLocation(shaderProgram, "u_aspectRatio");
    }

    public static void uploadGLData() {
        glBindVertexArray(vao);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
        vertexBuffer.put(vertices).flip();
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        glBindVertexArray(0);

        glClearColor(0.1f, 0.1f, 0.1f, 1f);
    }

    private static void cleanupGLData() {
        glDeleteBuffers(vbo);
        glDeleteVertexArrays(vao);
    }
}