package com.valdemar.view.main.ui.send;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.valdemar.R;
import com.valdemar.utilidades.library.MarshMallowPermission;
import com.valdemar.view.auth.AccessRelato;
import com.valdemar.view.main.ViewSpook;
import com.valdemar.view.main.ui.inicio.InicioFragment;

import java.sql.Timestamp;

import static android.app.Activity.RESULT_OK;

public class SendFragment extends Fragment {
    private ImageButton mImgView;
    private static final int GALLERY_REQUEST = 1;
    private Uri mImageUri = null;

    MarshMallowPermission marshMallowPermission;

    private TextInputEditText mAutor;
    private TextInputEditText mPostTitle;
    private TextInputEditText mPostDesciption;
    private Button mBtnAddPost;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private ProgressDialog mProgresDialog;
    private Spinner spinner;

    Task<Uri> mRuta;
    private Dialog MyDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_send, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) {
            initValidarAccess();
        }

        initView(root);
        initDeployHistory(root);
        return root;
    }

    private void initValidarAccess() {

        MyDialog = new Dialog(getActivity());
        MyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        MyDialog.setContentView(R.layout.moda_need_permiso);
        MyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button btnModalAcessoRelato = MyDialog.findViewById(R.id.modal_need_inicia_sesion);
        Button btnModalCancel = MyDialog.findViewById(R.id.modal_need_cancel);
        TextView mModal_need_try_feature_text_body = MyDialog.findViewById(R.id.modal_need_text_body);
        // mModal_need_try_feature_text_body.setText("Pronto Estará Disponible");

        btnModalAcessoRelato.setEnabled(true);

        btnModalAcessoRelato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciaSesion();
                MyDialog.dismiss();

            }
        });

        btnModalCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InicioFragment inicio = new InicioFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.coordinator, inicio)
                        .addToBackStack(null)
                        .remove(inicio)
                        .commit();
                MyDialog.dismiss();
            }
        });

        MyDialog.show();
    }

    private void iniciaSesion(){
        startActivity(new Intent(getActivity(), AccessRelato.class));
    }

    private void initDeployHistory(final View root) {
        spinner = (Spinner) root.findViewById(R.id.planets_spinner);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            spinner.setBackgroundColor(getActivity().getColor(R.color.white));
        }
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.planets_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);



        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Historias");
        mProgresDialog= new ProgressDialog(getActivity());

       // FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        mPostTitle = (TextInputEditText) root.findViewById(R.id.postTitle);
        mAutor = (TextInputEditText) root.findViewById(R.id.postAutor);

        //mAutor.setText(user.getDisplayName());


        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null) {
            Uri photoUrl = user.getPhotoUrl();

            String name_ = user.getDisplayName();
            String email_ = user.getEmail();
            mAutor.setText(name_);
        }else{
            mAutor.setText("Anónimo--");
        }

        mPostDesciption = (TextInputEditText) root.findViewById(R.id.postDescription);

        mBtnAddPost = (Button) root.findViewById(R.id.btnAddPost);


        mBtnAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user != null) {
                    startPosting(root);
                }else{
                    initValidarAccess();
                }
            }
        });
    }

    private void initView(View root) {
        mImgView = root.findViewById(R.id.postImageSelect);
        marshMallowPermission = new MarshMallowPermission(getActivity());
        captureImage();
    }

    private void captureImage() {

        mImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(getActivity().getApplicationContext(),"OTRO FRAGMENT", Toast.LENGTH_SHORT).show();
                if (!marshMallowPermission.checkPermissionForExternalStorage()) {
                    marshMallowPermission.requestPermissionForExternalStorage();
                } else {
                    seleccionarFoto();
                }
            }
        });
    }

    private void seleccionarFoto() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GALLERY_REQUEST);
    }

    private void startPosting(final View root){

        final String title_val = mPostTitle.getText().toString().trim();
        final String desc_val = mPostDesciption.getText().toString().trim();
        final String author_val = mAutor.getText().toString().trim();

        spinner = (Spinner) root.findViewById(R.id.planets_spinner);
        final String textSpinner = spinner.getSelectedItem().toString();


        if(mImageUri == null){
            showSnackBar("Seleccione una imagen para su portada.", root);
            Toast.makeText(getActivity().getApplicationContext(), "Porfavor seleccione una imagen ", Toast.LENGTH_SHORT).show();

        }

        else if(title_val.length() < 1) {
            showSnackBar("Título muy corto.", root);
            mPostTitle.setError("Este título es muy corto.");

        }
        else if(author_val.length() < 1){
            showSnackBar("Ingrese el nombre del autor(a).", root);
            mAutor.setError("Ingrese el nombre del autor(a).");
        }

        else if(desc_val.length() < 10){
            showSnackBar("Esta Descripción es demasiado corto.", root);
            mPostDesciption.setError("Esta Descripción es demasiado corto.");

        }
        else if(textSpinner.equals("Seleccion una Categoria:")){
            showSnackBar("Seleccione una categoria", root);
        }
        else{
            mProgresDialog.setMessage("Publicando Lectura");
            mProgresDialog.setCancelable(false);
            mProgresDialog.show();
            if(!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(desc_val) && mImageUri !=null){
                final StorageReference filepath = mStorage.child("Blog_images").child(mImageUri.getLastPathSegment());
                filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {

                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // getting image uri and converting into string
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                String userId = user.getUid();

                                mRuta = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                //     String asd =  taskSnapshot.getStorage().getMetadata().getResult().getPath();
                                Uri downloadUrl = uri;
                                String asd = downloadUrl.toString();

                                String url = taskSnapshot.getMetadata().getName();
                                String raiz = String.valueOf(taskSnapshot.getMetadata());
                                mDatabase = FirebaseDatabase.getInstance().getReference().child("Historias");

                                DatabaseReference newPost = mDatabase.push();

                                newPost.child("author").setValue(author_val);
                                newPost.child("title").setValue(title_val);
                                newPost.child("desc").setValue(desc_val);

                                newPost.child("IdMiLectura").setValue(userId);

                                newPost.child("image").setValue(downloadUrl.toString());
                                newPost.child("category").setValue(textSpinner);

                                Timestamp fechaRegistro = getFecha();
                                newPost.child("fechaRegistro").setValue(fechaRegistro);
                                mProgresDialog.dismiss();
                                //Toast.makeText(getActivity(), "Satisfactoriamente subido " + asd, Toast.LENGTH_LONG).show(); //Upload Was Success Message
                                startActivity(new Intent(getActivity(), ViewSpook.class));
                            }
                        });

                    }
                });
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
                mImageUri = data.getData();

                //mPostImageSelect.setImageURI(mImageUri);
                Glide.with(getActivity().getApplicationContext())
                        .load(mImageUri)
                        .thumbnail(Glide.with(getActivity().getApplicationContext()).load(R.drawable.item_placeholder))
                        .into(mImgView);
            }
    }

    public void showSnackBar(String msg, View root) {
        Snackbar
                .make(root.findViewById(R.id.coordinator), msg, Snackbar.LENGTH_LONG)
                .show();
    }


    public Timestamp getFecha(){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        return timestamp;
    }


}