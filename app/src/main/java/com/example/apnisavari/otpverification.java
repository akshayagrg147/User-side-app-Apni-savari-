package com.example.apnisavari;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.apnisavari.Common.Common;
import com.example.apnisavari.Model.Rider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

import ticker.views.com.ticker.widgets.circular.timer.callbacks.CircularViewCallback;
import ticker.views.com.ticker.widgets.circular.timer.view.CircularView;

public class otpverification extends AppCompatActivity {
    EditText editText;
    ProgressBar progressBar;
    Button button,resend;
    String mVerificationId;
    private FirebaseAuth Auth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    // String number="+917973434833";
    String email;
    String name;
    String password;
    String number;
    DatabaseReference users;
    private CircularView circularViewWithTimer;
    FirebaseDatabase db;
    //private FirebaseAuth auth;
    RelativeLayout rootLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);
        Intent intent=getIntent();
        email=intent.getStringExtra("email");
        name=intent.getStringExtra("name");
        password=intent.getStringExtra("password");
        number=intent.getStringExtra("phone");
        Auth=FirebaseAuth.getInstance();
        db= FirebaseDatabase.getInstance();
        users=db.getReference(Common.user_rider_tb1);
        rootLayout= findViewById(R.id.rootLayout);
        resend=findViewById(R.id.resend);

        resend.setVisibility(View.INVISIBLE);
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendVerificationCode(number, mResendToken);
            }
        });
        number="+91"+number;

        editText=findViewById(R.id.code);
        button=findViewById(R.id.btn);
        Auth=FirebaseAuth.getInstance();
        progressBar=findViewById(R.id.progressBar);

        sendVerificationCode(number);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code=editText.getText().toString().trim();
                button.setText("verifying..");
                button.setEnabled(false);
                if(code.isEmpty() || code.length()<6){
                    Toast.makeText(otpverification.this, "Invalid Code Entered", Toast.LENGTH_SHORT).show();
                    button.setText("verify");
                    button.setEnabled(true);
                    editText.requestFocus();
                    return;
                }
                verifyCode(code);
            }


        });
        circularViewWithTimer=findViewById(R.id.verificatontimer);

        CircularView.OptionsBuilder builderWithTimer = new
                CircularView.OptionsBuilder()
                .shouldDisplayText(true)
                .setCounterInSeconds(30)
                .setCircularViewCallback(new CircularViewCallback() {
                    @Override
                    public void onTimerFinish() {

                        try {
                            resend.setBackgroundColor(Color.rgb(124, 252, 0));

                            resend.setVisibility(View.VISIBLE);
                            resend.setEnabled(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onTimerCancelled() {

                    }
                });
        try {
            circularViewWithTimer.setOptions(builderWithTimer);

            circularViewWithTimer.startTimer();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void verifyCode(String code) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }
    private void resendVerificationCode(String phonenumber,PhoneAuthProvider.ForceResendingToken token)
    {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phonenumber,60,TimeUnit.SECONDS,this,mCallbacks,token);
    }

    private void sendVerificationCode(String number) {

        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacksPhoneAuthActivity.java
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            String code=credential.getSmsCode();
            if(code!=null){
                editText.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            button.setText("verify");
            button.setEnabled(true);

            if (e instanceof FirebaseAuthInvalidCredentialsException) {

                Toast.makeText(getApplicationContext(),"Invalid Number",Toast.LENGTH_LONG).show();
                // Invalid request
                // ...
            } else if (e instanceof FirebaseTooManyRequestsException) {
                Toast.makeText(getApplicationContext(),"Sms Limit Exceed,Try on Tomorrorw",Toast.LENGTH_LONG).show();

            }

            // Show a message and update the UI
            // ...
        }

        @Override
        public void onCodeSent(String verificationId,
                               PhoneAuthProvider.ForceResendingToken token) {


            mVerificationId = verificationId;
            mResendToken = token;
        }
    };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        Auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {





                            Auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Rider rider = new Rider();
                                    rider.setEmail(email);
                                    rider.setName(name);
                                    rider.setPhone(number);
                                    rider.setPassword(password);

                                    users.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(rider).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            DatabaseReference dbrequest1 = FirebaseDatabase.getInstance().getReference(Common.SwitchingSystem);
                                            Rider ob = new Rider();
                                            ob.setSwitchingSystem("");
                                            dbrequest1.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(ob).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {


                                                }
                                            });
                                            DatabaseReference dbrequest2 = FirebaseDatabase.getInstance().getReference("Transactons").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                                            dbrequest2.child("TransactionId").setValue("").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                }
                                            });


                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });



                            // Snackbar.make(rootLayout,"successfully created",Snackbar.LENGTH_SHORT).show();

                            Toast.makeText(otpverification.this,"successfully created",Toast.LENGTH_SHORT).show();

                            Intent intent=new Intent(otpverification.this,IntroActivity.class);

                            intent.putExtra("success","he");
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }


}
