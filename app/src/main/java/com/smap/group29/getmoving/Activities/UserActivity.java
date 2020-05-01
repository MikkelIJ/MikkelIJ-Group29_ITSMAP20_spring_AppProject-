package com.smap.group29.getmoving.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.smap.group29.getmoving.R;
import com.smap.group29.getmoving.Utils.FirebaseUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;


public class UserActivity extends AppCompatActivity {

    private TextView tv_weatherTemp, tv_weatherDescription;

    // variables for the edit text of NewUser
    TextView mTextViewName;
    TextView mTextViewAge;
    TextView mTextViewCity;
    TextView mTextTextDailySteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUtil.openFirebaseReference("TheNewRealDeal",this);

        tv_weatherTemp = findViewById(R.id.tv_WeatherTemp);
        tv_weatherDescription = findViewById(R.id.tv_Weather);

        populateTextView();
        findWeather();

    }

    public void populateTextView(){
        mTextViewName= findViewById(R.id.tv_userName_desc);
        mTextViewAge = findViewById(R.id.tv_userAge_desc);
        mTextViewCity = findViewById(R.id.tv_userCity_desc);
        mTextTextDailySteps = findViewById(R.id.tv_StepsToday_header);

        String name = getIntent().getExtras().getString("name");
        String age = getIntent().getExtras().getString("age");
        String city = getIntent().getExtras().getString("city");
        String dailySteps = getIntent().getExtras().getString("dailySteps");

        mTextViewName.setText(name);
        mTextViewAge.setText(age);
        mTextViewCity.setText(city);
        mTextTextDailySteps.setText(dailySteps);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.logout_menu:
                AuthUI.getInstance().signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("Logout", "User logged Out!");
                                FirebaseUtil.attachListener();
                            }
                        });
                FirebaseUtil.detachListener();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void findWeather() {
        String url = "http://api.openweathermap.org/data/2.5/weather?id=2624652&appid=a1b9c2c209a84cd50be4c25fbcd02a88&units=metric";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject main_object = response.getJSONObject("main");
                    JSONArray array = response.getJSONArray("weather");
                    JSONObject object = array.getJSONObject(0);
                    String temp = String.valueOf(main_object.getDouble("temp"));
                    String description = object.getString("description");
                    String city = object.getString("name");

                    tv_weatherTemp.setText(temp);
                    tv_weatherDescription.setText(description);


                }
                catch (JSONException e){
                    e.printStackTrace();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jor);

    }

    @Override
    protected void onPause(){
        super.onPause();
        FirebaseUtil.detachListener();
    }

    @Override
    protected void onResume(){
        super.onResume();
        FirebaseUtil.attachListener();
    }

}
