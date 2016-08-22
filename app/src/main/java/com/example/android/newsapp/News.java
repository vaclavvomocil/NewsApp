package com.example.android.newsapp;

public class News {
    private String mSection;
    private String mTitle;
    private String mUrl;
    private String mAuthor;

    public News(String section, String title, String url, String author) {
        mSection = section;
        mTitle = title;
        mUrl = url;
        mAuthor = author;
    }

    public String getSection() {
        return mSection;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getAuthor() {
        return mAuthor;
    }

}