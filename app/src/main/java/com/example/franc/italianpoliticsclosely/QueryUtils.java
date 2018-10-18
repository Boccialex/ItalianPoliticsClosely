package com.example.franc.italianpoliticsclosely;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Helper methods related to requesting and receiving news data from the GUARDIAN.
 */

public final class QueryUtils {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {
    }

    /**
     * Making the Url request
     */
    public static List<Ipc> fetchIpcData(String requestUrl) {
        Log.i(LOG_TAG, "TEST: fetchIpcData() called ...");
        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return extractFeatureFromJson(jsonResponse);
    }

    /**
     * Return new URL object from the given string URL
     */
    private static URL createUrl(String stringUrl) {
        Log.i(LOG_TAG, "TEST: createUrl()called....");

        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {

            e.printStackTrace();
        }
        return url;
    }

    /**
     * Date Helper
     */
    private static String formatDate(String rawDate) {
        String jsonDatePattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        SimpleDateFormat jsonFormatter = new SimpleDateFormat(jsonDatePattern, Locale.UK);
        try {
            Date parsedJsonDate = jsonFormatter.parse(rawDate);
            String finalDatePattern = "MMM d, yyy";
            SimpleDateFormat finalDateFormatter = new SimpleDateFormat(finalDatePattern, Locale.UK);
            return finalDateFormatter.format(parsedJsonDate);
        } catch (ParseException e) {
            Log.e("QueryUtils", "Error parsing JSON date: ", e);
            return "";
        }
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // If the request is successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.d("Error response code: ", String.valueOf(urlConnection.getResponseCode()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * <p>
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        Log.i(LOG_TAG, "TEST: readFromStream()called....");

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

    /**
     * Return a list of {@link Ipc} objects that has been built up from
     * <p>
     * parsing the given JSON response.
     */
    private static List<Ipc> extractFeatureFromJson(String ipcsJSON) {
        // Create an empty ArrayList that we can start adding ipcs to
        List<Ipc> ipcs = new ArrayList<>();
        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(ipcsJSON);
            JSONObject response = baseJsonResponse.getJSONObject("response");
            // Extract the JSONArray associated with the key called "results",
            // which represents a list of news
            JSONArray resultsArray = response.getJSONArray("results");
            // For each news in the resultsArray, create an {@link Ipc} object
            for (int i = 0; i < resultsArray.length(); i++) {
                // Get a single news at position i within the list of news
                JSONObject currentResults = resultsArray.getJSONObject(i);
                // Extract the values for the different JSON keys
                String Title = currentResults.getString("webTitle");
                String section = currentResults.getString("sectionName");
                String date = currentResults.getString("webPublicationDate");
                date = formatDate(date);
                String url = currentResults.getString("webUrl");
                // Extract the value for the contributor of the article, which is extracted from the tags JSONArray
                List<String> tagList = new ArrayList<>();
                JSONArray tags = currentResults.getJSONArray("tags");

                if ((tags != null)) {
                    for (int y = 0; y < tags.length(); y++) {
                        JSONObject tag = tags.getJSONObject(y);
                        String contributor =
                                tag.getString("webTitle");
                        if (contributor != null) {
                            tagList.add(contributor + "                       ");
                        }
                    }
                    Ipc ipc = new Ipc(Title, section, date, url, tagList);
                    ipcs.add(ipc);
                }
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }
        // Return the list of news
        return ipcs;
    }
}

