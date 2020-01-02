package com.valdemar.utilidades;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.valdemar.R;



/**
 * Created by CORAIMA on 21/11/2017.
 */


public class RelatoViewHolderStructure extends RecyclerView.ViewHolder{

    public View mViewStructure;
    public TextView mItem_recycler_structure_title;
    public TextView mItem_recycler_structure_category;
    public TextView mItem_recycler_structure_author;
    public ImageView mPost_image;


    public RelatoViewHolderStructure(View itemView) {
        super(itemView);
        mViewStructure = itemView ;
    }

    public void setTitle(String title){
        //mItem_recycler_structure_title.setTypeface(Pacifico);
        mItem_recycler_structure_title = mViewStructure.findViewById(R.id.item_recycler_structure_title);
        mItem_recycler_structure_title.setText(title);
    }

    public void setCatergory(String category){
        mItem_recycler_structure_category = mViewStructure.findViewById(R.id.item_recycler_structure_category);
        mItem_recycler_structure_category.setText("Género: "+category);
    }
    public void setAuthor(String author){
        mItem_recycler_structure_author = mViewStructure.findViewById(R.id.item_recycler_structure_author);
        mItem_recycler_structure_author.setText("Escrito por: "+author);
    }

    public void setImage(Context context, String image){
        mPost_image = mViewStructure.findViewById(R.id.item_recycler_structure_imagen);

        Glide.with(context)
                .load(image)
                .thumbnail(Glide.with(context).load(R.drawable.item_placeholder))
                .into(mPost_image);

    }

}