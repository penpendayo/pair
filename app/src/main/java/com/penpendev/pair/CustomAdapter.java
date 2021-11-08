package com.penpendev.pair;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private int layoutID;
    private ArrayList<String> nameList;
    private ArrayList<String>levelList;
    private ArrayList<String>genderList;

     static class ViewHolder {
        TextView name;
        TextView level;
    }


    CustomAdapter(Context context, int itemLayoutId,ArrayList<String> nameList, ArrayList<String> levelList,ArrayList<String> genderList){
        inflater = LayoutInflater.from(context);
        layoutID = itemLayoutId;

        this.nameList = nameList;
        this.levelList = levelList;
        this.genderList = genderList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(layoutID, null);
            holder = new ViewHolder();

            holder.name = convertView.findViewById(R.id.text_name);
            holder.level = convertView.findViewById(R.id.text_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.level.setText(levelList.get(position));
        holder.name.setText(nameList.get(position));

        GradientDrawable drawable = (GradientDrawable)holder.level.getBackground().getCurrent();

        switch (levelList.get(position)) {
            case "入門":
                drawable.setColor(Color.parseColor("#0000cd"));
                drawable.setStroke(2,Color.parseColor("#660000"));
                break;
            case "初級":
                drawable.setColor(Color.parseColor("#228b22"));
                break;
            case "中級":
                drawable.setColor(Color.parseColor("#ffa500"));
               // drawable.setStroke(2,Color.parseColor("#660000"));
                break;
            case "上級":
                drawable.setColor(Color.parseColor("#dc143c"));
                break;
        }

        switch(genderList.get(position)){
            case "男":
                holder.name.setTextColor(Color.parseColor("#0000a0"));
                break;
            case "女":
                holder.name.setTextColor(Color.parseColor("#a00000"));
                break;
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return nameList.size();
    }

    @Override
    public Object getItem(int position) {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 1;
    }
}
