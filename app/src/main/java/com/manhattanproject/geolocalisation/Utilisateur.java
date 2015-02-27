package com.manhattanproject.geolocalisation;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by vince_000 on 25/02/2015.
 */
public class Utilisateur {
    String identifiant;
    String nom;
    String prenom;
    String statut;
    LatLng position;

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(String identifiant) {
        this.identifiant = identifiant;
    }

    public Utilisateur() {

        this.identifiant = "";
        this.nom = "";
        this.statut = "";
        this.prenom = "";
        this.position = null;
    }

    public Utilisateur( String identifiant, String nom, String prenom, String statut, LatLng position) {

        this.identifiant = identifiant;
        this.nom = nom;
        this.statut = statut;
        this.prenom = prenom;
        this.position = position;
    }

    public Utilisateur(Utilisateur u) {

        this.identifiant = u.identifiant;
        this.nom = u.nom;
        this.statut = u.statut;
        this.prenom = u.prenom;
        this.position = u.position;
    }

    public String toString(){
        return new String("Utilisateur "+this.identifiant+" :\n"+this.prenom+" "+this.nom+"\n"+"statut : "+this.statut+"\n"+this.position);
    }

    public void save(Context c){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("identifiant", this.identifiant);
        editor.putString("nom", this.nom);
        editor.putString("prenom", this.prenom);
        editor.putString("statut",this.statut);
        editor.putFloat("latitude", (float)this.position.latitude);
        editor.putFloat("longitude",(float)this.position.longitude);
        editor.commit();
    }

    public void recup(Context c){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
        this.setIdentifiant(preferences.getString("identifiant",""));
        this.setNom(preferences.getString("nom", ""));
        this.setPrenom(preferences.getString("prenom", ""));
        this.setStatut(preferences.getString("statut", ""));
        this.setPosition(new LatLng(preferences.getFloat("latitude",0.f),preferences.getFloat("longitude",0.f)));
    }
}
