package com.smap.group29.getmoving.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String name;
    private String age;
    private String city;
    private String steps;
    private String mUid;

    private List<User> dummyData = new ArrayList<User>();

    public User() {
    }

    public User(String name, String age, String city, String steps) {
        this.name = name;
        this.age = age;
        this.city = city;
        this.steps = steps;
    }

    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }

    public String getCity() {
        return city;
    }

    public String getSteps() {
        return steps;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }


    public String getmUid() {
        return mUid;
    }

    public void setmUid(String mUid) {
        this.mUid = mUid;
    }
}
