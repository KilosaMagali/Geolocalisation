package com.manhattanproject.geolocalisation;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.File;
import java.io.IOException;
import java.util.Timer;


public class Activity_profil extends Activity implements AdapterView.OnItemSelectedListener{
    Button boutonModif;
    Button boutonApp,modif,cancel;
    TextView pseudo;
    ImageButton imagebtn;
    Button mdp;
    Dialog modifMdp;
    EditText statut,oldMdp,newMdp;
    Bitmap imageChange;
    String dureeSelected;
    CheckBox partagePos;
    private Spinner dureeCategory;
    private ArrayAdapter<CharSequence> adapterDureeCategories;
    Timer diffusion = new Timer("toto",true);
    Utilisateur user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        user = new Utilisateur();
        boutonModif = (Button)findViewById(R.id.btnmod);
        boutonApp = (Button)findViewById(R.id.btnapp);
        pseudo = (TextView)findViewById(R.id.pseudo);
        mdp = (Button)findViewById(R.id.mdp);
        statut = (EditText)findViewById(R.id.Statut);
        dureeCategory = (Spinner)findViewById(R.id.SpinnerDuree);
        partagePos = (CheckBox)findViewById(R.id.checkBoxPartagePos);
        imagebtn = (ImageButton)findViewById(R.id.imageButton);

        user.recup(getApplicationContext());
        diffusion.purge();
        if(user.getPartagePos()) {
            diffusion.schedule(new PartagePosition(getApplicationContext()), (user.getDuree() * 60 * 1000), (user.getDuree() * 60 * 1000));
        }
        Switch on = (Switch)findViewById(R.id.OnOff);
        on.setChecked(user.onligne);
        if(!user.getPseudo().equals(""))
            pseudo.setText(user.getPseudo());
        if(!user.getMdp().equals("")) {
            String code = "";
            for(int i = 0;i < user.getMdp().length();++i)
                code = code+"*";
            mdp.setText(code);
        }
        if(!user.getStatut().equals(""))
            statut.setText(user.getStatut());
        if(user.getPartagePos())
            partagePos.setChecked(user.getPartagePos());
        populateCategoryCheckBox();
        imagebtn.setImageBitmap(user.getImage());

