package com.valdemar.view.main.ui.memes;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.valdemar.R;
import com.valdemar.model.ItemFeed;
import com.valdemar.utilidades.RelatoViewHolderStructureMemes;

public class MemesFragment extends Fragment {
    private RecyclerView mRecyclerMisLecturas;
    private DatabaseReference mDatabaseMisLecturas;
    private ProgressDialog mProgress;
    private AdView mAdView;
    private Dialog MyDialog;

    public MemesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View root = inflater.inflate(R.layout.fragment_memes, container, false);
        initView(root);

        return root;
    }

    private void initView(View root) {
        initAnuncio(root);

        mProgress = new ProgressDialog(getContext());
        mDatabaseMisLecturas = FirebaseDatabase.getInstance().getReference().child("Memes");

        mDatabaseMisLecturas.keepSynced(true);
        // mAdView = (AdView) root.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        // mAdView.loadAd(adRequest);
        LinearLayoutManager layoutManagerMisLecturas
                = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);

        layoutManagerMisLecturas.setReverseLayout(true);
        layoutManagerMisLecturas.setStackFromEnd(true);

        mRecyclerMisLecturas = (RecyclerView) root.findViewById(R.id.fragmento_memes);
        mRecyclerMisLecturas.setHasFixedSize(true);

        mRecyclerMisLecturas.setLayoutManager(layoutManagerMisLecturas);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
       // String userId = user.getUid();


        //Query queryRef = mDatabaseMisLecturas.orderByChild("IdMiLectura").equalTo(userId);
        //Query queryRef = mDatabaseMisLecturas.orderByChild("IdMiLectura").equalTo(userId);


        FirebaseRecyclerAdapter<ItemFeed, RelatoViewHolderStructureMemes>
                firebaseRecyclerAdapterMyLecturas = new FirebaseRecyclerAdapter<ItemFeed, RelatoViewHolderStructureMemes>(
                ItemFeed.class,
                R.layout.design_structure_relato_menu,
                RelatoViewHolderStructureMemes.class,mDatabaseMisLecturas) {
            @Override
            protected void populateViewHolder(RelatoViewHolderStructureMemes viewHolder, final ItemFeed model, final int position) {
                final String post_key = getRef(position).getKey();
                viewHolder.setTitle(model.getTitle());
                viewHolder.setCatergory("Memes");

                viewHolder.setImage(getActivity().getApplicationContext(), model.getImage());

                viewHolder.mViewStructure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mProgress.setMessage("Accediendo...");
                        mProgress.show();
                        //Toast.makeText(getContext(),"Identificador "+post_key,Toast.LENGTH_SHORT).show();



                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mProgress.hide();
                                mProgress.dismiss();
                                //viewDetails(post_key);
                                initValidarAccess(model.getImage());
                            }
                        }, 200);
                    }
                });
            }
        };

        mRecyclerMisLecturas.setAdapter(firebaseRecyclerAdapterMyLecturas);

    }

    private void initAnuncio(View root) {
        mAdView = (AdView) root.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void initValidarAccess(String photoUrl) {

        MyDialog = new Dialog(getActivity());
        MyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        MyDialog.setContentView(R.layout.modal_meme);
        MyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button btnModalAcessoRelato = MyDialog.findViewById(R.id.modal_need_inicia_sesion);
        //Button btnModalCancel = MyDialog.findViewById(R.id.modal_need_cancel);
        ImageView imgMeme = MyDialog.findViewById(R.id.imgMeme);


        Glide.with(getActivity())
                .load(photoUrl)
                .thumbnail(Glide.with(getActivity())
                        .load(R.drawable.item_placeholder))
                .into(imgMeme);



      /*  btnModalCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InicioFragment inicio = new InicioFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.contenido_dinamico, inicio)
                        .addToBackStack(null)
                        .remove(inicio)
                        .commit();
                MyDialog.dismiss();
            }
        });*/

        MyDialog.show();
    }


    /*private void viewDetails(String post_key){
        DescBlankFragment descBlankFragment = new DescBlankFragment();
        Bundle datosSend = new Bundle();
        datosSend.putString("blog_id", post_key);
        descBlankFragment.setArguments(datosSend);

        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.contenido_dinamico, descBlankFragment)
                .addToBackStack(null).commit();
        Log.v("id","id"+post_key);

    }*/




}
