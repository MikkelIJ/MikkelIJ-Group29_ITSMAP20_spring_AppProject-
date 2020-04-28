package com.smap.group29.getmoving.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.smap.group29.getmoving.model.weather.OpenWeather;
import com.smap.group29.getmoving.model.weather.Weather;

import java.util.ArrayList;
import java.util.List;

public class WeatherJsonParser {


    //example of parsing with Gson - note that the Gson parser uses the model object CityWeather, Clouds, Coord, Main, Sys, Weather and Wind extracted with http://www.jsonschema2pojo.org/
    public static ArrayList<String> parseWeather(String jsonString){

        ArrayList<String> weatherInfo = new ArrayList<>();
        Gson gson = new GsonBuilder().create();
        OpenWeather mOpenWeather =  gson.fromJson(jsonString, OpenWeather.class);

        if(mOpenWeather != null) {
            weatherInfo.add(0,"Temp " + (mOpenWeather.main.temp.doubleValue())+ "\u2103");
            weatherInfo.add(1,"Humid " + (mOpenWeather.main.humidity.doubleValue() + "%"));
            weatherInfo.add(2,"Desciption: " + (mOpenWeather.weather.get(0).description));
            return  weatherInfo;
        } else {
            return null; // needs to be changed
        }
    }
}
