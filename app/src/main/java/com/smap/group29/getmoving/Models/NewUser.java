package com.smap.group29.getmoving.Models;

public class NewUser {
    private String name;
    private String age;
    private String city;
    private String dailySteps;
    private String imageUrl;

    // Constructor
    public NewUser(String name, String age, String city, String dailySteps, String imageUrl) {
        this.name = name;
        this.age = age;
        this.city = city;
        this.dailySteps = dailySteps;
        this.imageUrl = imageUrl;
    }

    // GETTERS
    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }

    public String getCity() {
        return city;
    }

    public String getDailySteps() {
        return dailySteps;
    }

    public String getImageUrl() {
        return imageUrl;
    }


    // SETTERS

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setDailySteps(String dailySteps) {
        this.dailySteps = dailySteps;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
