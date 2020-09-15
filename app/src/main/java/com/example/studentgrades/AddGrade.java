package com.example.studentgrades;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

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

public class AddGrade extends AppCompatActivity {
    public final String TAG = "demo";
    private final OkHttpClient client = new OkHttpClient();
    RadioGroup radiogroup;
    EditText coursename, credithours;
    Button cancel, submit;
    String token, body, letter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_grade);
        setTitle("Add Grade");
        if(getIntent() != null && getIntent().getExtras() != null) {
            token = getIntent().getStringExtra(TOKEN_KEY);
        }
        coursename = findViewById(R.id.courseName);
        credithours = findViewById(R.id.creditHours);
        submit = findViewById(R.id.submitbutton);
        cancel = findViewById(R.id.cancelButtonGradePage);
        radiogroup = findViewById(R.id.radioGroup);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://ec2-18-207-196-44.compute-1.amazonaws.com/api/grade/add";

                if(radiogroup.getCheckedRadioButtonId() == R.id.buttonA){
                    letter = "A";
                } else if(radiogroup.getCheckedRadioButtonId() == R.id.radioButton2) {
                    letter = "B";
                }else if(radiogroup.getCheckedRadioButtonId() == R.id.radioButton3) {
                    letter = "C";
                }else if(radiogroup.getCheckedRadioButtonId() == R.id.radioButton4) {
                    letter = "D";
                }else if(radiogroup.getCheckedRadioButtonId() == R.id.radioButton5) {
                    letter = "F";
                }

                RequestBody formbody = new FormBody.Builder()
                        .add("course_name", coursename.getText().toString())
                        .add("credit_hours", credithours.getText().toString())
                        .add("letter_grade", letter)
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

                            }
                        });
                    }
                });
            }
        });


    }
}