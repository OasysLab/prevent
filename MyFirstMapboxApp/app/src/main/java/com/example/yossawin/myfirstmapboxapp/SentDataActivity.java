package com.example.yossawin.myfirstmapboxapp;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class SentDataActivity extends AppCompatActivity {

    private static final String TABLE_ROUND = "rounds";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_ROUND = "round";
    private static final String COLUMN_ISUPLOAD = "isUpload";

    private static final String TABLE_GPX = "gpx";
    private static final String GPX_COLUMN_IDROUND = "roundid";
    private static final String GPX_COLUMN_LATITUDE = "latitude";
    private static final String GPX_COLUMN_LONGITUDE = "longitude";
    private static final String GPX_COLUMN_ALTITUDE = "altitude";
    private static final String GPX_COLUMN_TIME = "dateTime";

    private static final String TABLE_DATA = "data";
    private static final String DATA_COLUMN_ID = "data_id";
    private static final String DATA_COLUMN_IDROUND = "roundid";
    private static final String DATA_COLUMN_LATITUDE = "latitude";
    private static final String DATA_COLUMN_LONGITUDE = "longitude";
    private static final String DATA_COLUMN_TYPE = "type";
    private static final String DATA_COLUMN_NAME = "name";
    private static final String DATA_COLUMN_NOTE = "note";
    private static final String DATA_COLUMN_TIME = "timeAdd";

    private static final String TABLE_IMAGE = "image";
    private static final String IMAGE_COLUMN_ID = "data_id";
    private static final String IMAGE_COLUMN_DATA = "image_data";

    private static final String TAG = "SentDataActivity";

    Button base1;
    Button base2;

    ListView lv;
    ArrayList<Model> modelItems;

    DBHelper helper = new DBHelper(this);

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(":: ส่งเข้าระบบ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_sent_data);

        ConstraintLayout layout = (ConstraintLayout)findViewById(R.id.sent_data_layout);
        ConstraintSet set = new ConstraintSet();

        progressDialog  = new ProgressDialog(this);
        base1 = (Button)findViewById(R.id.base_button1);
        base2 = (Button)findViewById(R.id.base_button2);
        lv = (ListView) findViewById(R.id.listView1);

        ArrayList<ContentValues> roundData =  helper.getRoundHaveData();

        Log.d("Size", String.valueOf(roundData.size()));

        if(roundData.size() != 0)
        {
            modelItems = new ArrayList<>();
            for (Iterator<ContentValues> i = roundData.iterator(); i.hasNext();) {
                ContentValues item = i.next();
                modelItems.add(new Model(item.getAsString("date")+" #"+item.getAsString("round"), item.getAsInteger("isUpload"), item.getAsInteger("id")));
            }
        }
        else
        {
            modelItems = new ArrayList<>();
        }

        final CustomAdapter adapter = new CustomAdapter(this, modelItems);
        lv.setAdapter(adapter);

        Button upload = (Button)findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Integer> arrayId = adapter.getArrayUpload();

                for (Iterator<Integer> i = arrayId.iterator(); i.hasNext();)
                {
                    Integer item = i.next();
                    Log.d("Data  ITEM", String.valueOf(item));
                    ContentValues dataRound =  helper.getRoundContentValues(item);
                    HashMap<String, String> params = new HashMap<>();
                    params.put("id", dataRound.getAsString(COLUMN_ID));
                    params.put("date_round", dataRound.getAsString(COLUMN_DATE));
                    params.put("round_number", dataRound.getAsString(COLUMN_ROUND));

                    apiFunction(params, Utils.COMMIT_ROUND, "กำลังส่งข้อมูลรอบ");

                    ArrayList<ContentValues> dataGpx = helper.getDataGpx(item);
                    Log.d("Size  dataGpx", String.valueOf(dataGpx.size()));
                    for (Iterator<ContentValues> j = dataGpx.iterator(); j.hasNext();)
                    {
                        ContentValues gpx = j.next();
                        params.clear();
                        params.put("round_id", item.toString());
                        params.put("latitude", gpx.getAsString(GPX_COLUMN_LATITUDE));
                        params.put("longitude", gpx.getAsString(GPX_COLUMN_LONGITUDE));
                        params.put("altitude", gpx.getAsString(GPX_COLUMN_ALTITUDE));
                        params.put("date_time", gpx.getAsString(GPX_COLUMN_TIME));

                        apiFunction(params, Utils.COMMIT_GPX, "กำลังส่งข้อมูลตำแหน่ง");
                    }

                    ArrayList<ContentValues> dataData = helper.getDataFromRound(item);
                    for (Iterator<ContentValues> j = dataData.iterator(); j.hasNext();)
                    {
                        ContentValues dataIndata = j.next();
                        params.clear();
                        params.put("data_id", dataIndata.getAsString(DATA_COLUMN_ID));
                        params.put("round_id", item.toString());
                        params.put("latitude", dataIndata.getAsString(DATA_COLUMN_LATITUDE));
                        params.put("longitude", dataIndata.getAsString(DATA_COLUMN_LONGITUDE));
                        params.put("type", dataIndata.getAsString(DATA_COLUMN_TYPE));
                        params.put("name", dataIndata.getAsString(DATA_COLUMN_NAME));
                        params.put("note", dataIndata.getAsString(DATA_COLUMN_NOTE));
                        params.put("time_commit", dataIndata.getAsString(DATA_COLUMN_TIME));

                        apiFunction(params, Utils.COMMIT_DATA, "กำลังส่งข้อมูลที่บันทึก");

                        ArrayList<byte[]> imageArray = helper.getImage(dataIndata.getAsInteger(DATA_COLUMN_ID));
                        Log.d("Size  image", String.valueOf(imageArray.size()));
                        int countImage = 1;
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        for (Iterator<byte[]> k = imageArray.iterator(); k.hasNext();)
                        {
                            byte[] image = k.next();
                            final String imageString = getStringImage(image);
                            params.clear();
                            params.put("data_id", dataIndata.getAsString(DATA_COLUMN_ID));
                            params.put("image_name", timestamp.getTime() + "-" + String.valueOf(countImage));
                            params.put("image_data", imageString);

                            apiFunction(params, Utils.COMMIT_IMAGE, "กำลังส่งข้อมูลรูปภาพ");
                            countImage++;
                        }
                    }
                    helper.setIsUpload(item);
                }
                finish();
                startActivity(getIntent());
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    public void apiFunction(HashMap params, String url, String message)
    {
        String tag_string_req = "req_commit";
        progressDialog.setMessage(message);
        progressDialog.show();

        Log.d("Commit Data", params.toString());
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST,
                url,  new JSONObject(params), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject  response) {
                Log.d(TAG, "Commit Response: " + response.toString());
                try {
                    if(response.getBoolean("status"))
                    {
                        toast("Success");
                        progressDialog.hide();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    toast("Json error: " + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                toast("Unknown Error occurred");
                progressDialog.hide();
            }
        });
        AndroidLoginController.getInstance().addToRequestQueue(jsObjRequest, tag_string_req);
    }

    public String getStringImage(byte[] img) {
        return Base64.encodeToString(img, Base64.DEFAULT);
    }

    private void toast(String x){
        Toast.makeText(this, x, Toast.LENGTH_SHORT).show();
    }
}
