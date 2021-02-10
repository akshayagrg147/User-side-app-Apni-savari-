package com.example.apnisavari.Remote;

import com.example.apnisavari.Model.DataMessage;
import com.example.apnisavari.Model.FCMResponse;
import com.google.firebase.database.DataSnapshot;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAhF6Mjwk:APA91bHvwrEKbQVlJFJ9LM3MFRzL_uaG_VL4OzbubtwbKDzKumQj6OiHoHU63HehcIDyuptJoG-9M_yox4eIiq8cUmBZF9jm_iRk1HjLC9rz0yABu-eNW3VU8dVgbya2ASumQRv0Dlg1"
    })

    @POST("fcm/send")
    Call<FCMResponse> sendMessage(@Body DataMessage body);
}
