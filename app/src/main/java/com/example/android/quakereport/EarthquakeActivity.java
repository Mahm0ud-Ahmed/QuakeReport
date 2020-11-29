/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<EarthquakeData>> {

    private MyAdapter adapter;
    ListView earthquakeListView;
    private TextView tv_noData;
    private static final String SAMPLE_JSON_RESPONSE = "https://earthquake.usgs.gov/fdsnws/event/1/query";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);


        // Find a reference to the {@link ListView} in the layout
        earthquakeListView = (ListView) findViewById(R.id.list);

        //View Empty Activity to User.
        tv_noData = (TextView) findViewById(R.id.no_data);
        earthquakeListView.setEmptyView(tv_noData);

        //Check Network is Connected out of ConnectivityManager object.
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();

        if (info != null && info.isConnected()) {
            // calling LoaderManger to call initLoader.
            LoaderManager loaderManager = getSupportLoaderManager();
            loaderManager.initLoader(0, null, this).forceLoad();
        }else {
            // View a Message in case Network disconnected
            View progress = findViewById(R.id.progress_circular);
            progress.setVisibility(View.GONE);
            tv_noData.setText(R.string.no_internet);
        }

        // go information about object clicked
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EarthquakeData earthquakeData = adapter.getItem(position);
                String url = earthquakeData.getUrl();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_setting){
            startActivity(new Intent(getBaseContext(), SettingActivity.class));
            return true;
        }
        return false;
    }

    @NonNull
    @Override
    public Loader<List<EarthquakeData>> onCreateLoader(int id, @Nullable Bundle args) {
        //جلب الملف لقراءة البيانات من داخله
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        // قراءة البيانات من داخل الملف
        // اذا لم يعثر على القيمة من خلال ال key الممرره او الملف سيتم اخذ القيمة الافتراضيه الثانيه
        String minMagnitude = sharedPreferences.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));
        String orderBy = sharedPreferences.getString(getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        //تحويل رابط ال api لبدء التعديل عليه من قبل المستخدم وارسال الاستعلامات التي تخصه عليه
        Uri uri = Uri.parse(SAMPLE_JSON_RESPONSE);
        Uri.Builder builder = uri.buildUpon();
        //الحاق الاستعلامات على رابط ال api
        builder.appendQueryParameter("format", "geojson");
        builder.appendQueryParameter("limit", "100");
        builder.appendQueryParameter("minmag", minMagnitude);
        builder.appendQueryParameter("orderby", orderBy);

        return new EarthquakeAsyncTask(this, builder.toString());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<EarthquakeData>> loader, List<EarthquakeData> data) {
        View progress = findViewById(R.id.progress_circular);
        progress.setVisibility(View.GONE);

        if (data != null && !data.isEmpty()) {
            adapter = new MyAdapter(getBaseContext(), data);
            earthquakeListView.setAdapter(adapter);
        }else {
            tv_noData.setText(R.string.no_data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<EarthquakeData>> loader) {
    }

    private static class EarthquakeAsyncTask extends AsyncTaskLoader<List<EarthquakeData>> {
        String urls;

        public EarthquakeAsyncTask(Context context, String urls) {
            super(context);
            this.urls = urls;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public List<EarthquakeData> loadInBackground() {
            if (urls == null)
                return null;

            return QueryUtils.callAll(urls);
        }

    }




}
