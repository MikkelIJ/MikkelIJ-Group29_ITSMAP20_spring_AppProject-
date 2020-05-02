package com.smap.group29.getmoving.model;

public class NewUser {
    private String name;
    private String age;
    private String city;
    private String dailysteps;
    private String liveSteps;
    private String password;
    private String email;


    public NewUser() {
    }

    public NewUser(String name, String age, String city, String dailysteps) {
        this.name = name;
        this.age = age;
        this.city = city;
        this.dailysteps = dailysteps;
    }

    public String getLiveSteps() {
        return liveSteps;
    }

    public void setLiveSteps(String liveSteps) {
        this.liveSteps = liveSteps;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getDailysteps() {
        return dailysteps;
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

    public void setDailysteps(String dailysteps) {
        this.dailysteps = dailysteps;
    }

}
