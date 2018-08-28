package com.ortaib.shiftinspector.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ortaib.shiftinspector.FireBase.DBHelper;
import com.ortaib.shiftinspector.Logic.User;
import com.ortaib.shiftinspector.R;

public class EditProfileActivity extends AppCompatActivity {
    private User user;
    private static final String TAG = "EditProfileActivity";
    private FirebaseAuth mAuth;
    private Button confirmBtn;
    private EditText nameET, wageET;
    private View progressView, editForm;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Bundle extras = getIntent().getExtras();
        user = (User) extras.getSerializable("user");
        mAuth = FirebaseAuth.getInstance();
        dbHelper = new DBHelper(this);
        nameET = (EditText) findViewById(R.id.name);
        wageET = (EditText) findViewById(R.id.hourly_wage);
        progressView = (View) findViewById(R.id.sign_up_progress);
        editForm = (View) findViewById(R.id.edit_form);
        confirmBtn = (Button) findViewById(R.id.confirm_btn);
        nameET.setText(user.getName());
        wageET.setText(Double.toString(user.getWage()));
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (attempEdit()) {
                    Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    public boolean attempEdit() {
        //store views data
        String name = nameET.getText().toString();
        String stringWage = wageET.getText().toString();
        Double wage;

        if (isWageValid(stringWage)) {
            wage = Double.parseDouble(stringWage);
        }

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(name)) {
            nameET.setError("This field is required");
            focusView = nameET;
            cancel = true;
        }

        if (TextUtils.isEmpty(stringWage)) {
            wageET.setError("this field is required");
            focusView = wageET;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
            return false;
        } else {
            user.setName(nameET.getText().toString());
            user.setWage(Double.valueOf(wageET.getText().toString()));
            dbHelper.editUser(user);
            Toast.makeText(this, "Edit profile", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    protected boolean isWageValid(String wage) {
        try {
            double price = Double.parseDouble(wage);
            if (price >= 0)
                return true;
            else
                return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }
}
