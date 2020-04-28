
package com.smap.group29.getmoving.model.weather;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OpenWeather
{

    @Expose
    public Coord coord;

    @Expose
    public List<Weather> weather = null;

    @Expose
    public String base;

    @Expose
    public Main main;

    @Expose
    public Integer visibility;

    @Expose
    public Wind wind;

    @Expose
    public Clouds clouds;

    @Expose
    public Integer dt;

    @Expose
    public Sys sys;

    @Expose
    public Integer timezone;

    @Expose
    public Integer id;

    @Expose
    public String name;

    @Expose
    public Integer cod;

}
