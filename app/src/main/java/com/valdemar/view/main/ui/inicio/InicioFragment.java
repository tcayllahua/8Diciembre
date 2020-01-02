package com.valdemar.view.main.ui.inicio;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.valdemar.R;
import com.valdemar.view.category.Category;
import com.valdemar.view.category.view.CategoryViewHolder;
import com.valdemar.view.main.ui.modo_lectura.DescBlankFragment;

public class InicioFragment extends Fragment {

    private RecyclerView mRecyclerAsesinos,mRecyclerFantasmas,
            mRecyclerLeyendasUrbandas,mRecyclerCreepypastas,
            mRecyclerTerror,mRecyclerEpisodiosPerdidos;
    private DatabaseReference mDatabaseVersionApp;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseHeader,mDatabaseTreding;
    private ProgressDialog mProgress;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private Dialog MyDialog;




    private static Typeface Pacifico;
    private TextView mSpook_main;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        initConfigNetwork();
        initView(root);
        initAnuncio(root);
        validarActulizacion(root);
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

    private void initConfigNetwork() {
        mProgress = new ProgressDialog(getActivity().getApplicationContext());
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Historias");
        mDatabase.keepSynced(true);
    }

    private void initView(View root) {
        initFonts(root);
        initTerror(root);
        initCreepypastas(root);
        initLeyendasUrbanas(root);
        initFantasmas(root);
        initAsesinos(root);

        Log.v("Seguimiento","fin");

    }

