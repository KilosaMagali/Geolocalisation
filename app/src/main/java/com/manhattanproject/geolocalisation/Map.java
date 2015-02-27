package com.manhattanproject.geolocalisation;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;



public class Map extends Activity implements View.OnClickListener {
    private GoogleMap googleMap;
    private LatLng latLong=new LatLng(0,0);
    private ArrayList<LatLng> tabLocations=new ArrayList<>();
    private ArrayList<String> tabNames=new ArrayList<>();
    private ArrayList<String> tabCategories=new ArrayList<>();
    private Dialog addLieuDialog;
    private EditText locationName;
    private EditText locationCategory;
    private Button btnAjouter;
    private Button btnSugg, btnLieu,btnAmis;
    private LatLng currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        createMapView();
        tabLocations.add(new LatLng(43.6042600 , 1.4436700));
        tabNames.add("Jean Jaures");
        tabCategories.add("bar");
        tabLocations.add(new LatLng(43.6142600 , 1.4436700));
        tabNames.add("Auchan");
        tabCategories.add("parking");
        addMarker();
        CameraPosition cameraPosition = new CameraPosition.Builder().target(tabLocations.get(1))
                .zoom(15).build();
        /*CameraPosition cameraPosition=new CameraPosition.Builder().target(currentPosition).zoom(10).build();*/

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        btnSugg=(Button)findViewById(R.id.btnSugg);
        btnLieu=(Button)findViewById(R.id.btnLieu);
        btnAmis=(Button)findViewById(R.id.btnAmis);
        btnSugg.setOnClickListener(this);
        btnAmis.setOnClickListener(this);
        btnLieu.setOnClickListener(this);

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {
                addLieuDialog=new Dialog(Map.this,android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth);
                addLieuDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                addLieuDialog.setCancelable(true);
                addLieuDialog.setContentView(R.layout.add_lieu);
                locationName=(EditText)addLieuDialog.findViewById(R.id.locationName);
                locationCategory=(EditText)addLieuDialog.findViewById(R.id.locationCategory);
                btnAjouter=(Button)addLieuDialog.findViewById(R.id.btnAjout);
                btnAjouter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(),
                                "Lieu:" + locationName.getText() +
                                        "\nCategorie:" + locationCategory.getText() +
                                        "\nSuccès!! :) ",
                                Toast.LENGTH_LONG).show();
                        tabLocations.add(latLng);
                        tabNames.add(locationName.getText().toString());
                        tabCategories.add(locationCategory.getText().toString());
                        addLieuDialog.dismiss();
                        addMarker();
                    }
                });
                addLieuDialog.show();
            }
        });
        Context c = getApplicationContext();
        Utilisateur vincent;
        vincent = new Utilisateur("iorjebpoer","Bonnemains","Vincent","en couple",new LatLng(1.2344444444444444,4.567777777777777));
        vincent.save(c);
        Utilisateur test = new Utilisateur();
        test.recup(c);
        System.out.println("recuperation");
        System.out.println(test);

        DataBase db = new DataBase(c,"base de donne",null,1);
        int i;
        for(i = 0;i < 100;++i)
            db.supprAmi(new Ami(i,null,null,null,null));
        Ami a = new Ami(new LatLng(40.0,40.0),"Mahot","Lucien","mange de la pizza");
        db.ajoutAmi(a);
        //A executer une fois
        /*Ami a = new Ami(new LatLng(40.0,40.0),"Mahot","Lucien","mange de la pizza");
        Lieu l1 = new Lieu(Categorie_lieu.Bar,"De danu","Pub Irlandais qui sert la nuit comme le jour une biere de qualite", true,new LatLng(43.6042600 , 1.4436700));
        Lieu l2 = new Lieu(Categorie_lieu.Magasin,"Auchan","Supermarche de bonne qualite, pas cher", false,new LatLng(43.6142600 , 1.4436700));
        DataBase db = new DataBase(c,"base de donne",null,1);
        db.ajoutLieu(l1);
        db.ajoutLieu(l2);
        db.ajoutAmi(a);
        */
    }

    private void createMapView(){
        /**
         * Catch the null pointer exception that
         * may be thrown when initialising the activity_map
         */
        try {
            if(null == googleMap){
                googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapView)).getMap();
                googleMap.getUiSettings().setZoomControlsEnabled(true); // true to enable
                //googleMap.getUiSettings().setMyLocationButtonEnabled(true); //my location button
                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                googleMap.setMyLocationEnabled(true);
                currentPosition=new LatLng(googleMap.getMyLocation().getLatitude(),
                        googleMap.getMyLocation().getLongitude());

                /**
                 * If the activity_map is still null after attempted initialisation,
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
            for(int i=0; i<tabLocations.size(); i++) {
                googleMap.addMarker(new MarkerOptions()
                                .position(tabLocations.get(i))
                                .title(tabNames.get(i)).snippet(tabCategories.get(i))
                );
            }
        }
    }
   /* private void addMarker(LatLng latlong){

        /** Make sure that the map has been initialised **/
       /* if(null != googleMap && latLong!=null){
            googleMap.addMarker(new MarkerOptions()
                            .position(latlong)
                            .title("My Marker")
            );
            googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(0,0))
                            .title("Equator")
            );
        }
    }*/

    @Override
    public void onClick(View v) {
        Intent intent;
        switch(v.getId()){
            case R.id.btnLieu:
                intent=new Intent(getApplicationContext(),Activity_list_lieu.class);
                startActivity(intent);
                break;
            case R.id.btnSugg:
                Toast.makeText(getApplicationContext(),"Go to Suggestion screen",Toast.LENGTH_LONG).show();
                break;
            case R.id.btnAmis:
                intent=new Intent(getApplicationContext(),Activity_list_ami.class);
                startActivity(intent);
                break;


        }
    }


    /*private class MyLocationListener implements LocationListener {
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


    }*/
}
