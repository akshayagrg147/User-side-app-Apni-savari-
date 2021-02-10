package com.example.apnisavari;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.apnisavari.Common.Common;
import com.example.apnisavari.Model.DataMessage;
import com.example.apnisavari.Model.FCMResponse;
import com.example.apnisavari.Model.Rider;
import com.example.apnisavari.Model.Token;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CancellationReason extends AppCompatActivity {
    ListView listview;
    List list=new ArrayList();
    ArrayAdapter ADAPTER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancellation_reason);
        listview = (ListView) findViewById(R.id.listview);
        list.add("Driver is not Reachable");
        list.add("Driver is doing misbehave");
        list.add("Driver say to decline");
        list.add("Testing Purposes");


        ADAPTER = new ArrayAdapter(CancellationReason.this, android.R.layout.simple_dropdown_item_1line, list);
        listview.setAdapter(ADAPTER);


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String str=list.get(i).toString();
                FirebaseDatabase.getInstance().getReference("Destination").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String dstination=dataSnapshot.child("destination").getValue(String.class);

                                FirebaseDatabase.getInstance().getReference("SwitchingSystem").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String driverid=dataSnapshot.child("switchingSystem").getValue(String.class);
                                        final String  customerKey=FirebaseDatabase.getInstance().getReference("DriverTripHistory").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().getKey();

                                        FirebaseDatabase.getInstance().getReference("StartingPoint").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                String startingPoint=dataSnapshot.child("destination").getValue(String.class);
                                                FirebaseDatabase.getInstance().getReference("TripHistory").child("CancelledHistory").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(customerKey).child("PickUpPoint").setValue(startingPoint).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                    }
                                                });


                                            }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                        FirebaseDatabase.getInstance().getReference("TripHistory").child("CancelledHistory").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(customerKey).child("Destination").setValue(dstination).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                            }
                                        });
                                        Date c= Calendar.getInstance().getTime();
                                        SimpleDateFormat df=new SimpleDateFormat("dd-MMM-yyyy");
                                        String formatedate=df.format(c);

                                        FirebaseDatabase.getInstance().getReference("TripHistory").child("CancelledHistory").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(customerKey).child("Date").setValue(formatedate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                            }
                                        });
                                        FirebaseDatabase.getInstance().getReference("TripHistory").child("CancelledHistory").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(customerKey).child("BookingId").setValue(customerKey).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                            }
                                        });
                                        if(!driverid.isEmpty()) {
                                            FirebaseDatabase.getInstance().getReference("TripHistory").child("CancelledHistory").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(customerKey).child("driverId").setValue(driverid).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                }
                                            });
                                        }
                                        else
                                        {
                                            Log.d("driver id","driverid is null");
                                        }
                                        FirebaseDatabase.getInstance().getReference("TripHistory").child("CancelledHistory").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(customerKey).child("Reason").setValue(str).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                            }
                                        });

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



                FirebaseDatabase.getInstance().getReference(Common.SwitchingSystem).
                        addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                final String[] driver1 = new String[1];
                                for (DataSnapshot childsnap : dataSnapshot.getChildren())
                                    driver1[0] = childsnap.child("switchingSystem").getValue(String.class);
                                if (!driver1[0].isEmpty()) {




                                    DatabaseReference tokens = FirebaseDatabase.getInstance().getReference(Common.token_Tb1);


                                    tokens.orderByKey().equalTo(driver1[0])
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                        Token token = postSnapshot.getValue(Token.class);
                                                        Map<String,String> content=new HashMap<>();
                                                        content.put("title","Cancel");
                                                        content.put("message","Customer has cancelled your request");
                                                        content.put("response","cancel");
                                                        DataMessage dataMessage = new DataMessage(token.getToken(),content );
                                                        Log.d("bk","bk");
                                                        Common.getFCMsService().sendMessage(dataMessage)
                                                                .enqueue(new Callback<FCMResponse>() {
                                                                    @Override
                                                                    public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                                                                        if (response.body() != null && response.body().success == 1) {
                                                                            DatabaseReference dbrequest1 = FirebaseDatabase.getInstance().getReference(Common.SwitchingSystem);
                                                                            Rider ob = new Rider();
                                                                            ob.setSwitchingSystem("");
                                                                            dbrequest1.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(ob).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {


                                                                                }
                                                                            });
                                                                            FirebaseDatabase.getInstance().getReference("CallForward").child(driver1[0]).setValue("").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {

                                                                                }
                                                                            });
                                                                            Toast.makeText(CancellationReason.this, "Cancelled", Toast.LENGTH_SHORT).show();
                                                                          //  Paper.init(CancellationReason.this);
                                                                         //   Paper.book().destroy();
                                                                          //  FirebaseAuth.getInstance().signOut();
                                                                        //    SharedPreferences preferences=getSharedPreferences("DriverInfo", Context.MODE_PRIVATE);
                                                                        //    preferences.edit().remove("driverId").apply();
                                                                            Intent intent = new Intent(CancellationReason.this, Map_destination_Select.class);
                                                                            startActivity(intent);
                                                                            overridePendingTransition(R.anim.fade_in,R.anim.fadeout);
                                                                            finish();

                                                                        } else
                                                                            Toast.makeText(CancellationReason.this, "!Failed", Toast.LENGTH_SHORT).show();
                                                                    }

                                                                    @Override
                                                                    public void onFailure(Call<FCMResponse> call, Throwable t) {
                                                                        Log.e("ERROR", t.getMessage());
                                                                    }
                                                                });
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

            }
        });





    }

}
