package com.manhattanproject.geolocalisation;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by lulu on 18/04/2015.
 */
public class Position {
    Context c;
    public Position(Context c){
        this.c=c;
    }
    public LatLng recupCoord() {
        LocationManager locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
        Location gps;
        // Si on ne peut avoir le GPS ou s'il ne renvoie rien (par exemple, pas assez
        // de satellites obtenus, prendre les informations du réseau
        // Sinon, prendre les informations du GPS
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) == null) {
            gps = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } else {
            gps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        return new LatLng(gps.getLatitude(),gps.getLongitude());
    }
}
