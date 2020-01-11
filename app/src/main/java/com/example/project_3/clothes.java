package com.example.project_3;

import android.graphics.Bitmap;

public class clothes {
    private Bitmap image;
    private String group1;
    private String group2;

    public clothes(Bitmap _image, String _group1, String _group2) {
        image = _image;
        group1 = _group1;
        group2 = _group2;
    }

    public Bitmap getImage() {return image;}

    public String getGroup1() {return group1;}

    public String getGroup2() {return group2;}
}
