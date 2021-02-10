package com.example.apnisavari;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apnisavari.Common.Common;
import com.example.apnisavari.Model.DataMessage;
import com.example.apnisavari.Model.FCMResponse;
import com.example.apnisavari.Model.Rider;
import com.example.apnisavari.Model.Token;
import com.example.apnisavari.Remote.IFCMService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallDriver extends AppCompatActivity {

    CircleImageView avtarimage;
    TextView txt_name, txt_phone, txt_rate;
    Button btn_call_driver, btn_call_driver_phone;
    String driverId;
    Location mLastLocation;
    IFCMService mService;
    DatabaseReference rateDetailRef;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database=FirebaseDatabase.getInstance();
        rateDetailRef=database.getReference(Common.rate_detail_tb1);
        setContentView(R.layout.activity_call_driver);
        mService = Common.getFCMsService();
        avtarimage = (CircleImageView) findViewById(R.id.avatar_image);
        txt_name = findViewById(R.id.txtName);
        txt_phone = findViewById(R.id.txt_phone);
        txt_rate = findViewById(R.id.txt_rate);
        btn_call_driver = findViewById(R.id.btn_call_driver);
        btn_call_driver_phone = findViewById(R.id.btn_call_driver_phone);

            driverId=getIntent().getStringExtra("driverId");
            double lat=getIntent().getDoubleExtra("lat",-1.0);
            double lng=getIntent().getDoubleExtra("lng",-1.0);
            mLastLocation=new Location("");
            mLastLocation.setLatitude(lat);
            mLastLocation.setLongitude(lng);
            loadDriverInfo(driverId);

      //  Log.d("Driver",driverId);
        btn_call_driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             if (driverId != null && !driverId.isEmpty())

                    sendRequestToDriver(driverId, mService, getBaseContext(), mLastLocation);
                else
                 Toast.makeText(CallDriver.this,"\n1."+driverId+"\n2."+mService+"\n3."+mLastLocation,Toast.LENGTH_SHORT).show();



            }
        });
        btn_call_driver_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + txt_phone.getText().toString()));
                if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                startActivity(intent);
            }
        });


    }

    public  void sendRequestToDriver(final String driverId, final IFCMService mServicee, final Context context, final Location currentLocation) {
        final String customerid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference(Common.token_Tb1);


        tokens.orderByKey().equalTo(driverId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Token token = postSnapshot.getValue(Token.class);


                            String riderToken = FirebaseInstanceId.getInstance().getToken();
                            Map<String,String> content=new HashMap<>();
                            content.put("customer",customerid);
                            content.put("lat",String.valueOf(mLastLocation.getLatitude()));
                            content.put("lng",String.valueOf(mLastLocation.getLongitude()));

                            DataMessage dataMessage = new DataMessage(token.getToken(),content );



                            //    Toast.makeText(Home.this,"Request sent"+content,Toast.LENGTH_SHORT).show();
                            mServicee.sendMessage(dataMessage)
                                    .enqueue(new Callback<FCMResponse>() {
                                        @Override
                                        public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {

                                            if (response.body() != null && response.body().success == 1) {
                                                new SweetAlertDialog(CallDriver.this,SweetAlertDialog.SUCCESS_TYPE).setTitleText("..Request sent..!")
                                                        .setTitleText("Wait 30 seconds").show();


                                            } else
                                                new SweetAlertDialog(CallDriver.this,SweetAlertDialog.ERROR_TYPE).setTitleText("..Oops..!")
                                                        .setTitleText("Something went wrong").show();


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


    private void loadDriverInfo(String driverId) {

        FirebaseDatabase.getInstance()
                .getReference(Common.user_driver_tb1)
                .child(driverId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Rider driverUser=dataSnapshot.getValue(Rider.class);

                       if(!driverUser.getProfileAks().isEmpty())
                        {
                            Picasso.get()
                                    .load(driverUser.getProfileAks())
                                    .into(avtarimage);
                        }

                        txt_name.setText(driverUser.getName());
                        txt_phone.setText(driverUser.getPhone());
                        txt_rate.setText(driverUser.getRates());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
