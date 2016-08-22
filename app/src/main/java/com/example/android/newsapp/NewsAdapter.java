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
        ViewHolder holder;
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
            holder = new ViewHolder();
            holder.sectionTextView = (TextView) listItemView.findViewById(R.id.section);
            holder.titleTextView = (TextView) listItemView.findViewById(R.id.title);
            holder.authorTextView = (TextView) listItemView.findViewById(R.id.author);
            listItemView.setTag(holder);
        } else {
            holder = (ViewHolder) listItemView.getTag();
        }

        News currentListItem = getItem(position);

        String section = currentListItem.getSection();
        holder.sectionTextView.setText(section);

        String title = currentListItem.getTitle();
        holder.titleTextView.setText(title);

        String author = currentListItem.getAuthor();
        holder.authorTextView.setText(author);

        return listItemView;
    }

    static class ViewHolder {
        private TextView sectionTextView;
        private TextView titleTextView;
        private TextView authorTextView;
    }

}

