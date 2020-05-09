package com.smap.group29.getmoving.model;



public class NewUser {

    private String uID;
    private String name;
    private String age;
    private String city;
    private long dailysteps;
    private String liveSteps;
    private String password;
    private String email;
    private String totalsteps;
    private String wins;


    public NewUser() {
    }

    public NewUser(String name, String age, String city, long dailysteps) {
        this.name = name;
        this.age = age;
        this.city = city;
        this.dailysteps = dailysteps;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getTotalsteps() {
        return totalsteps;
    }

    public String getWins() {
        return wins;
    }

    public void setWins(String wins) {
        this.wins = wins;
    }

    public void setTotalsteps(String totalsteps) {
        this.totalsteps = totalsteps;
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

    public long getDailysteps() {
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

    public void setDailysteps(long dailysteps) {
        this.dailysteps = dailysteps;
    }

}
