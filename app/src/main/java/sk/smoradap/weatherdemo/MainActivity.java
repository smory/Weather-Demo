package sk.smoradap.weatherdemo;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

import org.json.JSONException;
import org.json.JSONObject;

import sk.smoradap.weatherdemo.db.City;
import sk.smoradap.weatherdemo.db.CityDataSource;
import sk.smoradap.weatherdemo.gui.BasicInfoRecyclerFragment;
import sk.smoradap.weatherdemo.gui.DetailsFragment;
import sk.smoradap.weatherdemo.utils.HttpUtils;
import sk.smoradap.weatherdemo.weather.Weather;
import sk.smoradap.weatherdemo.weather.WeatherBuilder;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements LocationListener, BasicInfoRecyclerFragment.WeatherDateSelectedListener {

    private LocationManager mLocationManager;
    private Location mLastLocation;

    private BasicInfoRecyclerFragment mBasicInfoRecyclerFragment;
    private DetailsFragment mDetailsFragment;

    private View mFragmentHolder;
    private Toolbar mToolbar;
    private ImageButton mBackButton;
    private ImageButton mSearchButton;
    private ImageButton mLocationButton;

    private CityDataSource mCityDataSource;

    private File mFilesDir;
    private File mLastManualSearchFile; // used to indicate that last search was not based on LocationProvider + holds city data


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFragmentHolder = findViewById(R.id.fragment_holder_ll);
        mBasicInfoRecyclerFragment = new BasicInfoRecyclerFragment();
        mDetailsFragment = new DetailsFragment();
        mFilesDir = getFilesDir();
        mLastManualSearchFile = new File(mFilesDir, Consts.LAST_SEARCH_CITY_FILE_NAME);
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        setupToolbar();

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .add(mFragmentHolder.getId(), mBasicInfoRecyclerFragment)
                .commit();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Locale.setDefault(Locale.US);
    }

    /**
     * Method will perform initial setup of toolbar
     */
    private void setupToolbar() {
        setTitle(null);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        ((TextView) mToolbar.findViewById(R.id.toolbar_title)).setText(getString(R.string.app_name));

        mBackButton = (ImageButton) mToolbar.findViewById(R.id.back_button);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mBackButton.setVisibility(View.INVISIBLE);
        mSearchButton = (ImageButton) mToolbar.findViewById(R.id.search_button);

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocationFromUser();
            }
        });

        mLocationButton = (ImageButton) mToolbar.findViewById(R.id.location_button);
        mLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBasedOnLocationProvider();
            }
        });

        if (mLastManualSearchFile.exists()) {
            mLocationButton.setVisibility(View.VISIBLE);
        } else {
            mLocationButton.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Method will perform weather search based on data from location provider and will initiate regular
     * location updates.
     */
    private void searchBasedOnLocationProvider() {
        //Delete file which indicates that last search was done manually by user
        if (mLastManualSearchFile.exists()) {
            mLastManualSearchFile.delete();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                try {
                    mLastLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                    if (mLastLocation != null) {
                        System.out.println(mLastLocation);
                    }

                    searchFromLocation(mLastLocation);
                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                askUserToEnableLocationService();
            }
        }

        mLocationButton.setVisibility(View.INVISIBLE);
    }

    /**
     * Method will ask user to enter city name for search
     */
    private void getLocationFromUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.enter_city));

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton(getString(R.string.search), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                search(input.getText().toString());
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    /**
     * Sets the text for the location
     *
     * @param city City object to take data from.
     */
    private void setLocationTexts(City city) {
        String s = String.format("%s, %s", city.getName(), city.getCountry());
        ((TextView) findViewById(R.id.location_title)).setText(s);
    }

    /**
     * Shows error message to user.
     *
     * @param message message to show
     */
    private void showErrorMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.error));
        builder.setMessage(message);
        builder.setPositiveButton(getString(android.R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create().show();
    }

    /**
     * Shows an allert dialog to user in case location provider is not turned on. If user clicks OK
     * it will open settings
     */
    private void askUserToEnableLocationService() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.loc_provider_not_turned_on));
        builder.setPositiveButton(getString(android.R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(getString(android.R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create().show();
    }

    /**
     * Provides default GET request parameters to the user.
     *
     * @return
     */
    private Map<String, String> defaultRequestParams() {
        HashMap<String, String> params = new HashMap<>();
        params.put("appid", Consts.API_KEY);
        params.put("mode", "json");
        params.put("units", "metric");
        return params;
    }

    /**
     * Method performs search initialted by user when entering menu manually
     *
     * @param city city to search
     */
    private void search(String city) {
        Map<String, String> params = defaultRequestParams();
        params.put("q", city);
        WeatherUpdateTask task = new WeatherUpdateTask();
        task.setUserSearch(true);
        task.execute(params);
    }

    /**
     * Performs weather search based on location provided by LocationProvider
     *
     * @param location location object used for query.
     */
    private void searchFromLocation(Location location) {

        if (location != null) {
            Map<String, String> params = defaultRequestParams();
            params.put("lat", Double.toString(mLastLocation.getLatitude()));
            params.put("lon", Double.toString(mLastLocation.getLongitude()));
            WeatherUpdateTask task = new WeatherUpdateTask();
            task.execute(params);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mLastManualSearchFile.exists()) {
            UpdateWeatherFromFileTask task = new UpdateWeatherFromFileTask();
            task.execute(mLastManualSearchFile);
        } else {
            searchBasedOnLocationProvider();
        }
    }

    @Override
    public void onBackPressed() {
        if (mDetailsFragment.isVisible()) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .remove(mDetailsFragment)
                    .add(mFragmentHolder.getId(), mBasicInfoRecyclerFragment).commit();
            mBackButton.setVisibility(View.INVISIBLE);
            mSearchButton.setVisibility(View.VISIBLE);
            if (mLastManualSearchFile.exists()) {
                mLocationButton.setVisibility(View.VISIBLE);
            }
        } else {
            super.onBackPressed();
        }

    }

    private void stopLocationUpates() {
        if (mLocationButton != null &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mLocationManager.removeUpdates(this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        System.out.println("Location update: " + location);
        searchFromLocation(location);
        stopLocationUpates();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // no need to handle
    }

    @Override
    public void onProviderEnabled(String provider) {
        // no need to handle
    }

    @Override
    public void onProviderDisabled(String provider) {
        // no need to handle
    }

    @Override
    public void onWeatherDateSelected(Weather weather) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .remove(mBasicInfoRecyclerFragment)
                .add(mFragmentHolder.getId(), mDetailsFragment)
                .commit();
        mDetailsFragment.setWetherDetails(weather);
        mBackButton.setVisibility(View.VISIBLE);
        mLocationButton.setVisibility(View.INVISIBLE);
        mSearchButton.setVisibility(View.INVISIBLE);
    }


    /**
     * Task downloads list of cities and creates database with it.
     */
    private class LocationDatabaseCreateTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            byte[] bytes;
            Scanner s = null;
            try {
                bytes = HttpUtils.loadUrlContentAsByteArray(Consts.CITY_LIST_URL, null, null);
                ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                GZIPInputStream gzin = new GZIPInputStream(bais);
                s = new Scanner(gzin);

                City c = null;
                while (s.hasNextLine()) {
                    String json = s.nextLine();
                    try {
                        c = City.fromJsonString(json);
                        System.out.println(c);
                        mCityDataSource.insertCity(c);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } catch (IOException e) {
                System.out.println("Loading of city list failed.");
            } finally {
                if (s != null) {
                    s.close();
                }
            }
            return null;
        }

    }

    /**
     * Task downloads weather information and polutes guy.
     */
    private class WeatherUpdateTask extends AsyncTask<Map<String, String>, Void, List<Weather>> {
        ProgressDialog progressDialog;
        private String error;
        private boolean userSearch = false;

        public void setUserSearch(boolean userSearch) {
            this.userSearch = userSearch;
        }

        @Override
        public void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage(getString(R.string.work_in_progress));
            progressDialog.show();
        }

        @Override
        protected List<Weather> doInBackground(Map<String, String>... params) {
            System.out.println("executing task");
            String jsonString;
            JSONObject json;
            try {
                jsonString = HttpUtils.simpleGet(Consts.FORCAST_URL, params == null ? null : params[0], null);
                System.out.println("json response " + jsonString);
                json = new JSONObject(jsonString);

                String errorString = WeatherBuilder.checkJsonForErrors(json);
                if (errorString != null) {
                    error = errorString;
                    cancel(false);
                    return null;
                }

                List<Weather> l = WeatherBuilder.buildWeatherList(json, userSearch);
                System.out.println(l);
                return l;

            }catch (IOException ioe){
                error = getString(R.string.check_inet_connection) + "\n" +  ioe.toString();
                cancel(false);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onCancelled() {
            System.out.println("task canceled");
            progressDialog.dismiss();
            showErrorMessage(error);

        }

        @Override
        protected void onPostExecute(List<Weather> list) {
            progressDialog.dismiss();
            mBasicInfoRecyclerFragment.setWeatherList(list);
            City city = list.get(0).getCity();
            setLocationTexts(city);

            if (city.isFromUserSearch()) {
                mLocationButton.setVisibility(View.VISIBLE);
                CityFileWriteTask task = new CityFileWriteTask();
                task.execute(city);
                stopLocationUpates();
            }
        }

    }

    /**
     * Task creates a file which contains information about last search from user.
     */
    private class CityFileWriteTask extends AsyncTask<City, Void, Void> {

        @Override
        protected Void doInBackground(City... params) {
            City city = params[0];
            String jsonString = city.asJsonString();
            PrintWriter pw = null;
            try {
                pw = new PrintWriter(mLastManualSearchFile);
                pw.write(jsonString);
                pw.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    /**
     * Task reads last user manual serch and initiate update of the data.
     */
    private class UpdateWeatherFromFileTask extends AsyncTask<File, Void, City> {

        @Override
        protected City doInBackground(File... params) {
            String c = null;
            try {
                Scanner s = new Scanner(params[0]);
                c = s.nextLine();
                s.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (c != null) {
                try {
                    return City.fromJsonString(c);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(City city) {
            Map<String, String> params = defaultRequestParams();
            params.put("q", city.getName());
            WeatherUpdateTask task = new WeatherUpdateTask();
            task.setUserSearch(true);
            task.execute(params);
        }

    }
}