package com.manhattanproject.geolocalisation;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


public class Activity_profil_Utilisateur extends Activity {
    TextView pseudo;
    ImageView imagebtn;
    //CheckBox partagePos;
    Ami user;
    EditText statut;
    Switch toggleButton;
    TextView addressZone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_utilisateur);
        retrieveDataPassed();
        pseudo = (TextView)findViewById(R.id.pseudo);
        statut = (EditText)findViewById(R.id.Statut);
        imagebtn = (ImageView)findViewById(R.id.imageButton);
        toggleButton=(Switch)findViewById(R.id.toggleButton);
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
        System.out.println(imagebtn.getHeight()+" "+imagebtn.getWidth());
    }

   public void retrieveDataPassed() {
       String pseudoAmi="";
       if(getIntent().hasExtra("pseudoAmi"))
           pseudoAmi=getIntent().getStringExtra("pseudoAmi");
       long idAmi=-1;
       if(getIntent().hasExtra("idAmi"))
            idAmi=getIntent().getLongExtra("idAmi",-1);
       Bitmap bitmapImageAmi=null;
       if(getIntent().hasExtra("BitmapImageAmi")) {
           String [] p = {pseudoAmi};
           loadImg l = new loadImg();
           l.execute(p);
           try {
               l.get();
           } catch (InterruptedException e) {
               e.printStackTrace();
           } catch (ExecutionException e) {
               e.printStackTrace();
           }
           bitmapImageAmi=l.getResult();
       }
       double latitudeAmi=getIntent().getDoubleExtra("latitudeAmi",0);
       double longitudeAmi=getIntent().getDoubleExtra("longitudeAmi", 0);
       boolean connect=getIntent().getBooleanExtra("connectStatus",false);
       String statut = getIntent().getStringExtra("Statut");
       LatLng positionAmi=new LatLng(latitudeAmi,longitudeAmi);
       user=new Ami(idAmi,positionAmi,pseudoAmi,statut);
       if(bitmapImageAmi!=null)
            user.setImage(bitmapImageAmi);
       else {
           Bitmap no = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.nobody);
           imagebtn = (ImageView) findViewById(R.id.imageButton);
           //Bitmap image = Bitmap.createScaledBitmap(no,imagebtn.getWidth(),imagebtn.getHeight(), false);
           Bitmap image = Bitmap.createScaledBitmap(no,100,100, false);
           user.setImage(image);

       }
       user.setConnect(connect);
   }

    public class loadImg extends AsyncTask<String,Void,Boolean> {


        Bitmap result;

        public Bitmap getResult() {
            return result;
        }

        public loadImg(){}

        @Override
        protected Boolean doInBackground(String... params) {
            String url = "http://lmahot.hd.free.fr/img/" + params[0];
            System.out.println("url : " + url);
            final URLConnection conn;
            try {
                conn = new URL(url).openConnection();
                conn.connect();
                final InputStream is = conn.getInputStream();

                final BufferedInputStream bis = new BufferedInputStream(is, 100000);

                result = BitmapFactory.decodeStream(bis);
                bis.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
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
