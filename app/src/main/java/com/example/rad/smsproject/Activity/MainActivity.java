package com.example.rad.smsproject.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.rad.smsproject.R;

import java.lang.reflect.Array;

public class MainActivity extends AppCompatActivity {

    public Button sendSMS;
    public EditText smsText;
    private String[] smsNumbers = {

    };
    private String basicMessage = System.getProperty("line.separator")+"Send by My App";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();

        sendSMS = (Button) findViewById(R.id.sendSMS);
        smsText = (EditText) findViewById(R.id.smsText);
        sendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( !smsText.getText().toString().isEmpty()) {
                    for (String number : smsNumbers) {
                        try {
                            if (checkPermission()) {
                                sendSMS(number, smsText.getText().toString()+basicMessage);
                            }
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(MainActivity.this, "Can't send SMS", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Message can't be empty!!!", Toast.LENGTH_SHORT).show();
                }


            }
        });
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

    private void sendSMS(String number, String message){
        SmsManager.getDefault().sendTextMessage(number, null, message, null, null);
    }
}
