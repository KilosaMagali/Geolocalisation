package com.manhattanproject.geolocalisation;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class Activity_profil extends ActionBarActivity implements AdapterView.OnItemSelectedListener{
    Button boutonModif;
    Button boutonApp;
    EditText pseudo;
    EditText mdp;
    EditText statut;
    String dureeSelected;
    CheckBox partagePos;
    private Spinner dureeCategory;
    private ArrayAdapter<CharSequence> adapterDureeCategories;
    Utilisateur user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = new Utilisateur();
        setContentView(R.layout.activity_profil);
        boutonModif = (Button)findViewById(R.id.btnmod);
        boutonApp = (Button)findViewById(R.id.btnapp);
        pseudo = (EditText)findViewById(R.id.pseudo);
        mdp = (EditText)findViewById(R.id.mdp);
        statut = (EditText)findViewById(R.id.Statut);
        dureeCategory = (Spinner)findViewById(R.id.SpinnerDuree);
        partagePos = (CheckBox)findViewById(R.id.checkBoxPartagePos);

        user.recup(getApplicationContext());
        if(!user.getPseudo().equals(""))
            pseudo.setText(user.getPseudo());
        if(!user.getMdp().equals(""))
            mdp.setText(user.getMdp());
        if(!user.getStatut().equals(""))
            statut.setText(user.getStatut());
        if(user.getPartagePos())
            partagePos.setChecked(user.getPartagePos());
        System.out.println("ok"+dureeCategory.getSelectedItemPosition());
        populateCategoryCheckBox();

        partagePos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if(isChecked) {
                    dureeCategory.setEnabled(true);
                }
                else {
                    dureeCategory.setEnabled(false);
                }
            }
        });

        pseudo.setEnabled(false);
        mdp.setEnabled(false);
        boutonApp.setEnabled(false);
        statut.setEnabled(false);
        dureeCategory.setEnabled(false);
        partagePos.setEnabled(false);
    }

    public int dureeToInd(int duree){
        switch(duree){
            case 1:
                return 0;
            case 5:
                return 1;
            case 10:
                return 2;
            case 15:
                return 3;
            case 30:
                return 4;
            case 60:
                return 5;
            case 240:
                return 6;
            default:
                return 0;
        }
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

    public void gestionFichier(View view){
        Toast.makeText(getApplicationContext(),"Ouverture de selection image",Toast.LENGTH_LONG).show();
    }

    public void populateCategoryCheckBox(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
// Create an ArrayAdapter using the string array and a default spinner layout
        adapterDureeCategories = ArrayAdapter.createFromResource(this,
                R.array.DureeCategory, android.R.layout.simple_spinner_dropdown_item);
// Specify the layout to use when the list of choices appears
        adapterDureeCategories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        dureeCategory.setAdapter(adapterDureeCategories);
        dureeCategory.setSelection(dureeToInd(preferences.getInt("duree", 1)));
        dureeCategory.setOnItemSelectedListener(this);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        dureeSelected=(String)parent.getItemAtPosition(position);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void modif(View view){
        pseudo.setEnabled(true);
        mdp.setEnabled(true);
        boutonApp.setEnabled(true);
        statut.setEnabled(true);
        partagePos.setEnabled(true);
        if(partagePos.isChecked())
            dureeCategory.setEnabled(true);
        boutonModif.setEnabled(false);
    }

    public void appliquerModif(View view){
        user.setPseudo(pseudo.getText().toString());
        user.setMdp(mdp.getText().toString());
        user.setStatut(statut.getText().toString());
        user.setPartagePos(partagePos.isChecked());
        if(user.getPartagePos()) {
            user.setDuree(Integer.parseInt(dureeSelected));
        }
        else
            user.setDuree(-1);
        user.save(getApplicationContext());
        pseudo.setEnabled(false);
        mdp.setEnabled(false);
        boutonApp.setEnabled(false);
        statut.setEnabled(false);
        dureeCategory.setEnabled(false);
        partagePos.setEnabled(false);
        boutonModif.setEnabled(true);
    }
}
