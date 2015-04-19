package com.manhattanproject.geolocalisation;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;


public class AcceptRefuseLocalisation extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_refuse_localisation);
    }

    public void onClickAccept(View v){
        Context c = getApplicationContext();
        Utilisateur courant = new Utilisateur();
        courant.recup(c);
        Position p =new Position(c);
        LatLng l = p.recupCoord();
        String[] params={"updateLocation.php","pseudo",courant.getPseudo(),"px",Double.toString(l.latitude),"py",Double.toString(l.longitude)};
        Requete r = new Requete();
        r.execute(params);
        finish();
    }

    public void onClickRefuse(View v){
        finish();
    }
}
