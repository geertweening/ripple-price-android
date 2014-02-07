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

import org.json.JSONException;
import org.json.JSONObject;

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
    List<JSONObject> listDataHeader;
    HashMap<Integer, List<String>> listDataChild;

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

        // preparing list data
        prepareListData();

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

    /*
   * Preparing the list data
   */
    private void prepareListData()
    {
        listDataHeader = new ArrayList<JSONObject>();
        listDataChild = new HashMap<Integer, List<String>>();

        try {
            JSONObject xrpUsd = new JSONObject("{'base': {'currency': 'XRP'},'trade': {'currency': 'USD','issuer': 'rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B'},'rate': 0.0203400926139021}");
            listDataHeader.add(xrpUsd);
            listDataHeader.add(xrpUsd);
            listDataHeader.add(xrpUsd);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Adding child data
        List<String> subList = new ArrayList<String>();
        subList.add("Bitstamp");
        subList.add("RippleCN");
        subList.add("TheRock");
        subList.add("ChrisWhen");

        listDataChild.put(0, subList); // Header, Child data
        listDataChild.put(1, subList);
        listDataChild.put(2, subList);
        listDataChild.put(3, subList);
        listDataChild.put(4, subList);
    }

    public void update()
    {
        if (listAdapter != null) {
            getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    listAdapter.notifyDataSetChanged();
                }
            });

        }
    }

    @Override
    public void update(Observable observable, Object o)
    {
        Log.debug("updated in CurrencyFragment");
        if (observable instanceof PriceManager) {
            Map<String, List<PriceManager.CurrencyRate>> currencyRates = PriceManager.instance.getCurrencyRates();

            if (currencyRates != null && currencyRates.size() > 0) {
                for (Map.Entry<String, List<PriceManager.CurrencyRate>> entry : currencyRates.entrySet()) {
                    Log.debug("= %s =", entry.getKey());
                    for (PriceManager.CurrencyRate rate : entry.getValue()) {
                        Log.debug("- %s - %s : %s (%s)", rate.base, rate.trade, rate.issuer, rate.rate);
                    }
                }
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
        private List<JSONObject> _listDataHeader;
        private HashMap<Integer, List<String>> _listDataChild;

        public ExpandableListAdapter(Context context, List<JSONObject> listDataHeader, HashMap<Integer, List<String>> listChildData)
        {
            this._context = context;
            this._listDataHeader = listDataHeader;
            this._listDataChild = listChildData;
        }

        @Override
        public Object getChild(int groupPosition, int childPosititon)
        {
            return this._listDataChild.get(groupPosition).get(childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition)
        {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
        {

            System.out.println("getChildView for " + groupPosition);

            final String childText = (String) getChild(groupPosition, childPosition);

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.list_item, null);
            }

            TextView txtListChild = (TextView) convertView.findViewById(R.id.textLeft);

            TextView txtListChildRight = (TextView) convertView.findViewById(R.id.textRight);

            txtListChild.setText(childText);
            txtListChildRight.setText(childText);
            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition)
        {
            return this._listDataChild.get(groupPosition).size();
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
            try {

                boolean xrpBase = ((MainActivity) _context).getXrpBase() == 0;

                JSONObject object = (JSONObject) getGroup(groupPosition);
                String baseCurrency = object.getJSONObject("base").getString("currency");
                String tradeCurrency = object.getJSONObject("trade").getString("currency");
                String rate = object.getString("rate");
                String headerTitle = "1 " + (xrpBase ? baseCurrency : tradeCurrency);
                if (convertView == null) {
                    LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.list_group, null);
                }

                TextView headerTextLeft = (TextView) convertView.findViewById(R.id.headerTextLeft);
                TextView headerTextRight = (TextView) convertView.findViewById(R.id.headerTextRight);
                headerTextLeft.setText(headerTitle);

                String showRate = xrpBase ? rate : String.valueOf(1 / Double.valueOf(rate));
                headerTextRight.setText(showRate + " " + (xrpBase ? tradeCurrency : baseCurrency));

            } catch (JSONException e) {
                e.printStackTrace();
            }

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