    private void initTerror(View root) {
        Query queryEpisodiosPerdidos = mDatabase.orderByChild("category").equalTo("EpisodiosPerdidos");

        mRecyclerEpisodiosPerdidos = (RecyclerView) root.findViewById(R.id.recyclerEpisodiosPerdidos);
        mRecyclerEpisodiosPerdidos.setHasFixedSize(true);

        LinearLayoutManager layoutManagermRecyclerEpisodiosPerdidos
                = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        layoutManagermRecyclerEpisodiosPerdidos.setReverseLayout(true);
        layoutManagermRecyclerEpisodiosPerdidos.setStackFromEnd(true);

        mRecyclerEpisodiosPerdidos.setLayoutManager(layoutManagermRecyclerEpisodiosPerdidos);

        FirebaseRecyclerAdapter<Category, CategoryViewHolder> firebaseRecyclerAdaptermRecyclerEpisodiosPerdidos =
                new FirebaseRecyclerAdapter<Category, CategoryViewHolder>(
                        Category.class,
                        R.layout.album_card,
                        CategoryViewHolder.class,
                        queryEpisodiosPerdidos
                ) {
                    @Override
                    protected void populateViewHolder(CategoryViewHolder viewHolder, Category model, int position) {
                        final String post_key = getRef(position).getKey();
                        viewHolder.setTitle(model.getTitle());
                        viewHolder.setSendBy(model.getAuthor());

                        viewHolder.setImage(getActivity().getApplicationContext(),
                                model.getImage());

                        Log.v("Seguimiento","dentro");

                        viewHolder.mViewStructure_h.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                viewDetails(post_key);
                            }
                        });

                    }
                };

        mRecyclerEpisodiosPerdidos.setAdapter(firebaseRecyclerAdaptermRecyclerEpisodiosPerdidos);
    }

    private void initCreepypastas(View root) {
        Query queryCreepypastas = mDatabase.orderByChild("category").equalTo("Creepypastas");

        mRecyclerCreepypastas = (RecyclerView) root.findViewById(R.id.recyclerViewCreepypastas);
        mRecyclerCreepypastas.setHasFixedSize(true);

        LinearLayoutManager layoutManagermRecyclerCreepypastas
                = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        layoutManagermRecyclerCreepypastas.setReverseLayout(true);
        layoutManagermRecyclerCreepypastas.setStackFromEnd(true);

        mRecyclerCreepypastas.setLayoutManager(layoutManagermRecyclerCreepypastas);

        FirebaseRecyclerAdapter<Category, CategoryViewHolder> firebaseRecyclerAdaptermRecyclerCreepypastas =
                new FirebaseRecyclerAdapter<Category, CategoryViewHolder>(
                        Category.class,
                        R.layout.album_card,
                        CategoryViewHolder.class,
                        queryCreepypastas
                ) {
                    @Override
                    protected void populateViewHolder(CategoryViewHolder viewHolder, Category model, int position) {
                        final String post_key = getRef(position).getKey();
                        viewHolder.setTitle(model.getTitle());
                        viewHolder.setSendBy(model.getAuthor());

                        viewHolder.setImage(getActivity().getApplicationContext(),
                                model.getImage());

                        Log.v("Seguimiento","dentro");

                        viewHolder.mViewStructure_h.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                viewDetails(post_key);
                            }
                        });

                    }
                };

        mRecyclerCreepypastas.setAdapter(firebaseRecyclerAdaptermRecyclerCreepypastas);
    }

    private void initLeyendasUrbanas(View root) {
        Query queryLeyendasUrbandas = mDatabase.orderByChild("category").equalTo("LeyendasUrbanas");

        mRecyclerLeyendasUrbandas = (RecyclerView) root.findViewById(R.id.recyclerViewLeyendasUrbandas);
        mRecyclerLeyendasUrbandas.setHasFixedSize(true);

        LinearLayoutManager layoutManagermRecyclerLeyendasUrbandas
                = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        layoutManagermRecyclerLeyendasUrbandas.setReverseLayout(true);
        layoutManagermRecyclerLeyendasUrbandas.setStackFromEnd(true);

        mRecyclerLeyendasUrbandas.setLayoutManager(layoutManagermRecyclerLeyendasUrbandas);

        FirebaseRecyclerAdapter<Category, CategoryViewHolder> firebaseRecyclerAdaptermRecyclerLeyendasUrbandas =
                new FirebaseRecyclerAdapter<Category, CategoryViewHolder>(
                        Category.class,
                        R.layout.album_card,
                        CategoryViewHolder.class,
                        queryLeyendasUrbandas
                ) {
                    @Override
                    protected void populateViewHolder(CategoryViewHolder viewHolder, Category model, int position) {
                        final String post_key = getRef(position).getKey();
                        viewHolder.setTitle(model.getTitle());
                        viewHolder.setSendBy(model.getAuthor());

                        viewHolder.setImage(getActivity().getApplicationContext(),
                                model.getImage());

                        Log.v("Seguimiento","dentro");

                        viewHolder.mViewStructure_h.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                viewDetails(post_key);
                            }
                        });

                    }
                };

        mRecyclerLeyendasUrbandas.setAdapter(firebaseRecyclerAdaptermRecyclerLeyendasUrbandas);
    }

    private void initAsesinos(View root) {
        Query queryAsesinos = mDatabase.orderByChild("category").equalTo("AsesinosSeriales");

        mRecyclerAsesinos = (RecyclerView) root.findViewById(R.id.recyclerView);
        mRecyclerAsesinos.setHasFixedSize(true);

        LinearLayoutManager layoutManagerAsesinos
                = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        layoutManagerAsesinos.setReverseLayout(true);
        layoutManagerAsesinos.setStackFromEnd(true);

        mRecyclerAsesinos.setLayoutManager(layoutManagerAsesinos);

        FirebaseRecyclerAdapter<Category, CategoryViewHolder> firebaseRecyclerAdapterAsesinos =
                new FirebaseRecyclerAdapter<Category, CategoryViewHolder>(
                        Category.class,
                        R.layout.album_card,
                        CategoryViewHolder.class,
                        queryAsesinos
                ) {
                    @Override
                    protected void populateViewHolder(CategoryViewHolder viewHolder, Category model, int position) {
                        final String post_key = getRef(position).getKey();
                        viewHolder.setTitle(model.getTitle());
                        viewHolder.setSendBy(model.getAuthor());

                        viewHolder.setImage(getActivity().getApplicationContext(),
                                model.getImage());

                        Log.v("Seguimiento","dentro");

                        viewHolder.mViewStructure_h.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                viewDetails(post_key);
                            }
                        });

                    }
                };

        mRecyclerAsesinos.setAdapter(firebaseRecyclerAdapterAsesinos);
    }

    private void initFantasmas(View root) {
        Query queryfantasmas = mDatabase.orderByChild("category").equalTo("Fantasmas");
        mRecyclerFantasmas = (RecyclerView) root.findViewById(R.id.recyclerViewFantasmas);
        mRecyclerFantasmas.setHasFixedSize(true);
        LinearLayoutManager layoutManagerFantasmas
                = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        layoutManagerFantasmas.setReverseLayout(true);
        layoutManagerFantasmas.setStackFromEnd(true);
        mRecyclerFantasmas.setLayoutManager(layoutManagerFantasmas);

        FirebaseRecyclerAdapter<Category, CategoryViewHolder> firebaseRecyclerAdapterFantasmas =
                new FirebaseRecyclerAdapter<Category, CategoryViewHolder>(
                        Category.class,
                        R.layout.album_card,
                        CategoryViewHolder.class,
                        queryfantasmas
                ) {
                    @Override
                    protected void populateViewHolder(CategoryViewHolder viewHolder, Category model, int position) {
                        final String post_key = getRef(position).getKey();
                        viewHolder.setTitle(model.getTitle());
                        viewHolder.setSendBy(model.getAuthor());

                        viewHolder.setImage(getActivity().getApplicationContext(),
                                model.getImage());

                        viewHolder.mViewStructure_h.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                viewDetails(post_key);
                            }
                        });

                    }
                };

        mRecyclerFantasmas.setAdapter(firebaseRecyclerAdapterFantasmas);

    }

    private void viewDetails(String post_key){
       // mProgress.setMessage("Accediendo...");
        //mProgress.show();
        //mProgress.setCancelable(true);

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
            Log.v("Anuncio","click");
        }else{
            /*
            Intent singleBlogIntent = new Intent(getActivity().getApplicationContext(), DetailsRelato.class);
            singleBlogIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            singleBlogIntent.putExtra("blog_id", post_key);
            startActivity(singleBlogIntent);
            */

            DescBlankFragment descBlankFragment = new DescBlankFragment();

            Bundle datosSend = new Bundle();
            datosSend.putString("blog_id", post_key);
            descBlankFragment.setArguments(datosSend);

            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.contenido_dinamico, descBlankFragment)
                    .addToBackStack(null).commit();

        }

        //mProgress.dismiss();
        Log.v("id","id"+post_key);
    }

    public void validarActulizacion(View root){

        mDatabaseVersionApp = FirebaseDatabase.getInstance().getReference().child("VersionApp");
        mDatabaseVersionApp.keepSynced(true);
        mDatabaseVersionApp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String appVersion = (String) dataSnapshot.child("version").getValue();
                String title = (String) dataSnapshot.child("title").getValue();
                String body = (String) dataSnapshot.child("body").getValue();
                // Boolean banner = (Boolean) dataSnapshot.child("ads").child("banner").getValue();
                //Boolean Interstitial = (Boolean) dataSnapshot.child("ads").child("interstitial").getValue();

                System.out.println("::");

                Log.v("PackageInfo",""+appVersion);

                PackageInfo pInfo = null;

                try {
                    pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                String version = pInfo.versionName;
                int verCode = pInfo.versionCode;

                Log.v("PackageInfo",""+version);
                Log.v("PackageInfo",""+verCode);

                int verCodeActual = Integer.parseInt(appVersion);

                if(verCode < verCodeActual){
                    //ModalCheckUpdate(title,body);
                    Log.v("PackageInfo",""+verCode+""+verCodeActual);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void ModalCheckUpdate(String title,String body){

        MyDialog = new Dialog(getActivity().getApplicationContext());
        MyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        MyDialog.setContentView(R.layout.warning_update);
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
                                + getActivity().getPackageName()));
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

    private void initFonts(View root) {
        String pacificoFuente= "fuentes/BloodLust.ttf";
        this.Pacifico = Typeface.createFromAsset(getActivity().getAssets(),pacificoFuente);
        mSpook_main = root.findViewById(R.id.spook_main);
        mSpook_main.setTypeface(Pacifico);
    }
}