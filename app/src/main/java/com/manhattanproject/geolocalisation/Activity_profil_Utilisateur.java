package com.manhattanproject.geolocalisation;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.maps.model.LatLng;


public class Activity_profil_Utilisateur extends ActionBarActivity{
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



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_profil, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
