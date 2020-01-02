package com.valdemar.view.main.ui.share;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.valdemar.R;
import com.valdemar.SplashActivity;

public class ShareFragment extends Fragment {

    private DatabaseReference mMensajeShare;
    private ProgressDialog mProgress;

    private LinearLayout mLinearLayout_;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_share, container, false);

        mLinearLayout_ = root.findViewById(R.id.linearLayout);
        mLinearLayout_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 share();
            }
        });
        share();
        return root;
    }
    private void iniciaSesion(){
        LoginManager.getInstance().logOut();
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(getActivity(), SplashActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    private void share() {
        mProgress = new ProgressDialog(getActivity());
        mProgress.setMessage("Cargando...");
        mProgress.show();

        mMensajeShare = (DatabaseReference) FirebaseDatabase.getInstance().getReference();
        mMensajeShare.keepSynced(true);

        mMensajeShare.child("Message").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String texto = (String) dataSnapshot.child("text").getValue();
                String link = (String) dataSnapshot.child("link").getValue();

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,texto+" "+link);
                //sendIntent.putExtra(Intent.EXTRA_TEXT,"Esta app te hará sufrir un infarto con sus Sangrientas Lecturas, Descargala YA!! https://play.google.com/store/apps/details?id=relato.app.dems.com.relato.beta");

                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                mProgress.hide();
                mProgress.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mProgress.hide();
                mProgress.dismiss();
            }
        });

    }
}