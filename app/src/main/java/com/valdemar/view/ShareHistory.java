package com.valdemar.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.valdemar.R;

public class ShareHistory extends AppCompatActivity {

    private ImageButton mPostImageSelect;

    private EditText mAutor;
    private EditText mPostTitle;
    private EditText mPostDesciption;


    private Button mBtnAddPost;
    private Uri mImageUri = null;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private static final int GALLERY_REQUEST = 1;
    private ProgressDialog mProgresDialog;
    private SharedPreferences monedas = null;
    private Spinner spinner;
    private GestureDetector mDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_history);
    }
}
