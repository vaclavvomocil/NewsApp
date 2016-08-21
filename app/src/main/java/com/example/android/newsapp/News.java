package com.example.android.newsapp;


public class News {
    private String mSection;
    private String mTitle;
    private String mUrl;


    public News(String section, String title, String url) {
        mSection = section;
        mTitle = title;
        mUrl = url;
    }

    public String getSection() {
        return mSection;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getUrl() { return mUrl; }



}

