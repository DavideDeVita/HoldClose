package com.example.dave.gameEngine;

import android.util.Log;

public class _Log {
    public static boolean LOG_ACTIVE=true;
    public static final void v(String tag, String msg){
        Log.v(tag, msg);
    }

    public static final void v(String msg){
        Log.v(MainActivity.TAG, msg);
    }

    public static final void d(String tag, String msg){
        Log.d(tag, msg);
    }

    public static final void d(String msg){
        Log.d(MainActivity.TAG, msg);
    }

    public static final void i(String tag, String msg){
        Log.i(tag, msg);
    }

    public static final void i(String msg){
        Log.i(MainActivity.TAG, msg);
    }

    public static final void w(String tag, String msg){
        Log.w(tag, msg);
    }

    public static final void w(String msg){
        Log.w(MainActivity.TAG, msg);
    }

    public static final void e(String tag, String msg){
        Log.e(tag, msg);
    }

    public static final void e(String msg){
        Log.e(MainActivity.TAG, msg);
    }

    public static final void e(Exception e){
        Log.e(MainActivity.TAG, e.getMessage());
        e.printStackTrace();
    }

    public static final void e(String tag, Exception e){
        Log.e(tag, e.getMessage());
        e.printStackTrace();
    }

    /**/
    public enum Priority {  Info(Log.INFO), Warn(Log.WARN), Error(Log.ERROR);
        private final int logp;

        Priority(int logp){
            this.logp=logp;
        }
    };

    public static final void log(Priority priority, String tag, String msg){
        Log.println(priority.logp, tag, msg);
    }

    public static final void log(int priority, String tag, String msg){
        Log.println(priority, tag, msg);
    }

    public static final void log(Priority priority, String msg){
        Log.println(priority.logp, MainActivity.TAG, msg);
    }
}
