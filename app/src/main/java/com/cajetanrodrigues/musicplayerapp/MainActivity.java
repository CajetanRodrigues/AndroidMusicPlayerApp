package com.cajetanrodrigues.musicplayerapp;
//Inside Recycler view : All below extend Recycler view
 /*1] Linear Layout Manager . for listing vertically
   2] DividerItemDecoration . for dividing contents between LinearLayoutManager
  */
import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public ArrayList<songInfo> songs = new ArrayList<songInfo>();
    RecyclerView recyclerView; // For storing huge sets of data.
    SeekBar seekBar;    // To implement multithreading
    songAdapter  songAdapter; // Reference of songAdapter class
    MediaPlayer mediaPlayer; // to implement music
    Handler h = new Handler();
    //android.os.Handler allows us to send and process Message and Runnable objects associated with a thread's MessageQueue.
    // Each Handler instance is associated with a single thread and that thread's message queue.
    // Handler used for: ... Processing messages on the consumer thread. Managing messages in the queue
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //songInfo s = new songInfo("It ain't me","selena gomez","https://www.saavn.com/s/song/english/Wolves/Wolves.mp3");
        //songs.add(s);

        // Initialising Variables
        recyclerView = (RecyclerView)findViewById(R.id.recyclerview); // finding view of recyclerView
        seekBar = (SeekBar)findViewById(R.id.seekBar); //finding view
        songAdapter = new songAdapter(this,songs); // create object of songAdapter class and initialise constructor.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this); //Creates a vertical LinearLayoutManager its a class form Recycler view
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),linearLayoutManager.getOrientation());
        //DividerItemDecoration is a RecyclerView.ItemDecoration that can be used as a divider between items of a LinearLayoutManager.
        // It supports both HORIZONTAL and VERTICAL orientations.

        // Now making the base for Recycler view
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(songAdapter);

        // Line 62 - Line 123 is are two threads . one for checking whether the button content is play or stop/ and /other to move the seekBar.
        //****Handler takes care of runnable **** Thread takes care of run()
        songAdapter.setOnItemClickListener(new songAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final Button b, View view, final songInfo c, int position) {
                Runnable r = new Runnable() { //Runnable interface should be implemented by any class whose instances are intended to be executed by a thread
                    @Override
                    public void run() {
                        try {
                            if(b.getText().toString().equals("STOP")){
                                b.setText("PLAY");
                                mediaPlayer.stop(); // goes to stop state
                                mediaPlayer.reset(); // goes to idle state
                                mediaPlayer.release(); // goes to end state
                                mediaPlayer = null;
                            }
                            else{

                                mediaPlayer = new MediaPlayer();
                                mediaPlayer.setDataSource(c.getSongUrl()); // goes to initialised state
                                mediaPlayer.prepareAsync(); // goes to preparing state from initialised state
                                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mediaPlayer) {
                                        mediaPlayer.start(); // goes to start state
                                        seekBar.setProgress(0);
                                        seekBar.setMax(mediaPlayer.getDuration()); // sets the seek bar based relative to the song.
                                        b.setText("STOP");

                                    }
                                });

                            }}
                        catch(IOException e){

                        }

                    }
                }; // this takes care of the above runnable
                h.postDelayed(r,100); // Handler Used to communicate between the UI and Background thread

            }
        });
        checkPermission();
        Thread t = new MyThread(); // this takes care of the below run()
        t.start();
    }

    public class MyThread extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(1000);
                if(mediaPlayer!=null){
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void checkPermission(){
        if(Build.VERSION.SDK_INT>=23) { //Marshmello(API 23)and higher
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
                return;
            }
        }
        else{
            loadSongs();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 123 : if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                loadSongs();
            }else{
                Toast.makeText(this , "Permission Denied" , Toast.LENGTH_LONG).show();
                checkPermission();
            }
            break;
            default:super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }
    private void loadSongs(){
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI; // finds the song
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"; // Checks if the music is present
        Cursor cursor = getContentResolver().query(uri,null , selection , null ,null );
        //Cursors are what contain the result set of a query made against a database in Android. The Cursor class has an API that allows an app to read (in a type-safe manner)
        // the columns that were returned from the query as well as
        // iterate over the rows of the result set.
        if(cursor!=null)
        {
            if(cursor.moveToFirst()){ // moves the cursor to the first row , returns false if the cursor is empty
                do {
                    String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));

                    String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    songInfo s = new songInfo(name,artist,url); // initialising the parameters
                    songs.add(s); // add songs to arraylist
                }while(cursor.moveToNext()); // loop runs till it gets the next element , will terminate when there is no next element
                cursor.close();
                //Closes the Cursor, releasing all of its resources and making it completely invalid
                songAdapter = new songAdapter(this , songs); // finally passing the array list to the adapter.

            }
        }

    }

}
