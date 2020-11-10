package com.gads.audiocompanion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ListAudio extends AppCompatActivity {

    ImageButton gotoRecord;
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_audio);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String fileName = snapshot.getKey(); // return fileName
                String url = snapshot.getValue(String.class); //return URL for fileName

                ((RAdapter) mRecyclerView.getAdapter()).update(fileName,url);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        mRecyclerView = (RecyclerView) findViewById(R.id.recView);
        final LinearLayoutManager audioLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(audioLayoutManager);

        RAdapter rAdapter = new RAdapter(mRecyclerView,new ArrayList<String>(),ListAudio.this);
        mRecyclerView.setAdapter(rAdapter);


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

