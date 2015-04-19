package com.manhattanproject.geolocalisation;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class Activity_list_ami extends Activity implements AdapterView.OnItemSelectedListener{
    private ExpandableListView expandableList;
    private AdapterListAmi adaptor;
    private ArrayList<Ami> listeAmi=new ArrayList<>();
    private DataBase db;
    private String categorySelected;
    public static ArrayList<Ami> listeA;
    Button notifCount;
    private Ami amiClicked;

    public void onNothingSelected(AdapterView<?> parent) {
        categorySelected=parent.getSelectedItem().toString();
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        categorySelected=(String)parent.getItemAtPosition(position);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_ami);
        expandableList = (ExpandableListView) findViewById(R.id.expandableListAmi);
        registerForContextMenu(expandableList);
        /*db = new DataBase(getApplicationContext(),"base de donne",null,4);
        listeAmi = db.recupAmiBD();*/
        //System.out.println(listeAmi.get(0));
        Utilisateur courant = new Utilisateur();
        courant.recup(getApplicationContext());
        listeAmi=recupereAmi(courant);
        adaptor = new AdapterListAmi(this, listeAmi);
        expandableList.setAdapter(adaptor);
        registerForContextMenu(expandableList);

    }

    public static ArrayList<Ami> recupereAmi(Utilisateur courant){
        //final String[] params={"selectUsersFriends.php","pseudo","4rrrrrr"};
        final String[] params={"selectUsersFriends.php","pseudo",courant.getPseudo()};
        listeA=new ArrayList<>();
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
                Ami a=new Ami(i,new LatLng(json_data.getDouble("positionx"),json_data.getDouble("positiony")), json_data.getString("pseudo"), json_data.getString("statut"));
                if(json_data.getDouble("etat")==0){
                    a.setConnect(false);
                }
                else{
                    a.setConnect(true);
                }
                listeA.add(i,a);
            }
        }catch(JSONException e){
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
        return listeA;
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
                intent = new Intent(getApplicationContext(), Activity_Search.class);
                startActivity(intent);
                return true;
            case R.id.demandes:
                intent = new Intent(getApplicationContext(), Activity_list_utilisateur.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void lancerDemandes (View v){
        Intent intent = new Intent(getApplicationContext(), Activity_list_utilisateur.class);
        startActivity(intent);
    }

    public void supprimerAmi(String pseudoAmi){
        //suppression d'un ami et maj de la liste des amis
        Utilisateur courant = new Utilisateur();
        courant.recup(getApplicationContext());
        int ami = -1;
        int user = -1;
        final String[] params={"selectUser.php","pseudo",pseudoAmi};
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
            JSONObject json_data = jArray.getJSONObject(0);
            ami=json_data.getInt("id");
        }catch(JSONException e){
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
        final String[] p={"selectUser.php","pseudo",courant.getPseudo()};
        r = new Requete();
        r.execute(p);
        try {
            r.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        response=r.getResult();
        try{
            JSONArray jArray = new JSONArray(response);
            System.out.println("Donnée de la réponse : "+jArray);
            JSONObject json_data = jArray.getJSONObject(0);
            user=json_data.getInt("id");
        }catch(JSONException e){
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
        System.out.println("ami : "+Integer.toString(ami)+"   user : "+Integer.toString(user));
        final String[] ps={"deleteFriend.php","iduser",Integer.toString(user),"idami",Integer.toString(ami)};
        r = new Requete();
        r.execute(ps);
        listeAmi=recupereAmi(courant);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        ExpandableListView.ExpandableListContextMenuInfo
                info=(ExpandableListView.ExpandableListContextMenuInfo)menuInfo;
        if(ExpandableListView.getPackedPositionType(info.packedPosition)
                == ExpandableListView.PACKED_POSITION_TYPE_GROUP){
            amiClicked=listeAmi.get((int)info.id);
            menu.setHeaderTitle("Options: "+amiClicked.getPseudo());
            String [] menuItems=getResources().getStringArray(R.array.menuAmiLongClick);
            for(int i=0; i<menuItems.length; i++){
                menu.add(Menu.NONE,i,i,menuItems[i]);
            } }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int menuItemIndex=item.getItemId();
        String [] menuItems=getResources().getStringArray(R.array.menuAmiLongClick);
        Intent intentParser;
        switch(menuItemIndex){
            case 0:  //Consulter son profil
                intentParser=new Intent(getApplicationContext(),Activity_profil_Utilisateur.class);
                intentParser.putExtra("BitmapImageAmi", amiClicked.getImage());
                intentParser.putExtra("pseudoAmi",amiClicked.getPseudo());
                intentParser.putExtra("idAmi",amiClicked.getId());
                intentParser.putExtra("latitudeAmi",amiClicked.getPosition().latitude);
                intentParser.putExtra("longitudeAmi",amiClicked.getPosition().longitude);
                intentParser.putExtra("connectStatus",amiClicked.isConnect());
                intentParser.putExtra("Statut",amiClicked.getStatut());
                startActivity(intentParser);
                break;
            case 1:  //Demander sa position
                String rid =null;
                String[] params = {"selectUser.php", "pseudo", amiClicked.getPseudo()};
                Requete r = new Requete();
                r.execute(params);
                try {
                    r.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                String response = r.getResult();
                try {
                    JSONArray jArray = new JSONArray(response);
                    System.out.println("Donnée de la réponse : " + jArray);
                    JSONObject json_data = jArray.getJSONObject(0);
                    rid = json_data.getString("rid");
                } catch (JSONException e) {
                    Log.e("log_tag", "Error parsing data " + e.toString());
                }
                String[] p = {"pushDemandePos.php", "rid", rid};
                r = new Requete();
                r.execute(p);
                break;
            case 2: //Me rendre à sa position partagé
                intentParser=new Intent(getApplicationContext(),MapDrawerActivityAmi.class);
                intentParser.putExtra("pseudoAmi",amiClicked.getPseudo());
                intentParser.putExtra("idAmi",amiClicked.getId());
                intentParser.putExtra("latitudeAmi",amiClicked.getPosition().latitude);
                intentParser.putExtra("longitudeAmi",amiClicked.getPosition().longitude);
                startActivity(intentParser);

                break;
            case 3: //Supprimer
                supprimerAmi(amiClicked.getPseudo());
                adaptor.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(),"Ami supprimé",Toast.LENGTH_LONG).show();
                break;
            default:

        }
        /*String menuItemName=menuItems[menuItemIndex];
        Toast.makeText(getApplicationContext(), menuItemName + "  " + "Clicked", Toast.LENGTH_LONG).show();*/
        adaptor = new AdapterListAmi(this, listeAmi);
        expandableList.setAdapter(adaptor);
        return true;
    }
}
