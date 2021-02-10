package com.example.apnisavari;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;



import android.content.DialogInterface;
import android.content.Intent;

import android.app.AlertDialog;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.agrawalsuneet.dotsloader.loaders.LazyLoader;
import com.example.apnisavari.Common.Common;
import com.example.apnisavari.Model.Rider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import ticker.views.com.ticker.widgets.circular.timer.view.CircularView;

public class IntroActivity extends AppCompatActivity  {
    private CircularView circularViewWithTimer;
    private ViewPager screenPager;
    IntroViewPageAdapter introViewPagerAdapter ;
    TabLayout tabIndicator;




    Animation btnAnim ;





    Button btnSignIn,btnRegister;

    RelativeLayout rootLayout;
    private FirebaseAuth auth;
    TextView txt_forgot_password;

    FirebaseDatabase db;
    DatabaseReference users;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_intro);

        getSupportActionBar().hide();

        tabIndicator = findViewById(R.id.tab_indicator);
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.button_animation);


        // fill list screen

        final List<ScreenItem> mList = new ArrayList<>();
        mList.add(new ScreenItem("Location Tracked","Get accurate driver's vechile location",R.drawable.locationtrac));
        mList.add(new ScreenItem("Reliable Drivers"," Drivers with valid governmental documents",R.drawable.driversreli));
        mList.add(new ScreenItem("Easy Payment","Pay your fares with proper security ",R.drawable.moneyms));

        // setup viewpager
        screenPager =findViewById(R.id.screen_viewpager);
        introViewPagerAdapter = new IntroViewPageAdapter(this,mList);
        screenPager.setAdapter(introViewPagerAdapter);

        // setup tablayout with viewpager

        tabIndicator.setupWithViewPager(screenPager);

        // next button click Listner



        // tablayout add change listener


        tabIndicator.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == mList.size()-1) {

                    loaddLastScreen();

                }


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



        // Get Started button click listener



        // skip button click listener


        Paper.init(this);
        Log.d("Error : ", "App Started");
        auth=FirebaseAuth.getInstance();
        db=FirebaseDatabase.getInstance();
        users=db.getReference(Common.user_rider_tb1);

        btnSignIn= findViewById(R.id.btnSignIn);
        btnRegister= findViewById(R.id.btnRegister);
        rootLayout= findViewById(R.id.rootLayout);
        txt_forgot_password=findViewById(R.id.txt_forgot_password);
        txt_forgot_password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                DatabaseReference dbrequest1 = FirebaseDatabase.getInstance().getReference(Common.SwitchingSystem);
                Rider ob = new Rider();
                ob.setSwitchingSystem("");
                dbrequest1.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(ob).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {


                    }
                });

              //  showDialogForgotPassword();
                return false;
            }
        });
        final Animation myanim= AnimationUtils.loadAnimation(this,R.anim.bounce);

        btnSignIn.startAnimation(myanim);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegisterDialog();
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginDialog();
            }
        });

        String user= Paper.book().read(Common.user_field);
        String pwd=Paper.book().read(Common.pwd_field);
        if(user!=null && pwd!=null)
        {
            if(!TextUtils.isEmpty(user)&& !TextUtils.isEmpty(pwd))
            {
              //  autoLogin(user,pwd);
            }
        }
    }





    // show the GETSTARTED Button and hide the indicator and the next button
    private void loaddLastScreen() {


        // TODO : ADD an animation the getstarted button
        // setup animation




    }

    private void showDialogForgotPassword() {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(IntroActivity.this);
        alertDialog.setTitle("Forgot password");
        alertDialog.setMessage("Please enter your details");
        LayoutInflater inflater=LayoutInflater.from(IntroActivity.this);
        View forgot_pwd_layout=inflater.inflate(R.layout.layout_forgot_password,null);
        final MaterialEditText edtEmail=forgot_pwd_layout.findViewById(R.id.edtEmail);
        alertDialog.setView(forgot_pwd_layout);
        alertDialog.setView(forgot_pwd_layout);
        alertDialog.setPositiveButton("RESET", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                final AlertDialog waitingDialog=new SpotsDialog(IntroActivity.this);
                waitingDialog.show();
                auth.sendPasswordResetEmail(edtEmail.getText().toString().trim())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dialogInterface.dismiss();
                                waitingDialog.dismiss();
                                Snackbar.make(rootLayout,"Reset password link has been sent",Snackbar.LENGTH_LONG)
                                        .show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialogInterface.dismiss();
                        waitingDialog.dismiss();
                        Snackbar.make(rootLayout,""+e.getMessage(),Snackbar.LENGTH_LONG)
                                .show();

                    }
                });
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    private void autoLogin(String user, String pwd) {

        boolean pagecheck=false;
        final AlertDialog waitingDialog=new SpotsDialog(IntroActivity.this);
        waitingDialog.show();
        //login
        btnSignIn.setText("Loading...");


       // loader.addView(loader1);
        auth.signInWithEmailAndPassword(user,pwd).
                addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        FirebaseDatabase.getInstance().getReference(Common.user_rider_tb1)
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Common.currentUser=dataSnapshot.getValue(Rider.class);




                                        FirebaseDatabase.getInstance().getReference(Common.SwitchingSystem).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        try {
                                                            final Rider driverUser = dataSnapshot.getValue(Rider.class);

                                                            if (driverUser.getSwitchingSystem().isEmpty()) {
                                                                startActivity(new Intent(IntroActivity.this,Map_destination_Select.class));

                                                                DatabaseReference dbrequest = FirebaseDatabase.getInstance().getReference(Common.destination_request_tb1);
                                                                Rider rider = new Rider();
                                                                rider.setDestination("");
                                                                dbrequest.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(rider).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {




                                                                    }
                                                                });
                                                                overridePendingTransition(R.anim.fade_in,R.anim.fadeout);


                                                                finish();

                                                            } else {
                                                                FirebaseDatabase.getInstance().getReference("RiderCompleted").addValueEventListener(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                        Map<String,String> mp=(Map)dataSnapshot.getValue();
                                                                        String m1=mp.get(driverUser.getSwitchingSystem().toString());
                                                                        if(m1.isEmpty())
                                                                        {
                                                                            Intent intent = new Intent(IntroActivity.this, acceptingcustomerwindow.class);
                                                                            intent.putExtra("driverid", driverUser.getSwitchingSystem());

                                                                            startActivity(intent);
                                                                            overridePendingTransition(R.anim.fade_in, R.anim.fadeout);

                                                                            finish();

                                                                        }
                                                                        else
                                                                        {
                                                                            Intent intent = new Intent(IntroActivity.this, PaymentGateWay.class);
                                                                            intent.putExtra("driverid", driverUser.getSwitchingSystem());

                                                                            startActivity(intent);
                                                                            overridePendingTransition(R.anim.fade_in, R.anim.fadeout);

                                                                            finish();

                                                                        }

                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                    }
                                                                });


                                                            }
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }


                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });




                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                waitingDialog.dismiss();

                Snackbar.make(rootLayout,"Failed"+e.getMessage(),Snackbar.LENGTH_SHORT).show();
                btnSignIn.setEnabled(true);
            }
        });

    }

    private void showLoginDialog() {


        final  AlertDialog.Builder dialog=new AlertDialog.Builder(IntroActivity.this);
        dialog.setTitle("SignIn");
        dialog.setMessage("please use mail to SignIn");
        LayoutInflater inflater=LayoutInflater.from(IntroActivity.this);
        View login_layout=inflater.inflate(R.layout.layout_login,null);
        final MaterialEditText edtEmail=login_layout.findViewById(R.id.edtEmail);
        final MaterialEditText edtPassword=login_layout.findViewById(R.id.edtPassword);


        dialog.setView(login_layout);
        dialog.setPositiveButton("SignIn", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                btnSignIn.setEnabled(false);

                if (TextUtils.isEmpty(edtEmail.getText().toString())) {
                    Snackbar.make(rootLayout, "Please enter Email adress", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(edtPassword.getText().toString())) {
                    Snackbar.make(rootLayout, "Please enter password", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (edtPassword.getText().toString().length() < 6) {
                    Snackbar.make(rootLayout, "password too short!!!", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                final AlertDialog waitingDialog=new SpotsDialog(IntroActivity.this);
                waitingDialog.show();
                //login
                auth.signInWithEmailAndPassword(edtEmail.getText().toString(),edtPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        waitingDialog.dismiss();


                        FirebaseDatabase.getInstance().getReference(Common.user_rider_tb1)
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        try{
                                            Map<String, String> bb = (Map<String, String>) dataSnapshot.getValue();
                                            String str = bb.get("email");
                                            if (str != null) {
                                                if (str.equals(edtEmail.getText().toString())) {

                                                    Paper.book().write(Common.user_field, edtEmail.getText().toString());
                                                    Paper.book().write(Common.pwd_field, edtPassword.getText().toString());
                                                    Common.currentUser = dataSnapshot.getValue(Rider.class);
                                                    FirebaseDatabase.getInstance().getReference(Common.SwitchingSystem).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    try {
                                                                        final Rider driverUser = dataSnapshot.getValue(Rider.class);

                                                                        if (driverUser.getSwitchingSystem().isEmpty()) {
                                                                            startActivity(new Intent(IntroActivity.this,Map_destination_Select.class));

                                                                            DatabaseReference dbrequest = FirebaseDatabase.getInstance().getReference(Common.destination_request_tb1);
                                                                            Rider rider = new Rider();
                                                                            rider.setDestination("");
                                                                            dbrequest.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(rider).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {




                                                                                }
                                                                            });
                                                                            overridePendingTransition(R.anim.fade_in,R.anim.fadeout);


                                                                            finish();

                                                                        } else {
                                                                            FirebaseDatabase.getInstance().getReference("RiderCompleted").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                    Map<String,String> mp=(Map)dataSnapshot.getValue();
                                                                                    String m1=mp.get(driverUser.getSwitchingSystem().toString());
                                                                                    if(m1.isEmpty())
                                                                                    {
                                                                                        Intent intent = new Intent(IntroActivity.this, acceptingcustomerwindow.class);
                                                                                        intent.putExtra("driverid", driverUser.getSwitchingSystem());

                                                                                        startActivity(intent);
                                                                                        overridePendingTransition(R.anim.fade_in, R.anim.fadeout);

                                                                                        finish();

                                                                                    }
                                                                                    else
                                                                                    {
                                                                                        Intent intent = new Intent(IntroActivity.this, PaymentGateWay.class);
                                                                                        intent.putExtra("driverid", driverUser.getSwitchingSystem());

                                                                                        startActivity(intent);
                                                                                        overridePendingTransition(R.anim.fade_in, R.anim.fadeout);

                                                                                        finish();

                                                                                    }

                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                }
                                                                            });


                                                                        }
                                                                    } catch (Exception e) {
                                                                        e.printStackTrace();
                                                                    }


                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });
                                                    DatabaseReference dbrequest = FirebaseDatabase.getInstance().getReference(Common.destination_request_tb1);
                                                    Rider rider = new Rider();
                                                    rider.setDestination("");
                                                    dbrequest.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(rider).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                        }
                                                    });

                                                    startActivity(new Intent(IntroActivity.this, Map_destination_Select.class));
                                                    waitingDialog.dismiss();
                                                    overridePendingTransition(R.anim.fade_in,R.anim.fadeout);

                                                    finish();
                                                } } }catch (Exception e) {
                                            Toast.makeText(IntroActivity.this, "Invalid Customer Details", Toast.LENGTH_SHORT).show();
                                            e.printStackTrace();
                                            Intent ab=new Intent(IntroActivity.this,IntroActivity.class);
                                            startActivity(ab);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        waitingDialog.dismiss();

                        Snackbar.make(rootLayout,"Failed"+e.getMessage(),Snackbar.LENGTH_SHORT).show();
                        btnSignIn.setEnabled(true);
                    }
                });
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showRegisterDialog()
    {
        final  AlertDialog.Builder dialog=new AlertDialog.Builder(IntroActivity.this);
        dialog.setTitle("Register");
        dialog.setMessage("please use mail to register");
        LayoutInflater inflater=LayoutInflater.from(IntroActivity.this);
        final View register_layout=inflater.inflate(R.layout.layout_register,null);
        final MaterialEditText edtEmail=register_layout.findViewById(R.id.edtEmail);
        final MaterialEditText edtPassword=register_layout.findViewById(R.id.edtPassword);
        final MaterialEditText edtName=register_layout.findViewById(R.id.edtName);
        final MaterialEditText edtPhone=register_layout.findViewById(R.id.edtPhone);

        dialog.setView(register_layout);

        dialog.setPositiveButton("Register", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(TextUtils.isEmpty(edtEmail.getText().toString()))
                {
                    Snackbar.make(rootLayout,"Please enter Email adress",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(edtPassword.getText().toString()))
                {
                    Snackbar.make(rootLayout,"Please enter password",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(edtPassword.getText().toString().length()<6)
                {
                    Snackbar.make(rootLayout,"password too short!!!",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(edtPhone.getText().toString()))
                {
                    Snackbar.make(rootLayout,"Please enter phone number",Snackbar.LENGTH_SHORT).show();
                    return;
                }

                final DatabaseReference userref=FirebaseDatabase.getInstance().getReference(Common.user_rider_tb1);
                userref.orderByChild("phone").equalTo("+91"+edtPhone.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        userref.orderByChild("email").equalTo(edtEmail.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                if(dataSnapshot1.getValue()==null){
                                    if(dataSnapshot.getValue()!=null)
                                    {   Toast.makeText(IntroActivity.this, "Mobile number already registered", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Intent intent = new Intent(IntroActivity.this, otpverification.class);
                                        intent.putExtra("email",edtEmail.getText().toString());
                                        intent.putExtra("name",edtName.getText().toString());
                                        intent.putExtra("password",edtPassword.getText().toString());
                                        intent.putExtra("phone",edtPhone.getText().toString());
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.fade_in,R.anim.fadeout);
                                        finish();


                                    }
                                }
                                else
                                {
                                    Toast.makeText(IntroActivity.this, "email aready exist", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });





                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




                // Intent intent1=getIntent();
                //   String ss=intent1.getStringExtra("success");


            }
        });


        dialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();



    }
}
