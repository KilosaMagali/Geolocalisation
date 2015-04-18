package com.manhattanproject.geolocalisation;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.maps.model.LatLng;


public class Activity_profil_Utilisateur extends Activity {
    TextView pseudo;
    ImageButton imagebtn;
    //CheckBox partagePos;
    Ami user;
    EditText statut;
    ToggleButton toggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        retrieveDataPassed();
        setContentView(R.layout.activity_profil);
        pseudo = (TextView)findViewById(R.id.pseudo);
        statut = (EditText)findViewById(R.id.Statut);
        //partagePos = (CheckBox)findViewById(R.id.checkBoxPartagePos);
        imagebtn = (ImageButton)findViewById(R.id.imageButton);
        toggleButton=(ToggleButton)findViewById(R.id.toggleButton);
        if(!user.getPseudo().equals(""))
            pseudo.setText(user.getPseudo());
        if(!user.getStatut().equals(""))
            statut.setText(user.getStatut());
            //partagePos.setChecked(user.);
        imagebtn.setImageBitmap(user.getImage());
        toggleButton.setChecked(user.isConnect());
        //partagePos.setChecked(user.getPartagePos());
        statut.setEnabled(false);
        //partagePos.setEnabled(false);
        imagebtn.setEnabled(false);
    }

   public void retrieveDataPassed() {
       String pseudoAmi="";
       if(getIntent().hasExtra("pseudoAmi"))
           pseudoAmi=getIntent().getStringExtra("nameLieu");
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


   }

}
