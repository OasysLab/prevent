package com.example.yossawin.myfirstmapboxapp;

/**
 * Created by Yossawin on 10-Nov-17.
 */

public class Model {
    String name;
    int value; /* 0 -&gt; checkbox disable, 1 -&gt; checkbox enable */
    int id;

    Model(String name, int value, int id){
        this.name = name;
        this.value = value;
        this.id = id;
    }

    public String getName()
    {
        return this.name;
    }

    public int getValue()
    {
        return this.value;
    }

    public int getId()
    {
        return this.id;
    }
}
