package com.example.sukarora.chekredmi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class recyclerViewAdapter extends RecyclerView.Adapter<recyclerViewAdapter.ProductViewHolder> {


    private ArrayList<String> paths;
    private String STORE_DIRECTORY;


    public recyclerViewAdapter( ArrayList<String> paths) {
        this.paths = paths;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_notes, parent, false);

        return new ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        Bitmap bmp = BitmapFactory.decodeFile(paths.get(position));
        holder.imageView.setImageBitmap(bmp);
        //holder.imageView.setOnCreateContextMenuListener(this);

        /*holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {



                return true;
            }
        });*/



        holder.imageView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {



                MenuItem delete= menu.add("Delete");
                delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        Log.d("in delete","menu");

                        

                        return true;
                    }
                });


            }
        });

        //holder.imageView.setOnClickListener(new );
    }

    @Override
    public int getItemCount() {
        return paths.size();
    }



    class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView= itemView.findViewById(R.id.imageView);



        }


    }
}


