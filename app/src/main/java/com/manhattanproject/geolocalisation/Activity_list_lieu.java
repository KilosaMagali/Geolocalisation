package com.manhattanproject.geolocalisation;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by kilosakeyrocker on 22/02/15.
 */
public class Activity_list_lieu extends Activity implements AdapterView.OnItemSelectedListener  {
    private ExpandableListView expandableList;
    private AdapterListLieu adaptor;
    private ArrayList<Lieu> listeLieu;
    private DataBase db;
    private LatLng latLong=new LatLng(0,0);
    private Lieu lieuClicked;  //lieu selectionné au longclick d'un lieu
    private Bundle savedInstanceState;
    private Dialog modifyLieuDialog;
    private Dialog addLieuDialog;
    private ArrayAdapter<CharSequence> adapterLocationCategories;
    private Spinner locationCategory;
    private EditText locationName,locationDescription;
    private CheckBox shareLocation,checkBoxAddCurrentLoc;
    private Button btnModify;
    private Button btnAjouter,btnAnnule;
    private String categorySelected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState=savedInstanceState;
        setContentView(R.layout.activity_list_lieu);
        expandableList = (ExpandableListView) findViewById(R.id.expandableListLieu);
        db = new DataBase(getApplicationContext(),"base de donne",null,4);
        listeLieu = db.recupLieuBD();
        adaptor = new AdapterListLieu(this, listeLieu);
        expandableList.setAdapter(adaptor);
        registerForContextMenu(expandableList);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_list_lieu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        ExpandableListView.ExpandableListContextMenuInfo
                info=(ExpandableListView.ExpandableListContextMenuInfo)menuInfo;
        if(ExpandableListView.getPackedPositionType(info.packedPosition)
                == ExpandableListView.PACKED_POSITION_TYPE_GROUP){
            lieuClicked=listeLieu.get((int)info.id);
            menu.setHeaderTitle("Options: "+lieuClicked.getDesignation());
            String [] menuItems=getResources().getStringArray(R.array.menuLieuLongClick);
            for(int i=0; i<menuItems.length; i++){
                menu.add(Menu.NONE,i,i,menuItems[i]);
            } }
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int menuItemIndex=item.getItemId();
        String [] menuItems=getResources().getStringArray(R.array.menuLieuLongClick);

