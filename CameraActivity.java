package com.example.hrishi.c2k;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Provider;
import java.util.ArrayList;
import java.util.Locale;
import java.util.jar.*;

public class CameraActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private TextView e;
    private Bitmap bitmap;
    private File imageFile;
    private static TextToSpeech textToSpeech;
    private DatabaseReference myRef;
    static private String finalResult;
 // private StorageReference mStorageRef;
    private Uri tempUri;
    private FirebaseAuth mAuth;

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        e = (TextView) findViewById(R.id.camera_textView);
        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        finalResult = "";

        textToSpeech = new TextToSpeech(this, this);

        //textToSpeech.speak("HELLO USER HOW ARE YOU", TextToSpeech.QUEUE_ADD,null);
        //Log.d("abcOnCreate1","HELLO USER HOW ARE YOU"+textToSpeech.isSpeaking());
        //boolean isSpeak = textToSpeech.isSpeaking();
        //while(isSpeak)
        //    isSpeak = textToSpeech.isSpeaking();
        //Log.d("abcOnCreate2","HELLO USER HOW ARE YOU");
        //textToSpeech.speak("HELLO USER HOW ARE YOU", TextToSpeech.QUEUE_FLUSH,null);

        createActivity();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("abc0","abc0");
        if(requestCode==0)
        {
            switch (resultCode)
            {
                case Activity.RESULT_OK:
                    if(imageFile.exists())
                    {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 8;
                        bitmap = BitmapFactory.decodeFile(tempUri.getPath(),options);
                        Log.d("abc1","abc1");
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                        byte[] bytes = baos.toByteArray();
                        String base64img = Base64.encodeToString(bytes,Base64.DEFAULT);
                        Log.d("abc2","abc2");
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        myRef.child("User").child(user.getUid()).setValue(base64img);
                        //__________________1/11
                        ImageView i = (ImageView) findViewById(R.id.camera_imageView);
                        i.setImageBitmap(bitmap);
                        //_________________
                        Toast.makeText(this,"done",Toast.LENGTH_SHORT).show();
                        Log.d("abc3","abc3");





                        myRef.child("Result").addChildEventListener(new ChildEventListener() {

                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s)
                            {
                                String res = dataSnapshot.getValue(String.class);
                                Log.d("abcFromFirebaseRes1",res);
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                String uid = user.getUid();

                                if(dataSnapshot.getKey().equals(uid))
                                {
                                    Log.d("abcFromFirebaseRes2",res);

                                    finalResult = res;

                                    e.setText(finalResult);

                                    textToSpeech.speak(finalResult, TextToSpeech.QUEUE_FLUSH, null);
                                    myRef.child("Result").child(dataSnapshot.getKey()).setValue(null);
                                    try
                                    {
                                        boolean isSpe = textToSpeech.isSpeaking();
                                        while(isSpe)
                                            isSpe = textToSpeech.isSpeaking();
                                        //Thread.sleep(10000);
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }
                                    Log.d("abcActivityChange", "abc");
                                    //createActivity();
                                }
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s)
                            {

                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot)
                            {

                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s)
                            {

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError)
                            {

                            }
                        });



                    }
                    else
                    {
                        Toast.makeText(this,"FILE NOT SAVED",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Activity.RESULT_CANCELED:
                    Log.d("abc-1","abc-1");
            }
        }
    }

    private void createActivity()
    {
        imageFile = new File(Environment.getExternalStorageDirectory(),"test.jpg");

        if(!imageFile.exists())
        {
            try
            {
                imageFile.createNewFile();
                Log.d("abc10","abc10");
            }
            catch (IOException e)
            {
                Log.d("abc20","abc20");
                e.printStackTrace();
            }
        }
        else
        {
            imageFile.delete();

            try
            {
                Log.d("abc30","abc30");
                imageFile.createNewFile();
            } catch (IOException e)
            {
                Log.d("abc40","abc40");
                e.printStackTrace();
            }
        }

        tempUri = Uri.fromFile(imageFile);


        //permission camera capture
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,tempUri);

        startActivityForResult(intent,0);
    }

    @Override
    public void onInit(int i) {
        //public void onInit(int i) {
            if(i==TextToSpeech.SUCCESS)
            {
                Log.d("abc1","SUCCE");
                textToSpeech.setLanguage(Locale.ENGLISH);
                textToSpeech.speak(finalResult,TextToSpeech.QUEUE_FLUSH, null);
                boolean r = textToSpeech.isSpeaking();
                while (r)
                    r = textToSpeech.isSpeaking();
            }
        }
    }
