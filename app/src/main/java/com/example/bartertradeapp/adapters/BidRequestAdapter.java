package com.example.bartertradeapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bartertradeapp.DataModels.BidRequestModel;
import com.example.bartertradeapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BidRequestAdapter extends RecyclerView.Adapter<BidRequestAdapter.ViewHolder> {
    private ArrayList<BidRequestModel> bidRequestList;
    private View.OnClickListener listener;

    public BidRequestAdapter(ArrayList<BidRequestModel> bidRequestList) {
        this.bidRequestList = bidRequestList;
    }

    @NonNull
    @Override
    public BidRequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BidRequestAdapter.ViewHolder holder, int position) {
        BidRequestModel requestModel = bidRequestList.get(holder.getAdapterPosition());

        if(requestModel.getProductModel().getTag().equals("Product")){
            holder.username.setText("Sender Name :" + " " + requestModel.getUserModel().getUserName());
            holder.productName.setText("Product Name :" + " " + requestModel.getProductModel().getProductName());

            if(requestModel.getRequestModel().isAccepted() == true){
                holder.isAccepted.setText("Request Status :" + " " + "Bid Accepted");
                holder.isAccepted.setVisibility(View.VISIBLE);
            } else {
                holder.isAccepted.setText("Request Status :" + " " + "Bid Not Accepted");
                holder.isAccepted.setVisibility(View.VISIBLE);
            }

            Picasso.get().load(requestModel.getUserModel().getUserImageUrl()).fit().centerCrop().into(holder.imageView);
            holder.itemView.setTag(requestModel);
            holder.itemView.setOnClickListener(listener);
        }else {
            holder.username.setText("Sender Name :" + " " + requestModel.getUserModel().getUserName());
            holder.productName.setText("Service Name :" + " " + requestModel.getProductModel().getServiceName());

            if(requestModel.getRequestModel().isAccepted() == true){
                holder.isAccepted.setText("Request Status :" + " " + "Bid Accepted");
                holder.isAccepted.setVisibility(View.VISIBLE);
            } else {
                holder.isAccepted.setText("Request Status :" + " " + "Bid Not Accepted");
                holder.isAccepted.setVisibility(View.VISIBLE);
            }

            Picasso.get().load(requestModel.getUserModel().getUserImageUrl()).fit().centerCrop().into(holder.imageView);
            holder.itemView.setTag(requestModel);
            holder.itemView.setOnClickListener(listener);
        }

    }

    @Override
    public int getItemCount() {
        return bidRequestList != null ? bidRequestList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public TextView productName;
        public TextView isAccepted;
        public ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            productName = itemView.findViewById(R.id.product_title);
            isAccepted = itemView.findViewById(R.id.isAccepted);
            imageView = itemView.findViewById(R.id.ad_image);
        }
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }
}
