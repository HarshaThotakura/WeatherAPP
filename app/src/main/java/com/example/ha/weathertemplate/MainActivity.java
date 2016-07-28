package com.example.ha.weathertemplate;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSION_REQUEST_CODE = 1;
    private CurrentWeather mCurrentWeather;

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;

    @BindView(R.id.tempresure_label)
    TextView mTempLabel;
    @BindView(R.id.summary_label)
    TextView mSummaryLabel;
    @BindView(R.id.feelslike_value)
    TextView mFeelsLikeValue;
    @BindView(R.id.humidity_value)
    TextView mHumidityValue;
    @BindView(R.id.Icon_imageView)
    ImageView mImageView;
    @BindView(R.id.time_label)
    TextView mTimeLabel;
    @BindView(R.id.location_label)
    TextView mLocationLabel;
    @BindView(R.id.refreshbutton)
    ImageView RefreshImage;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    double latitude = 0;
    double longitude = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mProgressBar.setVisibility(View.INVISIBLE);

        requestPermission();
        buildGoogleApiClient();
        RefreshImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    latitude = mLastLocation.getLatitude();
                    longitude = mLastLocation.getLongitude();

                    getForecast(latitude, longitude);

                } else {
                    Toast.makeText(getApplicationContext(), "No Location Found", Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    private void requestPermission() {
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);

        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSION_REQUEST_CODE);
        }

    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void getForecast(double latitude, double longitude) {
        String apiKey = "587adc31bb468095";
        String forecastURL = "http://api.wunderground.com/api/" + apiKey + "/forecast/geolookup/conditions/q/" +
                latitude + "," + longitude + ".json";
        if (isNetworkAvilable()) {
            toogleRefresh();
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(forecastURL).build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toogleRefresh();
                        }
                    });
                    alertUser();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toogleRefresh();
                        }
                    });
                    try {
                        String jsonData = response.body().string();

                        Log.v(TAG, response.body().string());
                        if (response.isSuccessful()) {
                            mCurrentWeather = getCurrentDetails(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    UpdateDisplay();
                                }
                            });


                        } else {
                            alertUser();

                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception Caught: ", e);

                    } catch (JSONException e) {
                        Log.e(TAG, "Exception Caught", e);
                    }

                }

            });
        } else {
            Toast.makeText(this, R.string.network_unavailable, Toast.LENGTH_LONG).show();
        }
    }

    private void toogleRefresh() {
        if (mProgressBar.getVisibility() == View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
            RefreshImage.setVisibility(View.INVISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
            RefreshImage.setVisibility(View.VISIBLE);
        }

    }


    private void UpdateDisplay() {

        mTempLabel.setText(mCurrentWeather.getTemperature() + "");
        mSummaryLabel.setText(mCurrentWeather.getSummary());
        mHumidityValue.setText(mCurrentWeather.getHumidity());
        mTimeLabel.setText(mCurrentWeather.getFormattedTime());
        mLocationLabel.setText(mCurrentWeather.getLocation());
        mFeelsLikeValue.setText(mCurrentWeather.getFormattedFeelsLike() + "");
        Ion.with(mImageView)
                .placeholder(R.drawable.clear_day)
                .error(R.drawable.clear_day)
                .load(mCurrentWeather.getIcon());

    }

    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        JSONObject current_observation = forecast.getJSONObject("current_observation");
        JSONObject display_location = current_observation.getJSONObject("display_location");

        String humidity = current_observation.getString("relative_humidity");
        Log.i(TAG, "From JSON" + humidity);

        CurrentWeather currentWeather = new CurrentWeather();
        currentWeather.setHumidity(current_observation.getString("relative_humidity"));
        currentWeather.setSummary(current_observation.getString("weather"));
        currentWeather.setTemperature(current_observation.getDouble("temp_f"));
        currentWeather.setTimeZone(current_observation.getString("local_tz_short"));
        currentWeather.setTime(current_observation.getString("local_epoch"));
        currentWeather.setLocation(display_location.getString("full"));
        currentWeather.setFeelsLike(current_observation.getString("feelslike_f"));
        currentWeather.setIcon(current_observation.getString("icon_url"));

        // Log.d(TAG,currentWeather.getFormattedTime());*/
        return currentWeather;
    }


    private boolean isNetworkAvilable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = manager.getActiveNetworkInfo();
        boolean isAvilable = false;
        if (networkinfo != null && networkinfo.isConnected()) {
            isAvilable = true;
        }
        return isAvilable;
    }

    private void alertUser() {
        AlertFragment dialog = new AlertFragment();
        dialog.show(getFragmentManager(), "error_dialog");

    }


    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            getForecast(latitude, longitude);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    if (mLastLocation != null) {
                        latitude = mLastLocation.getLatitude();
                        longitude = mLastLocation.getLongitude();
                        getForecast(latitude, longitude);

                    } else {
                        Toast.makeText(this, "No Location Found", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "Location Permission are needed", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}

