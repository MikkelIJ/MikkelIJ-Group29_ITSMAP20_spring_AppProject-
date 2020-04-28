
package com.smap.group29.getmoving.model.weather;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Sys
{
    @Expose
    public Integer type;

    @Expose
    public Integer id;

    @Expose
    public Double message;

    @Expose
    public String country;

    @Expose
    public Integer sunrise;

    @Expose
    public Integer sunset;
}
