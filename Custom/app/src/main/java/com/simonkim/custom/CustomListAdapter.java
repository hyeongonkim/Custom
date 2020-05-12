package com.simonkim.custom;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomListAdapter extends BaseAdapter {
    LayoutInflater inflater = null;
    private ArrayList<CustomListItemClass> m_oData = null;
    private int nListCnt = 0;

    public CustomListAdapter(ArrayList<CustomListItemClass> _oData)
    {
        m_oData = _oData;
        nListCnt = m_oData.size();
    }

    @Override
    public int getCount()
    {
        Log.i("TAG", "getCount");
        return nListCnt;
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            final Context context = parent.getContext();
            if (inflater == null)
            {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            convertView = inflater.inflate(R.layout.listview_custom, parent, false);
        }

        TextView list_time = (TextView) convertView.findViewById(R.id.status_time);
        TextView list_message = (TextView) convertView.findViewById(R.id.status_message);
        TextView list_location = (TextView) convertView.findViewById(R.id.status_location);

        list_time.setText(m_oData.get(position).statusTime);
        list_message.setText(m_oData.get(position).statusMessage);
        list_location.setText(m_oData.get(position).statusLocation);

        return convertView;
    }
}
