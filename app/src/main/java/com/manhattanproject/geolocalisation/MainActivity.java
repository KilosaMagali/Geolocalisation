package com.manhattanproject.geolocalisation;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MainActivity extends ActionBarActivity {
    private GoogleMap googleMap;
    private LatLng latLong= new LatLng(0,0); //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createMapView();
        addMarker();
        LocationManager lm=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location lastLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastLocation != null)
        {
            latLong = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
        }
        LocationListener ll= new MyLocationListener();
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, ll);
    }
    private void createMapView(){
        /**
         * Catch the null pointer exception that
         * may be thrown when initialising the map
         */
        try {
            if(null == googleMap){
                googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapView)).getMap();

                /**
                 * If the map is still null after attempted initialisation,
                 * show an error to the user
                 */
                if(null == googleMap) {
                    Toast.makeText(getApplicationContext(),
                            "Error creating map", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (NullPointerException exception){
            //Log.e("mapApp", exception.toString());
        }
    }
    private void addMarker(){

        /** Make sure that the map has been initialised **/
        if(null != googleMap && latLong!=null){
            googleMap.addMarker(new MarkerOptions()
                            .position(latLong)
                            .title("My Marker")
            );
        }
    }



    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if(location!=null){
                latLong=new LatLng(location.getLatitude(), location.getLongitude());
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }


    }
}
