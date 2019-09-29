package com.theindianappguy.loginandsignupui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mukesh.OtpView;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    OtpView verificationcodeet;
    EditText input_phonenumber,_passwordText, _nameText, _emailText;
    Button _signupButton;
    ImageView  backbtn;
    String emailst,passst,namest, phonenumber;
    TextView phonenumbertxtll, titletxt, desctxt;
    LinearLayout userinfo_tab,verification_tab, _loginLink, namell, emailll, passwordll, phonenumberll;

    int step = 1;
    private FirebaseAuth mAuth;
    public String codeSent;

    //name strings
    String lastName = "";
    String firstName= "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        _passwordText =  findViewById(R.id.input_password);
        _nameText =  findViewById(R.id.input_name);
        _emailText =  findViewById(R.id.input_email);
        _signupButton = findViewById(R.id.btn_signup);
        input_phonenumber =  findViewById(R.id.input_phonenumber);
        _loginLink = findViewById(R.id.link_login);
        namell =  findViewById(R.id.namell);
        emailll =  findViewById(R.id.emailll);
        passwordll =  findViewById(R.id.passwordll);
        phonenumberll =  findViewById(R.id.phonenumberll);
        phonenumbertxtll = findViewById(R.id.phonenumbertextll);


        titletxt = findViewById(R.id.titletxt);
        desctxt = findViewById(R.id.desctxt);


        userinfo_tab = findViewById(R.id.userinfo_tab);
        verification_tab = findViewById(R.id.verification_tab);
        verification_tab.setVisibility(View.GONE);

        verificationcodeet =  findViewById(R.id.verificationcodeet);
        Button btn = findViewById(R.id.verificationcodebtn);

        backbtn= findViewById(R.id.backbtn);


        hideall();
        namell.setVisibility(View.VISIBLE);

        /*Setting on click listeners*/
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                verifyCode();

            }
        });

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(step==1){
                    namest = _nameText.getText().toString();

                    if(namest.isEmpty() || havelastname(namest)){
                        if(namest.isEmpty()){
                            Toast.makeText(SignUpActivity.this, "Please write your name!", Toast.LENGTH_SHORT).show();
                        }else if(havelastname(namest)){
                            Toast.makeText(SignUpActivity.this, "Please add full name", Toast.LENGTH_SHORT).show();
                        }

                    }else{

                        namell.setVisibility(View.GONE);
                        emailll.setVisibility(View.VISIBLE);
                        titletxt.setText("Choose email");
                        desctxt.setText("enter your email address below.");
                        step++;

                    }
                }else if(step == 2){
                    emailst = _emailText.getText().toString();
                    if(emailst.isEmpty()){
                        Toast.makeText(SignUpActivity.this, "Please write your email!", Toast.LENGTH_SHORT).show();
                    }else{
                        emailll.setVisibility(View.GONE);
                        passwordll.setVisibility(View.VISIBLE);
                        titletxt.setText("Create Password");
                        desctxt.setText("Your password must have atleast one symbol & 4 or more characters.");
                        step++;
                    }

                }else if(step == 3){
                    passst = _passwordText.getText().toString();
                    if(passst.isEmpty()){

                    }else{
                        passwordll.setVisibility(View.GONE);
                        phonenumberll.setVisibility(View.VISIBLE);
                        phonenumbertxtll.setVisibility(View.VISIBLE);
                        titletxt.setText("Let's Get Started");
                        desctxt.setText("Enter your mobile number to enable 2-step verification.");
                        step++;
                    }
                }else if(step == 4){
                    phonenumber = input_phonenumber.getText().toString();
                    if(phonenumber.isEmpty()){

                    }else{
                        phonenumberll.setVisibility(View.GONE);
                        phonenumbertxtll.setVisibility(View.GONE);
                        titletxt.setText("Verification");
                        desctxt.setText("We texted you a code to verify your phone number.");
                        signup();
                        step++;
                    }
                }

            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });

        mAuth.useAppLanguage();

    }


    private void hideall() {
        //removing all the edit text will be opened one by one
        namell.setVisibility(View.GONE);
        emailll.setVisibility(View.GONE);
        passwordll.setVisibility(View.GONE);
        phonenumberll.setVisibility(View.GONE);
        phonenumbertxtll.setVisibility(View.GONE);
    }


    private void startPhoneNumberVerification(String phoneNumber) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks


    }


    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            codeSent = s;

        }
    };

    private void verifyCode() {

        String code = verificationcodeet.getText().toString();

        final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        if(code.isEmpty()){
            Toast.makeText(this, "Code Required", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }else{
            try {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
                signInWithPhoneAuthCredential(credential);

            }catch (Exception e){
                Toast toast = Toast.makeText(this, "Verification Code is wrong "+e, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
        }


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            updateUI(user);
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }


    private void updateUI(FirebaseUser currentUser) {

        if (currentUser != null) {

            // as we already have the user id you can add this code to signup the user with email because we can reset password with email
            mAuth.createUserWithEmailAndPassword(emailst, passst)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }

                            // ...
                        }
                    });

             } else {

            onSignupFailed();
        }
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }else{
            userinfo_tab.setVisibility(View.GONE);
            verification_tab.setVisibility(View.VISIBLE);
            startPhoneNumberVerification("+91"+input_phonenumber.getText().toString());
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Sending Verification Code...");
        progressDialog.show();


        String name = _nameText.getText().toString();

        // TODO: Implement your own signup logic here.


        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }



    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();


        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            _emailText.setFocusable(true);
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }


    private boolean havelastname(String UserFullName) {

        Log.e(TAG,"i am here iin have last name function");
        try{
            int firstSpace = UserFullName.indexOf(" "); // detect the first space character
            firstName = UserFullName.substring(0, firstSpace);  // get everything upto the first space character
            lastName = UserFullName.substring(firstSpace).trim(); // get everything after the first space, trimming the spaces off
            Log.e(TAG,"i am here i got first name and last name and firstname is "+ firstName);

            return false;
        }catch (Exception e){
            Log.e(TAG,"i am here i did not got first name and last name");
            return true;

        }

    }


}
