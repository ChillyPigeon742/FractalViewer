package net.alek.fractalviewer.ui.util;

import javax.swing.*;

import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

public class MessageUtility {
    public static int DeleteMessage(){
        // Convert your strings to native memory
        long title = MemoryUtil.memUTF8("Hello from LWJGL + tinyfd! 😎");
        long message = MemoryUtil.memUTF8("You just opened a native message box.");
        long dialogType = MemoryUtil.memUTF8("ok"); // Can also be "yesno", "okcancel"
        long iconType = MemoryUtil.memUTF8("info"); // "info", "warning", "error", "question"

        // Show the message box
        int result = TinyFileDialogs.tinyfd_messageBox(title, message, dialogType, iconType, 1);

        // Free the memory manually!
        MemoryUtil.memFree(title);
        MemoryUtil.memFree(message);
        MemoryUtil.memFree(dialogType);
        MemoryUtil.memFree(iconType);

        System.out.println("Result: " + result);
    }

    public static int DeleteMessage2(){
        String[] responses = {"Ok", "Cancel"};
        return JOptionPane.showOptionDialog(
                null,
                "This will delete the save for a long time! (forever)",
                "",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE,
                ReadUtility.warningIcon,
                responses,
                responses[0]);
    }

    public static void AlreadyDeletedMessage(){
        String[] responses = {"Ok"};
        JOptionPane.showOptionDialog(
                null,
                "That Save is Already Deleted Silly!",
                "",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                ReadUtility.infoIcon,
                responses,
                responses[0]);
    }

    public static void NotAllowedSaveNames(){
        String[] responses = {"Ok"};
        JOptionPane.showOptionDialog(
                null,
                "Disallowed Save Names: null and (empty)",
                "",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.ERROR_MESSAGE,
                ReadUtility.errorIcon,
                responses,
                responses[0]);
    }
}