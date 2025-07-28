package net.alek.fractalviewer.transfer.event.payload;

import net.alek.fractalviewer.core.log.LogType;

public record LogPayload(LogType logType, String message) {}