        partagePos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if(isChecked) {
                    dureeCategory.setEnabled(true);
                }
                else {
                    dureeCategory.setEnabled(false);
                }
            }
        });

        boutonApp.setEnabled(false);
        statut.setEnabled(false);
        dureeCategory.setEnabled(false);
        partagePos.setEnabled(false);
        imagebtn.setEnabled(false);
    }

    public int dureeToInd(int duree){
        switch(duree){
            case 1:
                return 0;
            case 5:
                return 1;
            case 10:
                return 2;
            case 15:
                return 3;
            case 30:
                return 4;
            case 60:
                return 5;
            case 240:
                return 6;
            default:
                return 0;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_profil, menu);
        return true;
    }



    public void populateCategoryCheckBox(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
// Create an ArrayAdapter using the string array and a default spinner layout
        adapterDureeCategories = ArrayAdapter.createFromResource(this,
                R.array.DureeCategory, android.R.layout.simple_spinner_dropdown_item);
// Specify the layout to use when the list of choices appears
        adapterDureeCategories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        dureeCategory.setAdapter(adapterDureeCategories);
        dureeCategory.setSelection(dureeToInd(preferences.getInt("duree", 1)));
        dureeCategory.setOnItemSelectedListener(this);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        dureeSelected=(String)parent.getItemAtPosition(position);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void modif(View view){
        if((boutonModif.getText()).equals("Modifier")) {
            boutonApp.setEnabled(true);
            statut.setEnabled(true);
            partagePos.setEnabled(true);
            if (partagePos.isChecked())
                dureeCategory.setEnabled(true);
            boutonModif.setText("Annuler");
            imagebtn.setEnabled(true);
        }
        else {
            user.recup(getApplicationContext());
            imagebtn.setImageBitmap(Bitmap.createScaledBitmap(user.getImage(), imagebtn.getWidth(), imagebtn.getHeight(), false));
            imageChange = null;
            statut.setText(user.getStatut());
            partagePos.setChecked(user.getPartagePos());
            boutonApp.setEnabled(false);
            statut.setEnabled(false);
            dureeCategory.setEnabled(false);
            partagePos.setEnabled(false);
            boutonModif.setText("Modifier");
            imagebtn.setEnabled(false);
            populateCategoryCheckBox();
        }
    }

    public void online(View view){
        Switch on = (Switch)findViewById(R.id.OnOff);
        Utilisateur user = new Utilisateur();
        user.recup(getApplicationContext());
        int boolbyint;
        if(on.isChecked()) {
            user.onligne = true;
            user.save(getApplicationContext());
            Toast.makeText(getApplicationContext(), "Vous êtes maintenant en ligne", Toast.LENGTH_LONG).show();
            boolbyint=1;
        }
        else{
            user.onligne = false;
            user.save(getApplicationContext());
            Toast.makeText(getApplicationContext(), "Vous êtes maintenant hors ligne", Toast.LENGTH_LONG).show();
            boolbyint=0;
        }
        final String[] params={"updateEtat.php","pseudo",user.getPseudo(),"etat",Integer.toString(boolbyint)};
        Requete r = new Requete();
        r.execute(params);
    }

    public void appliquerModif(View view){
        user.setStatut(statut.getText().toString());
        user.setPartagePos(partagePos.isChecked());
        if(user.getPartagePos()) {
            user.setDuree(Integer.parseInt(dureeSelected));
        }
        else
            user.setDuree(-1);
        diffusion.purge();
        if(user.getPartagePos()) {
            diffusion.schedule(new PartagePosition(getApplicationContext()), (user.getDuree() * 60 * 1000), (user.getDuree() * 60 * 1000));
        }
        if(imageChange != null)
            user.setImage(imageChange);
        user.save(getApplicationContext());
        boutonApp.setEnabled(false);
        statut.setEnabled(false);
        dureeCategory.setEnabled(false);
        partagePos.setEnabled(false);
        boutonModif.setText("Modifier");
        imagebtn.setEnabled(false);
        new registrationIdSetter(getApplicationContext()).execute();
    }

    public class registrationIdSetter extends AsyncTask<String,Void,Boolean> {

        Context c;

        public registrationIdSetter(Context c) {
            this.c = c;
        }

        @Override
        protected Boolean doInBackground(String... p) {
            GoogleCloudMessaging gcm = null;
            Context context = getApplicationContext();
            String SENDER_ID = "590360343154";
            String regid = null;
            int i;
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(context);
            }
            try {
                regid = gcm.register(SENDER_ID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            final String[] params = {"updateUser.php", "pseudo", user.getPseudo(), "mdp", user.getMdp(), "rid", regid, "etat", Integer.toString(0), "statut", user.getStatut()};
            Requete r = new Requete();
            r.execute(params);
            return true;
        }
    }

    public void selectImage(View view){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        photoPickerIntent.setType("image/*");
        photoPickerIntent.putExtra("crop", "true");
        photoPickerIntent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
        photoPickerIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(photoPickerIntent, 1);
    }




    private Uri getTempUri() {
        return Uri.fromFile(getTempFile());
    }

    private File getTempFile() {
        if (isSDCARDMounted()) {

            File f = new File(Environment.getExternalStorageDirectory(),"temporary_holder.jpg");
            try {
                f.createNewFile();
            } catch (IOException e) {

            }
            return f;
        } else {
            return null;
        }
    }

    private boolean isSDCARDMounted(){
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED))
            return true;
        return false;
    }

    public void changeMdp(View view){
        modifMdp=new Dialog(this,android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth);
        modifMdp.requestWindowFeature(Window.FEATURE_NO_TITLE);
        modifMdp.setCancelable(true);
        modifMdp.setContentView(R.layout.change_mdp);
        oldMdp=(EditText)modifMdp.findViewById(R.id.oldMdp);
        newMdp=(EditText)modifMdp.findViewById(R.id.newMdp);
        modif=(Button)modifMdp.findViewById(R.id.btnModif);
        cancel=(Button)modifMdp.findViewById(R.id.btnAnnule);

        modif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ancien = oldMdp.getText().toString();
                String nouveau = newMdp.getText().toString();
                Utilisateur user = new Utilisateur();
                user.recup(getApplicationContext());
                //Si le mot de passe est correct
                if (ancien.equals(user.getMdp())) {
                    user.setMdp(nouveau);
                    user.save(getApplicationContext());
                    String code = "";
                    for(int i = 0;i < nouveau.length();++i)
                        code = code+"*";
                    mdp.setText(code);
                    Toast.makeText(getApplicationContext(),
                            "Mot de passe correct\nChangement reussi",
                            Toast.LENGTH_LONG).show();
                    final String[] params={"updatemdp.php","pseudo",user.getPseudo(),"mdp",nouveau};
                    Requete r = new Requete();
                    r.execute(params);
                } else {
                    Toast.makeText(getApplicationContext(), "Mot de passe incorrect", Toast.LENGTH_LONG).show();
                }
                modifMdp.dismiss();
            }
        });
        cancel=(Button)modifMdp.findViewById(R.id.btnAnnule);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                modifMdp.dismiss();
            }
        });
        modifMdp.show();
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    if (imageReturnedIntent != null) {


                        File tempFile = getTempFile();

                        String filePath = Environment.getExternalStorageDirectory()
                                + "/temporary_holder.jpg";
                        System.out.println("path " + filePath);


                        Bitmap selectedImage = BitmapFactory.decodeFile(filePath);
                        imagebtn = (ImageButton) findViewById(R.id.imageButton);
                        imagebtn.setImageBitmap(Bitmap.createScaledBitmap(selectedImage,imagebtn.getWidth(),imagebtn.getHeight(), false));
                        imageChange = Bitmap.createScaledBitmap(selectedImage,imagebtn.getWidth(),imagebtn.getHeight(), false);
                        //maj bdd externe
                        final Bitmap[] params={selectedImage};
                        uploadImg up =new uploadImg(user.getPseudo());
                        up.execute(params);
                        System.out.println("okok");
                    }
                }
        }
    }
}
