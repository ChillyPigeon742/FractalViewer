package net.alek.fractalviewer.ui.util;

import net.alek.fractalviewer.core.log.LogType;
import net.alek.fractalviewer.transfer.event.payload.LogPayload;
import net.alek.fractalviewer.transfer.event.type.Event;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

public class MessageUtility {
    public static void ErrorMessage(String message){
        boolean result = TinyFileDialogs.tinyfd_messageBox(
                "Error",
                message,
                "ok",
                "error",
                true
        );
        Event.LOG.publish(new LogPayload(LogType.DEBUG, "Result: " + result));
    }
}