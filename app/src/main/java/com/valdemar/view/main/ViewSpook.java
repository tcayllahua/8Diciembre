package com.valdemar.view.main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.valdemar.R;
import com.valdemar.utilidades.sounds.BackgroundSoundService;
import com.valdemar.view.auth.AccessRelato;
import com.valdemar.view.main.chat.MensajeEnviar;
import com.valdemar.view.main.ui.Configuration;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewSpook extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private CircleImageView mMenu_profile_image;
    private ImageView mConfigurarcion;
    private TextView mMenu_profile_name;
    private TextView mMenu_profile_email;
    private AppBarConfiguration mAppBarConfiguration;
    private static final int PHOTO_SEND = 1;
    private static final int PHOTO_PERFIL = 2;
    private StorageReference storageReference;
    private FirebaseStorage storage;
    private ProgressDialog mProgresDialog;
    private FirebaseAuth mAuth;

    private Dialog MyDialog;

    private DatabaseReference mDatabaseVersionApp;


    private SharedPreferences prefs_sound = null;
    private SharedPreferences prefs_notificacion = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_spook);
        initView();
        initToolbar();
        initView1();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return true;
    }


    private void initNotificacion() {
        prefs_sound = getSharedPreferences("com.valdemar.spook", MODE_PRIVATE);
        prefs_notificacion = getSharedPreferences("com.valdemar.spook", MODE_PRIVATE);

 //       prefs_sound.edit().putBoolean("prefs_sound", true).commit();
 //      prefs_notificacion.edit().putBoolean("prefs_notificacion", true).commit();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
       // inflater.inflate(R.menu.view_spook, menu);



        storage = FirebaseStorage.getInstance();
        mProgresDialog= new ProgressDialog(ViewSpook.this);

        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.view_spook, menu);
        mConfigurarcion = findViewById(R.id.configurarcion);
        mConfigurarcion.setVisibility(View.GONE);
        mConfigurarcion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(ViewSpook.this, Configuration.class));
            }
        });

        mMenu_profile_image = (CircleImageView) findViewById(R.id.menu_profile_image);
        mMenu_profile_name = findViewById(R.id.menu_profile_name);
        mMenu_profile_email = findViewById(R.id.menu_profile_email);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null) {
            Uri photoUrl = user.getPhotoUrl();
            String name = user.getDisplayName();
            String email = user.getEmail();

            mMenu_profile_name.setText(name);
            if(user.getEmail() != null){
                mMenu_profile_email.setText(email);
            }

            Glide.with(ViewSpook.this)
                    .load(photoUrl)
                    .thumbnail(Glide.with(ViewSpook.this)
                            .load(R.drawable.item_placeholder))
                    .into(mMenu_profile_image);
        }

        mMenu_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user != null) {
                    // 1. Instantiate an <code><a href="/reference/android/app/AlertDialog.Builder.html">AlertDialog.Builder</a></code> with its constructor
                    AlertDialog.Builder makeDialog = new AlertDialog.Builder(ViewSpook.this);
                    makeDialog.setMessage("Si continuar editarás tu foto de perfil");
                    makeDialog.setTitle("Foto perfil");

                    makeDialog.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                            i.setType("image/jpeg");
                            i.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                            startActivityForResult(Intent.createChooser(i,"Selecciona una foto"),PHOTO_PERFIL);
                        }
                    });

                    makeDialog.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog ad = makeDialog.create();
                    ad.show();
                }
            }
        });

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void initView1() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send, R.id.nav_chat)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton setting_ = findViewById(R.id.setting_);
        setting_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                   //     .setAction("Action", null).show();

                ModalCheckSetting();


            }
        });
    }

    private void initView() {
        mAuth = FirebaseAuth.getInstance();
        validarActualizacion();
        initNotificacion();
        initSettingCheck();

    }

    private void initSettingCheck() {

        if(prefs_sound.getBoolean("prefs_sound", true)){
            Intent svc = new Intent(getApplicationContext(), BackgroundSoundService.class);
            startService(svc);
        }else{
            Intent svc = new Intent(getApplicationContext(), BackgroundSoundService.class);
            stopService(svc);
        }

        if(prefs_notificacion.getBoolean("prefs_notificacion", true)){
            FirebaseMessaging.getInstance().subscribeToTopic("Historias");
            FirebaseMessaging.getInstance().subscribeToTopic("chat");
        }else{
            FirebaseMessaging.getInstance().unsubscribeFromTopic("Historias");
            FirebaseMessaging.getInstance().unsubscribeFromTopic("chat");
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PHOTO_PERFIL && resultCode == RESULT_OK){
            mProgresDialog.setMessage("Actualizando perfil");
            mProgresDialog.setCancelable(false);
            mProgresDialog.show();


            final FirebaseUser user =  mAuth.getCurrentUser();


            Uri u = data.getData();
            storageReference = storage.getReference("foto_perfil");//imagenes_chat
            final StorageReference fotoReferencia = storageReference.child(u.getLastPathSegment());
            fotoReferencia.putFile(u).addOnSuccessListener(ViewSpook.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    fotoReferencia.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUrl = uri;
                            // Uri u = taskSnapshot.getDownloadUrl();
                            String fotoPerfilCadena = downloadUrl.toString();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(Uri.parse(fotoPerfilCadena))
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("TAG", "User profile updated.");
                                            }
                                        }
                                    });

                            Glide.with(ViewSpook.this)
                                    .load(fotoPerfilCadena)
                                    .thumbnail(Glide.with(ViewSpook.this)
                                            .load(R.drawable.item_placeholder))
                                    .into(mMenu_profile_image);

                            mProgresDialog.dismiss();
                        }
                    });

                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        initSettingCheck();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Intent svc = new Intent(getApplicationContext(), BackgroundSoundService.class);
        stopService(svc);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent svc = new Intent(getApplicationContext(), BackgroundSoundService.class);
        stopService(svc);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent svc = new Intent(getApplicationContext(), BackgroundSoundService.class);
        stopService(svc);
    }

    public void validarActualizacion(){

        mDatabaseVersionApp = FirebaseDatabase.getInstance().getReference().child("VersionApp");
        mDatabaseVersionApp.keepSynced(true);
        mDatabaseVersionApp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String appVersion = (String) dataSnapshot.child("version").getValue();
                String title = (String) dataSnapshot.child("title").getValue();
                String body = (String) dataSnapshot.child("body").getValue();

                Log.v("PackageInfo",""+appVersion);

                PackageInfo pInfo = null;

                try {
                    pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                String version = pInfo.versionName;
                int verCode = pInfo.versionCode;

                Log.v("PackageInfo",""+version);
                Log.v("PackageInfo",""+verCode);

                int verCodeActual = Integer.parseInt(appVersion);

                if(verCode < verCodeActual){
                    ModalCheckUpdate(title,body);
                    Log.v("PackageInfo",""+verCode+""+verCodeActual);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    public void ModalCheckUpdate(String title,String body){

        MyDialog = new Dialog(ViewSpook.this);
        MyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        MyDialog.setContentView(R.layout.activity_check_update_version_app);
        MyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView mModal_check_update_title = MyDialog.findViewById(R.id.modal_check_update_title);
        TextView mModal_check_update_body = MyDialog.findViewById(R.id.modal_check_update_body);

        mModal_check_update_title.setText(title);
        mModal_check_update_body.setText(body);

        Button btnModalActualizar = MyDialog.findViewById(R.id.modal_check_update_actualizar);
        Button btnModalCancel = MyDialog.findViewById(R.id.modal_check_update_later);

        btnModalActualizar.setEnabled(true);

        btnModalActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDialog.dismiss();
                Intent intent1 = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id="
                                + ViewSpook.this.getPackageName()));
                startActivity(intent1);
            }
        });

        btnModalCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDialog.hide();
            }
        });

        MyDialog.show();

    }



    public void ModalCheckSetting(){

        MyDialog = new Dialog(ViewSpook.this);
        MyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        MyDialog.setContentView(R.layout.activity_setting);
        MyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button btnModalCancel = MyDialog.findViewById(R.id.setting_cerrar);
        final ImageView sonido = MyDialog.findViewById(R.id.setting_sonido);
        final ImageView notificacion = MyDialog.findViewById(R.id.setting_notification);

        if(prefs_sound.getBoolean("prefs_sound", true)){
            sonido.setBackground(ContextCompat.getDrawable(ViewSpook.this, R.drawable.ic_volumen));
        }else{
            sonido.setBackground(ContextCompat.getDrawable(ViewSpook.this, R.drawable.ic_volumen_off));
        }

        if(prefs_notificacion.getBoolean("prefs_notificacion", true)){
            notificacion.setBackground(ContextCompat.getDrawable(ViewSpook.this, R.drawable.ic_notification));

        }else{
            notificacion.setBackground(ContextCompat.getDrawable(ViewSpook.this, R.drawable.ic_notification_off));
        }

        sonido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(prefs_sound.getBoolean("prefs_sound", true)){
                    prefs_sound.edit().putBoolean("prefs_sound", false).commit();
                    initSettingCheck();
                    sonido.setBackground(ContextCompat.getDrawable(ViewSpook.this, R.drawable.ic_volumen_off));

                }else{
                    prefs_sound.edit().putBoolean("prefs_sound", true).commit();
                    initSettingCheck();
                    sonido.setBackground(ContextCompat.getDrawable(ViewSpook.this, R.drawable.ic_volumen));
                }
            }
        });

        notificacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(prefs_notificacion.getBoolean("prefs_notificacion", true)){
                    prefs_notificacion.edit().putBoolean("prefs_notificacion", false).commit();
                    initSettingCheck();
                    notificacion.setBackground(ContextCompat.getDrawable(ViewSpook.this, R.drawable.ic_notification_off));

                }else{
                    prefs_notificacion.edit().putBoolean("prefs_notificacion", true).commit();
                    initSettingCheck();
                    notificacion.setBackground(ContextCompat.getDrawable(ViewSpook.this, R.drawable.ic_notification));
                }

            }
        });

        btnModalCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDialog.hide();
            }
        });

        MyDialog.show();

    }

}