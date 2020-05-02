package com.smap.group29.getmoving.utils;

import com.smap.group29.getmoving.model.NewUser;

import java.util.ArrayList;
import java.util.List;

public abstract class DummyData {

    static List<NewUser> dummyData = new ArrayList<>();

    public static List<NewUser> getUsers(){
        dummyData.add(0,new NewUser("Bobby","47","Odense","7500"));
        dummyData.add(1,new NewUser("Flemming","21","Køge","1200"));
        dummyData.add(2,new NewUser("Folmer","57","Malling","300"));
        dummyData.add(3,new NewUser("Kevin","12","Aarhus","9000"));
        return dummyData;
        }
}
