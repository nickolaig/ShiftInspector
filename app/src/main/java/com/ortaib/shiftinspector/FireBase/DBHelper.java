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

    public static void addUser(final String name,final String email,final double wage){
        User user = new User(name,email,wage);
        final String emailKey = user.getEmail().replace(".","_dot_");
        mDatabase.child("users").child(emailKey).setValue(user);
    }
    public static void editUser(User user){
        final String emailKey = user.getEmail().replace(".","_dot_");
        mDatabase.child("users").child(emailKey).setValue(user);
    }
}
