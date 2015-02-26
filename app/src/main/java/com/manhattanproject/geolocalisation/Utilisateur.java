package com.manhattanproject.geolocalisation;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by vince_000 on 25/02/2015.
 */
public class Utilisateur {
    Bitmap image;
    String identifiant;
    String nom;
    String prenom;
    String Statue;
    LatLng position;

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public String getStatue() {
        return Statue;
    }

    public void setStatue(String statue) {
        Statue = statue;
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

    public Utilisateur(Bitmap image, String identifiant, String nom, String statue, String prenom, LatLng position) {

        this.image = image;
        this.identifiant = identifiant;
        this.nom = nom;
        Statue = statue;
        this.prenom = prenom;
        this.position = position;
    }

    public void save(Context c) {


    }
}
