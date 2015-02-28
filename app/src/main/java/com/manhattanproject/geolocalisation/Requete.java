package com.manhattanproject.geolocalisation;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by lulu on 25/02/2015.
 */

/*
* Une requete doit etre de la forme : tableau_de_string=["fichier.php","nom_du_champ1","valeur1","nom_du_champ2","valeur2", ...]
*
* Faire new requete(tableau_de_string);
* puis getResult();
 */
public class Requete {

    String result=null;

    public String getResult() {
        return result;
    }

    public Requete() {
    }

    public String LancerRequete(String[] params) {
        int i;
        InputStream is=null;
        String [] p=params;
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
        HttpResponse response;
        BufferedReader reader = null;
        for (i=1;i<params.length;i=i+2) {
            nameValuePairs.add(new BasicNameValuePair(params[i], params[i+1]));
        }
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://lmahot.hd.free.fr/" + p[0]);
        try {
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result=sb.toString();
        }catch ( IOException e ){ e.printStackTrace();}
        return result;
    }
}
