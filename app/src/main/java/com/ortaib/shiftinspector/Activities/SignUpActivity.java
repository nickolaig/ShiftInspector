package com.ortaib.shiftinspector.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.ortaib.shiftinspector.FireBase.DBHelper;
import com.ortaib.shiftinspector.R;

public class SignUpActivity extends AppCompatActivity {
    final String TAG = "SignUpActivity";
    private FirebaseAuth mAuth;

    //set up UI VIEWS
    private Button signUpBtn;
    private EditText nameET, emailET, wageET, passwordET;
    private View progressView, signUpForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        nameET = (EditText) findViewById(R.id.name);
        emailET = (EditText) findViewById(R.id.email);
        wageET = (EditText) findViewById(R.id.hourly_wage);
        passwordET = (EditText) findViewById(R.id.password);
        progressView = (View) findViewById(R.id.sign_up_progress);
        signUpForm = (View) findViewById(R.id.sign_up_form);
        signUpBtn = (Button) findViewById(R.id.sign_up_btn);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                attempSignUp();
            }
        });

    }

    protected void attempSignUp() {
        emailET.setError(null);
        passwordET.setError(null);
        //store views data
        String name = nameET.getText().toString();
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String stringWage = wageET.getText().toString();
        Double wage = 0.0;
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
        if (TextUtils.isEmpty(email)) {
            emailET.setError("This field is required");
            focusView = emailET;
            cancel = true;
        } else if (!isEmailValid(email)) {
            emailET.setError("Invalid email address");
            focusView = emailET;
            cancel = true;
        }
        //Check if password is valid and not empty
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            passwordET.setError("password is too short");
            focusView = passwordET;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgressBar(true);
            signUp(name, email, password, wage);
        }
    }


    private void signUp(final String name, final String email, final String password, final double wage) {
        //firebase auth
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name).build();
                            user.updateProfile(profileUpdates);
                            //add user to db
                            DBHelper.addUser(name, email, wage);
                            //return to mainActivity
                            Intent MainActivityIntent = new Intent(SignUpActivity.this, MainActivity.class);
                            startActivity(MainActivityIntent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void showProgressBar(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            signUpForm.setVisibility(show ? View.GONE : View.VISIBLE);
            signUpForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    signUpForm.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            signUpForm.setVisibility(show ? View.GONE : View.VISIBLE);
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

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

}
