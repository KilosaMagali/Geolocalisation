package com.manhattanproject.geolocalisation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button profil_btn = (Button) findViewById(R.id.profil_button);
        Button register_btn = (Button) findViewById(R.id.register_button);
        Context c=getApplicationContext();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
        if(preferences.getInt("identifiant",-1)==-1) {
            profil_btn.setVisibility(View.GONE);
            register_btn.setVisibility(View.VISIBLE);
        }
        else{
            profil_btn.setVisibility(View.VISIBLE);
            register_btn.setVisibility(View.GONE);
        }
        Intent intent = new Intent(this, Timer.class);
        startActivity(intent);
    }

    public void onResume(){
        super.onResume();
        Context c=getApplicationContext();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
        Button profil_btn = (Button) findViewById(R.id.profil_button);
        if(preferences.getInt("identifiant",-1)==-1) {
            profil_btn.setVisibility(View.GONE);
        }
        else{
            Button register_btn = (Button) findViewById(R.id.register_button);
            register_btn.setVisibility(View.GONE);
            profil_btn.setVisibility(View.VISIBLE);
        }
    }

    public void openMap(View view){
        Intent intent = new Intent(this, Map.class);
        startActivity(intent);
    }

    public void openProfil(View view){
        Intent intent = new Intent(this, Activity_profil.class);
        startActivity(intent);
    }

    public void openRegister(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

}
