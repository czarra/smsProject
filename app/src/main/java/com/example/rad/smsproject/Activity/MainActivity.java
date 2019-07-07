package com.example.rad.smsproject.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rad.smsproject.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button sendSMS, openFile;
    private EditText smsText;
    private TextView fileName;
    private Uri selectedfile;
    private ProgressBar progressBar;
    private String textToSend;
    private File file;
//    private String[] smsNumbers = {
//
//    };
    private ArrayList<String> smsNumbers = new ArrayList<String>();

    private String basicMessage = System.getProperty("line.separator")+"Send by My App";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        progressBar= (ProgressBar) findViewById(R.id.progressBar);
        sendSMS = (Button) findViewById(R.id.sendSMS);
        smsText = (EditText) findViewById(R.id.smsText);
        openFile = (Button) findViewById(R.id.openFile);
        fileName = (TextView) findViewById(R.id.fileName);

        sendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                RetrieveSend retrieveSend = new RetrieveSend() {
                    @Override
                    protected void onPreExecute() {
//                        if(file != null  && getExtension(file).equals("txt")) {
                            sendSMS.setVisibility(View.INVISIBLE);
                            openFile.setVisibility(View.INVISIBLE);
                            progressBar.setVisibility(View.VISIBLE);
                            textToSend = smsText.getText().toString();

//                        } else {
//                            Toast.makeText(MainActivity.this, "Please choose a file (.txt)!!!", Toast.LENGTH_SHORT).show();
//                        }

                    }
                    @Override
                    protected void onPostExecute(String error) {
                        if(!error.isEmpty()) {
                            Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                        sendSMS.setVisibility(View.VISIBLE);
                        openFile.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                };
                retrieveSend.execute();

            }

        });

        openFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    // start runtime permission
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                            PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},99);
                    } else {
                        Log.e("ss", "get permision-- already granted ");
                        showFileChooser();
                    }
                }else {
                    //readfile();
                    showFileChooser();
                }


            }
        });
    }

    private void showFileChooser(){
        try {
            Intent intent = new Intent()
                    .setType("*/*")
                    .setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(intent, "Select a file"), 123);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, "Can't open File", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) !=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.SEND_SMS, Manifest.permission.SEND_SMS},99);
        } else {
            return true;
        }
        return false;
    }

    private String getPath(Uri uri){
        String toRemove = "/document/raw:";
        return uri.getPath().replace(toRemove, "");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 123 && resultCode == RESULT_OK) {
            selectedfile = data.getData(); //The uri with the location of the file
            Log.e("a", getPath(selectedfile));//.getPath());
            file = new File( getPath(selectedfile));
            if(file.exists()) {
                String extension = getExtension(file);
                if (extension.equals("txt")) {
                    fileName.setText(file.getName());
                    fileName.setVisibility(View.VISIBLE);
                } else {
                    fileName.setVisibility(View.INVISIBLE);
                    file = null;
                }
            } else {
                Log.e("error","file not exist");
                fileName.setVisibility(View.INVISIBLE);
                file = null;
            }
        }
    }

    /*
     * Get the extension of a file.
     */
    private static String getExtension(File f) {
        String ext = "";
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        Log.e("ext",ext);
        return ext;
    }

    private void setSmsNumbers(File f){
        smsNumbers =  new ArrayList<String>();
        //
        try{

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            String[] items ;
            while ((line = br.readLine()) != null) {
                items = line.split(";");
                for (String item : items)
                {
                    smsNumbers.add(item.replace("-", "").replace(" ",""));
                }

            }
            br.close();
        }

        catch (IOException e) {
            e.printStackTrace();
            Log.e("error","can't read "+e.getMessage());

        }

    }

    private String sendSMS(String number, String message){
        String err = "";
        try {
            if(!number.isEmpty()) {
                SmsManager.getDefault().sendTextMessage(number, null, message, null, null);
            }
        }catch (Exception e){
            //Toast.makeText(MainActivity.this,number +"  "+ e.getMessage(), Toast.LENGTH_SHORT).show();
            err = e.getMessage();
        }
        return err;
    }

    class RetrieveSend extends AsyncTask<String, String, String> {

        protected String doInBackground(String... urls) {
            String error = "Done!";
            Integer iterator = 0;
            try {
                if(file != null  && getExtension(file).equals("txt")) {
                    if (!smsText.getText().toString().isEmpty()) {
                        setSmsNumbers(file);
                        if (!smsNumbers.isEmpty()) {
                            for (String number : smsNumbers) {
                                Log.e("number", number);
                                try {
                                    if (checkPermission()) {
                                       String err = sendSMS(number, textToSend + basicMessage);
                                       if(!err.isEmpty()){
                                           iterator ++;
                                       }
                                    }
                                } catch (android.content.ActivityNotFoundException ex) {
//                                    Toast.makeText(MainActivity.this, "Can't send SMS", Toast.LENGTH_SHORT).show();
                                    error =  "Can't send SMS";
                                }
                            }
                        } else {
//                            Toast.makeText(MainActivity.this, "Empty phone list", Toast.LENGTH_SHORT).show();
                            error = "Empty phone list";
                        }
                    } else {
//                        Toast.makeText(MainActivity.this, "Message can't be empty!!!", Toast.LENGTH_SHORT).show();
                        error = "Message can't be empty!!!";
                    }
                    if(iterator>0){
                        error = "Wrong data in "+iterator+" numbers";
                    }
                } else {
                    error = "Please choose a file (.txt)!!!";
                }
            } catch (Exception exp) {
                Log.e("error", exp.getMessage());
                error =  exp.getMessage();
//                return false;
            }
            return error;
        }
    }
}
