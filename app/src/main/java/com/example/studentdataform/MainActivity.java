package com.example.studentdataform;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    Spinner salutation,emailDomain,countryCode;
    EditText etName,etEmail,etPhone,etDob;
    ImageView ivDob,ivProfileImage;
    Button choosePic,save;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String sal[],domains[],cCode[];
    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date;
    Bitmap bitmap;
    Student student;
    ArrayList<Student> arr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        salutation = findViewById(R.id.salutation);
        emailDomain = findViewById(R.id.emailDomain);
        countryCode = findViewById(R.id.countryCode);
        etName = findViewById(R.id.name);
        etEmail = findViewById(R.id.email);
        etPhone = findViewById(R.id.phone);
        etDob = findViewById(R.id.dob);
        ivDob = findViewById(R.id.dobIcon);
        ivProfileImage = findViewById(R.id.profileImage);
        choosePic = findViewById(R.id.browseImage);
        save = findViewById(R.id.submit_data);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();

        /* Reading from shared prefences as string data */
        String s = sharedPreferences.getString("arrayStudents",null);
        arr = new Gson().fromJson(s,new TypeToken<ArrayList<Student>>(){}.getType());
        if(arr == null){
            arr = new ArrayList<>();
        }
        /* finished reading */

        /* Writing data to shared preferences using json */
       // editor.putString("arrayStudents",new Gson().toJson(arr));
        //editor.commit();
        /* finished writing */



        sal = getResources().getStringArray(R.array.title);
        salutation.setAdapter(setSpinnerData(sal));

        domains = getResources().getStringArray(R.array.domains);
        emailDomain.setAdapter(setSpinnerData(domains));

        cCode = getResources().getStringArray(R.array.country_code);
        countryCode.setAdapter(setSpinnerData(cCode));

        ivDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchDatePicker();
            }
        });

        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day_of_month) {
                      myCalendar.set(Calendar.YEAR,year);
                      myCalendar.set(Calendar.MONTH,month);
                      myCalendar.set(Calendar.DAY_OF_MONTH,day_of_month);
                      SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
                      etDob.setText(sdf.format(myCalendar.getTime()));
            }
        };

        choosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 getPicture();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                student = new Student();
                String name = salutation.getSelectedItem().toString()+" "+etName.getText().toString();
                student.setName(name);
                String email = etEmail.getText().toString()+"@"+emailDomain.getSelectedItem().toString();
                student.setEmail(email);
                String phone = countryCode.getSelectedItem().toString()+etPhone.getText().toString();
                student.setPhone(phone);
                String dob = etDob.getText().toString();
                student.setDob(dob);
                student.setProfilePic(covertBitmapToString(bitmap));
                arr.add(student);
                setResult(100);
                finish();
            }
        });

   }

    private String covertBitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();

        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    @Override
    protected void onStop() {
        super.onStop();
        /* Writing data to shared preferences using json */
         editor.putString("arrayStudents",new Gson().toJson(arr));
         editor.commit();
        /* finished writing */
    }

    private void getPicture() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                ivProfileImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                Log.e("hello",e.getMessage());
            }

        }
    }

    private void launchDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,date,
                myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();

    }

    private ArrayAdapter setSpinnerData(String[] data) {
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,data);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return aa;
    }
}