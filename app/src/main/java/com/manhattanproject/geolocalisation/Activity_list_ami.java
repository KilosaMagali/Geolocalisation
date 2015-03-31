package com.manhattanproject.geolocalisation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class Activity_list_ami extends ActionBarActivity {
    private ExpandableListView expandableList;
    private AdapterListAmi adaptor;
    private ArrayList<Ami> listeAmi=new ArrayList<>();
    private DataBase db;
    Button notifCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_ami);
        expandableList = (ExpandableListView) findViewById(R.id.expandableListAmi);
        /*db = new DataBase(getApplicationContext(),"base de donne",null,4);
        listeAmi = db.recupAmiBD();*/
        //System.out.println(listeAmi.get(0));
        Utilisateur courant = new Utilisateur();
        courant.recup(getApplicationContext());
        final String[] params={"selectUsersFriends.php","pseudo","4rrrrrr"};
        //String[] params={"selectUsersFriends.php","pseudo",courant.getPseudo()};
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
        try{
            JSONArray jArray = new JSONArray(response);
            System.out.println("Donnée de la réponse : "+jArray);
            for(int i=0;i<jArray.length();i++){
                JSONObject json_data = jArray.getJSONObject(i);
                System.out.println("Donnée de l'ami : "+json_data.getString("pseudo"));
                listeAmi.add(i,new Ami(i,new LatLng(json_data.getDouble("positionx"),json_data.getDouble("positionx")), json_data.getString("pseudo"), json_data.getString("statut")));
            }
        }catch(JSONException e){
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
        adaptor = new AdapterListAmi(this, listeAmi);
        expandableList.setAdapter(adaptor);

    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_list_ami, menu);
        MenuItem item = menu.findItem(R.id.demandes);
        MenuItemCompat.setActionView(item, R.layout.action_bar_notification_icon);
        notifCount = (Button) MenuItemCompat.getActionView(item);
        SharedPreferences settings = getSharedPreferences("DemandesNonLues", getApplicationContext().MODE_PRIVATE);
        notifCount.setText(String.valueOf(settings.getInt("nb",0)));
        notifCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    lancerDemandes(v);
            }
        });
        return super.onCreateOptionsMenu(menu);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        System.out.println("yala : "+item.getItemId());
        Intent intent;
        switch (item.getItemId()) {
            case R.id.add:
                //Toast.makeText(getApplicationContext(), "Ouverture d'ajout message", Toast.LENGTH_LONG).show();
                intent = new Intent(this, Activity_Search.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void lancerDemandes (View v){
        Intent intent = new Intent(this, Activity_list_utilisateur.class);
        startActivity(intent);
    }

    public void supprimerAmi(String pseudoAmi){
        //suppression d'un ami et maj de la liste des amis
    }
}
