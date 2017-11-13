package com.example.durai23.musicplayerapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;

import java.util.concurrent.TimeUnit;
import java.util.logging.LogRecord;

public class MainActivity extends AppCompatActivity {

    static final int READ_STORAGE_REQUEST_CODE = 1;

    private MP3Player mp3Player = new MP3Player();
    private ArrayList<File> songList = new ArrayList<File>();
    private SeekBar seekBar;
    private TextView currentTime;
    private TextView totalDuration;
    private ImageButton playButton;
    private ImageButton rewindButton;
    private ImageButton forwardButton;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //check for permission
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Request the permission.
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_REQUEST_CODE);
        }
        else{
            loadSongs_listView();
        }

        seekBar = (SeekBar) findViewById(R.id.progressBar);
        currentTime = (TextView) findViewById(R.id.currentTime);
        totalDuration = (TextView) findViewById(R.id.totalDuration);
        playButton = (ImageButton) findViewById(R.id.play_button);
        rewindButton = (ImageButton) findViewById(R.id.back_button);
        forwardButton = (ImageButton) findViewById(R.id.forward_button);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case READ_STORAGE_REQUEST_CODE:
                loadSongs_listView();
                break;
            default:
                Toast.makeText(MainActivity.this,"ERROR",Toast.LENGTH_SHORT).show();
        }
    }

    private void loadSongs_listView(){

        final ListView listView = (ListView)findViewById(R.id.listView);
        File musicDir = new File(Environment.getExternalStorageDirectory().getPath());
        scanMP3_Files(musicDir);
        String[] songName_list = new String[songList.size()];

        for(int i = 0; i < songName_list.length; i++){
            songName_list[i] = songList.get(i).getName().substring(0, songList.get(i).getName().length()-4);
        }

        listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,songName_list));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt,
                                    long mylng) {
                File selectedFromList = songList.get(myItemInt);
                mp3Player.stop();
                mp3Player.load(selectedFromList.getPath());
                playButton.setImageResource(R.drawable.pause_button);
                seekBar.setMax(mp3Player.getDuration()/1000);

                totalDuration.setText(String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(mp3Player.getDuration())% TimeUnit.HOURS.toMinutes(1),
                        TimeUnit.MILLISECONDS.toSeconds(mp3Player.getDuration())% TimeUnit.MINUTES.toSeconds(1)));

                MainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if(mp3Player != null){
                            int mCurrentPosition = mp3Player.getProgress() / 1000;
                            seekBar.setProgress(mCurrentPosition);

                            currentTime.setText(String.format("%02d:%02d",
                                    TimeUnit.MILLISECONDS.toMinutes(mp3Player.getProgress())% TimeUnit.HOURS.toMinutes(1),
                                    TimeUnit.MILLISECONDS.toSeconds(mp3Player.getProgress())% TimeUnit.MINUTES.toSeconds(1)));
                        }
                        mHandler.postDelayed(this, 1000);
                    }
                });

                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }

                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if(mp3Player != null && fromUser){
                            mp3Player.seek(progress * 1000);
                        }
                    }
                });

            }
        });

        Toast.makeText(MainActivity.this,"Total MP3 songs: "+ Integer.toString(songList.size()),Toast.LENGTH_SHORT).show();
    }

    private void scanMP3_Files(File directory){
        if(directory != null){
            File[] fileList = directory.listFiles();
            if(fileList != null && fileList.length > 0){
                for(File file : fileList){
                    if(file.isDirectory()){
                        scanMP3_Files(file);
                    }
                    else{
                        addSong(file);
                    }
                }
            }
        }
    }

    private void addSong(File file) {
        if(file.getName().toLowerCase().endsWith(".mp3")){
            songList.add(file);
        }
    }

    public void play_pauseOnClick(View view){
        if(mp3Player.getState() == MP3Player.MP3PlayerState.PLAYING){
            playButton.setImageResource(R.drawable.play_button);
            mp3Player.pause();
        }
        else if(mp3Player.getState() == MP3Player.MP3PlayerState.PAUSED){
            playButton.setImageResource(R.drawable.pause_button);
            mp3Player.play();
        }
        else if(mp3Player.getState() == MP3Player.MP3PlayerState.STOPPED){
            Toast.makeText(MainActivity.this,"Choose a song to play first.",Toast.LENGTH_SHORT).show();
        }

    }

}
