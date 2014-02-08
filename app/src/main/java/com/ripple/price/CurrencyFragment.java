package com.ripple.price;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.ripple.price.util.Log;

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

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader = new ArrayList<String>();
    Map<String, List<PriceManager.CurrencyRate>> listDataChild = new HashMap<String, List<PriceManager.CurrencyRate>>();

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
        expListView = new CurrencyListView(container.getContext());
        return expListView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        // Listview Group click listener
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener()
        {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id)
            {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener()
        {

            @Override
            public void onGroupExpand(int groupPosition)
            {
                // group expanded
            }
        });

        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener()
        {

            @Override
            public void onGroupCollapse(int groupPosition)
            {
                // group collapsed
            }
        });


        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener()
        {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
            {
                // child clicked
                return false;
            }
        });
    }

    public void update()
    {
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void update(Observable observable, Object o)
    {
        Log.debug("updated in CurrencyFragment");
        if (observable instanceof PriceManager) {
            Map<String, List<PriceManager.CurrencyRate>> currencyRates = PriceManager.instance.getCurrencyRates();

            if (currencyRates != null && currencyRates.size() > 0) {

                listDataHeader.clear();
                listDataChild.clear();
                Log.debug("cleaning list");

                for (Map.Entry<String, List<PriceManager.CurrencyRate>> entry : currencyRates.entrySet()) {
                    Log.debug("= %s =", entry.getKey());

                    listDataHeader.add(0, entry.getKey());

                    for (PriceManager.CurrencyRate rate : entry.getValue()) {
                        Log.debug("- %s - %s : %s (%s)", rate.base, rate.trade, rate.issuer, rate.rate);
                        listDataChild.put(entry.getKey(), entry.getValue());
                    }
                }

                listAdapter.notifyDataSetChanged();
            }
        }
    }

    public static class CurrencyListView extends ExpandableListView
    {

        public CurrencyListView(Context context)
        {
            super(context);
        }

    }

    public static class ExpandableListAdapter extends BaseExpandableListAdapter
    {

        private Context _context;
        private List<String> _listDataHeader;
        private Map<String, List<PriceManager.CurrencyRate>> _listDataChild;

        public ExpandableListAdapter(Context context, List<String> listDataHeader, Map<String, List<PriceManager.CurrencyRate>> listChildData)
        {
            this._context = context;
            this._listDataHeader = listDataHeader;
            this._listDataChild = listChildData;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition)
        {
            return this._listDataChild.get(_listDataHeader.get(groupPosition)).get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition)
        {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
        {
            final PriceManager.CurrencyRate childRate = (PriceManager.CurrencyRate) getChild(groupPosition, childPosition);

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_item, null);
            }

            TextView txtListChild = (TextView) convertView.findViewById(R.id.textLeft);

            TextView txtListChildRight = (TextView) convertView.findViewById(R.id.textRight);

            txtListChild.setText(childRate.issuer);
            txtListChildRight.setText(String.format("%.9f", childRate.rate));
            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition)
        {
            return _listDataChild.get(this._listDataHeader.get(groupPosition)).size();
        }

        @Override
        public Object getGroup(int groupPosition)
        {
            return this._listDataHeader.get(groupPosition);
        }

        @Override
        public int getGroupCount()
        {
            return this._listDataHeader.size();
        }

        @Override
        public long getGroupId(int groupPosition)
        {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
        {
            boolean xrpBase = ((MainActivity) _context).getXrpBase() == 0;
            PriceManager.CurrencyRate currencyRate = (PriceManager.CurrencyRate) getChild(groupPosition, 0);

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_group, null);
            }

            if (groupPosition % 2 == 0) {
                convertView.setBackgroundColor(0xffe0e0e0);
            } else {
                convertView.setBackgroundColor(0xfff4f4f4);
            }

            TextView baseTxt = (TextView) convertView.findViewById(R.id.base);
            TextView tradeTxt = (TextView) convertView.findViewById(R.id.trade);
            TextView issuerTxt = (TextView) convertView.findViewById(R.id.issuer);
            TextView trendTxt = (TextView) convertView.findViewById(R.id.trend);
            TextView rateTxt = (TextView) convertView.findViewById(R.id.rate);

            baseTxt.setText(String.format("1 %s = ", currencyRate.base));
            tradeTxt.setText(currencyRate.trade);
            issuerTxt.setText(currencyRate.issuer);
            trendTxt.setText("^ " + String.format("%.3f", Math.random()) + " %");
            rateTxt.setText(String.format("%.6f", currencyRate.rate));

//            String showThisRate = xrpBase ? showRate : String.valueOf(1 / Double.valueOf(showRate));
//            headerTextRight.setText(showThisRate + " " + (xrpBase ? tradeCurrency : baseCurrency));

            return convertView;
        }

        @Override
        public boolean hasStableIds()
        {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition)
        {
            return true;
        }

    }

}


