package com.example.yossawin.myfirstmapboxapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AddDataActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final int CAMERA_REQUEST = 1888;
    ArrayAdapter<CharSequence> adapter_type;

    ArrayAdapter<CharSequence> adapter__name_animal ;
    ArrayAdapter<CharSequence> adapter__name_plant ;
    ArrayAdapter<CharSequence> adapter__name_mushroom ;

    AutoCompleteTextView acTextView ;
    Spinner dropdown;

    TextView note;
    TextView latitude ;
    TextView longitude;

    ImageView imageView1;
    ImageView imageView2;
    ImageView imageView3;
    ImageView imageView4;
    ImageView imageView5;
    ImageView imageView6;
    ImageView imageView7;
    ImageView imageView8;
    ImageView imageView9;
    ImageView imageView10;

    Location lastKnownLocation;

    ArrayList<Bitmap> bitmapArray = new ArrayList<Bitmap>();

    String noteText = "";
    String nameText = "";

    DBHelper helper = new DBHelper(this);

    private int countPicture = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(":: เพิ่มข้อมูล");
        setContentView(R.layout.activity_add_data);

        adapter_type = ArrayAdapter.createFromResource(this,R.array.type, android.R.layout.simple_spinner_dropdown_item);
        adapter__name_animal = ArrayAdapter.createFromResource(this,R.array.name_animal, android.R.layout.simple_spinner_dropdown_item);
        adapter__name_plant = ArrayAdapter.createFromResource(this,R.array.name_plant, android.R.layout.simple_spinner_dropdown_item);
        adapter__name_mushroom = ArrayAdapter.createFromResource(this,R.array.name_mushroom, android.R.layout.simple_spinner_dropdown_item);


        dropdown = (Spinner)findViewById(R.id.type_spinner);
        acTextView =  (AutoCompleteTextView)findViewById(R.id.name_auto);
        note = (TextView)findViewById(R.id.note_more);
        latitude = (TextView)findViewById(R.id.lat_text) ;
        longitude = (TextView)findViewById(R.id.longi_text);

        acTextView.setThreshold(1);
        adapter_type.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        dropdown.setAdapter(adapter_type);

        dropdown.setOnItemSelectedListener(this);
        dropdown.setSelection(0);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        String locationProvider = LocationManager.NETWORK_PROVIDER;
        // Or use LocationManager.GPS_PROVIDER

        lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);

        latitude.setText(String.valueOf(lastKnownLocation.getLatitude()));
        longitude.setText(String.valueOf(lastKnownLocation.getLongitude()));

        imageView1 =(ImageView)this.findViewById(R.id.picture1);
        imageView2 =(ImageView)this.findViewById(R.id.picture2);
        imageView3 =(ImageView)this.findViewById(R.id.picture3);
        imageView4 =(ImageView)this.findViewById(R.id.picture4);
        imageView5 =(ImageView)this.findViewById(R.id.picture5);
        imageView6 =(ImageView)this.findViewById(R.id.picture6);
        imageView7 =(ImageView)this.findViewById(R.id.picture7);
        imageView8 =(ImageView)this.findViewById(R.id.picture8);
        imageView9 =(ImageView)this.findViewById(R.id.picture9);
        imageView10 =(ImageView)this.findViewById(R.id.picture10);

        View.OnClickListener listenCamera = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(countPicture < 10)
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        };
        imageView1.setOnClickListener(listenCamera);
        imageView2.setOnClickListener(listenCamera);
        imageView3.setOnClickListener(listenCamera);
        imageView4.setOnClickListener(listenCamera);
        imageView5.setOnClickListener(listenCamera);
        imageView6.setOnClickListener(listenCamera);
        imageView7.setOnClickListener(listenCamera);
        imageView8.setOnClickListener(listenCamera);
        imageView9.setOnClickListener(listenCamera);
        imageView10.setOnClickListener(listenCamera);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            bitmapArray.add(photo);
            switch (countPicture)
            {
                case(1):
                    imageView1.setImageBitmap(photo);
                    countPicture++;
                    imageView1.setBackgroundResource(0);
                    imageView2.setBackgroundResource(R.drawable.camara);
                    break;
                case(2):
                    imageView2.setImageBitmap(photo);
                    countPicture++;
                    imageView2.setBackgroundResource(0);
                    imageView3.setBackgroundResource(R.drawable.camara);
                    break;
                case(3):
                    imageView3.setImageBitmap(photo);
                    countPicture++;
                    imageView3.setBackgroundResource(0);
                    imageView4.setBackgroundResource(R.drawable.camara);
                    break;
                case(4):
                    imageView4.setImageBitmap(photo);
                    countPicture++;
                    imageView4.setBackgroundResource(0);
                    imageView5.setBackgroundResource(R.drawable.camara);
                    break;
                case(5):
                    imageView5.setImageBitmap(photo);
                    countPicture++;
                    imageView5.setBackgroundResource(0);
                    imageView6.setBackgroundResource(R.drawable.camara);
                    break;
                case(6):
                    imageView6.setImageBitmap(photo);
                    countPicture++;
                    imageView6.setBackgroundResource(0);
                    imageView7.setBackgroundResource(R.drawable.camara);
                    break;
                case(7):
                    imageView7.setImageBitmap(photo);
                    countPicture++;
                    imageView7.setBackgroundResource(0);
                    imageView8.setBackgroundResource(R.drawable.camara);
                    break;
                case(8):
                    imageView8.setImageBitmap(photo);
                    countPicture++;
                    imageView8.setBackgroundResource(0);
                    imageView9.setBackgroundResource(R.drawable.camara);
                    break;
                case(9):
                    imageView9.setImageBitmap(photo);
                    countPicture++;
                    imageView9.setBackgroundResource(0);
                    imageView10.setBackgroundResource(R.drawable.camara);
                    break;
                case(10):
                    imageView10.setImageBitmap(photo);
                    countPicture++;
                    imageView10.setBackgroundResource(0);
                    break;

            }
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view,int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
//         Log.v("item", (String) parent.getItemAtPosition(pos));
        switch (pos) {
            case 0:
                // Whatever you want to happen when the first item gets selected
                acTextView.setAdapter(adapter__name_animal);
                break;
            case 1:
                // Whatever you want to happen when the second item gets selected
                acTextView.setAdapter(adapter__name_plant);
                break;
            case 2:
                // Whatever you want to happen when the thrid item gets selected
                acTextView.setAdapter(adapter__name_mushroom);
                break;

        }
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                new AlertDialog.Builder(this)
                        .setTitle("ยืนยัน")
                        .setMessage("คุณต้องการกลับไปหน้าก่อนหน้า \nใช่ หรือ ไม่")
                        .setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                finish();
                            }
                        }).setNegativeButton("ไม่", null).show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void onSubmitButtonClick(View v) {
        if(v.getId() == R.id.submit_save)
        {
            nameText = String.valueOf(acTextView.getText());
            noteText = String.valueOf(note.getText());

            String tmpName = "";
            String tmpNote = "";

            Log.v("Type", String.valueOf(dropdown.getSelectedItemPosition()));
            Log.v("Name", String.valueOf(nameText.isEmpty()));
            Log.v("Latitude", String.valueOf(lastKnownLocation.getLatitude()));
            Log.v("Longitude", String.valueOf(lastKnownLocation.getLongitude()));
            Log.v("Note", String.valueOf(noteText.isEmpty()));

            if(nameText.isEmpty())
            {
                Log.v("Name","EMPTY");
            }
            else
            {
                Log.v("Name","NOT EMPTY");
                tmpName = nameText;
            }

            if(noteText.isEmpty())
            {
                Log.v("Note","EMPTY");
            }
            else
            {
                Log.v("Note","NOT EMPTY");
                tmpNote = noteText;
            }

            Log.v("IMG", String.valueOf(bitmapArray.size()));

            Intent result = new Intent();
            result.putExtra("Latitude", lastKnownLocation.getLatitude());
            result.putExtra("Longitude", lastKnownLocation.getLongitude());
            result.putExtra("Name", tmpName);

            helper.addDataToDB(dropdown.getSelectedItemPosition(),tmpName,lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude(),tmpNote,bitmapArray);
            setResult(Activity.RESULT_OK,result);
            finish();
        }
    }

}
