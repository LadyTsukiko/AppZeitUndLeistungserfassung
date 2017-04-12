package com.example.alex.zeit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ErfassungActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_erfassung);

        Intent intent = getIntent();
        String mitarbeiterId = intent.getStringExtra("MITARBEITER");

        TextView textView = (TextView) findViewById(R.id.various);
        textView.setText(mitarbeiterId);


    }
}
