package com.smap.group29.getmoving.model;

public class User {
    private String email;
    private String password;
    private String name;
    private String age;
    private String city;
    private String steps;

    public User() {
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User(String name, String age, String city, String steps) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.age = age;
        this.city = city;
        this.steps = steps;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
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

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
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
}
