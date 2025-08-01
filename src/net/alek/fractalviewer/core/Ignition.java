package net.alek.fractalviewer.core;

import net.alek.fractalviewer.core.log.LogType;
import net.alek.fractalviewer.transfer.event.payload.CloseAppPayload;
import net.alek.fractalviewer.transfer.event.payload.LogPayload;
import net.alek.fractalviewer.transfer.event.type.Event;
import net.alek.fractalviewer.transfer.event.type.SubscribeMethod;
import net.alek.fractalviewer.transfer.request.type.Request;

public class Ignition {
    static {
        Event.START_APP.subscribe(SubscribeMethod.SYNC, ignored -> startApp());
        Event.CLOSE_APP.subscribe(SubscribeMethod.SYNC, (CloseAppPayload p) -> closeApp(p.exitCode()));
    }

    private static void startApp() {
        Event.LOG.publish(new LogPayload(LogType.INFO, "Starting App..."));
        Event.LOG.publish(new LogPayload(LogType.INFO, "Fractal Viewer "+ Spark.getAppData().version()));

        Event.INIT_GUI.publish();
    }

    private static void closeApp(int exitCode){
        Event.LOG.publish(new LogPayload(LogType.WARN, "Closing app..."));
        Event.shutdown();
        Request.shutdown();
        System.exit(exitCode);
    }
}