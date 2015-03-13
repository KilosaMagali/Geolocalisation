package com.manhattanproject.geolocalisation;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class Activity_Search extends ActionBarActivity {
    private ExpandableListView expandableList;
    private AdapterListUtilisateur adaptor;
    private ArrayList<Utilisateur> listeUser=new ArrayList<>();
    private EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__search);
        expandableList = (ExpandableListView) findViewById(R.id.expandableListUtilisateur);
        et=(EditText)findViewById(R.id.editText);
        et.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listeUser.clear();
                if(count!=0)
                    recherche(s.toString());
            }
        });
        adaptor = new AdapterListUtilisateur(this, listeUser);
        expandableList.setAdapter(adaptor);
    }
    protected void recherche(String pseudo){
        final String[] params={"selectUsersStartingBy.php","pseudo",pseudo};
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
                listeUser.add(i,new Utilisateur(i, json_data.getString("pseudo"), null, json_data.getString("statut"), new LatLng(json_data.getDouble("positionx"),json_data.getDouble("positiony"))));            }
        }catch(JSONException e){
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
        expandableList.deferNotifyDataSetChanged();

    }
}
