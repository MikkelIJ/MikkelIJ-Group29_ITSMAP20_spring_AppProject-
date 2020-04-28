package com.smap.group29.getmoving.service;

import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
//import com.firebase.ui.auth.data.model.User;
import com.smap.group29.getmoving.UserActivity;
import com.smap.group29.getmoving.utils.WeatherJsonParser;

import java.util.ArrayList;

public class OpenWeatherAPI {

    private static final String LOGD = "openweatherapi";

    public static final String BROADCAST_ACTION_WEATHER = "com.smap.group29.getmoving.service.openweatherapi_broadcast";


    //for Volley
    RequestQueue queue;
    private static final String API_KEY = "08839120feb837b758b4cbc96d482977";
    public static long CITY_ID = 2624652; // aarhus 2624652
    public static String WEATHER_API_CALL = "https://api.openweathermap.org/data/2.5/weather?id=" + CITY_ID + "&APPID=" + API_KEY +"&units=metric";
    private Intent weatherIntent;

    GetMovingService mGetMovingService;
    UserActivity mUserActivity;

    public OpenWeatherAPI(GetMovingService getMovingService) {
        this.mGetMovingService = getMovingService;
    }

    public OpenWeatherAPI(UserActivity userActivity, long cityID){
        this.mUserActivity = userActivity;
        this.CITY_ID = cityID;
    }

    public void sendRequest(){
        //send request using Volley
        if(queue==null){
            queue = Volley.newRequestQueue(this.mUserActivity);
        }
        weatherIntent = new Intent(BROADCAST_ACTION_WEATHER);
        String url = WEATHER_API_CALL;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //Log.v("OnResponse",response);
                        ArrayList<String> weather = WeatherJsonParser.parseWeather(response);
                        Log.v("OnResponse", String.valueOf(weather));

                        broadcastWeather(weather);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("onErrorResponse",String.valueOf(error));
            }
        });
        queue.add(stringRequest);
    }

    private void broadcastWeather(ArrayList<String> weather){
        Log.d(LOGD, "broadcasting weather temp and city");
        weatherIntent.putExtra("temp",weather);
        mUserActivity.sendBroadcast(weatherIntent);
    }
}
