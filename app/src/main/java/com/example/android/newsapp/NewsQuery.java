package com.example.android.newsapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public class NewsQuery {

    private static final String LOG_TAG = NewsQuery.class.getSimpleName();

    private NewsQuery() {

    }

    public static List<News> fetchData(String requestUrl) {


        URL url = createUrl(requestUrl);

        String jsonResponse = "";
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }


        ArrayList<News> newsList = extractFeatureFromJson(jsonResponse);

        return newsList;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            String finalUrl = stringUrl;
            Log.v(LOG_TAG, finalUrl);
            url = new URL(finalUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }


    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        int httpResponse = 0;
        String stringHttpResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();
            httpResponse = urlConnection.getResponseCode();
            if (httpResponse == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "not 200" + httpResponse);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "problem", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    private static ArrayList<News> extractFeatureFromJson(String bookJSON) {
        // If JSON string is empty or null, then return early
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }


        ArrayList<News> newses = new ArrayList<>();


        try {
            JSONObject baseJsonResponse = new JSONObject(bookJSON);
            JSONObject response = baseJsonResponse.getJSONObject("response");
            JSONArray itemsArray = response.getJSONArray("results");

            if (itemsArray.length() > 0) {

                for (int i = 0; i < itemsArray.length(); i++) {

                    JSONObject news = itemsArray.getJSONObject(i);

                    String section = news.getString("sectionName");
                    String title = news.getString("webTitle");
                    String url = news.getString("webUrl");

                    JSONArray tagsArray = new JSONArray();
                    String author = "";
                    try {
                        tagsArray = news.getJSONArray("tags");
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, "no tags", e);
                        author = "no author";
                    }


                    if (tagsArray.length() > 0) {
                        StringBuilder outputAuthor = new StringBuilder();
                        for (int j = 0; j < tagsArray.length(); j++) {
                            JSONObject auth = tagsArray.getJSONObject(j);
                            outputAuthor.append(auth.getString("webTitle"));
                            outputAuthor.append(" ");
                        }
                        author = outputAuthor.toString();
                    }


                    News newsTemp = new News(section, title, url, author);
                    newses.add(newsTemp);
                }
                return newses;

            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the book list JSON results", e);
        }
        return null;
    }
}

