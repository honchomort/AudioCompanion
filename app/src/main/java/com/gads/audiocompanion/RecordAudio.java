package com.gads.audiocompanion;

import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class RecordAudio extends AppCompatActivity {
    private Button startbtn, stopbtn, playbtn, stopplay, saveupload;
    private EditText description;
    private TextView tvPath;
    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private static final String LOG_TAG = "AudioRecording";
    private static String mFileName = null;
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    FirebaseStorage mStorage;
    FirebaseDatabase mDatabase;
    String descriptionText = null;
    Uri audioUri = null;

    public boolean CheckPermissions() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void RequestPermissions() {
        ActivityCompat.requestPermissions(RecordAudio.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (permissionToRecord && permissionToStore) {
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {

                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_audio);

        mStorage = FirebaseStorage.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        File folder = new File(RecordAudio.this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "Your directory name");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        startbtn = findViewById(R.id.btnRecord);
        stopbtn = findViewById(R.id.btnStop);
        playbtn = findViewById(R.id.btnPlay);
        stopplay = findViewById(R.id.btnStopPlay);
        saveupload = findViewById(R.id.btnSave);
        description = findViewById(R.id.textDescription);
        tvPath = findViewById(R.id.tvPath);
        stopbtn.setEnabled(false);
        playbtn.setEnabled(false);
        stopplay.setEnabled(false);
        saveupload.setEnabled(false);
        String savePath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/";
        mFileName = savePath + "FindMe.3gp";

        startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CheckPermissions()) {
                    stopbtn.setEnabled(true);
                    startbtn.setEnabled(false);
                    playbtn.setEnabled(false);
                    stopplay.setEnabled(false);
                    mRecorder = new MediaRecorder();
                    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    mRecorder.setOutputFile(mFileName);
                    try {
                        mRecorder.prepare();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "prepare() failed");
                    }
                    mRecorder.start();
                    Toast.makeText(getApplicationContext(), "Recording Started", Toast.LENGTH_LONG).show();
                }
                else
                {
                    RequestPermissions();
                }
            }
        });
        stopbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopbtn.setEnabled(false);
                startbtn.setEnabled(true);
                playbtn.setEnabled(true);
                stopplay.setEnabled(false);
                saveupload.setEnabled(true);
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
                tvPath.setText(mFileName);
                Toast.makeText(getApplicationContext(), "Recording Stopped", Toast.LENGTH_LONG).show();
            }
        });
        playbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopbtn.setEnabled(false);
                startbtn.setEnabled(true);
                playbtn.setEnabled(false);
                stopplay.setEnabled(true);
                mPlayer = new MediaPlayer();
                try {
                    mPlayer.setDataSource(mFileName);
                    mPlayer.prepare();
                    mPlayer.start();
                    Toast.makeText(getApplicationContext(), "Recording Started Playing", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "prepare() failed");
                }
            }
        });
        stopplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.release();
                mPlayer = null;
                stopbtn.setEnabled(false);
                startbtn.setEnabled(true);
                playbtn.setEnabled(true);
                stopplay.setEnabled(false);
                Toast.makeText(getApplicationContext(),"Playing Audio Stopped", Toast.LENGTH_SHORT).show();
            }
        });
        saveupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(RecordAudio.this, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    stopbtn.setEnabled(false);
                    startbtn.setEnabled(true);
                    playbtn.setEnabled(true);
                    stopplay.setEnabled(false);
                    descriptionText = description.getText().toString();
                    if (TextUtils.isEmpty(descriptionText)) {
                        Toast.makeText(getApplicationContext(), "Please enter a description", Toast.LENGTH_LONG).show();
                    } else {
                        File dir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                        if (dir.exists()) {
                            File from = new File(dir, "FindMe.3gp");
                            File to = new File(dir, descriptionText + ".3gp");
                            if (from.exists())
                                from.renameTo(to);
                            audioUri = Uri.fromFile(new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + descriptionText + ".3gp"));
                            uploadFile(audioUri);

                        }
                    }
                } else {
                    ActivityCompat.requestPermissions(RecordAudio.this, new String[]{READ_EXTERNAL_STORAGE}, 88);
                }
            }

            private void uploadFile(Uri audioUri) {

                final String uploadFileName = descriptionText + ".3gp";
                final StorageReference storageReference = mStorage.getReference();

                storageReference.child("AudioUploads").child(uploadFileName).putFile(audioUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(RecordAudio.this, "File uploaded successfully", Toast.LENGTH_SHORT).show();
                        Task<Uri> dUrl = storageReference.child("AudioUploads").child(uploadFileName).getDownloadUrl();
                        System.out.println(dUrl);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RecordAudio.this, "File not uploaded", Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                    }
                });
            }



        });
    }

        }
