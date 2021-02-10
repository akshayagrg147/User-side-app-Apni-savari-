package com.example.apnisavari;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;




import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class CancelledHistoryTrips extends AppCompatActivity {

    DatabaseReference reference;
    RecyclerView recyclerView;
    ArrayList<CancelledHistory> list;
    CancelledAdapter adapter;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancelled_history_trips);

        recyclerView = (RecyclerView) findViewById(R.id.myRecycler2);
        recyclerView.setLayoutManager( new LinearLayoutManager(this));
        toolbar= findViewById(R.id.tootlbarBack);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setTitle("Cancelled History");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Map_destination_Select.class));
                overridePendingTransition(R.anim.fade_in,R.anim.fadeout);
                finish();
            }
        });




        reference = FirebaseDatabase.getInstance().getReference("TripHistory").child("CancelledHistory").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list = new ArrayList<CancelledHistory>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    CancelledHistory p = dataSnapshot1.getValue(CancelledHistory.class);
                    list.add(p);
                }
                adapter = new CancelledAdapter(CancelledHistoryTrips.this,list);

                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CancelledHistoryTrips.this, "Opsss.... Something is wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }


}