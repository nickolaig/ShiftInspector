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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ortaib.shiftinspector.FireBase.DBHelper;
import com.ortaib.shiftinspector.R;

public class LoginActivity extends AppCompatActivity {
    final private String TAG = "MainActivity";
    private Button loginBtn;
    private EditText emailET,passwordET;
    private TextView moveToSignUp;
    private View progressView,loginForm;
    private DBHelper databaseHelper;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        databaseHelper = new DBHelper(getApplicationContext());
        progressView   = (View) findViewById(R.id.login_progress);
        loginForm      = (View) findViewById(R.id.login_form);
        emailET = (EditText) findViewById(R.id.email);
        passwordET = (EditText) findViewById(R.id.password);
        loginBtn = (Button) findViewById(R.id.sign_in);
        loginBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                mAuth = FirebaseAuth.getInstance();
                attempLogin();
            }
        });
        moveToSignUp = (TextView)findViewById(R.id.click_here);
        moveToSignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                moveToSignUp.setTextColor(getColor(R.color.secondaryColor));
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
                finish();
            }
        });

    }
    private void attempLogin(){
        emailET.setError(null);
        passwordET.setError(null);

        final String email = emailET.getText().toString();
        final String password = passwordET.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(password) || !checkValidPassword(password)){
            passwordET.setError("password is too short");
            focusView = passwordET;
            cancel = true;
        }
        if (TextUtils.isEmpty(email)) {
            emailET.setError("This field is required");
            focusView = emailET;
            cancel = true;
        } else if (!checkValidEmail(email)) {
            emailET.setError("not valid email address");
            focusView = emailET;
            cancel = true;
        }
        if(cancel){
            focusView.requestFocus();
        }else{
            showProgressBar(true);
            login();
        }
    }
    public void login(){
        final String email = emailET.getText().toString();
        final String password = passwordET.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            Intent mainActivityIntent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(mainActivityIntent);
                            finish();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            showProgressBar(false);
                            passwordET.setError("incorrect password");
                            passwordET.requestFocus();

                        }

                        // ...
                    }
                });
    }
    public boolean checkValidPassword(String password){
        return password.length()>4;
    }
    public boolean checkValidEmail(String email){
        return email.contains("@");
    }
    private void showProgressBar(final boolean show){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            loginForm.setVisibility(show ? View.GONE : View.VISIBLE);
            loginForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loginForm.setVisibility(show ? View.GONE : View.VISIBLE);
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
        }else{
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            loginForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
