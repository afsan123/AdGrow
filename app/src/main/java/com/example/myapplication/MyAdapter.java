package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private ArrayList<FBPage> fbPages;

    public MyAdapter(ArrayList<FBPage> fbPages) {
        this.fbPages = fbPages;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyecler_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Picasso.get().load("https://graph.facebook.com/" + fbPages.get(position).getId() + "/picture?type=large").into(holder.imageView);
        holder.textView.setText(fbPages.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return fbPages.size();
    }

    public void clear() {
        fbPages.clear();
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView textView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.page_pic);
            textView = itemView.findViewById(R.id.page_name);
        }
    }

}
