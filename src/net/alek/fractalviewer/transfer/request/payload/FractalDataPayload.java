package net.alek.fractalviewer.transfer.request.payload;

public record FractalDataPayload(
        int vao,
        int vbo,
        int resolutionLoc,
        int invMaxIterLoc,
        int aspectRatioLoc
) {}