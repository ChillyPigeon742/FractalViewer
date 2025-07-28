package net.alek.fractalviewer.core;

import net.alek.fractalviewer.core.log.LogType;
import net.alek.fractalviewer.transfer.event.payload.LogPayload;
import net.alek.fractalviewer.transfer.event.type.Event;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class ErrorHandler {
    static {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> Exception(throwable));
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

    private static void handleException(String name, String cause, String message, String packageName, String stackTrace){
        String caller = getCallerInfo();

        Event.LOG.publish(new LogPayload(LogType.INFO,
                name+"/"+caller+"/The Program has Suffered an "+name+"!"));


    }

    public static void Exception(Throwable t) {
        String name = t.getClass().getSimpleName();
        String packageName = t.getClass().getName();
        String cause = (t.getCause() != null) ? t.getCause().toString() : "No cause";
        String message = t.getMessage();
        String stackTrace = Arrays.toString(t.getStackTrace());

        handleException(name, cause, message, packageName, stackTrace);
    }
}