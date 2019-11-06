package com.example.custom;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {
    LayoutInflater inflater = null;
    private ArrayList<TraceListItemClass> m_oData = null;
    private int nListCnt = 0;

    public ListAdapter(ArrayList<TraceListItemClass> _oData)
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
            convertView = inflater.inflate(R.layout.listview_trace, parent, false);
        }

        TextView list_trace_company = (TextView) convertView.findViewById(R.id.list_trace_company);
        TextView list_product_name = (TextView) convertView.findViewById(R.id.list_product_name);
        TextView list_trace_number = (TextView) convertView.findViewById(R.id.list_trace_number);
        TextView list_now_status = (TextView) convertView.findViewById(R.id.list_now_status);

        list_trace_company.setText(m_oData.get(position).trace_company);
        list_product_name.setText(m_oData.get(position).product_name);
        list_trace_number.setText(m_oData.get(position).trace_number);
        list_now_status.setText(m_oData.get(position).now_status);
        return convertView;
    }
}
