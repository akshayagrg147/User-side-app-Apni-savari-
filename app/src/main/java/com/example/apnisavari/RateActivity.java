package com.example.apnisavari;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.apnisavari.Common.Common;
import com.example.apnisavari.Model.Rate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class RateActivity extends AppCompatActivity {
    Button btnSubmit;
    MaterialRatingBar ratingBar;
    MaterialEditText edtComment;
    FirebaseDatabase database;
    DatabaseReference rateDetailRef;
    DatabaseReference driverInformationRef;
    double ratingStars=0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        database=FirebaseDatabase.getInstance();
        rateDetailRef=database.getReference(Common.rate_detail_tb1);
        driverInformationRef=database.getReference(Common.user_driver_tb1);


        btnSubmit=findViewById(R.id.btnSubmit);
        ratingBar=findViewById(R.id.ratingBar);
        edtComment=findViewById(R.id.edtComment);
        ratingBar.setOnRatingChangeListener(new MaterialRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChanged(MaterialRatingBar ratingBar, float rating) {
                ratingStars=rating;
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitRateDetails(Common.driverId);
            }
        });
    }

    private void submitRateDetails(final String driverId) {

        final SpotsDialog alertDialog= new SpotsDialog(this);
        alertDialog.show();
Rate rate=new Rate();
rate.setRates(String.valueOf(ratingStars));
rate.setComments(edtComment.getText().toString());
rateDetailRef.child(driverId)
       .push().setValue(rate)
.addOnCompleteListener(new OnCompleteListener<Void>() {
    @Override
    public void onComplete(@NonNull Task<Void> task) {

        rateDetailRef.child(driverId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        double averageStars=0.0;
                        int count=0;
                        for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                        {
                            Rate rate=postSnapshot.getValue(Rate.class);
                            averageStars+=Double.parseDouble(rate.getRates());
                            count++;
                        }
                        double finalAverage=averageStars/count;
                        DecimalFormat df=new DecimalFormat("#.#");
                        String valueUpdate=df.format(finalAverage);

                        Map<String,Object> driverUpdateRate=new HashMap<>();
                        driverUpdateRate.put("rates",valueUpdate);

                        driverInformationRef.child(Common.driverId)
                                .updateChildren(driverUpdateRate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        alertDialog.dismiss();
                                        Toast.makeText(RateActivity.this,"thanks for your submission",Toast.LENGTH_SHORT).show();
                                        Intent ab=new Intent(RateActivity.this,PaymentGateWay.class);
                                        startActivity(ab);
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(RateActivity.this,"Rate update but can not write",Toast.LENGTH_SHORT).show();
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
        alertDialog.dismiss();
        Toast.makeText(RateActivity.this,"Rate Failed",Toast.LENGTH_SHORT).show();
    }
});

    }
}
