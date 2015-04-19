package com.manhattanproject.geolocalisation;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import java.util.TimerTask;

/**
 * Created by vince_000 on 19/04/2015.
 */
public class PartagePosition extends TimerTask {
    Context c;


    public PartagePosition(Context c) {
        this.c = c;
    }

    public void run() {
        Utilisateur courant = new Utilisateur();
        courant.recup(c);
        Position p =new Position(c);
        LatLng l = p.recupCoord();
        String[] params={"updateLocation.php","pseudo",courant.getPseudo(),"px",Double.toString(l.latitude),"py",Double.toString(l.longitude)};
        Requete r = new Requete();
        r.execute(params);
    }
}