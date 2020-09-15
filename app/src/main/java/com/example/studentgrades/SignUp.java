package com.example.studentgrades;

import androidx.appcompat.app.AppCompatActivity;

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

import static com.example.studentgrades.Login.TOKEN_KEY;

public class SignUp extends AppCompatActivity {
    public final String TAG = "demo";
    private final OkHttpClient client = new OkHttpClient();
    EditText firstName, lastName, email, password, password2;
    Button signup, cancel;
    String emailstring, passwordstring, fname, lname, body, token;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("Sign Up");
        firstName = findViewById(R.id.firstNameSignUp);
        lastName = findViewById(R.id.lastNameSignUp);
        email = findViewById(R.id.emailSignUp);
        password = findViewById(R.id.passwordSignUp);
        password2 = findViewById(R.id.confirmPasswordSignUp);
        signup = findViewById(R.id.signUpButtonSignUpPage);
        cancel = findViewById(R.id.cancelButton);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEverythingFilled() && doPasswordsMatch()) {
                    emailstring = email.getText().toString();
                    passwordstring = password.getText().toString();
                    fname = firstName.getText().toString();
                    lname = lastName.getText().toString();

                    String url = "http://ec2-18-207-196-44.compute-1.amazonaws.com/api/signup";

                    RequestBody formbody = new FormBody.Builder()
                            .add("email", emailstring)
                            .add("password", passwordstring)
                            .add("fname",fname)
                            .add("lname",lname)
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
                                        Intent intent = new Intent(SignUp.this, GradesActivity.class);
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
                    Toast.makeText(SignUp.this, "Please fill blanks and make sure password matches.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private boolean isFirstNameFilled() {
        return firstName.getText().toString().length() > 0;
    }
    private boolean isLastNameFilled() {
        return lastName.getText().toString().length() > 0;
    }
    private boolean isEmailValid() {
        return email.getText().toString().length() > 0 && email.getText().toString().contains("@") &&
                email.getText().toString().contains(".");
    }
    private boolean isPasswordFilled() {
        return password.getText().toString().length() > 0;
    }
    private boolean isPassword2Filled() {
        return password2.getText().toString().length() > 0;
    }
    private boolean doPasswordsMatch() {
        return password.getText().toString().equals(password2.getText().toString());
    }
    private boolean isEverythingFilled() {
        return isFirstNameFilled() && isLastNameFilled() && isEmailValid() && isPasswordFilled() && isPassword2Filled();
    }
}