package com.example.apnisavari.Helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.apnisavari.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustoInfoWindow implements GoogleMap.InfoWindowAdapter {
    View myView;
    public CustoInfoWindow(Context context){
        myView= LayoutInflater.from(context)
                .inflate(R.layout.custom_rider_info_window,null);
    }
    @Override
    public View getInfoWindow(Marker marker) {
        TextView txtPickTitle=((TextView)myView.findViewById(R.id.txtPickUpSnippet));
        txtPickTitle.setText(marker.getTitle());
        TextView txtPickUpSnippet=((TextView)myView.findViewById(R.id.txtPickUpSnippet));
        txtPickUpSnippet.setText(marker.getSnippet());
        return myView;

    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
