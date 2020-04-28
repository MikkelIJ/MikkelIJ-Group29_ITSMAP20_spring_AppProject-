
package com.smap.group29.getmoving.model.weather;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Weather
{
    @Expose
    public Integer id;

    @Expose
    public String main;

    @Expose
    public String description;

    @Expose
    public String icon;

}
