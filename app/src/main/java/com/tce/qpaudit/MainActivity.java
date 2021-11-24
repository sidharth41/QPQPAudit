package com.tce.qpaudit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;


import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    TextInputEditText a0,a1,a2,a3,a4,a5,a6,a7,a8,a9,a10;
    Button submit,view;
    String linkofpdf;
    PDFView pdfview;

    AutoCompleteTextView cnames;
    private static final int PERMISSION_REQUEST_CODE = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        a0=(TextInputEditText)findViewById(R.id.a0);
        a1=(TextInputEditText)findViewById(R.id.a1);
        a2=(TextInputEditText)findViewById(R.id.a2);
        a3=(TextInputEditText)findViewById(R.id.a3);
        a4=(TextInputEditText)findViewById(R.id.a4);
        a5=(TextInputEditText)findViewById(R.id.a5);
        a6=(TextInputEditText)findViewById(R.id.a6);
        a7=(TextInputEditText)findViewById(R.id.a7);
        a8=(TextInputEditText)findViewById(R.id.a8);
        a9=(TextInputEditText)findViewById(R.id.a9);
        a10=(TextInputEditText)findViewById(R.id.a10);
        pdfview = findViewById(R.id.pdfviewer);
        submit=findViewById(R.id.submit_input);
        view=findViewById(R.id.view_pdf);
        cnames=(AutoCompleteTextView) findViewById(R.id.coursename);


        String[] coursenames = new String[] {"Data Mining","Information Sec","Web Technologies","Accounting and Finance"};
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        getApplicationContext(),
                        R.layout.dropdown_item,
                        coursenames);
        cnames.setAdapter(adapter);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
                    //system OS >= Marshmallow(6.0), check if permission is enabled or not
                    if (checkPermission()){
                        savePdf();
                    }
                    else {
                        //permission already granted, call save pdf method
                        requestPermission();
                    }
                }
                else {
                    //system OS < Marshmallow, call save pdf method
                    savePdf();
                }
            }

        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference pdfextract = FirebaseDatabase.getInstance().getReference("Courses").child(cnames.getText().toString());
                pdfextract.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        linkofpdf = snapshot.getValue().toString();

                        new RetrivePdfStream().execute(linkofpdf);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });




    }
    class RetrivePdfStream extends AsyncTask<String, Void, InputStream> {

        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try {

                // adding url
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                // if url connection response code is 200 means ok the execute
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }
            }
            // if error return null
            catch (IOException e) {
                return null;
            }
            return inputStream;
        }

        @Override
        // Here load the pdf and dismiss the dialog box
        protected void onPostExecute(InputStream inputStream) {
            pdfview.setVisibility(View.VISIBLE);
            pdfview.fromStream(inputStream).load();

            Toast.makeText(MainActivity.this, "Loaded", Toast.LENGTH_SHORT).show();

        }
    }
    private boolean checkPermission() {
        // checking of permissions.
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    private void savePdf() {

        Document mDoc = new Document();

        String mFileName = cnames.getText().toString()+" "+a0.getText().toString();

        String mFilePath = Environment.getExternalStorageDirectory() + "/" + mFileName + ".pdf";

        try {

            PdfWriter.getInstance(mDoc, new FileOutputStream(mFilePath));

            mDoc.open();

            String completetext= "Course Name: "+cnames.getText().toString()+"\nFaculty In-Charge: "+a0.getText().toString()+"\n1.Questions are clear and definite with no ambiguities:\n"+a1.getText().toString()+"\n2.Course code, course name, date and session are given as per CAT timetable\n"+a2.getText().toString()+"\n3.Use of appropriate keyword for the revised Bloom level\n"+a3.getText().toString()+"\n" +
                    "4. Question paper is set for the Maximum marks allotted\n"+a4.getText().toString()+"\n5.Correctness of categorization of each Question as per CO list given in the Syllabus\n"+a5.getText().toString()+"\n6.Consistency of mapping of each question w.r.t revised Bloom's level and the CO\n"+a6.getText().toString()+"\n7.Correctness weightage of each question as per assessment pattern\n"+a7.getText().toString()+"\n8.Split-up marks for each sub-divisions are given correctly in the Question paper\n"+a8.getText().toString()+"\n9.Coverage of syllabus\n"+a9.getText().toString()+"\n10.Other Remarks:\n"+a10.getText().toString();






            mDoc.add(new Paragraph(completetext));


            mDoc.close();

            Toast.makeText(this, mFileName +".pdf\nis saved to\n"+ mFilePath, Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){

            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {

                // after requesting permissions we are showing
                // users a toast message of permission granted.
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
                    Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denined.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }
}