package com.valdemar.view.details;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.valdemar.R;
import com.valdemar.view.main.ViewSpook;

import java.util.Locale;


public class DetailsRelato extends AppCompatActivity{

    private String mPost_key = null;
    private DatabaseReference mDatabase;
    private TextView mPostTitleDetails;
    private ImageView mImage_paralax;
    private AdView mAdView;

    private ProgressDialog mProgresDialog;
    private Button mPostRemoveDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_relato);
        initView();
        initAnuncio();
    }

    private void initView() {
        mPost_key = getIntent().getExtras().getString("blog_id");

        mPostTitleDetails = (TextView) findViewById(R.id.postTitleDetails);
        mPostRemoveDetails = (Button) findViewById(R.id.postRemoveDetails);
        mImage_paralax = (ImageView) findViewById(R.id.image_paralax);

        initWebView();
        initRemove(mPost_key);
    }

    private void initRemove(final String mPost_key) {
        mProgresDialog= new ProgressDialog(this);

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

    private void initWebView() {

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Historias");

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

    private void initAnuncio() {

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setVisibility(View.GONE);

    }

}
