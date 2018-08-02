package com.ortaib.shiftinspector.FireBase;


import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ortaib.shiftinspector.Logic.User;

public class DBHelper {
    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private final Context context;

    public DBHelper(Context context){
        this.context = context;
    }

    public static void addUser(final String name,final String email,final double wage,final String profileType,String userid){

        User user = new User(name,email,wage,profileType);

        mDatabase.child("users").child(userid).setValue(user);
    }
}
