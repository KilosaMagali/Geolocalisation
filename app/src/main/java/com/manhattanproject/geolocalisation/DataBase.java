package com.manhattanproject.geolocalisation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by vince_000 on 25/02/2015.
 */

public class DataBase extends SQLiteOpenHelper {
    public static final String LIEU_ID = "id";
    public static final String LIEU_DESIGNATION = "nom";
    public static final String LIEU_DESCRIPTION = "description";
    public static final String LIEU_CATEGORIE = "categorie";
    public static final String LIEU_PARTAGE = "partage";
    public static final String LIEU_LATITUDE = "latitude";
    public static final String LIEU_LONGITUDE = "longitude";

    public static final String AMI_ID = "id";
    public static final String AMI_PSEUDO = "pseudo";
    public static final String AMI_STATUT = "statut";
    public static final String AMI_LATITUDE = "latitude";
    public static final String AMI_LONGITUDE = "longitude";

    public static final String LIEU_TABLE_NAME = "Lieu";
    public static final String LIEU_TABLE_CREATE =
            "CREATE TABLE " + LIEU_TABLE_NAME + " (" +
                    LIEU_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    LIEU_DESIGNATION + " TEXT, " +
                    LIEU_DESCRIPTION + " TEXT, " +
                    LIEU_CATEGORIE + " TEXT, " +
                    LIEU_LATITUDE + " REAL, " +
                    LIEU_LONGITUDE + " REAL, " +
                    LIEU_PARTAGE + " INTEGER);";

    public static final String AMI_TABLE_NAME = "Ami";
    public static final String AMI_TABLE_CREATE =
            "CREATE TABLE " + AMI_TABLE_NAME + " (" +
                    AMI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    AMI_PSEUDO + " TEXT, " +
                    AMI_STATUT + " TEXT, " +
                    AMI_LATITUDE + " REAL, " +
                    AMI_LONGITUDE + " REAL);";

