package net.alek.fractalviewer.transfer.event.payload;

import net.alek.fractalviewer.transfer.request.payload.FractalDataPayload;
import net.alek.fractalviewer.transfer.request.payload.ShaderProgramPayload;
import net.alek.fractalviewer.transfer.request.payload.WindowDataPayload;

public record DrawDataPayload(
        WindowDataPayload windowData,
        ShaderProgramPayload shaderProgramData,
        FractalDataPayload fractalData
) {}