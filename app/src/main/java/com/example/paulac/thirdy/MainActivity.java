package com.example.paulac.thirdy;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText etUsername;
    private EditText etPassword;
    private EditText etReenter;
    private EditText etAnswer;

    private Button buttonRegister;

    Spinner security;

    private static final String REGISTER_URL = "http://10.4.101.44/sbs/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etReenter = (EditText) findViewById(R.id.etReenter);
        etAnswer = (EditText) findViewById(R.id.etAnswer);



        buttonRegister = (Button) findViewById(R.id.signup);

        buttonRegister.setOnClickListener(this);


        security = (Spinner) findViewById(R.id.spinner);
        String[] questions = new String[]{"--", "What is the name of your first pet?",
                "What is your favorite movie?",
                "What is your favorite food?",
                "What is your favorite drink?",
                "What is your favorite color?"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, questions);
        security.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {
        if(v == buttonRegister){
            registerUser();
        }
    }

    private void registerUser() {
        String username = etUsername.getText().toString().trim().toLowerCase();
        String password = etPassword.getText().toString().trim().toLowerCase();
        String question = security.getSelectedItem().toString().trim().toLowerCase();
        String answer = etAnswer.getText().toString().trim().toLowerCase();
        String key = "70930f27";

        Register(username, password, question, answer, key);
    }

    private void Register(String username, String password, String question, String answer, String key) {
        class RegisterUser extends AsyncTask<String, Void, String>{
            ProgressDialog loading;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this, "Please Wait",null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(String... params) {

                HashMap<String, String> data = new HashMap<String,String>();
                data.put("username",params[0]);
                data.put("password",params[1]);
                data.put("question", params[2]);
                data.put("answer", params[3]);
                data.put("key", "70930f27");

                String result = sendPostReq(REGISTER_URL + "sign_up.php",data);

                return  result;
            }

            public String sendPostReq(String requestURL,
                                      HashMap<String, String> postDataParams) {
                URL url;
                String response = "";
                try {
                    url = new URL(requestURL);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(15000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);


                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(getPostDataString(postDataParams));

                    writer.flush();
                    writer.close();
                    os.close();
                    int responseCode=conn.getResponseCode();

                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        response = br.readLine();
                    }
                    else {
                        response="Error Registering";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return response;
            }

        }

        RegisterUser ru = new RegisterUser();
        ru.execute(username, password, question, answer, key);
    }


    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}
