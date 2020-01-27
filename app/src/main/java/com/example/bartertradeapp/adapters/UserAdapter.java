package com.example.bartertradeapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bartertradeapp.DataModels.UserModel;
import com.example.bartertradeapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import static java.security.AccessController.getContext;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<UserModel> userModels;
    private View.OnClickListener listener;

    public UserAdapter(Context mContext, List<UserModel> userModels) {
        this.mContext = mContext;
        this.userModels = userModels;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {

        UserModel userModel = userModels.get(position);
        holder.username.setText(userModel.getUserName());
        Picasso.get().load(userModel.getUserImageUrl()).into(holder.imageView);
        holder.itemView.setTag(userModel);
        holder.itemView.setOnClickListener(listener);

    }

    @Override
    public int getItemCount() {
        return userModels.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public TextView productName;
        public ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            productName = itemView.findViewById(R.id.product_title);
            imageView = itemView.findViewById(R.id.ad_image);
        }
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }
}
