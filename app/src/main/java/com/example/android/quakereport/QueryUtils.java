package com.example.android.quakereport;

import android.annotation.TargetApi;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {

    /** Sample JSON response for a USGS query */
    private static final String LOG_TAG = EarthquakeActivity.class.getName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Return a list of {@link EarthquakeData} objects that has been built up from
     * parsing a JSON response.
     * @param link
     */

    private static URL createURL(String link){
        URL url = null;
            try {
                url = new URL(link);
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Error with creating URL", e);
                return null;
            }
        return url;
    }

    private static String makeHttpRequest(String urls)throws IOException{

        URL url = createURL(urls);

        String responseJson = "";
        if (url == null){
            return responseJson;
        }
        HttpURLConnection urlConnection= null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                responseJson = convertStreamToString(inputStream);
            }else {
                Log.e(LOG_TAG, "Error in Page Response. "+urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if (inputStream != null){
                inputStream.close();
            }
        }

        return responseJson;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static String convertStreamToString(InputStream inputStream){
        StringBuilder stringBuilder = new StringBuilder();
        if (inputStream != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            try {
                String line = reader.readLine();
                while (line != null) {
                    stringBuilder.append(line);
                    line = reader.readLine();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return stringBuilder.toString();
    }

    private static List<EarthquakeData> extractEarthquakes(String response) {
        if (TextUtils.isEmpty(response)){
            return null;
        }

        // Create an empty ArrayList that we can start adding earthquakes to
        List<EarthquakeData> earthquakes = new LinkedList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // TODO: Parse the response given by the SAMPLE_JSON_RESPONSE string and
            // build up a list of Earthquake objects with the corresponding data.
            JSONObject root = new JSONObject(response);
            JSONArray arrJSON_features = root.optJSONArray("features");
            for (int i = 0; i < arrJSON_features.length(); i++) {
                JSONObject objJSON_i = arrJSON_features.optJSONObject(i);
                JSONObject objJSON_properties = objJSON_i.optJSONObject("properties");

                double mag = objJSON_properties.optDouble("mag");
                String location = objJSON_properties.optString("place");
                Long date = objJSON_properties.optLong("time");
                String url = objJSON_properties.optString("url");

                EarthquakeData earthquakeData = new EarthquakeData(mag, location, date, url);
                earthquakes.add(earthquakeData);

            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return earthquakes;
    }

    public static List<EarthquakeData> callAll(String urls){
        try {
            Thread.sleep(1 * 1000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        String response = "";
        try {
            response = makeHttpRequest(urls);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<EarthquakeData> earthquakeData = extractEarthquakes(response);
        return earthquakeData;
    }

}