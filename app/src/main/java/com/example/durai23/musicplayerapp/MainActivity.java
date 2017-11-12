package com.example.durai23.musicplayerapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MP3Player mp3Player = new MP3Player();
    private ArrayList<File> songList = new ArrayList<File>();
    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //check for permission
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Request the permission.
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        final ListView listView = (ListView)findViewById(R.id.listView);

        File musicDir = new File(Environment.getExternalStorageDirectory().getPath());

        Toast.makeText(this,musicDir.toString(),Toast.LENGTH_SHORT).show();

        scanMP3_Files(musicDir);

        String[] songName_list = new String[songList.size()];

        for(int i = 0; i < songName_list.length; i++){
            songName_list[i] = songList.get(i).getName();
        }

        listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,songName_list));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt,
                                    long mylng) {
                File selectedFromList = songList.get(myItemInt);
                Toast.makeText(MainActivity.this,selectedFromList.getName().toString(),Toast.LENGTH_SHORT).show();

                mp3Player.load(selectedFromList.getPath());
            }
        });
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
}
