package com.smap.group29.getmoving.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.smap.group29.getmoving.model.weather.OpenWeatherAPI;

public class WeatherJsonParser {
    //example of parsing with Gson - note that the Gson parser uses the model object CityWeather, Clouds, Coord, Main, Sys, Weather and Wind extracted with http://www.jsonschema2pojo.org/
    public static String parseCityWeatherJsonWithGson(String jsonString){

        Gson gson = new GsonBuilder().create();
        OpenWeatherAPI weatherInfo =  gson.fromJson(jsonString, OpenWeatherAPI.class);

        if(weatherInfo != null) {
            return weatherInfo.name + "\n" + "Temp: " + (weatherInfo.main.temp.doubleValue()) + "\u2103" + //unicode for celcius
                    "\nCountry: " + weatherInfo.sys.country;
        } else {
            return "could not parse with gson";
        }
    }
}
