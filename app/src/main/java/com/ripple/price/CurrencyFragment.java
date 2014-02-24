package com.ripple.price;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.ripple.price.gui.CurrencyExchangeAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Geert Weening (geert@ripple.com) on 2/5/14.
 */

public class CurrencyFragment extends Fragment implements Observer
{

    private CurrencyExchangeAdapter currencyExchangeAdapter;
    private ExpandableListView expandableListView;
    private List<String> listDataHeader = new ArrayList<String>();
    private Map<String, List<PriceManager.CurrencyRate>> listDataChild = new HashMap<String, List<PriceManager.CurrencyRate>>();

    public static CurrencyFragment newInstance()
    {
        CurrencyFragment fragment = new CurrencyFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        PriceManager.instance.addObserver(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        expandableListView = new ExpandableListView(getActivity());
        return expandableListView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        currencyExchangeAdapter = new CurrencyExchangeAdapter(getActivity(), listDataHeader, listDataChild);
        expandableListView.setAdapter(currencyExchangeAdapter);

        // Listview Group click listener
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener()
        {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id)
            {
                // group clicked
                return false;
            }
        });

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener()
        {

            @Override
            public void onGroupExpand(int groupPosition)
            {
                // group expanded
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener()
        {

            @Override
            public void onGroupCollapse(int groupPosition)
            {
                // group collapsed
            }
        });


        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener()
        {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
            {
                // child clicked
                return false;
            }
        });
    }

    @Override
    public void update(Observable observable, Object o)
    {

        // get notified by PriceManager that new currencyRate information is available
        if (observable instanceof PriceManager) {
            Map<String, List<PriceManager.CurrencyRate>> currencyRates = PriceManager.instance.getCurrencyRates();

            if (currencyRates != null && currencyRates.size() > 0) {

                listDataHeader.clear();
                listDataChild.clear();

                for (Map.Entry<String, List<PriceManager.CurrencyRate>> entry : currencyRates.entrySet()) {
                    Double biggestVolume = Double.MIN_VALUE;
                    String biggestIssuer = null;

                    for (PriceManager.CurrencyRate rate : entry.getValue()) {
                        listDataChild.put(entry.getKey(), entry.getValue());
                        if (rate.baseVolume > biggestVolume) {
                            biggestVolume = rate.baseVolume;
                            biggestIssuer = entry.getKey();
                        }
                    }

                    listDataHeader.add(0, biggestIssuer);
                }

                currencyExchangeAdapter.notifyDataSetChanged();
            }
        }
    }



}


