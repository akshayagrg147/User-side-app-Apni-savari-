package com.example.apnisavari.Service;
import android.util.Log;

import com.example.apnisavari.Common.Common;
import com.example.apnisavari.Model.Token;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyFirebaseIdService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String s)
    {
        super.onNewToken(s);
        String refreshToken=FirebaseInstanceId.getInstance().getToken();
        Log.e("New token",s);
        updateTokenServer(refreshToken);
    }


     /*   FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MyFirebaseIdService.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken=instanceIdResult.getToken();
            }
        });
        Log.e("New token",s);
       updateTokenServer(refreshToken);
       */



    private void updateTokenServer(String refreshToken) {
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference tokens=db.getReference(Common.token_Tb1);
        Token token=new Token(refreshToken);
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
            tokens.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
            .setValue(token);
    }
}
