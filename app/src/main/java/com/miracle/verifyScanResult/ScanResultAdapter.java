package com.miracle.verifyScanResult;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ScanResultAdapter extends ArrayAdapter<ScanResultItem> {
    private int resourceId;
    ScanResultAdapter(Context context, int textViewResourceId, List<ScanResultItem> objects){
        super(context,textViewResourceId,objects);
        resourceId=textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ScanResultItem scanResultItem=getItem(position);
        @SuppressLint("ViewHolder") View view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        TextView resultItem=view.findViewById(R.id.result_item);
        TextView barcodeTypeItem=view.findViewById(R.id.code_type);
        TextView timesItem =view.findViewById(R.id.times_item);
        TextView decodeItem = view.findViewById(R.id.decode_time);
        if (scanResultItem != null) {
            resultItem.setText(scanResultItem.getResult());
            barcodeTypeItem.setText(scanResultItem.getCodeType());
            timesItem.setText(String.valueOf(scanResultItem.getTimes()));
            decodeItem.setText(String.valueOf(scanResultItem.getDecodeTime()));
        }







        return view;
    }
}
