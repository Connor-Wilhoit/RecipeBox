package com.waynesplanet.connor.recipebox20;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

//import java.util.ArrayList;
//import java.util.List;

public class LazyAdapter extends BaseAdapter {
    /*
     * (see http://www.java2s.com/Open-Source/Android_Free_Code/Image/display/
     * com_androidbegin_sdimagetutorialLazyAdater_java.htm for more information)
     */

    private String[] data;
    private String[] name;

    // private ArrayList<String> data;
    // private ArrayList<String> name;

    private static LayoutInflater inflater = null;
    private ImageLoader imageLoader;

    public void loadBitmap(String imageFilePath, ImageView imageView) {
        BitmapWorkerTask task = new BitmapWorkerTask(imageView);
        task.execute(imageFilePath);
    }

    // LazyAdapter(Activity a, ArrayList<String> d, ArrayList<String> n) {
    LazyAdapter(Activity a, String[] d, String[] n) {
        // Variables/Objects used throughout class:
        data = d;
        name = n;
        inflater = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(a.getApplicationContext());
    }

    public int getCount() {
        // return data.size();
        return data.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.gridview_item, parent, false);
        }

        // Locate the TextView in item.xml
        TextView text = convertView.findViewById(R.id.text);

        // Locate the ImageView in item.xml
        ImageView image = convertView.findViewById(R.id.image);

        // Capture position and set to the TextView
        text.setText(name[position]);
        // text.setText(name.get(position));

        // Capture position
        imageLoader.DisplayImage(data[position], image);
        // loadBitmap(data.get(position), image);

        return convertView;
    }
}