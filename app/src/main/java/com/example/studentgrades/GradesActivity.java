package com.example.studentgrades;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.example.studentgrades.Login.TOKEN_KEY;

public class GradesActivity extends AppCompatActivity {
    public final String TAG = "demo";
    private final OkHttpClient client = new OkHttpClient();
    Button addGrade, logout;
    TextView gpa, hours;
    ListView listView;
    GradesAdapter adapter;
    Grades grade;
    List<Grade> grades = new ArrayList<>();
    String token, body, gpatext, hourstext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grades);
        setTitle("Grades");
        if(getIntent() != null && getIntent().getExtras() != null) {
            token = getIntent().getStringExtra(TOKEN_KEY);
        }
        addGrade = findViewById(R.id.addGradeButton);
        logout = findViewById(R.id.logoutButton);
        gpa = findViewById(R.id.gpaDisplay);
        hours = findViewById(R.id.hoursDisplay);
        listView = findViewById(R.id.listView);

        getGrades();

        addGrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GradesActivity.this, AddGrade.class);
                intent.putExtra(TOKEN_KEY, token);
                startActivity(intent);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GradesActivity.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
    }
public void getGrades(){
    String url = "http://ec2-18-207-196-44.compute-1.amazonaws.com/api/grades";

    Request request = new Request.Builder()
            .header("Authorization","BEARER " + token)
            .url(url)
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
                        grade = mapper.readValue(body, Grades.class);
                        grades = Arrays.asList(grade.getGrades());

                        getGPA();
                        gpa.setText(gpatext);
                        hours.setText(hourstext);

                        adapter = new GradesAdapter(GradesActivity.this,R.layout.activity_grades_layout, grades);
                        listView.setAdapter(adapter);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    });
}
    @Override
    protected void onResume() {
        super.onResume();
//        adapter.notifyDataSetChanged();
    }

    class GradesAdapter extends ArrayAdapter<Grade>{

        public GradesAdapter(@NonNull Context context, int resource, @NonNull List<Grade> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if(convertView == null){
                convertView = getLayoutInflater().inflate(R.layout.activity_grades_layout, parent, false);
            }
            Grade grades = getItem(position);
            String credithoursstring = grades.getCredit_hours() + " Credit Hours";
            ((TextView)convertView.findViewById(R.id.letterGrade)).setText(grades.getGrade());
            ((TextView)convertView.findViewById(R.id.courseNameLayout)).setText(grades.getCourse_name());
            ((TextView)convertView.findViewById(R.id.creditHoursLayout)).setText(credithoursstring);

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    //delete
                    return true;
                }
            });

            return convertView;
        }
    }
    public void getGPA() {
        double points = 0;
        double credithours = 0;
        for (int i = 0; i < grades.size(); i++) {
            switch (grades.get(i).getGrade()) {
                case "A":
                    points += Double.valueOf(grades.get(i).getCredit_hours()) * 4;
                    credithours += Double.valueOf(grades.get(i).getCredit_hours());
                    break;
                case "B":
                    points += Double.valueOf(grades.get(i).getCredit_hours()) * 3;
                    credithours += Double.valueOf(grades.get(i).getCredit_hours());
                    break;
                case "C":
                    points += Double.valueOf(grades.get(i).getCredit_hours()) * 2;
                    credithours += Double.valueOf(grades.get(i).getCredit_hours());
                    break;
                case "D":
                    points += Double.valueOf(grades.get(i).getCredit_hours()) * 1;
                    credithours += Double.valueOf(grades.get(i).getCredit_hours());
                    break;
                case "F":
                    points += Double.valueOf(grades.get(i).getCredit_hours()) * 0;
                    credithours += Double.valueOf(grades.get(i).getCredit_hours());
                    break;
            }
        }
        if (credithours == 0) {
            gpatext = "4.0";
            hourstext = "0";
        } else {
            gpatext = (points / credithours) + "";
            hourstext = credithours + "";
        }
    }
}