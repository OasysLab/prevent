package com.example.yossawin.myfirstmapboxapp;

import android.content.ContentValues;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.mapbox.mapboxsdk.offline.OfflineRegionError;
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus;
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class DetailActivity extends AppCompatActivity {
    private static final String TABLE_DATA = "data";
    private static final String DATA_COLUMN_ID = "data_id";
    private static final String DATA_COLUMN_IDROUND = "roundid";
    private static final String DATA_COLUMN_LATITUDE = "latitude";
    private static final String DATA_COLUMN_LONGITUDE = "longitude";
    private static final String DATA_COLUMN_TYPE = "type";
    private static final String DATA_COLUMN_NAME = "name";
    private static final String DATA_COLUMN_NOTE = "note";
    private static final String DATA_COLUMN_TIME = "timeAdd";

    private static final String TAG = "DetailActivity";
    private MapView mapView;
    DBHelper helper = new DBHelper(this);
    private OfflineManager offlineManager;

    // JSON encoding/decoding
    public static final String JSON_CHARSET = "UTF-8";
    public static final String JSON_FIELD_REGION_NAME = "FIELD_REGION_NAME";

    TableRow row;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        int id ;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                id = -99;
            } else {
                id = extras.getInt("ID");
            }
        } else {
            id = (int) savedInstanceState.getSerializable("ID");
        }
        Log.d("ID PASS", String.valueOf(id));

        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("ข้อมูล: " +helper.getRound(id));
        setContentView(R.layout.activity_detail);



        TableLayout tl = (TableLayout) findViewById(R.id.maintable);
        TextView templateTextView = (TextView)findViewById(R.id.col1);

        ArrayList<ContentValues> data = helper.getDataFromRound(id);
        int countRow = 1;

        for (Iterator<ContentValues> i = data.iterator(); i.hasNext();) {
            ContentValues item = i.next();

            row = new TableRow(this);
            TextView col1 = new TextView(this);
            TextView col2 = new TextView(this);
            TextView col3 = new TextView(this);
            TextView col4 = new TextView(this);

            col1.setText(item.getAsString(DATA_COLUMN_NAME));
            col2.setText(item.getAsString(DATA_COLUMN_LATITUDE));
            col3.setText(item.getAsString(DATA_COLUMN_LONGITUDE));
            col4.setText(item.getAsString(DATA_COLUMN_TIME));

            col1.setGravity(Gravity.CENTER_HORIZONTAL);
            col2.setGravity(Gravity.CENTER_HORIZONTAL);
            col3.setGravity(Gravity.CENTER_HORIZONTAL);
            col4.setGravity(Gravity.CENTER_HORIZONTAL);

            col1.setLayoutParams(templateTextView.getLayoutParams());
            col2.setLayoutParams(templateTextView.getLayoutParams());
            col3.setLayoutParams(templateTextView.getLayoutParams());
            col4.setLayoutParams(templateTextView.getLayoutParams());

            if(countRow % 2 == 0)
            {
                col1.setBackgroundColor(getResources().getColor(R.color.Select));
                col2.setBackgroundColor(getResources().getColor(R.color.Select));
                col3.setBackgroundColor(getResources().getColor(R.color.Select));
                col4.setBackgroundColor(getResources().getColor(R.color.Select));

            }


            countRow++;

            row.addView(col1);
            row.addView(col2);
            row.addView(col3);
            row.addView(col4);

            tl.addView(row);
        }

        Mapbox.getInstance(this, getString(R.string.access_token));

        // Create a mapView
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);


        // Add a MapboxMap
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                offlineManager = OfflineManager.getInstance(DetailActivity.this);

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
                        DetailActivity.this.getResources().getDisplayMetrics().density);
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
            }
        });

    }


    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }


}
