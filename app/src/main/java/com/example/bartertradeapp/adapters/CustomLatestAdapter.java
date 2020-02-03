package com.example.bartertradeapp.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.bartertradeapp.DataModels.UserUploadProductModel;
import com.example.bartertradeapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CustomLatestAdapter extends RecyclerView.Adapter<CustomLatestAdapter.ViewHolder> {

    private List<UserUploadProductModel> userlist;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    private Context context;

    private ArrayList<String> imageList = null;

    // data is passed into the constructor
    public CustomLatestAdapter(Context context, List<UserUploadProductModel> userlist) {
        this.mInflater = LayoutInflater.from(context);
        this.userlist = userlist;
    }

    // inflates the itemlist layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.nearest_items, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        UserUploadProductModel list = userlist.get(position);
        if (list.getTag().equals("Product")) {
            holder.title.setText(list.getProductName());
            try {
                if (list.getmImageUri() != null) {

                    Picasso.get().load(list.getmImageUri())
                            .fit()
                            .into(holder.image);

                } else {

                    imageList = list.getmArrList();
                    if (imageList != null) {
                        Uri uri = Uri.parse(imageList.get(0));
                        Picasso.get().load(uri)
                                .fit()
                                .into(holder.image);
                    }
                }

            } catch (Exception e) {
                System.out.println("Error " + e.getMessage());
                //Toast.makeText(this.context,"Error in multiple images" + e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        } else {

            holder.title.setText(list.getServiceName());
            Picasso.get().load(list.getServiceImageUri())
                    .fit()
                    .centerCrop()
                    .into(holder.image);
        }


    }

    // total number of rows
    @Override
    public int getItemCount() {
        return userlist.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        ImageView image;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.ad_title);
            image = itemView.findViewById(R.id.ad_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onLatestItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public UserUploadProductModel getItem(int id) {
        return userlist.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onLatestItemClick(View view, int position);
    }
}
