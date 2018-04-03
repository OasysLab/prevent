package com.example.yossawin.myfirstmapboxapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Yossawin on 10/31/2017.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final String TAG = DBHelper.class.getSimpleName();
    SQLiteDatabase db;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "prevent.db";

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

    private static final String CREATE_TABLE_ROUND = "create table rounds (id integer primary key not null, " +
            "date text not null, " +
            "round integer not null, " +
            "isUpload integer DEFAULT 0);";

    private static final String CREATE_TABLE_GPX = "create table gpx (roundid integer not null, " +
            "dateTime text primary key not null, " +
            "latitude double not null, " +
            "longitude double not null, " +
            "altitude double not null);";

    private static final String CREATE_TABLE_DATA = "create table data (data_id integer primary key not null, " +
            "roundid integer not null, " +
            "latitude double not null, " +
            "longitude double not null, " +
            "type integer not null, " +
            "name text, " +
            "note text, " +
            "timeAdd text not null);";

    private static final String CREATE_TABLE_IMAGE = "create table image (data_id integer not null, image_data BLOB not null);";

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ROUND);
        db.execSQL(CREATE_TABLE_GPX);
        db.execSQL(CREATE_TABLE_DATA);
        db.execSQL(CREATE_TABLE_IMAGE);
        this.db = db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS " + TABLE_ROUND;
        db.execSQL(query);
        query = "DROP TABLE IF EXISTS " + TABLE_GPX;
        db.execSQL(query);
        this.onCreate(db);
    }

    private int getRoundId()
    {
        DateFormat df = new SimpleDateFormat("dd MMM yyyy");
        String sdate = df.format(Calendar.getInstance().getTime());
        db = this.getReadableDatabase();
        String query = "select id, date, round from " + TABLE_ROUND + " where date = '" + sdate + "'";
        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToLast();

        return cursor.getInt(0);
    }

    private int getDataId()
    {
        db = this.getReadableDatabase();
        String query = "select data_id from " + TABLE_DATA;
        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToLast();

        return cursor.getInt(0);
    }

    public String getRound()
    {
        DateFormat df = new SimpleDateFormat("dd MMM yyyy");
        String sdate = df.format(Calendar.getInstance().getTime());
        db = this.getReadableDatabase();
        String query = "select id, date, round from " + TABLE_ROUND + " where date = '" + sdate + "'";
        Cursor cursor = db.rawQuery(query, null);

        String result;

        if(cursor.moveToFirst())
        {
            cursor.moveToLast();
            Log.d(TAG, "ZZZ : " + cursor.getString(0));
            result = cursor.getString(1) + " #" + cursor.getString(2);
            return result;
        }
        else
        {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_DATE, sdate);
            values.put(COLUMN_ROUND, 1);

            db.insert(TABLE_ROUND, null, values);

            return getRound();
        }
    }

    public String getRound(int id)
    {
        db = this.getReadableDatabase();
        String query = "select id, date, round from " + TABLE_ROUND + " where id = " + id ;
        Cursor cursor = db.rawQuery(query, null);

        String result  = "";

        if(cursor.moveToFirst())
        {
            Log.d(TAG, "Debug Get Round by id : " + cursor.getString(0));
            result = cursor.getString(1) + " #" + cursor.getString(2);
        }
        return result;

    }

    public ContentValues getRoundContentValues(int id)
    {
        db = this.getReadableDatabase();
        String query = "select id, date, round from " + TABLE_ROUND + " where id = " + id ;
        Cursor cursor = db.rawQuery(query, null);

        ContentValues values = new ContentValues();

        if(cursor.moveToFirst())
        {
            Log.d("getRoundContentValues", cursor.getString(0) + " : " + cursor.getString(1) + " : " + cursor.getString(2));
            values.put(COLUMN_ID,cursor.getInt(0));
            values.put(COLUMN_DATE,cursor.getString(1));
            values.put(COLUMN_ROUND,cursor.getInt(2));
        }
        return values;

    }

    public ArrayList<ContentValues> getRoundHaveData()
    {
        db = this.getReadableDatabase();
        String query = "select id, date, round, isUpload from " + TABLE_ROUND ;
        Cursor cursor = db.rawQuery(query, null);

        ArrayList<ContentValues> output = new ArrayList<ContentValues>();

        if(cursor.moveToFirst())
        {
            do {
                query = "select data_id from " + TABLE_DATA + " where " + DATA_COLUMN_IDROUND + " = " + cursor.getString(0) ;
                Log.d("Query", query);
                Cursor cursor2 = db.rawQuery(query, null);
                if(cursor2.moveToFirst())
                {
                    ContentValues values = new ContentValues();
                    Log.d("DATA getRoundHaveData", cursor.getString(1) +" #"+ cursor.getString(2));
                    values.put(COLUMN_ID,cursor.getInt(0));
                    values.put(COLUMN_DATE,cursor.getString(1));
                    values.put(COLUMN_ROUND,cursor.getInt(2));
                    values.put(COLUMN_ISUPLOAD,cursor.getInt(3));

                    output.add(values);
                }
            }while (cursor.moveToNext());
        }
        return output;
    }

    public ArrayList<ContentValues> getDataFromRound(int id)
    {
        db = this.getReadableDatabase();
        String query = "select name, latitude, longitude, timeAdd, data_id, type, note from " + TABLE_DATA + " where " + DATA_COLUMN_IDROUND + " = " + id ;
        Cursor cursor = db.rawQuery(query, null);

        ArrayList<ContentValues> output = new ArrayList<ContentValues>();

        if(cursor.moveToFirst())
        {
            do {

                ContentValues values = new ContentValues();
                Log.d("Debug getDataFromRound", cursor.getString(0));
                values.put(DATA_COLUMN_NAME,cursor.getString(0));
                values.put(DATA_COLUMN_LATITUDE,cursor.getString(1));
                values.put(DATA_COLUMN_LONGITUDE,cursor.getString(2));
                values.put(DATA_COLUMN_TIME,cursor.getString(3));
                values.put(DATA_COLUMN_ID,cursor.getString(4));
                values.put(DATA_COLUMN_TYPE,cursor.getString(5));
                values.put(DATA_COLUMN_NOTE,cursor.getString(6));

                output.add(values);

            }while (cursor.moveToNext());
        }
        return output;
    }

    public ArrayList<ContentValues> getDataGpx(int id)
    {
        db = this.getReadableDatabase();
        String query = "select latitude, longitude, altitude, dateTime from " + TABLE_GPX + " where " + GPX_COLUMN_IDROUND + " = " + id ;
        Cursor cursor = db.rawQuery(query, null);

        ArrayList<ContentValues> output = new ArrayList<ContentValues>();

        if(cursor.moveToFirst())
        {
            do {

                ContentValues values = new ContentValues();
                Log.d("Debug getDataGpx", cursor.getString(0) +" : "+ cursor.getString(1) +" : " + cursor.getString(2) +" : " + cursor.getString(3));
                values.put(GPX_COLUMN_LATITUDE, cursor.getString(0));
                values.put(GPX_COLUMN_LONGITUDE, cursor.getString(1));
                values.put(GPX_COLUMN_ALTITUDE, cursor.getString(2));
                values.put(GPX_COLUMN_TIME, cursor.getString(3));

                output.add(values);

            }while (cursor.moveToNext());
        }
        return output;
    }

