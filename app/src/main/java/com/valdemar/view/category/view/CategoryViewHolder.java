package com.valdemar.view.category.view;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.valdemar.R;

public class CategoryViewHolder extends RecyclerView.ViewHolder{

    public View mViewStructure_h;
    public TextView mItem_recycler_structure_title_h;
    public TextView mItem_recycler_structure_category_h;
    public TextView mItem_recycler_structure_send_by_h;
    public ImageView mPost_image_h;

    ImageButton mfavoriteBtn;

    public CategoryViewHolder(View itemView) {
        super(itemView);
        mViewStructure_h = itemView ;
    }

    public void setTitle(String title){
        //mItem_recycler_structure_title.setTypeface(Pacifico);
        mItem_recycler_structure_title_h = mViewStructure_h.findViewById(R.id.title_album);
        mItem_recycler_structure_title_h.setText(title);
    }
    public void setSendBy(String title){
        //mItem_recycler_structure_title.setTypeface(Pacifico);
        mItem_recycler_structure_send_by_h = mViewStructure_h.findViewById(R.id.send_by_album);
        mItem_recycler_structure_send_by_h.setText(title);
    }

    public void setImage(Context context, String image){
        mPost_image_h = mViewStructure_h.findViewById(R.id.thumbnail);

        Glide.with(context)
                .load(image)
                .thumbnail(Glide.with(context).load(R.drawable.b))
                //.thumbnail(Glide.with(context).load(R.drawable.item_placeholder))
                .into(mPost_image_h);

    }


}
