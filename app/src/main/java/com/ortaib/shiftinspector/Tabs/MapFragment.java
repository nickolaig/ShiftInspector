package com.ortaib.shiftinspector.Tabs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.itextpdf.text.pdf.PdfWriter;
import com.ortaib.shiftinspector.R;



public class MapFragment extends Fragment implements OnMapReadyCallback {
    private static final String TAG = "MapFragment";
    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private final float DEFUALT_ZOOM = 10f;
    boolean putMarkerS=false,putMarkerF=false;
    private PdfWriter pdfWriter;
    private LatLng sll,fll;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_map, container, false);

        initMap();
        return view;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(putMarkerS==true){
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(sll)).setTitle("Start");
            moveCamera(sll, DEFUALT_ZOOM);
            putMarkerS=false;
        }
        if(putMarkerF==true){
            mMap.clear();
            mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).position(fll)).setTitle("Finish");
            putMarkerF=false;
        }
        //getDeviceLocation();
        //mMap.setMyLocationEnabled(true);
        //putMarkers();
    }

    private void initMap() {
        mMapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        if(mMapFragment == null){
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            mMapFragment = SupportMapFragment.newInstance();
            ft.replace(R.id.map,mMapFragment).commit();
        }
        mMapFragment.getMapAsync(this);

        Log.d(TAG, "initMap: initializing map");
    }
    private void moveCamera(LatLng lat, float zoom) {
        Log.d(TAG, "moveCamera: moving the camere to : " + lat.latitude + ", " + lat.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lat, zoom));
    }
    public void putStartMarker(Double sLon, Double sLat){
        putMarkerS=true;
        sll = new LatLng(sLat,sLon);
        if(mMap != null) {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(sll)).setTitle("Start");
            moveCamera(sll, DEFUALT_ZOOM);
            putMarkerS=false;
        }

    }
    public void putFinishMarker(Double lon, Double lat){
        putMarkerF=true;
        fll = new LatLng(lat,lon);
        if(mMap != null) {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).position(fll)).setTitle("Finish");
            putMarkerF=false;
        }
        else{

        }

    }
}
