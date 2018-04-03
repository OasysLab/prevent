package pref;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 10/1/2016.
 */

public class UserSession {
    private static final String TAG = UserSession.class.getSimpleName();
    private static final String PREF_NAME = "login";
    private static final String KEY_IS_LOGGED_IN = "isloggedin";
    private static final String API_KEY = "apiKey";
    private static final String MAP_IS_READY = "mapIsReady";
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Context ctx;

    public UserSession(Context ctx) {
        this.ctx = ctx;
        prefs = ctx.getSharedPreferences(PREF_NAME, ctx.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void setLoggedin(boolean isLoggedin){
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedin);
        editor.apply();
    }

    public void setMapIsReady(boolean mapIsReady){
        editor.putBoolean(MAP_IS_READY, mapIsReady);
        editor.apply();
    }

    public void setApiKey(String key){
        editor.putString(API_KEY,key);
        editor.apply();
    }

    public boolean isUserLoggedin(){return prefs.getBoolean(KEY_IS_LOGGED_IN, false);}

    public boolean isMapIsReady(){return prefs.getBoolean(MAP_IS_READY, false);}

    public String getApiKey(){return prefs.getString(API_KEY, "");}
}
