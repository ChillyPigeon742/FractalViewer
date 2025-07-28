package net.alek.fractalviewer.core.log;

import net.alek.fractalviewer.data.model.AppData;
import net.alek.fractalviewer.transfer.event.payload.LogPayload;
import net.alek.fractalviewer.transfer.event.type.Event;
import net.alek.fractalviewer.transfer.event.type.SubscribeMethod;
import net.alek.fractalviewer.transfer.request.Request;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String GREEN = "\u001B[32m";

    private static PrintStream terminalStream;
    private static PrintStream fileStream;
    private static final Object lock = new Object();
    private static final boolean debug;
    private static final Path logPath;

    static {
        AppData appData = (AppData) Request.GET_APPDATA.request().await().get();
        debug = appData.debugMode();
        logPath = appData.LOGS_PATH();

        String logFileName = "BC-" + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()) + ".log";
        Path logFile = Paths.get(String.valueOf(logPath), logFileName);
        try {
            Files.createFile(logFile);
        } catch (IOException e) {
            System.err.println("Failed to initialize log file!");
            System.exit(-1);
        }

        Event.LOG.subscribe(SubscribeMethod.ASYNC, (LogPayload p) -> logWriter(p.message(), p.logType()));
        setupLogger();
    }

    private static void setupLogger() {
        try {
            fileStream = new PrintStream(Files.newOutputStream(logPath, StandardOpenOption.APPEND), true);
            terminalStream = System.out;
            PrintStream originalOut = System.out;
            PrintStream originalErr = System.err;

            System.setOut(new PrintStream(new TeeOutputStream(originalOut, fileStream), true));
            System.setErr(new PrintStream(new TeeOutputStream(originalErr, fileStream), true));
        } catch (IOException e) {
            System.err.println("Failed to setup logger: " + e.getMessage());
            System.exit(-1);
        }
    }

    private static String getCallerInfo() {
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        for (StackTraceElement element : stackTrace) {
            String className = element.getClassName();
            if (!className.equals(Logger.class.getName())) {
                String simpleClassName = className.substring(className.lastIndexOf('.') + 1);
                return simpleClassName + ":" + element.getLineNumber();
            }
        }
        return "UnknownCaller";
    }

    private static void logWriter(String toWrite, LogType type) {
        if (type == LogType.DEBUG) {
            if (!debug) return;
        }

        synchronized (lock) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String caller = getCallerInfo();
            String logMessage = timestamp + " " + type + "  " + caller + " - " + toWrite;

            fileStream.println(logMessage);
            fileStream.flush();

            String coloredMessage;
            switch (type) {
                case ERROR -> coloredMessage = RED + logMessage + RESET;
                case WARN -> coloredMessage = YELLOW + logMessage + RESET;
                case DEBUG -> coloredMessage = GREEN + logMessage + RESET;
                default -> coloredMessage = logMessage;
            }

            terminalStream.println(coloredMessage);
            terminalStream.flush();
        }
    }

    private static class TeeOutputStream extends OutputStream {
        private final OutputStream stream1;
        private final OutputStream stream2;

        public TeeOutputStream(OutputStream s1, OutputStream s2) {
            this.stream1 = s1;
            this.stream2 = s2;
        }

        @Override
        public void write(int b) throws IOException {
            stream1.write(b);
            stream2.write(b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            stream1.write(b);
            stream2.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            stream1.write(b, off, len);
            stream2.write(b, off, len);
        }

        @Override
        public void flush() throws IOException {
            stream1.flush();
            stream2.flush();
        }

        @Override
        public void close() throws IOException {
            stream1.close();
            stream2.close();
        }
    }
}