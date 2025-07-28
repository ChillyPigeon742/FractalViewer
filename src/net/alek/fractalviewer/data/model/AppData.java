package net.alek.fractalviewer.data.model;

import java.nio.file.Path;

public record AppData(
        boolean debugMode,
        String version,
        Path APPDATA_PATH,
        Path DATA_PATH,
        Path LOGS_PATH
) {}