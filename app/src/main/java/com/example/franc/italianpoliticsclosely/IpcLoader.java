package com.example.franc.italianpoliticsclosely;

import android.content.AsyncTaskLoader;
import android.content.Context;

import org.json.JSONException;

import java.util.List;


/**
 * Loads a list of news by using an AsyncTask to perform the
 * network request to the given URL.
 */

public class IpcLoader extends AsyncTaskLoader<List<Ipc>> {

    private String mUrl;

    /**
     * Constructs a new {@link IpcLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public IpcLoader(Context context, String url) throws JSONException {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is a background thread.
     */
    @Override
    public List<Ipc> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of news.
        return QueryUtils.fetchIpcData(mUrl);
    }
}


