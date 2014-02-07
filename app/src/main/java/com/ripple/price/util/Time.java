package com.ripple.price.util;

/**
 * Created by Geert Weening (geert@ripple.com) on 2/7/14.
 */
public interface Time
{
    public final static long MSEC = 1L;
    public final static long SEC = 1000 * MSEC;
    public final static long MIN = 60 * SEC;
    public final static long HOUR = 60 * MIN;
    public final static long DAY = 24 * HOUR;
    public final static long WEEK = 7 * DAY;
    public final static long MONTH = 31 * DAY;
    public final static long YEAR = 365 * DAY;
}
