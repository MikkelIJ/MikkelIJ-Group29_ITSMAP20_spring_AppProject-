
package com.smap.group29.getmoving.model.weather;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Main
{
    @Expose
    public Double temp;
    @Expose
    public Double feels_like;
    @Expose
    public Double tempMin;
    @Expose
    public Double tempMax;
    @Expose
    public Integer pressure;
    @Expose
    public Integer humidity;
}
