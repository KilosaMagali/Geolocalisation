package com.manhattanproject.geolocalisation;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ExpandableListView;

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
}
