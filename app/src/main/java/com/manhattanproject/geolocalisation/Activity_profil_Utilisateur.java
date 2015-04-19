package com.manhattanproject.geolocalisation;

import android.app.Activity;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class Activity_profil_Utilisateur extends Activity {
    TextView pseudo;
    ImageButton imagebtn;
    //CheckBox partagePos;
    Ami user;
    EditText statut;
    ToggleButton toggleButton;
    TextView addressZone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        retrieveDataPassed();
        setContentView(R.layout.activity_profil_utilisateur);
        pseudo = (TextView)findViewById(R.id.pseudo);
        statut = (EditText)findViewById(R.id.Statut);
        imagebtn = (ImageButton)findViewById(R.id.imageButton);
        toggleButton=(ToggleButton)findViewById(R.id.toggleButton);
        addressZone=(TextView)findViewById(R.id.addressZone);
        if(!user.getPseudo().equals(""))
            pseudo.setText(user.getPseudo());
        if(!user.getStatut().equals(""))
            statut.setText(user.getStatut());
        imagebtn.setImageBitmap(user.getImage());
        toggleButton.setChecked(user.isConnect());
        String address= getAddress(user.getPosition());
        if(address!=null) addressZone.setText(address);
        statut.setEnabled(false);
        imagebtn.setEnabled(false);
        toggleButton.setEnabled(false);
    }

   public void retrieveDataPassed() {
       String pseudoAmi="";
       if(getIntent().hasExtra("pseudoAmi"))
           pseudoAmi=getIntent().getStringExtra("pseudoAmi");
       long idAmi=-1;
       if(getIntent().hasExtra("idAmi"))
            idAmi=getIntent().getLongExtra("idAmi",-1);
       Bitmap bitmapImageAmi=null;
       if(getIntent().hasExtra("BitmapImageAmi"))
            bitmapImageAmi =(Bitmap) getIntent().getParcelableExtra("BitmapImageAmi");
       double latitudeAmi=getIntent().getDoubleExtra("latitudeAmi",0);
       double longitudeAmi=getIntent().getDoubleExtra("longitudeAmi", 0);
       boolean connect=getIntent().getBooleanExtra("connectStatus",false);
       String statut = getIntent().getStringExtra("Statut");
       LatLng positionAmi=new LatLng(latitudeAmi,longitudeAmi);
       user=new Ami(idAmi,positionAmi,pseudoAmi,statut);
       if(bitmapImageAmi!=null)
       user.setImage(bitmapImageAmi);
       user.setConnect(connect);



   }
    public String getAddress(LatLng location) {
        Geocoder geocoder;
        List<Address> addresses=new ArrayList<>();
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
            Log.w("Location", "Can not get Address!");
        }

        String address = addresses.get(0).getAddressLine(0);
        String city = addresses.get(0).getLocality();
        String country = addresses.get(0).getCountryName();
        return  "Dernière position: "+ address+" , \n"+city+" , "+country;

    }

}
