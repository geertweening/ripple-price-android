package com.ripple.price.util;

/**
 * Created by Geert Weening (geert@ripple.com) on 2/6/14.
 */
public class Log
{
    private final static String TAG = "ripple_price";
    public static enum Level {
        DEBUG, INFO, ERROR
    };

    private static void log(Log.Level level, String fmt, Object... args)
    {
        // SLOW! String formats are slow, use sparingly
        String message = String.format("[%s] %s", level, String.format(fmt, args));

        switch (level) {
            case DEBUG:
                android.util.Log.d(TAG, message);
                break;
            case INFO:
                android.util.Log.i(TAG, message);
                break;
            case ERROR:
                android.util.Log.e(TAG, message);
                break;
        }
    }

    public static void debug(String fmt, Object... args)
    {
        log(Level.DEBUG, fmt, args);
    }

    public static void info(String fmt, Object... args)
    {
        log(Level.INFO, fmt, args);
    }

    public static void error(String fmt, Object... args)
    {
        log(Level.ERROR, fmt, args);
    }

    public static void debug(Object object)
    {
        log(Level.DEBUG, "%s", String.valueOf(object));
    }

    public static void info(Object object)
    {
        log(Level.INFO, "%s", String.valueOf(object));
    }

    public static void error(Object object)
    {
        log(Level.ERROR, "%s", String.valueOf(object));
    }

    public static void debug(Throwable t)
    {
        log(Level.DEBUG, "%-E", t);
    }

    public static void info(Throwable t)
    {
        log(Level.INFO, "%-e", t);
    }

    public static void error(Throwable t)
    {
        log(Level.ERROR, "%-E", t);
    }

}
