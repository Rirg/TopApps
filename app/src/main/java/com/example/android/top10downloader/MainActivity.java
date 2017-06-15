package com.example.android.top10downloader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ListView mListView;
    private ProgressBar mProgressBar;

    private String mFeedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
    private int mFeedLimit = 10;

    public static final String STATE_URL = "feedURL";
    public static final String STATE_FEED_LIMIT = "feedLimit";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.listView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        if (savedInstanceState != null) {
            mFeedUrl = savedInstanceState.getString(STATE_URL);
            mFeedLimit = savedInstanceState.getInt(STATE_FEED_LIMIT);
        }

        downloadUrl();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feeds_menu, menu);
        if (mFeedLimit == 10) {
            menu.findItem(R.id.mmnu10).setChecked(true);
        } else if (mFeedLimit == 25) {
            menu.findItem(R.id.mmnu25).setChecked(true);
        } else if (mFeedLimit == 100) {
            menu.findItem(R.id.mmnu100).setChecked(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String currentUrl = mFeedUrl;
        int currentFeedLimit = mFeedLimit;
        switch (item.getItemId()) {
            case R.id.menuFree:
                mFeedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
                break;
            case R.id.menuPaid:
                mFeedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml";
                break;
            case R.id.menuSongs:
                mFeedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml";
                break;
            case R.id.mmnu10:
                item.setChecked(true);
                mFeedLimit = 10;
                break;
            case R.id.mmnu25:
                item.setChecked(true);
                mFeedLimit = 25;
                break;
            case R.id.mmnu100:
                item.setChecked(true);
                mFeedLimit = 100;
                break;
            case R.id.menuRefresh:
                downloadUrl();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        // Check if something in the URL has changed before download new content
        if (!mFeedUrl.equals(currentUrl) || mFeedLimit != currentFeedLimit) {
            downloadUrl();
        } else {
            Log.i(TAG, "onOptionsItemSelected: URL NOT CHANGED!");
        }
        return true;
    }

    /**
     * Private classes
     */

    private class Downloader extends AsyncTask<String, Void, String> {

        private static final String TAG = "Downloader";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mListView.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... urls) {
            Log.d(TAG, "doInBackground: starts with " + urls[0]);
            String rsFeed = downloadXML(urls[0]);
            if (null == rsFeed) {
                Log.e(TAG, "doInBackground: Error downloading");
            }

            return rsFeed;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mProgressBar.setVisibility(View.INVISIBLE);
            mListView.setVisibility(View.VISIBLE);
            if (null != s) {
                ParseApplications parseApplications = new ParseApplications();
                parseApplications.parse(s);

                FeedAdapter feedAdapter = new FeedAdapter(MainActivity.this, R.layout.list_record, parseApplications.getApplications());
                mListView.setAdapter(feedAdapter);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_URL, mFeedUrl);
        outState.putInt(STATE_FEED_LIMIT, mFeedLimit);
        super.onSaveInstanceState(outState);
    }

    private String downloadXML(String urlPath) {
        StringBuilder xmlResult = new StringBuilder();

        try {
            URL url = new URL(urlPath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            int response = connection.getResponseCode();
            Log.d(TAG, "downloadXML: The response code was " + response);
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            int charsRead;
            char[] inputBuffer = new char[500];
            while (true) {
                charsRead = reader.read(inputBuffer);
                if (charsRead < 0) {
                    break;
                }
                if (charsRead > 0) {
                    xmlResult.append(String.copyValueOf(inputBuffer, 0, charsRead));
                }
            }
            reader.close();

            return xmlResult.toString();

        } catch (MalformedURLException e) {
            Log.e(TAG, "downloadXML: Invalid URL " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "downloadXML: IO EXCEPTION reading data " + e.getMessage());
        }

        return null;
    }

    private void downloadUrl() {
        Log.d(TAG, "downloadUrl: starting AsyncTask");
        Downloader downloader = new Downloader();
        String url = (String.format(mFeedUrl, mFeedLimit));
        downloader.execute(url);
        Log.d(TAG, "downloadUrl: done AsyncTask");
    }

}




