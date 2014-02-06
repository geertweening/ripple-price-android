package com.ripple.price;

import android.os.Bundle;
import android.support.v4.app.ListFragment;

/**
 * Created by geert on 2/5/14.
 */
public class CurrencyFragment extends ListFragment {

    public static CurrencyFragment newInstance() {
        CurrencyFragment fragment = new CurrencyFragment();
        Bundle args = new Bundle();
        return fragment;
    }
}
