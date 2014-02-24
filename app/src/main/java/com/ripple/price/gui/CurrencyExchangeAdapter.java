package com.ripple.price.gui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.ripple.price.PriceManager;
import com.ripple.price.R;
import com.ripple.price.util.Log;

import java.util.List;
import java.util.Map;

/**
 * Created by Geert Weening (geert@ripple.com) on 2/24/14.
 */
public class CurrencyExchangeAdapter extends BaseExpandableListAdapter
{

    private Context _context;
    private List<String> _listDataHeader;
    private Map<String, List<PriceManager.CurrencyRate>> _listDataChild;

    private int trendColorGreen;
    private int trendColorRed;

    public CurrencyExchangeAdapter(Context context, List<String> listDataHeader, Map<String, List<PriceManager.CurrencyRate>> listChildData)
    {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        this.trendColorGreen = context.getResources().getColor(R.color.trend_green);
        this.trendColorRed = context.getResources().getColor(R.color.trend_red);
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

        TextView childIssuer = (TextView) convertView.findViewById(R.id.issuer);
        TextView childClose = (TextView) convertView.findViewById(R.id.close);
        TextView childVolume = (TextView) convertView.findViewById(R.id.volume);

        childIssuer.setText(childRate.issuer);
        childClose.setText(String.format("%.9f", childRate.close));
        childVolume.setText(String.format("%.4f", childRate.baseVolume));
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
        PriceManager.CurrencyRate currencyRate = null;
        Double biggestVolume = Double.MIN_VALUE;
        for (PriceManager.CurrencyRate rate : this._listDataChild.get(getGroup(groupPosition))) {
            if (rate.baseVolume > biggestVolume) {
                biggestVolume = rate.baseVolume;
                currencyRate = rate;
            }
        }

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
        rateTxt.setText(String.format("%.6f", currencyRate.close));


        // calculate difference between opening and close
        Double diff = currencyRate.close - currencyRate.open;
        Double perc = diff * (100/currencyRate.open);

        String trend = String.valueOf(perc);
        trend = trend.substring(0, trend.length() < 5 ? trend.length() : 5);


        Log.debug("open %s - close %s - diff %s - perc %s - trend %s", currencyRate.open, currencyRate.close, diff, perc, trend);


        trendTxt.setText(trend);
        trendTxt.setText(String.format("%s %s %s", perc < 0 ? "▼" : "▲", trend, "%"));
        trendTxt.setTextColor(perc < 0 ? trendColorRed : trendColorGreen);

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