        switch(menuItemIndex){
            case 0: //Partager
                newOnlineLieu(lieuClicked);
                break;
            case 1: //Modifier
                      modifyLieu();
                break;
            case 2: //M'y rendre
                Intent intentParser=new Intent(getApplicationContext(),MapDrawerActivity.class);
                intentParser.putExtra("nameLieu",lieuClicked.getDesignation());
                intentParser.putExtra("descriptionLieu",lieuClicked.getDescription());
                intentParser.putExtra("categoryLieu",lieuClicked.getCategorie().name());
                intentParser.putExtra("latitudeLieu",lieuClicked.getPosition().latitude);
                intentParser.putExtra("longitudeLieu",lieuClicked.getPosition().longitude);
                intentParser.putExtra("partage",lieuClicked.isPartage());
                startActivity(intentParser);

                break;
            case 3: //Supprimer
                 if(db.supprLieu(lieuClicked)!=-1) {
                     listeLieu.remove(lieuClicked);
                     Toast.makeText(getApplicationContext(), "SUPPRESSION AVEC SUCCES", Toast.LENGTH_LONG)
                             .show();
                 }
                else
                     Toast.makeText(getApplicationContext(),"SUPPRESSION ECHOUEE",Toast.LENGTH_LONG)
                             .show();
                break;
            default:

        }
        /*String menuItemName=menuItems[menuItemIndex];
        Toast.makeText(getApplicationContext(), menuItemName + "  " + "Clicked", Toast.LENGTH_LONG).show();*/
        adaptor = new AdapterListLieu(this, listeLieu);
        expandableList.setAdapter(adaptor);
        return true;
    }

    public void newOnlineLieu(Lieu l){
        final String[] params={"newLieu.php","px",Double.toString(l.getPosition().latitude),"py",Double.toString(l.getPosition().longitude),"des",l.getDesignation(),"descr",l.getDescription(),"cat",l.getCategorie().name()};
        Requete r = new Requete();
        r.execute(params);
        try {
            r.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        String response=r.getResult();
        System.out.println("rep : "+response);
    }


    public void modifyLieu(){
        modifyLieuDialog=new Dialog(this,android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth);
        modifyLieuDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        modifyLieuDialog.setCancelable(true);
        modifyLieuDialog.setContentView(R.layout.add_lieu);
        locationName=(EditText)modifyLieuDialog.findViewById(R.id.locationName);
        locationName.setText(lieuClicked.getDesignation());
        locationName.setEnabled(false);
        locationDescription=(EditText)modifyLieuDialog.findViewById(R.id.locationDescription);
        locationDescription.setText(lieuClicked.getDescription());
        locationCategory=(Spinner)modifyLieuDialog.findViewById(R.id.locationCategory);
        shareLocation=(CheckBox)modifyLieuDialog.findViewById(R.id.checkBoxShareLocation);
        shareLocation.setChecked(lieuClicked.isPartage());
        checkBoxAddCurrentLoc=(CheckBox)modifyLieuDialog.findViewById(R.id.checkBoxAddCurrentLoc);
        checkBoxAddCurrentLoc.setEnabled(false);
        btnModify=(Button)modifyLieuDialog.findViewById(R.id.btnAjout);
        btnAnnule = (Button)modifyLieuDialog.findViewById(R.id.btnAnnule);
        btnModify.setText("Modify");
        btnModify.setOnClickListener(new View.OnClickListener() {
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
                Lieu locationModified=lieuClicked;
                locationModified.setDescription(description);
                locationModified.setCategorie(Categorie_lieu.valueOf(categorySelected));
                locationModified.setPartage(toBeshared);

                //db = new DataBase(getApplicationContext(),"base de donne",null,4);
                if(db.updateLieu(locationModified)!=-1) {
                    listeLieu.remove(lieuClicked);
                    listeLieu.add(locationModified);
                    Toast.makeText(getApplicationContext(),
                            "MODIFICATION AVEC SUCCES", Toast.LENGTH_LONG).show();

                //refresh la liste
                    adaptor = new AdapterListLieu(getApplicationContext(), listeLieu);
                    expandableList.setAdapter(adaptor);
                }
                else{
                    Toast.makeText(getApplicationContext(),"MODIFICATION ECHOUEE",Toast.LENGTH_LONG).show();
                }
                modifyLieuDialog.dismiss();

            }
        });
        btnAnnule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyLieuDialog.dismiss();
            }
        });
        populateCategoryCheckBox();
        modifyLieuDialog.show();
    }
    public void populateCategoryCheckBox(){
// Create an ArrayAdapter using the string array and a default spinner layout
        adapterLocationCategories = ArrayAdapter.createFromResource(this,
                R.array.locationCategory, android.R.layout.simple_spinner_dropdown_item);
// Specify the layout to use when the list of choices appears
        adapterLocationCategories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        locationCategory.setAdapter(adapterLocationCategories);
        locationCategory.setSelection(Categorie_lieu.valueOf(lieuClicked.getCategorie().name()).ordinal());
        locationCategory.setOnItemSelectedListener(this);
    }

    //on location category selection
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        categorySelected=(String)parent.getItemAtPosition(position);


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
            categorySelected=parent.getSelectedItem().toString();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.add:
                //Toast.makeText(getApplicationContext(),"Ouverture d'ajout message",Toast.LENGTH_LONG).show();
                addLieu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addLieu(){
        addLieuDialog=new Dialog(this,android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth);
        addLieuDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addLieuDialog.setCancelable(true);
        addLieuDialog.setContentView(R.layout.add_lieu);
        locationName=(EditText)addLieuDialog.findViewById(R.id.locationName);
        locationDescription=(EditText)addLieuDialog.findViewById(R.id.locationDescription);
        locationCategory=(Spinner)addLieuDialog.findViewById(R.id.locationCategory);
        shareLocation=(CheckBox)addLieuDialog.findViewById(R.id.checkBoxShareLocation);
        checkBoxAddCurrentLoc=(CheckBox)addLieuDialog.findViewById(R.id.checkBoxAddCurrentLoc);
        checkBoxAddCurrentLoc.setChecked(true);
        checkBoxAddCurrentLoc.setEnabled(false);
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
                latLong=Map.getLocationFromOutsideTheClass();
                Lieu locationToAdd=new Lieu(Categorie_lieu.valueOf(categorySelected),name, description,toBeshared,latLong);
                //db = new DataBase(getApplicationContext(),"base de donne",null,4);
                if(db.ajoutLieu(locationToAdd)!=-1) {
                    listeLieu.add(locationToAdd);
                    Toast.makeText(getApplicationContext(),
                            "Lieu:" + name +
                                    "\nAjout reussi!! ",
                            Toast.LENGTH_LONG).show();
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
        populateCategoryCheckBoxForAdd();
        addLieuDialog.show();
    }
    public void populateCategoryCheckBoxForAdd(){
// Create an ArrayAdapter using the string array and a default spinner layout
        adapterLocationCategories = ArrayAdapter.createFromResource(this,
                R.array.locationCategory, android.R.layout.simple_spinner_dropdown_item);
// Specify the layout to use when the list of choices appears
        adapterLocationCategories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        locationCategory.setAdapter(adapterLocationCategories);
        locationCategory.setOnItemSelectedListener(this);
    }

}
