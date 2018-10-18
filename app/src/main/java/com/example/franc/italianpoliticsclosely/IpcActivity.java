package com.example.franc.italianpoliticsclosely;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class IpcActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Ipc>> {

    // URL for NEWS data from the GUARDIAN data set
    private static final String GUARDIAN_REQUEST_URL;
    static {
        GUARDIAN_REQUEST_URL = "https://content.guardianapis.com/search";
    }
    //API student key, Constant value for the newsApp loader ID, Adapter for the list of news,
    // TextView that is displayed when the list is empty
    private static final int IPC_LOADER_ID = 1;
    private IpcAdapter mAdapter;
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ipc_main_activity);

        // Find a reference to the {@link ListView} in the layout
        ListView ipcListView = findViewById(R.id.list);

        mEmptyStateTextView = findViewById(R.id.empty_view);
        ipcListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of news as input and set the adapter
        mAdapter = new IpcAdapter(this, new ArrayList<Ipc>());
        ipcListView.setAdapter(mAdapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected earthquake.
        ipcListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current news that was clicked on
                Ipc currentIpc = mAdapter.getItem(position);
                // Convert the String URL into a URI object (to pass into the Intent constructor)
                assert currentIpc != null;
                Uri ipcUri = Uri.parse(currentIpc.getWebUrl());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, ipcUri);

                startActivity(websiteIntent);
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        assert connMgr != null;
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();
            // Initialize the loader
            loaderManager.initLoader(IPC_LOADER_ID, null, this);
        } else {
            // Otherwise, display error by first hiding the loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    // onCreate Loader instantiates and returns a new Loader for the given ID
    public Loader<List<Ipc>> onCreateLoader(int i, Bundle bundle) {
        // Get a reference to the SharedPreferences file
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // for number of news per page
        String newsPerPage = sharedPrefs.getString(
                getString(R.string.settings_news_per_page_key),
                getString(R.string.settings_news_per_page_def));

        //for date order
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_def)
        );

        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameter and its value.
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("use-date", "newspaper-edition");
        uriBuilder.appendQueryParameter("page-size", newsPerPage);
        uriBuilder.appendQueryParameter("q", "italy AND politics");
        uriBuilder.appendQueryParameter("api-key", "9af1394f-94c8-4830-a179-a88423f8a29b");

        try {
            return new IpcLoader(this, uriBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<Ipc>> loader, List<Ipc> ipcs) {

        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No news found."
        mEmptyStateTextView.setText(R.string.no_news);

        // Clear the adapter of previous news data
        mAdapter.clear();

        // If there is a valid list of {@link Ipc}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (ipcs != null && !ipcs.isEmpty()) {
            mAdapter.addAll(ipcs);
            updateUi();
        }
    }

    private void updateUi() {
    }

    @Override
    public void onLoaderReset(Loader<List<Ipc>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    @Override
    // This method initializes the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    //this method passes the menu item that is selected
    public boolean onOptionsItemSelected(MenuItem item) {
        //In this case, the menu only has one item, android:id="@+id/action_settings".
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}