    public DataBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LIEU_TABLE_CREATE);
        db.execSQL(AMI_TABLE_CREATE);
    }

    public static final String LIEU_TABLE_DROP = "DROP TABLE IF EXISTS " + LIEU_TABLE_NAME + ";";
    public static final String AMI_TABLE_DROP = "DROP TABLE IF EXISTS " + AMI_TABLE_NAME + ";";

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(LIEU_TABLE_DROP);
        db.execSQL(AMI_TABLE_DROP);
        onCreate(db);
    }
    /* GESTION GENERALE BD */

    /* Return true si l'ami a (comparaison uniquement selon l'identifiant) existe dans la BD, false sinon */
    public boolean existBD(Ami a){
        SQLiteDatabase bd = this.getWritableDatabase();
        Cursor c = bd.rawQuery("select " + "*" + " from " + AMI_TABLE_NAME + " where id = ?", new String[] {String.valueOf(a.getId())});
        boolean res = c.moveToFirst();
        c.close();
        return res;
    }

    /* Return true si le lieu l (comparaison uniquement selon l'identifiant) existe dans la BD, false sinon */
    public boolean existBD(Lieu l){
        SQLiteDatabase bd = this.getWritableDatabase();
        Cursor c = bd.rawQuery("select " + "*" + " from " + LIEU_TABLE_NAME + " where id = ?", new String[] {String.valueOf(l.getId())});
        boolean res = c.moveToFirst();
        c.close();
        return res;
    }

    public ArrayList<Lieu> recupLieuBD(){
        ArrayList<Lieu> res = new ArrayList();
        Lieu l;
        SQLiteDatabase bd = this.getWritableDatabase();
        Cursor c = bd.rawQuery("select " + "*" + " from " + LIEU_TABLE_NAME,null);
        while (c.moveToNext()) {
            l = new Lieu(c.getLong(0),Categorie_lieu.valueOf(c.getString(3)),c.getString(1),c.getString(2),c.getInt(6) == 1,new LatLng(c.getDouble(4),c.getDouble(5)));
            res.add(l);
        }
        c.close();
        return res;
    }

    public ArrayList<Ami> recupAmiBD(){
        ArrayList<Ami> res = new ArrayList();
        Ami a;
        SQLiteDatabase bd = this.getWritableDatabase();
        Cursor c = bd.rawQuery("select " + "*" + " from " + AMI_TABLE_NAME,null);
        while (c.moveToNext()) {
            a = new Ami(c.getLong(0),new LatLng(c.getDouble(3),c.getDouble(4)),c.getString(1),c.getString(2));
            res.add(a);
        }
        c.close();
        return res;
    }

    /* GESTION DES LIEUS DANS LA BD */

    /* Renvoi numero ligne ajouté si OK, sinon -1
    *  ajoute l'id du lieu */
    public long ajoutLieu(Lieu l){
        SQLiteDatabase bd = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(LIEU_DESIGNATION, l.getDesignation());
        value.put(LIEU_DESCRIPTION, l.getDescription());
        value.put(LIEU_CATEGORIE, l.getCategorie().toString());
        value.put(LIEU_PARTAGE, (l.isPartage()) ? 1 : 0);
        value.put(LIEU_LATITUDE, l.getPosition().latitude);
        value.put(LIEU_LONGITUDE, l.getPosition().longitude);
        l.setId(bd.insert(LIEU_TABLE_NAME, null, value));
        return l.getId();
    }

    /* Return le nombre de ligne supprimées */
    public long supprLieu(Lieu l){
        SQLiteDatabase bd = this.getWritableDatabase();
        return bd.delete(LIEU_TABLE_NAME, LIEU_ID + " = ?", new String[] {String.valueOf(l.getId())});
    }

    /* Return le nombre de ligne modifiées
     * Dans notre cas l'update se fait lieu par lieu, et met à jour un lieu entierement selon le lieu en entrée
      * ATTENTION : ne jamais modifier l'id!!*/
    public long updateLieu(Lieu l){
        SQLiteDatabase bd = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(LIEU_DESIGNATION, l.getDesignation());
        value.put(LIEU_DESCRIPTION, l.getDescription());
        value.put(LIEU_CATEGORIE, l.getCategorie().toString());
        value.put(LIEU_PARTAGE, l.isPartage());
        value.put(LIEU_LATITUDE, l.getPosition().latitude);
        value.put(LIEU_LONGITUDE, l.getPosition().longitude);
        return bd.update(LIEU_TABLE_NAME, value, LIEU_ID  + " = ?", new String[] {String.valueOf(l.getId())});
    }

    /* GESTION DES AMIS DANS LA BD */

    /* Renvoi numero ligne ajouté si OK, sinon -1
    *  ajoute l'id de l'ami */
    public long ajoutAmi(Ami a){
        SQLiteDatabase bd = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(AMI_PSEUDO, a.getPseudo());
        value.put(AMI_STATUT, a.getStatut());
        value.put(AMI_LATITUDE, a.getPosition().latitude);
        value.put(AMI_LONGITUDE, a.getPosition().longitude);
        a.setId(bd.insert(AMI_TABLE_NAME, null, value));
        return a.getId();
    }

    /* Return le nombre de ligne supprimées */
    public long supprAmi(Ami a){
        SQLiteDatabase bd = this.getWritableDatabase();
        return bd.delete(AMI_TABLE_NAME, AMI_ID + " = ?", new String[] {String.valueOf(a.getId())});
    }

    /* Return le nombre de ligne modifiées
     * Dans notre cas l'update se fait ami par ami, et met à jour un ami entierement selon l'ami en entrée
      * ATTENTION : ne jamais modifier l'id!!*/
    public long updateAmi(Ami a){
        SQLiteDatabase bd = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(AMI_PSEUDO, a.getPseudo());
        value.put(AMI_STATUT, a.getStatut());
        value.put(AMI_LATITUDE, a.getPosition().latitude);
        value.put(AMI_LONGITUDE, a.getPosition().longitude);
        return bd.update(AMI_TABLE_NAME, value, AMI_ID  + " = ?", new String[] {String.valueOf(a.getId())});
    }
}



