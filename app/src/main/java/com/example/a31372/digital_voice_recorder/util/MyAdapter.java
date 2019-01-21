package com.example.a31372.digital_voice_recorder.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a31372.digital_voice_recorder.R;

import java.util.ArrayList;

public class MyAdapter extends ArrayAdapter<RecordingItem> {

    ArrayList<RecordingItem> recordList = new ArrayList<>();
    public MyAdapter(Context context, int textViewResourceId, ArrayList<RecordingItem> objects) {
        super(context, textViewResourceId, objects);
        recordList = objects;
    }
    @Override
    public int getCount() {
        return super.getCount();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.list_view_items, null);
        TextView recordName = (TextView) v.findViewById(R.id.record_name);
        TextView recordLength = (TextView) v.findViewById(R.id.record_length);
        TextView recordTime = (TextView) v.findViewById(R.id.record_time);
        ImageView timg = (ImageView) v.findViewById(R.id.timg);
        recordName.setText(recordList.get(position).getName());
        recordLength.setText(recordList.get(position).getLength());
        recordTime.setText(recordList.get(position).getTime());
        timg.setBackgroundResource(R.drawable.timg);
        return v;
    }
}
