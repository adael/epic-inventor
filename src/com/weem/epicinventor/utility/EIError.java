package com.weem.epicinventor.utility;

import com.weem.epicinventor.*;

import java.io.*;
import java.util.Date;

public class EIError implements Thread.UncaughtExceptionHandler {

    public enum ErrorLevel {

        Notice, Warning, Error, None
    }
    private static String errorFilterClass = "";
    private static EIError.ErrorLevel errorFilterLevel;
    private static String lastNotice = "";
    private static String lastWarning = "";
    private static String lastError = "";
    private static boolean ready = false;

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (!ready) {
            init();
        }

        Date date = new Date();

        try {
            FileWriter fstream = new FileWriter("debug.log", true);
            BufferedWriter out = new BufferedWriter(fstream);

            System.out.println(date.toString() + " Stack Trace:\n");

            out.write(date.toString() + " Stack Trace:");
            out.newLine();

            for (StackTraceElement ste : e.getStackTrace()) {
                System.out.println(ste + "\n");

                out.write(ste.toString());
                out.newLine();
            }
            
            out.close();
        } catch (Exception ex) {
        }

        terminate();
    }

    public static void init() {
        try {
            (new File("debug.log")).delete();
        } catch (Exception e) {
        }

        ready = true;

        //if(Game.RELEASE) {
        //    errorFilterLevel = EIError.ErrorLevel.Error;
        //} else {
            errorFilterLevel = EIError.ErrorLevel.Notice;
        //}
    }

    public static void terminate() {
    }

    public static void debugMsg(String msg) {
        debugMsg(msg, EIError.ErrorLevel.Notice);
    }

    public static void debugMsg(String msg, EIError.ErrorLevel level) {
        if (!ready) {
            init();
        }

        if (errorFilterLevel != EIError.ErrorLevel.None) {
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            Date date = new Date();
            String str;
            if(stackTraceElements[2].getClassName().contains("EIError")) {
                str = stackTraceElements[3].getClassName();
                str = str.substring(str.lastIndexOf(".")+1);
                msg = date.getTime() + " Line:" + stackTraceElements[3].getLineNumber()
                        + " Method:" + stackTraceElements[3].getMethodName()
                        + " Class:" + str + " Thread:" + Thread.currentThread().getName() + " - " + msg;
            } else {
                str = stackTraceElements[3].getClassName();
                str = str.substring(str.lastIndexOf(".")+1);
                msg = date.getTime() + " Line:" + stackTraceElements[2].getLineNumber()
                        + " Method:" + stackTraceElements[2].getMethodName()
                        + " Class:" + str + " Thread:" + Thread.currentThread().getName() + " - " + msg;
            }
            if (stackTraceElements[2].getClassName().equals(errorFilterClass) || errorFilterClass.isEmpty()) {
                if (shouldPrint(level)) {
                    System.out.println(msg);
                    try {
                        FileWriter fstream = new FileWriter("debug.log", true);
                        BufferedWriter out = new BufferedWriter(fstream);
                        out.write(msg);
                        out.newLine();
                        if(level == EIError.ErrorLevel.Error) {
                            for (StackTraceElement ste : stackTraceElements) {
                                out.write(ste.toString());
                                out.newLine();
                            }
                        }
                        out.close();
                    } catch (Exception e) {
                    }
                }
                //could use some max length queues here
                switch (level) {
                    case Notice:
                        lastNotice = msg;
                        break;
                    case Warning:
                        lastWarning = msg;
                        break;
                }
            }
        }
        //always log last error
        if (level == EIError.ErrorLevel.Error) {
            lastError = msg;
        }
    }

    private static boolean shouldPrint(EIError.ErrorLevel level) {
        boolean ret = false;
        if (errorFilterLevel == EIError.ErrorLevel.Notice
                || (errorFilterLevel == EIError.ErrorLevel.Warning && level != EIError.ErrorLevel.Notice)
                || (errorFilterLevel == EIError.ErrorLevel.Error && level == EIError.ErrorLevel.Error)) {
            ret = true;
        }
        return ret;
    }

    private static String getLastNotice() {
        return lastNotice;
    }

    private static String getLastWarning() {
        return lastWarning;
    }

    private static String getLastError() {
        return lastError;
    }
}
