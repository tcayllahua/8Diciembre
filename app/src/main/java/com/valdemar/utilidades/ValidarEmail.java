package com.valdemar.utilidades;

import android.content.Context;
import android.util.Patterns;

import java.util.regex.Pattern;

public class ValidarEmail {


    public ValidarEmail(Context context){

    }

    public boolean checkEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
}
