package com.smap.group29.getmoving.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserJson {

    @SerializedName("age")
    @Expose
    public String age;
    @SerializedName("city")
    @Expose
    public String city;
    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("password")
    @Expose
    public String password;
    @SerializedName("steps")
    @Expose
    public String steps;

}
