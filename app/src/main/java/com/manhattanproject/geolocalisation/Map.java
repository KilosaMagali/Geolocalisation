package com.manhattanproject.geolocalisation;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
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
    private Button btnSugg, btnLieu,btnAmis,btnAjouter;
    private LatLng currentPosition;
    private CameraPosition cameraPosition;
    private EditText locationDescription;
    private CheckBox shareLocation,checkBoxSuggestion,checkBoxLieu,checkBoxAmis,checkBoxAddCurrentLoc;
    private ArrayAdapter<CharSequence> adapterLocationCategories;
    private String categorySelected;
    private ArrayList<Lieu> listeLieu;
    private DataBase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        createMapView();
        checkBoxAmis=(CheckBox)findViewById(R.id.checkAmis);
        checkBoxLieu=(CheckBox)findViewById(R.id.checkLieu);
        checkBoxSuggestion=(CheckBox)findViewById(R.id.checkSugg);
        checkBoxLieu.setChecked(true);
        setOnCheckedChanged();
        db = new DataBase(getApplicationContext(),"base de donne",null,4);
        listeLieu = db.recupLieuBD();
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
                        .zoom(15).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }




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
                        if(checkBoxAddCurrentLoc.isChecked()) latLong=getMyCurrentLocation();
                        else latLong=latLng;
                        Lieu locationToAdd=new Lieu(Categorie_lieu.valueOf(categorySelected),name, description,toBeshared,latLong);
                        //db = new DataBase(getApplicationContext(),"base de donne",null,4);
                        if(db.ajoutLieu(locationToAdd)!=-1) {
                            listeLieu.add(locationToAdd);
                            Toast.makeText(getApplicationContext(),
                                    "Lieu:" + name +
                                            "\nCategorie:" + categorySelected +
                                            "\nSUCCESS!! :) ",
                                    Toast.LENGTH_LONG).show();


                            addMarkerLieu();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"FAILED",Toast.LENGTH_LONG).show();
                        }
                        addLieuDialog.dismiss();
                    }
                });
                populateCategoryCheckBox();
                addLieuDialog.show();
            }
        });
        Context c = getApplicationContext();
        /*Utilisateur vincent;
        vincent = new Utilisateur(1,"Bonnemains","Vincent","en couple",new LatLng(1.2344444444444444,4.567777777777777));
        vincent.save(c);
        Utilisateur test = new Utilisateur();
        test.recup(c);
        System.out.println("recuperation");
        System.out.println(test);*/

        /*DataBase db = new DataBase(c,"base de donne",null,1);
        long i;
        for(i = 0;i < 100;++i)
            db.supprAmi(new Ami(i,null,null,null));
        Ami a = new Ami(new LatLng(40.0,40.0),"MahotLucien","mange de la pizza");
        db.ajoutAmi(a);*/
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

    private LatLng getMyCurrentLocation() {
        googleMap.setMyLocationEnabled(true);

        Location currentLocation = googleMap.getMyLocation();

        if (currentLocation != null) {
            currentPosition = new LatLng(currentLocation.getLatitude(),
                    currentLocation.getLongitude());
        }
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
    private void addMarkerLieu(){

        /** Make sure that the map has been initialised **/
        if(null != googleMap && latLong!=null){
            if(listeLieu.size()!=0 && checkBoxLieu.isChecked()) {
                for (int i = 0; i < listeLieu.size(); i++) {
                    if(listeLieu.get(i).getCategorie()==Categorie_lieu.Parking)
                    googleMap.addMarker(new MarkerOptions()
                                    .position(listeLieu.get(i).getPosition())
                                    .title(listeLieu.get(i).designation).snippet(listeLieu.get(i).description).
                                            icon(BitmapDescriptorFactory.fromResource(R.drawable.parking))
                    );
                    else
                        googleMap.addMarker(new MarkerOptions()
                                        .position(listeLieu.get(i).getPosition())
                                        .title(listeLieu.get(i).designation).snippet(listeLieu.get(i).description) );
                }
            }
            else{   //remove all markers and re-add the checked ones
                googleMap.clear();
                //addMarkerSuggestion()   à coder
                //addMarkerAmis();         à coder
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
            case R.id.btnSugg:
                Toast.makeText(getApplicationContext(),"Go to Suggestion screen",Toast.LENGTH_LONG).show();
                break;
            case R.id.btnAmis:
                intent=new Intent(getApplicationContext(),Activity_list_ami.class);
                startActivity(intent);
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

        }
    });
    checkBoxLieu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        addMarkerLieu();
        }
    });
    checkBoxSuggestion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        }
    });
}



}
