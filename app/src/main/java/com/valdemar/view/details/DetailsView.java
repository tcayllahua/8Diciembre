package com.valdemar.view.details;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.valdemar.R;
import com.valdemar.view.main.MainActivity;
import com.valdemar.view.main.ViewSpook;

import java.util.Locale;

public class DetailsView extends AppCompatActivity {


    private String mPost_key = null;
    private DatabaseReference mDatabase,mMensajeShare;

    private ProgressDialog mProgresDialog;
    private TextToSpeech tts;
    private SharedPreferences prefs = null;
    private InterstitialAd mInterstitialAd;
    private Dialog MyDialog;
    private AdView mAdView;

    TextView mPostTitleDetails;

    ImageView mImage_paralax;

    Button mPostRemoveDetails;
    //Favorite
    FloatingActionButton mFav_favorite;

    //@BindView(R.id.fav_favorite)

    private FirebaseAuth mAuth;
    //favorite variables
    private boolean mProcessLike;
    private DatabaseReference mDatabaseLike,mDatabasePortada;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_view);
        initView();
    }

    private void initView() {

        mPostTitleDetails = (TextView) findViewById(R.id.postTitleDetails);
        mImage_paralax = (ImageView) findViewById(R.id.image_paralax);
        mPostRemoveDetails = (Button) findViewById(R.id.postRemoveDetails);

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        initWebView();
        MyDialog = new Dialog(DetailsView.this);
        mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
        mDatabasePortada = FirebaseDatabase.getInstance().getReference().child("Portada");
        mDatabaseLike.keepSynced(true);
        mDatabasePortada.keepSynced(true);

        mPost_key = getIntent().getExtras().getString("blog_id");
        mAuth = FirebaseAuth.getInstance();

        initWebView();
    }


    private void initWebView() {
        MobileAds.initialize(this,"ca-app-pub-2031757066481790/8039227225");

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-2031757066481790/8039227225");

        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        if (mInterstitialAd.isLoaded()) {
            // mInterstitialAd.show();
            Log.v("Anuncio","click");
        }

        // mAdView = (AdView) findViewById(R.id.adView);
        // AdRequest adRequest = new AdRequest.Builder().build();
        // mAdView.loadAd(adRequest);

        NestedScrollView nsv = (NestedScrollView) findViewById(R.id.scroll);

        //tts = new TextToSpeech(this, this);

        prefs = getSharedPreferences("relato.app.dems.com.relato.beta", MODE_PRIVATE);

        mProgresDialog= new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Historias");
        mPost_key = getIntent().getExtras().getString("blog_id");


        mDatabase.child(mPost_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String post_title = (String) dataSnapshot.child("title").getValue();
                String post_desc = (String) dataSnapshot.child("desc").getValue();
                String post_image = (String) dataSnapshot.child("image").getValue();
                String post_IdMiLectura = (String) dataSnapshot.child("IdMiLectura").getValue();
                // Toast.makeText(BlogSingleActicity.this,""+post_title+post_desc+post_image,Toast.LENGTH_SHORT).show();

                String textoCentradoDesc = post_desc;

                String text_string_center = "<html><body style='text-align:justify;'>"+textoCentradoDesc+"<body><html>";

                //Log.v("asdasvtvrt",text_string_center);
                /*****************************************/

                String justifyTag = "<html><body style='text-align:justify;background:black !important;color:#c1c0c0;font-size:15px;'>%s</body></html>";

                String dataString = String.format(Locale.US, justifyTag, text_string_center);
                WebView webViewDetail = (WebView) findViewById(R.id.webViewDetail);
                webViewDetail.loadDataWithBaseURL("", dataString, "text/html", "UTF-8", "");

                /*****************************************/


                mPostTitleDetails.setText(post_title);
                //mPostDescDetails.setText(post_desc);

                //validarVisibilidadAudio();
                Glide.with(getApplicationContext())
                        .load(post_image)
                        .into(mImage_paralax);

            //    Log.v("post_all",""+post_image+post_title+post_desc);
                showToolbar(post_title,true);

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null){
                    String userId = user.getUid();

                    Log.v("okey","Logueado");
                    Log.v("okey",""+mAuth.getCurrentUser().getUid());
                    Log.v("okey",""+post_IdMiLectura);
                    Log.v("okey",""+userId);
                    if(mAuth.getCurrentUser().getUid().equals(post_IdMiLectura)){
                        Log.v("okey","asd"+mAuth.getCurrentUser().getUid());
                        mPostRemoveDetails.setVisibility(View.VISIBLE);
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mPostRemoveDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgresDialog.setMessage("Removiendo Historia");
                mProgresDialog.show();
                mDatabase.child(mPost_key).removeValue();
                startActivity(new Intent(getApplicationContext(), ViewSpook.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                mProgresDialog.dismiss();
                finish();

            }
        });
    }
/*
    private void initFavorite() {

        mFav_favorite = findViewById(R.id.fav_favorite);
        mFav_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProcessLike = true;
                Log.v("TAG_LIKE","LINE click");

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


                if(user != null){
                    mDatabase.child(mPost_key).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final String post_title = (String) dataSnapshot.child("title").getValue();
                            final String post_image = (String) dataSnapshot.child("image").getValue();
                            final String post_category = (String) dataSnapshot.child("category").getValue();
                            final String post_author = (String) dataSnapshot.child("author").getValue();
                            final String post_desc = (String) dataSnapshot.child("desc").getValue();

                            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (mProcessLike){

                                        if(dataSnapshot.child(mAuth.getCurrentUser().getUid()).hasChild(mPost_key)){
                                            Log.v("TAG_LIKE","LINE NO");
                                            mDatabaseLike.child(mAuth.getCurrentUser().getUid()).child(mPost_key).removeValue();
                                            showSnackBar("Eliminado de favoritos");


                                            mFav_favorite.setImageResource(R.drawable.ic_star_half);
                                            mProcessLike = false;
                                        }else{
                                            //mDatabaseLike.child(mPost_key).child(mAuth.getCurrentUser().getUid()).setValue("ramdom like");
                                            mDatabaseLike.child(mAuth.getCurrentUser().getUid()).child(mPost_key).child("title").setValue(post_title);
                                            mDatabaseLike.child(mAuth.getCurrentUser().getUid()).child(mPost_key).child("image").setValue(post_image);
                                            mDatabaseLike.child(mAuth.getCurrentUser().getUid()).child(mPost_key).child("author").setValue(post_author);
                                            mDatabaseLike.child(mAuth.getCurrentUser().getUid()).child(mPost_key).child("category").setValue(post_category);


                                            Log.v("TAG_LIKE","LINE ramdom");
                                            mFav_favorite.setImageResource(R.drawable.ic_star);
                                            mProcessLike = false;
                                            showSnackBar("Agregado a favoritos");
                                        }



                                        if(dataSnapshot.child(mPost_key).hasChild(mAuth.getCurrentUser().getUid())){
                                            showSnackBar("Dislike");
                                        }



                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.v("TAG_LIKE","LINE onCancelled");

                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }else{
                    showSnackBar("Necesitas Iniciar Sesión");
                }


            }
        });

        mDatabaseLike.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null){
                    if(mAuth.getCurrentUser().getUid() != null){
                        if(dataSnapshot.child(mAuth.getCurrentUser().getUid()).hasChild(mPost_key)){
                            mFav_favorite.setImageResource(R.drawable.ic_star);
                            Log.v("TAG_LIKE","Favorito");

                        }else{
                            mFav_favorite.setImageResource(R.drawable.ic_star_half);
                            Log.v("TAG_LIKE","no Favorito");

                        }
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
*/
    public void showToolbar(String tittle, boolean upButton) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(tittle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
    }

    private void showSnackBar(String msg) {
        Snackbar
                .make(findViewById(R.id.coordinator), msg, Snackbar.LENGTH_LONG)
                .show();
    }

}
