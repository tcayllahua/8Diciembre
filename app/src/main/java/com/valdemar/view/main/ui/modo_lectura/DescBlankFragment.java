package com.valdemar.view.main.ui.modo_lectura;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
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
import com.valdemar.SplashActivity;
import com.valdemar.view.auth.AccessRelato;
import com.valdemar.view.main.ViewSpook;

import java.util.Locale;

public class DescBlankFragment extends Fragment {
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    private String mPost_key = null;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseLike;
    private TextView mPostTitleDetails;
    private ImageView mImage_paralax;
    private FloatingActionButton mFav_favorite;
    private boolean mProcessLike;

    private FirebaseAuth mAuth;

    private ProgressDialog mProgresDialog;
    private Button mPostRemoveDetails;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.desc_blank_fragment, container, false);
       // Toast.makeText(getActivity().getApplicationContext(),"OTRO FRAGMENT", Toast.LENGTH_SHORT).show();
        mAuth = FirebaseAuth.getInstance();

        initAnuncio(root);
        initView(root);
        initFavorite(root);
        return root;

    }



    private void initAnuncio(View root) {
        mInterstitialAd = new InterstitialAd(getActivity().getApplicationContext());
        mInterstitialAd.setAdUnitId("ca-app-pub-2031757066481790/8039227225");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mAdView = (AdView) root.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    private void initView(View root) {

        Bundle datosRecuperados = getArguments();
        if (datosRecuperados == null) {
            // No hay datos, manejar excepción
            return;
        }
        mPost_key = datosRecuperados.getString("blog_id");

        mPostTitleDetails = (TextView) root.findViewById(R.id.postTitleDetails);
        mImage_paralax = (ImageView) root.findViewById(R.id.image_paralax);

        initWebView(root);
        //initRemove(mPost_key,root);
    }

    private void initRemove(final String mPost_key, View root) {
        mPostRemoveDetails = (Button) root.findViewById(R.id.postRemoveDetails);
        //mPostRemoveDetails.setVisibility(View.VISIBLE);
        mProgresDialog= new ProgressDialog(getActivity());

        mPostRemoveDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgresDialog.setMessage("Removiendo Historia");
                mProgresDialog.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDatabase.child(mPost_key).removeValue();
                        startActivity(new Intent(getActivity().getApplicationContext(), ViewSpook.class));
                        mProgresDialog.dismiss();
                    }
                },500);

            }
        });
    }

    private void initWebView(final View root) {

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Historias");
        mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
        mDatabaseLike.keepSynced(true);
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
                WebView webViewDetail = (WebView) root.findViewById(R.id.webViewDetail);
                webViewDetail.loadDataWithBaseURL("", dataString, "text/html", "UTF-8", "");

                /*****************************************/


                mPostTitleDetails.setText(post_title);
                //mPostDescDetails.setText(post_desc);

                //validarVisibilidadAudio();
                Glide.with(getActivity().getApplicationContext())
                        .load(post_image)
                        .into(mImage_paralax);


                /*FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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

                }*/

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void initFavorite(final View root) {
        mFav_favorite =  root.findViewById(R.id.fav_favorite);
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
                            // Toast.makeText(BlogSingleActicity.this,""+post_title+post_desc+post_image,Toast.LENGTH_SHORT).show();

                            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (mProcessLike){

                                        if(dataSnapshot.child(mAuth.getCurrentUser().getUid()).hasChild(mPost_key)){
                                            Log.v("TAG_LIKE","LINE NO");
                                            //  mDatabaseLike.child(mPost_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                            mDatabaseLike.child(mAuth.getCurrentUser().getUid()).child(mPost_key).removeValue();
                                            showSnackBar("Eliminado de favoritos",root);


                                            mFav_favorite.setImageResource(R.drawable.ic_star_half);
                                            mProcessLike = false;
                                        }else{
                                            //mDatabaseLike.child(mPost_key).child(mAuth.getCurrentUser().getUid()).setValue("ramdom like");
                                            mDatabaseLike.child(mAuth.getCurrentUser().getUid()).child(mPost_key).child("title").setValue(post_title);
                                            mDatabaseLike.child(mAuth.getCurrentUser().getUid()).child(mPost_key).child("image").setValue(post_image);
                                            mDatabaseLike.child(mAuth.getCurrentUser().getUid()).child(mPost_key).child("author").setValue(post_author);
                                            mDatabaseLike.child(mAuth.getCurrentUser().getUid()).child(mPost_key).child("category").setValue(post_category);

/*
                                            mDatabasePortada.child("title").setValue(post_title);
                                            mDatabasePortada.child("images").setValue(post_image);
                                            mDatabasePortada.child("desc").setValue(post_desc);

*/


                                            Log.v("TAG_LIKE","LINE ramdom");
                                            mFav_favorite.setImageResource(R.drawable.ic_star);
                                            mProcessLike = false;
                                            showSnackBar("Agregado a favoritos", root);
                                        }



                                        if(dataSnapshot.child(mPost_key).hasChild(mAuth.getCurrentUser().getUid())){
                                            showSnackBar("Dislike", root);
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
                    showSnackBar("Necesitas Iniciar Sesión", root);
                   // popUsuarioSinSesion();
                }


            }
        });

        mDatabaseLike.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null){
                    //primero la historia el id
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




    private void showSnackBar(String msg, View root) {
        Snackbar
                .make(root.findViewById(R.id.coordinator), msg, Snackbar.LENGTH_LONG)
                .show();
    }

}