//    private static final String IMAGE_COLUMN_ID = "data_id";
//    private static final String IMAGE_COLUMN_DATA = "image_data";

    public ArrayList<byte[]> getImage(int id)
    {
        db = this.getReadableDatabase();
        String query = "select image_data from " + TABLE_IMAGE + " where " + IMAGE_COLUMN_ID + " = " + id ;
        Cursor cursor = db.rawQuery(query, null);

        ArrayList<byte[]> output = new ArrayList<>();

        if(cursor.moveToFirst())
        {
            do {
                output.add(cursor.getBlob(0));

            }while (cursor.moveToNext());
        }
        return output;
    }

    public void increaseRound()
    {
        DateFormat df = new SimpleDateFormat("dd MMM yyyy");
        String sdate = df.format(Calendar.getInstance().getTime());
        db = this.getReadableDatabase();
        String query = "select id, date, round from " + TABLE_ROUND + " where date = '" + sdate + "'";
        Cursor cursor = db.rawQuery(query, null);

        int countRound = cursor.getCount();

        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, sdate);
        values.put(COLUMN_ROUND, countRound+1);

        db.insert(TABLE_ROUND, null, values);
    }

    public void addLocationToDB(double latitude, double longitude, double altitude)
    {
        int roundId = this.getRoundId();
        DateFormat df = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
        String sdate = df.format(Calendar.getInstance().getTime());
        Log.d("Time add database", sdate);

        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(GPX_COLUMN_IDROUND, roundId);
        values.put(GPX_COLUMN_LATITUDE, latitude);
        values.put(GPX_COLUMN_LONGITUDE, longitude);
        values.put(GPX_COLUMN_ALTITUDE, altitude);
        values.put(GPX_COLUMN_TIME, sdate);

        db.insert(TABLE_GPX, null, values);
    }

    public void addDataToDB(int type, String name, double latitude, double longitude, String note, ArrayList<Bitmap> bitmapArray)
    {
        DateFormat df = new SimpleDateFormat("HH:mm");
        String sdate = df.format(Calendar.getInstance().getTime());
        int roundId = this.getRoundId();
        Log.d("ROUND ID", String.valueOf(roundId));
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DATA_COLUMN_IDROUND, roundId);
        values.put(DATA_COLUMN_LATITUDE, latitude);
        values.put(DATA_COLUMN_LONGITUDE, longitude);
        values.put(DATA_COLUMN_TYPE, type);
        values.put(DATA_COLUMN_NAME, name);
        values.put(DATA_COLUMN_NOTE, note);
        values.put(DATA_COLUMN_TIME, sdate);

        db.insert(TABLE_DATA, null, values);

        int dataID = this.getDataId();

        if(bitmapArray.size() != 0)
        {
            for (int i = 0; i < bitmapArray.size(); i++)
            {
                db = this.getWritableDatabase();
                values = new ContentValues();
                values.put(IMAGE_COLUMN_ID, dataID);
                values.put(IMAGE_COLUMN_DATA, this.getBytes(bitmapArray.get(i)));
                db.insert(TABLE_IMAGE, null, values);
            }
        }
    }

    public void setIsUpload(int id)
    {
        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ISUPLOAD,1);

        db.update(TABLE_ROUND, values, COLUMN_ID + " = " + id, null);
    }

    public void deleteData(int id)
    {
        db = this.getReadableDatabase();
        String query = "select " + DATA_COLUMN_ID +" from " + TABLE_DATA + " where " + DATA_COLUMN_IDROUND + " = " + id;
        Cursor cursor = db.rawQuery(query, null);

        db = this.getWritableDatabase();
        if(cursor.moveToFirst())
        {
            db.delete(TABLE_IMAGE, IMAGE_COLUMN_ID + " = " + cursor.getString(0), null);
        }

        db.delete(TABLE_DATA, DATA_COLUMN_IDROUND + " = " + id, null);
    }

    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
