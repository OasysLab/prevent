package com.example.yossawin.myfirstmapboxapp;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import pref.UserSession;

public class LoginActivity extends Activity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private ProgressDialog progressDialog;
    private EditText username, password;
    private UserSession session;

    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        progressDialog  = new ProgressDialog(this);
        username        = (EditText)findViewById(R.id.username_text);
        password        = (EditText)findViewById(R.id.password_text);

        session         = new UserSession(this);
        if(session.isUserLoggedin()){
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
        }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.INTERNET
                },10);
                return;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 10:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    configureButton();
                    return;
                }
        }
    }

    public void onLoginButtonClick(View v) {
        if(v.getId() == R.id.submit)
        {
//            Intent i = new Intent(LoginActivity.this, MainActivity.class);
//            startActivity(i);
//            finish();
            String uName = username.getText().toString().trim();
            String pass  = password.getText().toString().trim();

            login(uName, pass);
        }
    }

    private void login(final String username, final String password){
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        progressDialog.setMessage("Logging in...");
        progressDialog.show();

        HashMap<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        Log.d(TAG, "Login Response: " + params.toString());
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST,
                Utils.LOGIN_URL,  new JSONObject(params), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject  response) {
                Log.d(TAG, "Login Response: " + response.toString());
                try {
                    if(response.getBoolean("login"))
                    {
                        session.setLoggedin(true);
                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
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

    private void toast(String x){
        Toast.makeText(this, x, Toast.LENGTH_SHORT).show();
    }
}
