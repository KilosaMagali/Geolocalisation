package com.manhattanproject.geolocalisation;

import android.os.AsyncTask;
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


public class Activity_list_utilisateur extends ActionBarActivity {
    private ExpandableListView expandableList;
    private AdapterListUtilisateur adaptor;
    private ArrayList<Utilisateur> listeUser=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_utilisateur);
        select();

    }

    public Void select(){
        Utilisateur courant = new Utilisateur();
        courant.recup(getApplicationContext());
        final String[] p={"selectAllUsers.php","pseudo","4rrrrrr"};
        Requete r=new Requete();
        r.execute(p);
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
            for(int i=0;i<jArray.length();i++){
                JSONObject json_data = jArray.getJSONObject(i);
                listeUser.add(i,new Utilisateur(i, json_data.getString("pseudo"), null, null, null));
            }
        }catch(JSONException e){
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
        expandableList = (ExpandableListView) findViewById(R.id.expandableListAmi);
        adaptor = new AdapterListUtilisateur(getApplicationContext(), listeUser);
        expandableList.setAdapter(adaptor);
        return null;
    }

}
