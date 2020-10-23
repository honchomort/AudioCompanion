package com.gads.audiocompanion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class ListAudio extends AppCompatActivity {

    private ImageButton gotoRecord;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_audio);

        gotoRecord = findViewById(R.id.gotoRecordAudio);
        gotoRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RecordAudio.class);
                startActivity(intent);
            }
        });


        }


    }

