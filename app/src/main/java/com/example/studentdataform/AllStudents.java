package com.example.studentdataform;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Locale;

public class AllStudents extends AppCompatActivity {
    FloatingActionButton fab;
    SharedPreferences sharedPreferences;
    ArrayList<Student> arr;
    TextView name,email,phone,dob;
    ImageView profilePic;
    ViewGroup root;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_students);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        root = findViewById(R.id.studentList);
        setData();
        fab = findViewById(R.id.addNew);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(AllStudents.this,MainActivity.class),100);
            }
        });




    }

    void setData(){
        /* Reading from shared prefences as string data */
        String s = sharedPreferences.getString("arrayStudents",null);
        arr = new Gson().fromJson(s,new TypeToken<ArrayList<Student>>(){}.getType());
        if(arr == null){
            arr = new ArrayList<>();
        }
        /* finished reading */
        for(int i=0;i<arr.size();i++) {

            View view = LayoutInflater.from(this).inflate(R.layout.student_view, null);
            name = view.findViewById(R.id.name_out);
            email = view.findViewById(R.id.email_out);
            phone = view.findViewById(R.id.phone_out);
            dob = view.findViewById(R.id.dob_out);
            profilePic = view.findViewById(R.id.profileImage_out);
            name.setText("Name :" + arr.get(i).getName());
            email.setText("Email :" + arr.get(i).getEmail());
            phone.setText("Phone :" + arr.get(i).getPhone());
            dob.setText("DOB :" + arr.get(i).getDob());
            profilePic.setImageBitmap(getBitmapFromString(arr.get(i).getProfilePic()));
            root.addView(view);
        }

    }

    Bitmap getBitmapFromString(String bitmap){
        byte[] imageAsBytes = Base64.decode(bitmap.getBytes(),Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100){
            setData();
        }
    }

}