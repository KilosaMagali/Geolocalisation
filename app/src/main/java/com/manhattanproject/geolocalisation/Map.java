package com.manhattanproject.geolocalisation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;



public class Map extends Activity implements View.OnClickListener ,AdapterView.OnItemSelectedListener {
    private GoogleMap googleMap;
    private LatLng latLong=new LatLng(0,0);
    private Dialog addLieuDialog;
    private EditText locationName;
    private Spinner locationCategory;
    private Button btnLieu,btnAmis,btnAjouter,btnAnnule;
    private LatLng currentPosition;
    private CameraPosition cameraPosition;
    private EditText locationDescription;
    private CheckBox shareLocation,checkBoxSuggestion,checkBoxLieu,checkBoxAmis,checkBoxAddCurrentLoc;
    private ArrayAdapter<CharSequence> adapterLocationCategories;
    private String categorySelected;
    private ArrayList<Lieu> listeLieu;
    private DataBase db;
    private Location currentLocation;
    private static LatLng staticCurrentPosition;
    private static boolean gpsRequest=false;
    private ArrayList<Ami> listeAmi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        createMapView();
        checkBoxAmis=(CheckBox)findViewById(R.id.checkAmis);
        checkBoxLieu=(CheckBox)findViewById(R.id.checkLieu);
        checkBoxLieu.setChecked(true);
        setOnCheckedChanged();
        db = new DataBase(getApplicationContext(),"base de donne",null,4);
        listeLieu = db.recupLieuBD();
        Utilisateur courant = new Utilisateur();
        courant.recup(getApplicationContext());
        listeAmi=Activity_list_ami.recupereAmi(courant);
        addMarkerLieu();
        currentPosition=getMyCurrentLocation();
        if(currentPosition!=null) { //zoom to my current location
            cameraPosition = new CameraPosition.Builder().target(currentPosition)
                    .zoom(17).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        else {  // zoom to one of the existing locations
            if (listeLieu.size() != 0) {
                cameraPosition = new CameraPosition.Builder().target(listeLieu.get(0).getPosition())
                        .zoom(17).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }




        btnLieu=(Button)findViewById(R.id.btnLieu);
        btnAmis=(Button)findViewById(R.id.btnAmis);
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
                locationDescription=(EditText)addLieuDialog.findViewById(R.id.locationDescription);
                locationCategory=(Spinner)addLieuDialog.findViewById(R.id.locationCategory);
                shareLocation=(CheckBox)addLieuDialog.findViewById(R.id.checkBoxShareLocation);
                checkBoxAddCurrentLoc=(CheckBox)addLieuDialog.findViewById(R.id.checkBoxAddCurrentLoc);
                btnAjouter=(Button)addLieuDialog.findViewById(R.id.btnAjout);
                btnAjouter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name=locationName.getText().toString().trim();
                        String description=locationDescription.getText().toString().trim();
                        //categorySelected retrieved in onItemSelected() method
                        boolean toBeshared=shareLocation.isChecked();
                        if(name.isEmpty()){
                            locationName.setError("Champs requis");
                            locationDescription.setError("Nom de lieu requis");
                            return;
                        }
                        if(checkBoxAddCurrentLoc.isChecked())
                            latLong=getMyCurrentLocation();
                        else latLong=latLng;
                        Lieu locationToAdd=new Lieu(Categorie_lieu.valueOf(categorySelected),name, description,toBeshared,latLong);
                        //db = new DataBase(getApplicationContext(),"base de donne",null,4);
                        if(db.ajoutLieu(locationToAdd)!=-1) {
                            listeLieu.add(locationToAdd);
                            Toast.makeText(getApplicationContext(),
                                    "Lieu:" + name +
                                            "\nAjout reussi!! ",
                                    Toast.LENGTH_LONG).show();


                            addMarkerLieu();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"FAILED",Toast.LENGTH_LONG).show();
                        }
                        addLieuDialog.dismiss();
                    }
                });
                btnAnnule=(Button)addLieuDialog.findViewById(R.id.btnAnnule);
                btnAnnule.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        addLieuDialog.dismiss();
                    }
                });
                populateCategoryCheckBox();
                addLieuDialog.show();
            }
        });
        Context c = getApplicationContext();
    }

    @Override
    protected  void onResume(){
        super.onResume();
        gpsRequest=true;
    }

    private LatLng getMyCurrentLocation() {
        //Enable MyLocation Layer of Google Map
        googleMap.setMyLocationEnabled(true);

        LocationProvider locationProvider = new LocationProvider();

        //Get LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        currentPosition=locationProvider.getLocation();
        Location loc= googleMap.getMyLocation();
        if(loc!=null) return new LatLng(loc.getLatitude(),loc.getLongitude());
        else
        return currentPosition;
    }

    public void populateCategoryCheckBox(){
// Create an ArrayAdapter using the string array and a default spinner layout
        adapterLocationCategories = ArrayAdapter.createFromResource(this,
                R.array.locationCategory, android.R.layout.simple_spinner_dropdown_item);
// Specify the layout to use when the list of choices appears
        adapterLocationCategories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        locationCategory.setAdapter(adapterLocationCategories);
        locationCategory.setOnItemSelectedListener(this);
    }



    /**
     *    MapFragment requires the native API Level 11 fragment implementation,
     *    can only be used on API Level 11 and higher devices
     */

    private void createMapView(){
/**
 * Catch the null pointer exception that
 * may be thrown when initialising the activity_map
 */
        try {
            if(null == googleMap){
                googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapView)).getMap();
                googleMap.getUiSettings().setZoomControlsEnabled(false); // true to enable
                googleMap.getUiSettings().setCompassEnabled(true);
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
   Log.e("mapApp", exception.toString());
        }
    }

    /**
     *  Add markers on the map.
     */
    public void addMarkerLieu(){

        /** Make sure that the map has been initialised **/
        if(null != googleMap && latLong!=null){
            if(listeLieu.size()!=0 && checkBoxLieu.isChecked()) {
                for (int i = 0; i < listeLieu.size(); i++) {
                    //Traitement des icones
                    if(listeLieu.get(i).getCategorie()==Categorie_lieu.Parking)
                        googleMap.addMarker(new MarkerOptions()
                                        .position(listeLieu.get(i).getPosition())
                                        .title(listeLieu.get(i).designation).snippet(listeLieu.get(i).description).
                                                icon(BitmapDescriptorFactory.fromResource(R.drawable.parking))
                        );
                    else if(listeLieu.get(i).getCategorie()==Categorie_lieu.Bar)
                        googleMap.addMarker(new MarkerOptions()
                                        .position(listeLieu.get(i).getPosition())
                                        .title(listeLieu.get(i).designation).snippet(listeLieu.get(i).description).
                                                icon(BitmapDescriptorFactory.fromResource(R.drawable.bar))
                        );
                    else if(listeLieu.get(i).getCategorie()==Categorie_lieu.Restaurant)
                        googleMap.addMarker(new MarkerOptions()
                                        .position(listeLieu.get(i).getPosition())
                                        .title(listeLieu.get(i).designation).snippet(listeLieu.get(i).description).
                                                icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant))
                        );
                    else if(listeLieu.get(i).getCategorie()==Categorie_lieu.Cinema)
                        googleMap.addMarker(new MarkerOptions()
                                        .position(listeLieu.get(i).getPosition())
                                        .title(listeLieu.get(i).designation).snippet(listeLieu.get(i).description).
                                                icon(BitmapDescriptorFactory.fromResource(R.drawable.cinema))
                        );
                    else if(listeLieu.get(i).getCategorie()==Categorie_lieu.Magasin)
                        googleMap.addMarker(new MarkerOptions()
                                        .position(listeLieu.get(i).getPosition())
                                        .title(listeLieu.get(i).designation).snippet(listeLieu.get(i).description).
                                                icon(BitmapDescriptorFactory.fromResource(R.drawable.magasin))
                        );
                    else if(listeLieu.get(i).getCategorie()==Categorie_lieu.Home)
                        googleMap.addMarker(new MarkerOptions()
                                        .position(listeLieu.get(i).getPosition())
                                        .title(listeLieu.get(i).designation).snippet(listeLieu.get(i).description).
                                                icon(BitmapDescriptorFactory.fromResource(R.drawable.home))
                        );
                    else
                        googleMap.addMarker(new MarkerOptions()
                                .position(listeLieu.get(i).getPosition())
                                .title(listeLieu.get(i).designation).snippet(listeLieu.get(i).description) );
                }
            }
           else{   //remove all markers and re-add the checked ones
                googleMap.clear();
                //addMarkerSuggestion();
                if(checkBoxAmis.isChecked()){
                    addMarkerAmis();
                }
            }
        }
    }

    private void addMarkerAmis() {
        if(null != googleMap && latLong!=null){
            if(listeAmi.size()!=0 && checkBoxAmis.isChecked()) {
                for (int i = 0; i < listeAmi.size(); i++) {
                    googleMap.addMarker(new MarkerOptions()
                            .position(listeAmi.get(i).getPosition())
                            .title(listeAmi.get(i).pseudo));
                }

            }

        }
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch(v.getId()){
            case R.id.btnLieu:
                intent=new Intent(getApplicationContext(),Activity_list_lieu.class);
                startActivity(intent);
                break;
            case R.id.btnAmis:
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    intent=new Intent(getApplicationContext(),Activity_list_ami.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(),"Vous ne disposez pas de connection de données",Toast.LENGTH_LONG).show();
                }

                break;


        }
    }

    //on location category selection
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        categorySelected=(String)parent.getItemAtPosition(position);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    public void setOnCheckedChanged(){
        checkBoxAmis.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked) {
                            addMarkerAmis();
                        } else googleMap.clear();
                if(checkBoxLieu.isChecked()){
                    addMarkerLieu();
                }
            }
        });
        checkBoxLieu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                addMarkerLieu();
            }
        });
    }

    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS est desactivé. Veuillez l'activer pour plus de précision?")
                .setCancelable(false)
                .setPositiveButton("Activer GPS",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Continuer sans GPS",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    /**
     *A Class to Manage current location using GPS.
     */
    private class LocationProvider implements LocationListener {
        private double latitude, longitude;
        private String providerName;
        private final int TIME_INTERVAL=15000;
        private final String NETWORK_PROV=LocationManager.NETWORK_PROVIDER;
        private final String GPS_PROV=LocationManager.GPS_PROVIDER;

        public LocationProvider() {
            LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            if (!lm.isProviderEnabled(GPS_PROV) && !gpsRequest){
                showGPSDisabledAlertToUser();

            }
            gpsRequest=true;
            if (lm.isProviderEnabled(GPS_PROV)){
                providerName=GPS_PROV;
            }
            else
                providerName=NETWORK_PROV;


            Location lastLocation = lm.getLastKnownLocation(providerName);
            if (lastLocation != null) {
                latitude = lastLocation.getLatitude();
                longitude = lastLocation.getLongitude();
            }
            lm.requestLocationUpdates(providerName, TIME_INTERVAL, 0, this);

        }

        public  LatLng getLocation(){
            //Get current location as indicated on the map if the map is initialised
            try {
                if (googleMap != null)
                    currentLocation = googleMap.getMyLocation();
            }  catch (NullPointerException exception){
            Log.e("mapApp", exception.toString());
        }
            if(currentLocation!=null)
                currentPosition=new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
            else {
                /**
                 *  If we cant get the location marked on map, get loc set
                 *  by the network/GPS
                 */
                currentPosition = new LatLng(latitude, longitude);
            }
             //updte staticCurrentPosition
            staticCurrentPosition=currentPosition;
            return currentPosition;
        }

        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                currentLocation=location;

            }
            currentPosition=getLocation();

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            //if GPS has been activated, use it a provider
            if(provider.equals(GPS_PROV)) providerName = GPS_PROV;
            LocationManager lm=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
            Location location=lm.getLastKnownLocation(providerName);
            if(location!=null){
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                currentLocation=location;

            }
            currentPosition=getLocation();
        }

        @Override
        public void onProviderDisabled(String provider) {
            if(provider.equals(GPS_PROV))
                providerName = NETWORK_PROV;
            LocationManager lm=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
            Location location=lm.getLastKnownLocation(providerName);
            if(location!=null){
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                currentLocation=location;

            }
            currentPosition=getLocation();
        }
    }

    public static LatLng getLocationFromOutsideTheClass(){
                return staticCurrentPosition;
    }



}
