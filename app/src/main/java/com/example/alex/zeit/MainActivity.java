package com.example.alex.zeit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private final String tag_string_req = "log_in_req";

    private static final String TAG = MainActivity.class.getSimpleName();
    boolean ret = false;

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

        if(checkLogin(mitarbeiterId, pw)){
            Intent intent = new Intent(this, ErfassungActivity.class);
            intent.putExtra("MITARBEITER", mitarbeiterId);
            startActivity(intent);
        }
        else {
            editText.setText("");
            TextView errorV =(TextView) findViewById(R.id.errorView);
            errorV.setVisibility(View.VISIBLE);
        }

    }

    private boolean checkLogin(final String name, final String pw){

        ret = false;


        StringRequest strReq = new StringRequest(Request.Method.POST,
                Connections.URL_LOGIN, new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        // Create login session
                        ret = true;}

             /*else {
                // Error in login. Get the error message
                String errorMsg = jObj.getString("error_msg");
                Toast.makeText(getApplicationContext(),
                        errorMsg, Toast.LENGTH_LONG).show();
            }*/
        } catch (JSONException e) {
            // JSON error
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
}, new Response.ErrorListener() {

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e(TAG, "Login Error: " + error.getMessage());
        Toast.makeText(getApplicationContext(),
        error.getMessage(), Toast.LENGTH_LONG).show();
        //todo do smth smart
        }
        }) {

   /* @Override
    protected Map<String, String> getParams() {
        // Posting parameters to login url
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", name);
        params.put("password", pw);

        return params;
        }*/

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);





        return  ret;
    }
}
