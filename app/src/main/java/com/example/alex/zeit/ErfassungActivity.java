package com.example.alex.zeit;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ErfassungActivity extends AppCompatActivity {

    private Chronometer chronometer;
    private String mitarbeiterId;
    private final String tag_string_req = "erfassung_req";
    private static final String TAG = MainActivity.class.getSimpleName();
    private String datum;
    private Button start, stop, pause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_erfassung);

        populateSpinner();

        Intent intent = getIntent();
        mitarbeiterId = intent.getStringExtra("MITARBEITER");

        TextView textView = (TextView) findViewById(R.id.various);
        textView.setText("Hallo. Ihre Id ist:" + mitarbeiterId);
        chronometer = (Chronometer) findViewById(R.id.chronometer);

        start = (Button) findViewById(R.id.startButton);
        stop = (Button) findViewById(R.id.stopButton);
        pause = (Button) findViewById(R.id.pauseButton);
    }

    private void populateSpinner() {


        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                Connections.URL_GET, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jObj = new JSONArray(response);
                    //boolean error = jObj.getBoolean("error");
                    JSONArray projects = (JSONArray) jObj.get(0);
                    String projArr[] = new String[projects.length()];
                    for (int a=0; a<projects.length(); a++){
                       projArr[a] = projects.getJSONObject(a).optString("name");
                    }
                    Spinner projectSpinner = (Spinner) findViewById(R.id.Projects);
                    projectSpinner.setAdapter(new ArrayAdapter<String>(ErfassungActivity.this, android.R.layout.simple_spinner_dropdown_item, projArr));

                    JSONArray services = (JSONArray) jObj.get(1);
                    String servArr[] = new String[services.length()];
                    for (int a=0; a<services.length(); a++){
                        servArr[a] = services.getJSONObject(a).optString("name");
                    }
                    Spinner serviceSpinner = (Spinner) findViewById(R.id.Services);
                    serviceSpinner.setAdapter(new ArrayAdapter<String>(ErfassungActivity.this, android.R.layout.simple_spinner_dropdown_item, servArr));


                } catch (JSONException e) {
                    Toast toast = Toast.makeText(getApplicationContext(),"JSON Fehler projects!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Spinner Error: " + error.getMessage());
                Toast toast = Toast.makeText(getApplicationContext(),"Verbindungsfehler", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);
    }

    public void startClicked(View view){

        int stoppedMilliseconds = 0;

        String chronoText = chronometer.getText().toString();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        datum =  sdf.format(new Date());


        String array[] = chronoText.split(":");
        if (array.length == 2) {
            stoppedMilliseconds = Integer.parseInt(array[0]) * 60 * 1000
                    + Integer.parseInt(array[1]) * 1000;
        } else if (array.length == 3) {
            stoppedMilliseconds = Integer.parseInt(array[0]) * 60 * 60 * 1000
                    + Integer.parseInt(array[1]) * 60 * 1000
                    + Integer.parseInt(array[2]) * 1000;
        }
        chronometer.setBase(SystemClock.elapsedRealtime() - stoppedMilliseconds);
        chronometer.start();

        stop.setEnabled(true);
        pause.setEnabled(true);
        start.setEnabled(false);
    }
    public void stopClicked(View view){
        Spinner spinner = (Spinner) findViewById(R.id.Projects);
        String projekt = spinner.getSelectedItem().toString();

        spinner = (Spinner) findViewById(R.id.Services);
        String leistung = spinner.getSelectedItem().toString();

        String dauer = chronometer.getText().toString().replaceAll(":", "");

        makeRequest(dauer, projekt, leistung, datum);

        stop.setEnabled(false);
        pause.setEnabled(false);
        start.setEnabled(true);
    }

    private void makeRequest(final String dauer, final String projekt, final String leistung, final String datum) {


        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Connections.URL_ERFASSUNG, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        Toast toast = Toast.makeText(getApplicationContext(),"Die Leistung wurde erfolgreich erfasst", Toast.LENGTH_SHORT);
                        toast.show();
                        chronometer.setBase(SystemClock.elapsedRealtime());
                        chronometer.stop();
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(),"Datenbankfehler", Toast.LENGTH_SHORT);
                        toast.show();
                    }

                } catch (JSONException e) {
                    Toast toast = Toast.makeText(getApplicationContext(),"JSON Fehler!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            }, new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Login Error: " + error.getMessage());
                    Toast toast = Toast.makeText(getApplicationContext(),"Verbindungsfehler", Toast.LENGTH_SHORT);
                    toast.show();
                }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("Datum",datum );
                params.put("Dauer", dauer);
                params.put("Leistung", leistung);
                params.put("Projekt", projekt);
                params.put("MitarbeiterID", mitarbeiterId);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);
    }

    public void pauseClicked(View view){
        chronometer.stop();
        stop.setEnabled(true);
        pause.setEnabled(false);
        start.setEnabled(true);
    }
}
