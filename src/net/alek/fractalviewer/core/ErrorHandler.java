package net.alek.fractalviewer.core;

import net.alek.fractalviewer.core.log.LogType;
import net.alek.fractalviewer.transfer.event.payload.LogPayload;
import net.alek.fractalviewer.transfer.event.type.Event;
import net.alek.fractalviewer.ui.util.MessageUtility;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorHandler {
    private static final int MAX_MSG_LENGTH = 250;

    static {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> handleException(throwable));
    }

    private static String getCallerInfo() {
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        for (StackTraceElement element : stackTrace) {
            String className = element.getClassName();
            if (!className.equals(ErrorHandler.class.getName())) {
                String simpleClassName = className.substring(className.lastIndexOf('.') + 1);
                return simpleClassName + ":" + element.getLineNumber();
            }
        }
        return "UnknownCaller";
    }

    private static void handleException(Throwable t) {
        String caller = getCallerInfo();
        String exceptionName = t.getClass().getSimpleName();
        String packageName = t.getClass().getName();
        String cause = (t.getCause() != null) ? t.getCause().toString() : "No cause";
        String message = (t.getMessage() != null) ? t.getMessage() : "No message";

        // Get full stack trace as string
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        String stackTraceStr = sw.toString();

        // Log short summary
        Event.LOG.publish(new LogPayload(LogType.ERROR,
                exceptionName + "/" + caller + "/The Program has suffered an " + exceptionName + "!"));

        // Build user-friendly error message
        StringBuilder userMessage = new StringBuilder();
        userMessage.append(String.format("%s / %s / The Program has suffered an %s!\n\n", exceptionName, caller, exceptionName));
        userMessage.append("Details:\n");
        userMessage.append(String.format("Name: %s\n", exceptionName));
        userMessage.append(String.format("Cause: %s\n", cause));
        userMessage.append(String.format("Message: %s\n", message));
        userMessage.append(String.format("Package: %s\n\n", packageName));
        userMessage.append("Stack Trace:\n");
        userMessage.append(stackTraceStr);

        // Show message box with full info
        MessageUtility.ErrorMessage(sanitizeMessage(userMessage.toString()));
    }

    private static String sanitizeMessage(String msg) {
        if (msg.length() > MAX_MSG_LENGTH) {
            return msg.substring(0, MAX_MSG_LENGTH) + "\n\n[Message truncated...]";
        }
        return msg;
    }


    public static void Exception(Throwable t) {
        handleException(t);
    }
}
