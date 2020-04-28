
package com.smap.group29.getmoving.model.weather;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Wind
{
    @Expose
    public Double speed;

    @Expose
    public Integer deg;
}
