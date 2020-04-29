package com.smap.group29.getmoving.Models;

public class NewUser {
    private String id;
    private String name;
    private String age;
    private String city;
    private String dailySteps;
    private String imageUrl;
    private String imageName;

    public NewUser() {}

    // Constructor
    public NewUser(String name, String age, String city, String dailySteps, String imageUrl, String imageName) {
        this.setName(name);
        this.setAge(age);
        this.setCity(city);
        this.setDailySteps(dailySteps);
        this.setImageUrl(imageUrl);
        this.setImageName(imageName);
    }

    // GETTERS
    public String getId() {  return id; }

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

    public String getImageName() {  return imageName;  }

    // SETTERS
    public void setId(String id) {  this.id = id;  }

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

    public void setImageName(String imageName) { this.imageName = imageName; }
}
