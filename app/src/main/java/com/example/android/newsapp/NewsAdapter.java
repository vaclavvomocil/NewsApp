package com.example.android.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;



public class NewsAdapter extends ArrayAdapter<News> {

    public NewsAdapter(Context context, ArrayList<News> earth) {
        super(context, 0, earth);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        News currentListItem = getItem(position);

        TextView sectionTextView = (TextView) listItemView.findViewById(R.id.section);
        String section = currentListItem.getSection();
        sectionTextView.setText(section);

        TextView titleTextView = (TextView) listItemView.findViewById(R.id.title);
        String title = currentListItem.getTitle();
        titleTextView.setText(title);



        return listItemView;



    }


}

