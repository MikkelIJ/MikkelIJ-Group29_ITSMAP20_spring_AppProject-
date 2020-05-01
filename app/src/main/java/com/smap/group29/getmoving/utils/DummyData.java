package com.smap.group29.getmoving.utils;

import com.smap.group29.getmoving.model.User;

import java.util.ArrayList;
import java.util.List;

public abstract class DummyData {

    static List<User> dummyData = new ArrayList<>();

    public static List<User> getUsers(){
        dummyData.add(0,new User("Bobby","47","Odense","7500"));
        dummyData.add(1,new User("Flemming","21","Køge","1200"));
        dummyData.add(2,new User("Folmer","57","Malling","300"));
        dummyData.add(3,new User("Kevin","12","Aarhus","9000"));
        return dummyData;
        }
}
