package com.ortaib.shiftinspector.Tabs;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ortaib.shiftinspector.Activities.LoginActivity;
import com.ortaib.shiftinspector.FireBase.DBHelper;
import com.ortaib.shiftinspector.Logic.User;
import com.ortaib.shiftinspector.R;

import org.w3c.dom.Text;


public class ProfileFragment extends Fragment {
    View view;
    final private String TAG = "ProfileFragment";
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;
    private DBHelper databaseHelper;
    private User user;
    private  String email;
    private TextView fillName,fillEmail,fillWage;
    private Context context;
    private Button disconnect;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile,container,false);
        context = getActivity();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        email = currentUser.getEmail().replace(".","_dot_");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        databaseHelper = new DBHelper(context);
        user = new User();
        fillName = (TextView) view.findViewById(R.id.fill_name);
        fillEmail = (TextView) view.findViewById(R.id.fill_email);
        fillWage = (TextView) view.findViewById(R.id.fill_wage);

        disconnect = (Button) view.findViewById(R.id.disconnect);
        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
            }
        });
        getUserFromDB();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    protected void getUserFromDB(){
        final String userId = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: "+dataSnapshot);

                user.setEmail(dataSnapshot.child(userId).child("email").getValue(String.class));
                user.setName(dataSnapshot.child(userId).child("name").getValue(String.class));
                user.setWage(dataSnapshot.child(userId).child("wage").getValue(Double.class));
                fillEmail.setText(user.getEmail());
                fillName.setText(user.getName());
                fillWage.setText(Double.toString(user.getWage()));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
