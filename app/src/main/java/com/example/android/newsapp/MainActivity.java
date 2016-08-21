package com.example.android.newsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.Loader;


public class MainActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<List<News>>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private String book_url = "http://content.guardianapis.com/search";
    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int NEWS_LOADER_ID = 1;
    private NewsAdapter adapter;
    private ScheduledExecutorService scheduleTaskExecutor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView newsListView = (ListView) findViewById(R.id.newsList);
        adapter = new NewsAdapter(this, new ArrayList<News>());
        newsListView.setAdapter(adapter);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        scheduleTaskExecutor = Executors.newScheduledThreadPool(5);


        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                News currentNews = adapter.getItem(position);

                Uri newsUri = Uri.parse(currentNews.getUrl());

                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                startActivity(websiteIntent);
            }
        });

        runLoadManager();

        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {

                runOnUiThread(new Runnable() {
                    public void run() {
                        runLoadManager();
                        Log.v(LOG_TAG,"scheduler running");
                    }
                });
            }
        }, 0, 30, TimeUnit.SECONDS);

    }

    private void runLoadManager() {
        if (isNetworkAvailable() == false) {
            Toast.makeText(getApplicationContext(), "network not available", Toast.LENGTH_LONG).show();
        } else {
            android.app.LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String defaultTopic = sharedPrefs.getString(
                getString(R.string.settings_default_topic_key),
                getString(R.string.settings_default_topic_value));
        Uri baseUri = Uri.parse(book_url);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("q", defaultTopic);
        uriBuilder.appendQueryParameter("api-key", "test");

        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        adapter.clear();


        if (news != null && !news.isEmpty()) {
            adapter.addAll(news);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        adapter.clear();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, NewsSettings.class);
            startActivity(settingsIntent);
            return true;
        }
        if (id == R.id.menu_refresh) {
            adapter.clear();
            getLoaderManager().restartLoader(NEWS_LOADER_ID, null, this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (key.equals(getString(R.string.settings_default_topic_key))) {
            adapter.clear();

            if (isNetworkAvailable() == false) {
                Toast.makeText(getApplicationContext(), "network not available", Toast.LENGTH_LONG).show();
            } else {
                getLoaderManager().restartLoader(NEWS_LOADER_ID, null, this);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scheduleTaskExecutor.shutdown();
    }

}
