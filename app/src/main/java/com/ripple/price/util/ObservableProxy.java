package com.ripple.price.util;

import java.util.Observable;

/**
 * Created by Geert Weening (geert@ripple.com) on 2/6/14.
 */
public class ObservableProxy<C> extends Observable
{
    final C source;

    public ObservableProxy(C source)
    {
        this.source = source;
    }

    public C getSource()
    {
        return (C) source;
    }
}
