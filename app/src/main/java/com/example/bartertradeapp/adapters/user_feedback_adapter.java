package com.example.bartertradeapp.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bartertradeapp.DataModels.RatingModel;
import com.example.bartertradeapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class user_feedback_adapter extends RecyclerView.Adapter<user_feedback_adapter.ViewHolder>  {
    @NonNull
    private List<RatingModel> feedbacklist;
    private LayoutInflater mInflater;
    private user_feedback_adapter.ItemClickListener mClickListener;

    private Context context;

    private ArrayList<String> imageList = null;

    // data is passed into the constructor
    public user_feedback_adapter(Context context, List<RatingModel> feedbacklist) {
        this.mInflater = LayoutInflater.from(context);
        this.feedbacklist = feedbacklist;
    }

    // inflates the itemlist layout from xml when needed
    @Override
    public user_feedback_adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.feedback_list_item, parent, false);
        return new user_feedback_adapter.ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final user_feedback_adapter.ViewHolder holder, int position) {
        RatingModel list = feedbacklist.get(position);

        holder.feedback_bar.setIsIndicator(true);
        holder.feedback_bar.setRating(list.getRating());
        holder.username.setText(""+list.getUserName());
        holder.feedback_text.setText(""+list.getFeedback());
        Picasso.get().load(list.getImageUri())
                           .fit()
                           .centerCrop()
                           .into(holder.user_image);

//        try {
//            if (list.getmImageUri() != null) {
//
//                Picasso.get().load(list.getmImageUri())
//                        .fit()
//                        .centerCrop()
//                        .into(holder.image);
//
//            } else {
//
//                imageList = list.getmArrList();
//                if (imageList != null) {
//                    Uri uri = Uri.parse(imageList.get(0));
//                    Picasso.get().load(uri)
//                            .fit()
//                            .centerCrop()
//                            .into(holder.image);
//                }
//            }
//
//        } catch (Exception e) {
//            System.out.println("Error " + e.getMessage());
//            //Toast.makeText(this.context,"Error in multiple images" + e.getMessage(),Toast.LENGTH_SHORT).show();
//        }


    }

    // total number of rows
    @Override
    public int getItemCount() {
        return feedbacklist.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView feedback_text;
        TextView username;
        RatingBar feedback_bar;
        ImageView user_image;
        ArrayList<String> imagelist;
        Uri uri;

        ViewHolder(View itemView) {
            super(itemView);
            feedback_text = itemView.findViewById(R.id.feedback_text);
            username = itemView.findViewById(R.id.feedback_username);
            feedback_bar = itemView.findViewById(R.id.feedback_stars);
            user_image = itemView.findViewById(R.id.feedback_userimage);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public RatingModel getItem(int id) {
        return feedbacklist.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(user_feedback_adapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
