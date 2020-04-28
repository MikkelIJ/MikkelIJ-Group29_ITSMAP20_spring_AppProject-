package com.smap.group29.getmoving.service;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class OpenWeatherAPI {

    //for Volley
    RequestQueue queue;
    private static final String API_KEY = "08839120feb837b758b4cbc96d482977";
    public static final long CITY_ID_AARHUS = 2624652;
    public static final String WEATHER_API_CALL = "https://api.openweathermap.org/data/2.5/weather?id=" + CITY_ID_AARHUS + "&APPID=" + API_KEY;


    GetMovingService mGetMovingService;

    public OpenWeatherAPI(GetMovingService getMovingService) {
        this.mGetMovingService = getMovingService;
    }

    public void sendRequest(){
        //send request using Volley
        if(queue==null){
            queue = Volley.newRequestQueue(this.mGetMovingService);
        }
        String url = WEATHER_API_CALL;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.v("OnResponse",response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("onErrorResponse",String.valueOf(error));
            }
        });

        queue.add(stringRequest);
    }
}
