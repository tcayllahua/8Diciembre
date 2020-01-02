package com.valdemar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.valdemar.utilidades.sounds.BackgroundSoundService;
import com.valdemar.view.auth.AccessRelato;
import com.valdemar.view.main.MainActivity;
import com.valdemar.view.main.ViewSpook;

public class SplashActivity extends AppCompatActivity {
    private AdView mAdView;
    private static Typeface Pacifico;
    private TextView mTitle;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initAnuncio();
        initFonts();
       // FirebaseMessaging.getInstance().subscribeToTopic("Historias");
        //FirebaseMessaging.getInstance().subscribeToTopic("chat");


         new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /*
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null) {
                    startActivity(new Intent(SplashActivity.this, ViewSpook.class));
                    finish();
                }else{
                    startActivity(new Intent(SplashActivity.this, AccessRelato.class));
                    finish();
                }*/

                startActivity(new Intent(SplashActivity.this, ViewSpook.class));
                finish();
            }
        },1000);

    }

    private void initAnuncio() {

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-2031757066481790/1567729748");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

    }

    public void showAnuncio() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
            Log.v("Anuncio","click");
        }
    }


    private void initFonts() {
        String pacificoFuente= "fuentes/BloodLust.ttf";
        this.Pacifico = Typeface.createFromAsset(getAssets(),pacificoFuente);
        mTitle = findViewById(R.id.title);
        mTitle.setTypeface(Pacifico);
    }


}
