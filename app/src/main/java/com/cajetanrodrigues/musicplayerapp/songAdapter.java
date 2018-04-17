package com.cajetanrodrigues.musicplayerapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Cajetan Rodrigues on 14-03-2018.
 */

public class songAdapter extends RecyclerView.Adapter<songAdapter.songHolder> {
    //AdapterView is a group of widgets (aka view) components in Android that include the ListView, Spinner, and GridView.
    Context context; // Environment in which work is going on
    ArrayList<songInfo> songs; // holds songs of type songInfo ie extends AbstractList & implements List Interface
                                // Advantage : It is dynamic , can grow n shrink
    OnItemClickListener onItemClickListener;
   public songAdapter(Context context , ArrayList<songInfo> songs) // songs is an object of ArrayList<songinfo> type
   {
       this.context = context;
       this.songs = songs ;
   }

    @Override // auto gen
    public songHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_song,parent,false); //LayoutInflater is used to manipulate Android screen using predefined XML layouts.
        // This class is used to instantiate layout XML file into its corresponding View objects.
        return new songHolder(view); // returns view to the method in class songHolder.
    }

    public interface OnItemClickListener{
       void onItemClick(Button b, View view, songInfo c, int position);


    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){ // refer line 63 in main activity
       this.onItemClickListener = onItemClickListener;
    }

    @Override // auto gen
    public void onBindViewHolder(final songHolder holder, final int position) {
        final songInfo c = songs.get(position); // songs is object of array list.
        holder.songName.setText(c.songName);
        holder.artistName.setText(c.songName);
        holder.play.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(onItemClickListener != null){
                    onItemClickListener.onItemClick(holder.play , view , c , position);
                }
            }
        });
    }

    @Override // auto gen
    public int getItemCount() {
        return songs.size();
    }

    public class songHolder extends RecyclerView.ViewHolder { // class inside a class
       TextView songName ;
       TextView artistName;
       Button play ;


        public songHolder(View itemView) {
            super(itemView);
            songName = (TextView) itemView.findViewById(R.id.songName);
            artistName = (TextView) itemView.findViewById(R.id.artistName);
            play = (Button)itemView.findViewById(R.id.play);

        }
    }
}
