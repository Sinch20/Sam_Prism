package com.example.Blockchain_App.Model;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Request {

    private String name;
    private String reqID;
    private String url;
    private int no_of_approvals;

    //required empty constructor
    public Request(){}


    public Request(String name, String reqID, String url, int approvals){
        this.name = name;
        this.reqID = reqID;
        this.url = url;
        this.no_of_approvals = approvals;
    }


    //getter and setter for each of the variables
    public int getNo_of_approvals() {
        return no_of_approvals;
    }

    public String getReqID() {
        return reqID;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNo_of_approvals(int no_of_approvals) {
        this.no_of_approvals = no_of_approvals;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setReqID(String reqID) {
        this.reqID = reqID;
    }

    @Exclude
    public Map<String, String> toMap() {
        HashMap<String, String> result = new HashMap<>();
        result.put("ReqID", getReqID());
        result.put("Name", getName());
        result.put("URL", getUrl());
        result.put("Approvals", String.valueOf(getNo_of_approvals()));

        return result;
    }

}
