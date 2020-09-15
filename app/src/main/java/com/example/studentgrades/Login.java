package com.example.studentgrades;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Login extends AppCompatActivity {
    public final String TAG = "demo";
    static String TOKEN_KEY = "TOKEN";
    private final OkHttpClient client = new OkHttpClient();
    Button login, signup;
    EditText email, password;
    String emailstring, passwordstring, body, token;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        setTitle("Login");

        email = findViewById(R.id.emailLogin);
        password = findViewById(R.id.passwordLogin);
        login = findViewById(R.id.loginButton);
        signup = findViewById(R.id.signupButton);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailstring = email.getText().toString();
                passwordstring = password.getText().toString();
                if(isEmailValid() && isPasswordFilled()) {
                    String url = "http://ec2-18-207-196-44.compute-1.amazonaws.com/api/login";

                    RequestBody formbody = new FormBody.Builder()
                            .add("email", emailstring)
                            .add("password", passwordstring)
                            .build();

                    Request request = new Request.Builder()
                            .url(url)
                            .post(formbody)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            e.printStackTrace();
                            Log.d(TAG, "onFailure: " + e.getMessage());
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            ResponseBody responseBody = response.body();
                            if(response.isSuccessful()){
                                body = responseBody.string();
                                Log.d(TAG, "onResponse: " + body);
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ObjectMapper mapper = new ObjectMapper();
                                    try{
                                        user = mapper.readValue(body, User.class);
                                        token = user.getToken();
                                        Intent intent = new Intent(Login.this, GradesActivity.class);
                                        intent.putExtra(TOKEN_KEY, token);
                                        startActivity(intent);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    });

                } else{
                    Toast.makeText(Login.this, "Please enter email and password.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private boolean isEmailValid() {
        return email.getText().toString().length() > 0 && email.getText().toString().contains("@") &&
                email.getText().toString().contains(".");
    }
    private boolean isPasswordFilled() {
        return password.getText().toString().length() > 0;
    }
}