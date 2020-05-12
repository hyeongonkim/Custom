package com.simonkim.custom;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
        TextView list_product_name = (TextView) convertView.findViewById(R.id.list_product_name);
        TextView list_trace_number = (TextView) convertView.findViewById(R.id.list_trace_number);
        TextView list_now_status = (TextView) convertView.findViewById(R.id.list_now_status);
        ImageView img_ems = (ImageView) convertView.findViewById(R.id.img_ems);
        ImageView img_koreaexpress = (ImageView) convertView.findViewById(R.id.img_koreaexpress);
        ImageView img_koreapost = (ImageView) convertView.findViewById(R.id.img_koreapost);
        ImageView img_dhl = (ImageView) convertView.findViewById(R.id.img_dhl);
        ImageView img_fedex = (ImageView) convertView.findViewById(R.id.img_fedex);
        ImageView img_ceonil = (ImageView) convertView.findViewById(R.id.img_ceonil);
        ImageView img_logen = (ImageView) convertView.findViewById(R.id.img_logen);
        ImageView img_hanjin = (ImageView) convertView.findViewById(R.id.img_hanjin);

        img_koreapost.setVisibility(View.INVISIBLE);
        img_koreaexpress.setVisibility(View.INVISIBLE);
        img_hanjin.setVisibility(View.INVISIBLE);
        img_ems.setVisibility(View.INVISIBLE);
        img_dhl.setVisibility(View.INVISIBLE);
        img_fedex.setVisibility(View.INVISIBLE);
        img_logen.setVisibility(View.INVISIBLE);
        img_ceonil.setVisibility(View.INVISIBLE);

        String target_company = m_oData.get(position).trace_company;
        if(target_company.equals("우체국")) {
            img_koreapost.setVisibility(View.VISIBLE);
        } else if(target_company.equals("대한통운")) {
            img_koreaexpress.setVisibility(View.VISIBLE);
        } else if(target_company.equals("한진택배")) {
            img_hanjin.setVisibility(View.VISIBLE);
        } else if(target_company.equals("EMS")) {
            img_ems.setVisibility(View.VISIBLE);
        } else if(target_company.equals("DHL")) {
            img_dhl.setVisibility(View.VISIBLE);
        } else if(target_company.equals("FedEx")) {
            img_fedex.setVisibility(View.VISIBLE);
        } else if(target_company.equals("로젠택배")) {
            img_logen.setVisibility(View.VISIBLE);
        } else if(target_company.equals("천일택배")) {
            img_ceonil.setVisibility(View.VISIBLE);
        }
        list_product_name.setText(m_oData.get(position).product_name);
        list_trace_number.setText(m_oData.get(position).trace_number);
        list_now_status.setText(m_oData.get(position).now_status);
        if(m_oData.get(position).now_status.equals("배달완료"))
            list_now_status.setTextColor(Color.parseColor("#B0B0B0"));
        else
            list_now_status.setTextColor(Color.parseColor("#5CB8F3"));
        return convertView;
    }
}
