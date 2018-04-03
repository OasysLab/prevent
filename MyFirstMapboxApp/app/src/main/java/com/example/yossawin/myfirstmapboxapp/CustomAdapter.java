package com.example.yossawin.myfirstmapboxapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Yossawin on 10-Nov-17.
 */

public class CustomAdapter extends ArrayAdapter<Model>{
    ArrayList<Model> modelItems = new ArrayList<>();
    Context context;

    ArrayList<Integer> array_id = new ArrayList<>();

    DBHelper helper;

    public CustomAdapter(Context context,  ArrayList<Model> resource) {
        super(context,R.layout.row,resource);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.modelItems = resource;
        this.helper = new DBHelper(context);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return modelItems.size();
    }

    @Override
    public Model getItem(int arg0) {
        // TODO Auto-generated method stub
        return modelItems.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        // TODO Auto-generated method stub
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.row, parent, false);

        TextView name = (TextView) convertView.findViewById(R.id.textView1);
        final CheckBox cb = (CheckBox) convertView.findViewById(R.id.checkBox1);

        Button deleteButton = (Button)convertView.findViewById(R.id.base_button1);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new AlertDialog.Builder(context)
                        .setTitle("ยืนยัน")
                        .setMessage("คุณต้องการลบข้อมูล"+ modelItems.get(position).getName() +" \nใช่ หรือ ไม่")
                        .setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                helper.deleteData(modelItems.get(position).getId());
                                modelItems.remove(position);
                                CustomAdapter.this.notifyDataSetChanged();
                            }
                        }).setNegativeButton("ไม่", null).show();
                Log.d("DELETE",String.valueOf(modelItems.get(position).getName()));
            }
        });

        Button detailButton = (Button)convertView.findViewById(R.id.base_button2);

        detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Detail",String.valueOf(modelItems.get(position).getId()));
                Intent i = new Intent(context, DetailActivity.class);
                i.putExtra("ID", modelItems.get(position).getId());
                context.startActivity(i);
            }
        });

        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cb.isChecked())
                {
                    array_id.add(modelItems.get(position).getId());
                }
                else
                {
                    if(array_id.contains(modelItems.get(position).getId()))
                    {
                        array_id.remove(array_id.indexOf(modelItems.get(position).getId()));
                        Log.d("Remove List","Success");
                    }
                }
                Log.d("Check",String.valueOf(modelItems.get(position).getName()));
            }
        });

        name.setText(modelItems.get(position).getName());
        if(modelItems.get(position).getValue() == 1)
        {
            cb.setChecked(true);
            cb.setClickable(false);
        }
        else
        {
            deleteButton.setClickable(false);
            cb.setChecked(false);
        }
        return convertView;
    }

    public ArrayList<Integer> getArrayUpload()
    {
        return array_id;
    }
}
