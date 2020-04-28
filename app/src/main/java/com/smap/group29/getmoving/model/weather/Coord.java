
package com.smap.group29.getmoving.model.weather;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Coord
{
    @Expose
    public Double lon;

    @Expose
    public Double lat;
}
