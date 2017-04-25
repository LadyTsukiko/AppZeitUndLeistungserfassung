package com.example.alex.zeit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public  void okClicked (View view){
       //Get MitarbeiterId and PW out of the respective fields
        EditText editText = (EditText) findViewById(R.id.mitarbeiterId);
        String mitarbeiterId = editText.getText().toString();
        editText = (EditText) findViewById(R.id.pw );
        String pw = editText.getText().toString();

        //ToDo check if pw valid first
        Intent intent = new Intent(this, ErfassungActivity.class);
        intent.putExtra("MITARBEITER", mitarbeiterId);
        startActivity(intent);
    }
}
