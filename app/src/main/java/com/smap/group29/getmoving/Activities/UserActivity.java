package com.smap.group29.getmoving.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.smap.group29.getmoving.R;
import com.smap.group29.getmoving.Utils.FirebaseUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;


public class UserActivity extends AppCompatActivity {

    private TextView tv_weatherTemp, tv_weatherDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       FirebaseUtil.openFirebaseReference("TheNewRealDeal",this);

        tv_weatherTemp = findViewById(R.id.tv_WeatherTemp);
        tv_weatherDescription = findViewById(R.id.tv_Weather);

        //inspired by https://www.youtube.com/watch?v=8-7Ip6xum6E
        findWeather();

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
