package com.valdemar.view.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.valdemar.R;
import com.valdemar.utilidades.ValidarEmail;

public class ForgetPasswordRelato extends AppCompatActivity {

    private FirebaseAuth mAuth;
    public TextView title;
    private FrameLayout mForgetPasswordCuenta;
    private EditText mForget_user_email;
    private String display_email;
    private Dialog MyDialog;
    private ProgressDialog mProgress;
    private static Typeface Pacifico,Nightmare,Double,BloodLust;

    /*Animación*/
    private Animation mUp_to_down,mhide_to_bottom;
    private LinearLayout mRelato_forget_body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password_relato);
        initView();
    }

    private void initView() {
        mRelato_forget_body = (LinearLayout) findViewById(R.id.relato_forget_body);
        MyDialog = new Dialog(ForgetPasswordRelato.this);

        title = (TextView) findViewById(R.id.forget_title_sangrienta_lectura);
        String text = "<font color='#da152c'>Reiniciar</font> Password";
        Log.v("textq1",text);
        title.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);

        mAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(this);

        String bloodLustFuente= "fuentes/BloodLust.ttf";
        String nightmareFuente= "fuentes/Nigh.ttf";
        this.BloodLust = Typeface.createFromAsset(getAssets(),bloodLustFuente);
        this.Nightmare = Typeface.createFromAsset(getAssets(),nightmareFuente);
        title.setTypeface(BloodLust);

        mForget_user_email = (EditText) findViewById(R.id.forget_user_email);

        mForgetPasswordCuenta = (FrameLayout) findViewById(R.id.forgetPasswordCuenta);

        mUp_to_down = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.up_to_down);
        mhide_to_bottom = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.down_to_up);

        title.setAnimation(mUp_to_down);
        mRelato_forget_body.setAnimation(mUp_to_down);
        mForgetPasswordCuenta.setAnimation(mhide_to_bottom);


        mForgetPasswordCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgress.setMessage("Buscando...");
                mProgress.show();


                display_email = mForget_user_email.getText().toString();

                if(mForget_user_email.getText().toString().equals("")){
                    showSnackBar("Ingrese un Email");
                    mProgress.hide();

                }else {

                    mAuth.sendPasswordResetEmail(display_email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    hideSoftKeyboard();
                                    if (task.isSuccessful()) {
                                        mProgress.hide();
                                        Log.d("estado", "Email sent."+display_email);
                                        showSnackBar("Hemos enviado un enlace a su correo."+display_email);
                                        popUsuarioSinSesion("Hemos enviado un enlace a su correo.");
                                        //finish();
                                    }else{
                                        mProgress.hide();
                                        mForget_user_email.setError("Email no válido.");
                                        showSnackBar("Email no registrado. "+display_email);
                                    }
                                }
                            });
                }

            }
        });

    }


    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }


    private boolean validarEmail(String display_email) {
        ValidarEmail check = new ValidarEmail(this);
        boolean booleamCheckEmail = check.checkEmail(display_email);
        Log.v("checkEmail",""+booleamCheckEmail);
        return booleamCheckEmail;
    }

    public void showSnackBar(String msg) {
        Snackbar
                .make(findViewById(R.id.forgetRelato), msg, Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    protected void onStart() {
        super.onStart();
      //  Intent svc = new Intent(getApplicationContext(), BackgroundSoundService.class);
      //  startService(svc);
    }

    //modal validar usuario
    public void popUsuarioSinSesion(String text){

        MyDialog = new Dialog(ForgetPasswordRelato.this);
        MyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        MyDialog.setContentView(R.layout.modal_forget_pass);
        MyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView mTitle_pop = MyDialog.findViewById(R.id.title_pop);
        mTitle_pop = MyDialog.findViewById(R.id.title_pop);
        Button btnModalAcessoRelato = MyDialog.findViewById(R.id.modal_need_inicia_sesion);
        Button btnModalCancel = MyDialog.findViewById(R.id.modal_need_cancel);
        TextView mModal_need_try_feature_text_body = MyDialog.findViewById(R.id.modal_need_text_body);

        mModal_need_try_feature_text_body.setText(text);
        mTitle_pop.setText("Revisa tu Bandeja");
        btnModalAcessoRelato.setEnabled(true);
        btnModalAcessoRelato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDialog.dismiss();
                startActivity(new Intent(ForgetPasswordRelato.this,AccessRelato.class));
            }
        });
        btnModalCancel.setVisibility(View.GONE);


        MyDialog.show();

    }



}
