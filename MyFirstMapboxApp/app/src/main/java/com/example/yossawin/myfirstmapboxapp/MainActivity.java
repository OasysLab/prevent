package com.example.yossawin.myfirstmapboxapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.provider.Settings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import android.widget.Toast;

import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.mapbox.mapboxsdk.offline.OfflineRegionError;
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus;
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;

import org.json.JSONObject;

import pref.UserSession;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private boolean isEndNotified;
    private ProgressBar progressBar;
    private OfflineManager offlineManager;
    private boolean isStart = false;
    private UserSession session;
    private MapView mapView;
    private MapboxMap mainMap;
    private Marker user;
    private Marker start;
    private Marker end;
    private Polyline route;

    private Button start_button;
    private Button add_data;
    private Button sent_data;

    // JSON encoding/decoding
    public static final String JSON_CHARSET = "UTF-8";
    public static final String JSON_FIELD_REGION_NAME = "FIELD_REGION_NAME";

    LocationManager locationManager;
    LocationListener locationListener;

    DBHelper helper = new DBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("การเดินทางที่: " + helper.getRound());
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_main);


        session = new UserSession(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("Location", String.valueOf(location));
                helper.addLocationToDB(location.getLatitude(), location.getLongitude(), location.getAltitude());
                user.setPosition(new LatLng(location.getLatitude(),location.getLongitude()));
                route.addPoint(new LatLng(location.getLatitude(),location.getLongitude()));
//                Toast.makeText(MainActivity.this, String.valueOf(location), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        // Create a mapView
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);


        // Add a MapboxMap
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mainMap = mapboxMap;
                offlineManager = OfflineManager.getInstance(MainActivity.this);

                // Define the offline region
                LatLngBounds latLngBounds = new LatLngBounds.Builder()
                        .include(new LatLng(18.884946, 98.825240)) // Northeast
                        .include(new LatLng(18.709577, 99.063340)) // Southwest
                        .build();

                // Define the offline region
                OfflineTilePyramidRegionDefinition definition = new OfflineTilePyramidRegionDefinition(
                        mapboxMap.getStyleUrl(),
                        latLngBounds,
                        10,
                        20,
                        MainActivity.this.getResources().getDisplayMetrics().density);
                // Set the metadata
                byte[] metadata;
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(JSON_FIELD_REGION_NAME, "Yosemite National Park");
                    String json = jsonObject.toString();
                    metadata = json.getBytes(JSON_CHARSET);
                } catch (Exception exception) {
                    Log.e(TAG, "Failed to encode metadata: " + exception.getMessage());
                    metadata = null;
                }

                // Get the region bounds and zoom and move the camera.
                LatLngBounds bounds = (definition.getBounds());
                double regionZoom = (definition.getMinZoom());

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(bounds.getCenter())
                        .zoom(regionZoom)
                        .build();

                // Move camera to new position
                mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                if(!session.isMapIsReady()){
                    // Create the region asynchronously
                    offlineManager.createOfflineRegion(
                        definition,
                        metadata,
                        new OfflineManager.CreateOfflineRegionCallback() {
                            @Override
                            public void onCreate(OfflineRegion offlineRegion) {
                                offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE);

                                // Display the download progress bar
                                progressBar = (ProgressBar) findViewById(R.id.progress_bar);
                                startProgress();

                                // Monitor the download progress using setObserver
                                offlineRegion.setObserver(new OfflineRegion.OfflineRegionObserver() {
                                    @Override
                                    public void onStatusChanged(OfflineRegionStatus status) {

                                        // Calculate the download percentage and update the progress bar
                                        double percentage = status.getRequiredResourceCount() >= 0
                                                ? (100.0 * status.getCompletedResourceCount() / status.getRequiredResourceCount()) :
                                                0.0;

                                        if (status.isComplete()) {
                                            // Download complete
                                            endProgress(getString(R.string.simple_offline_end_progress_success));
                                            session.setMapIsReady(true);
                                        } else if (status.isRequiredResourceCountPrecise()) {
                                            // Switch to determinate state
                                            setPercentage((int) Math.round(percentage));
                                        }
                                    }

                                    @Override
                                    public void onError(OfflineRegionError error) {
                                        // If an error occurs, print to logcat
                                        Log.e(TAG, "onError reason: " + error.getReason());
                                        Log.e(TAG, "onError message: " + error.getMessage());
                                    }

                                    @Override
                                    public void mapboxTileCountLimitExceeded(long limit) {
                                        // Notify if offline region exceeds maximum tile count
                                        Log.e(TAG, "Mapbox tile count limit exceeded: " + limit);
                                    }
                                });
                            }

                            @Override
                            public void onError(String error) {
                                Log.e(TAG, "Error: " + error);
                            }
                        });
                }
            }
        });

        add_data = (Button)findViewById(R.id.add_data);
        sent_data = (Button)findViewById(R.id.sent_data);
        start_button = (Button)findViewById(R.id.start_button);

        if(!isStart)
        {
            add_data.setEnabled(false);
            sent_data.setEnabled(true);
        }


    }



    public void onStartButtonClick(View v){
        if(v.getId() == R.id.start_button)
        {
            if(isStart)
            {
                isStart = false;
                start_button.setText("เริ่ม \nเดินทาง");
                start_button.setBackgroundColor(getResources().getColor(R.color.Start_Colour));
                add_data.setEnabled(false);
                sent_data.setEnabled(true);
                locationManager.removeUpdates(locationListener);
                helper.increaseRound();
                getSupportActionBar().setTitle("การเดินทางที่: " + helper.getRound());
                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                String locationProvider = LocationManager.NETWORK_PROVIDER;
                // Or use LocationManager.GPS_PROVIDER

                Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
                end = mainMap.addMarker(new MarkerViewOptions()
                        .position(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()))
                        .title("End")
                );
                user.remove();
            }
            else
            {
                mainMap.clear();

                isStart = true;
                start_button.setText("จบ \nการเดินทาง");
                start_button.setBackgroundColor(getResources().getColor(R.color.Stop_Colour));
                add_data.setEnabled(true);
                sent_data.setEnabled(false);
                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                String locationProvider = LocationManager.NETWORK_PROVIDER;
                // Or use LocationManager.GPS_PROVIDER

                Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
                start = mainMap.addMarker(new MarkerViewOptions()
                        .position(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()))
                        .title("Start")
                );
                user = mainMap.addMarker(new MarkerViewOptions()
                        .position(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()))
                        .title("User")
                );
                route = mainMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()))
                        .color(Color.parseColor("#3bb2d0"))
                        .width(2)
                );

                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 0, locationListener);

            }
        }
    }

    public void onAddDataButtonClick(View v){
        if(v.getId() == R.id.add_data)
        {
            Intent i = new Intent(MainActivity.this, AddDataActivity.class);
//            startActivity(i);
            startActivityForResult(i,0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            int latitude = data.getIntExtra("latitude", 0);
            int longitude = data.getIntExtra("longitude", 0);
            mainMap.addMarker(new MarkerViewOptions()
                    .position(new LatLng(data.getDoubleExtra("Latitude",0), data.getDoubleExtra("Longitude",0)))
                    .title(data.getStringExtra("Name"))
            );
            // do something with B's return values
        }
    }

    public void onSentDataButtonClick(View v){
        if(v.getId() == R.id.sent_data)
        {
            Intent i = new Intent(MainActivity.this, SentDataActivity.class);
            startActivity(i);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
//        if (offlineManager != null) {
//            offlineManager.listOfflineRegions(new OfflineManager.ListOfflineRegionsCallback() {
//                @Override
//                public void onList(OfflineRegion[] offlineRegions) {
//                    if (offlineRegions.length > 0) {
//                        // delete the last item in the offlineRegions list which will be yosemite offline map
//                        offlineRegions[(offlineRegions.length - 1)].delete(new OfflineRegion.OfflineRegionDeleteCallback() {
//                            @Override
//                            public void onDelete() {
//                                Toast.makeText(
//                                        MainActivity.this,
//                                        getString(R.string.basic_offline_deleted_toast),
//                                        Toast.LENGTH_LONG
//                                ).show();
//                            }
//
//                            @Override
//                            public void onError(String error) {
//                                Log.e(TAG, "On Delete error: " + error);
//                            }
//                        });
//                    }
//                }
//
//                @Override
//                public void onError(String error) {
//                    Log.e(TAG, "onListError: " + error);
//                }
//            });
//        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    // Progress bar methods
    private void startProgress() {

        // Start and show the progress bar
        isEndNotified = false;
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void setPercentage(final int percentage) {
        progressBar.setIndeterminate(false);
        progressBar.setProgress(percentage);
    }

    private void endProgress(final String message) {
        // Don't notify more than once
        if (isEndNotified) {
            return;
        }

        // Stop and hide the progress bar
        isEndNotified = true;
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.GONE);

        // Show a toast
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
    }
}
