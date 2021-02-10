package com.example.apnisavari.Model;

import android.util.Log;
import android.widget.Toast;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FCMResponse {
    @SerializedName("multicast_id")
    public long multicast_id;


    @SerializedName("success")
    public int success;
    @SerializedName("failure")
    public int failure;
    @SerializedName("canonical_ids")
    public int canonical_ids;
    public List<Result> results;

    public FCMResponse()
    {

    }
    public FCMResponse(long multicast_id, int success, int failure, int canonical_ids, List<Result> results) {
        this.multicast_id = multicast_id;
        this.success = success;
        this.failure = failure;
        this.canonical_ids = canonical_ids;
        this.results = results;
        Log.v("fcm response check",success+""+multicast_id+"");
    }

    public long getMulticast_id() {
        return multicast_id;
    }

    public void setMulticast_id(long multicast_id) {
        this.multicast_id = multicast_id;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public int getFailure() {
        return failure;
    }

    public void setFailure(int failure) {
        this.failure = failure;
    }

    public int getCanonical_ids() {
        return canonical_ids;
    }

    public void setCanonical_ids(int canonical_ids) {
        this.canonical_ids = canonical_ids;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }
}
