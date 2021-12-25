package com.example.Blockchain_App.Model;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Request {

    private String name;
    private String reqID;
    private String mail;
    private String url;
    private List<String> approvals;
    private List<String> denials;

    //required empty constructor
    public Request(){}

    public Request(String name, String reqID, String mail, String url, List<String> approvals, List<String> denials){
        this.name = name;
        this.reqID = reqID;
        this.url = url;
        this.mail = mail;
        this.approvals = approvals;
        this.denials = denials;
    }


    //getter and setter for each of the variables
    public List<String> getApprovals() {
        return approvals;
    }

    public List<String> getDenials() {
        return denials;
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

    public String getMail() {
        return mail;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setApprovals(List<String> approvals) {
        this.approvals = approvals;
    }

    public void setDenials(List<String> denials) {
        this.denials = denials;
    }

    public void addApproval(String approval){
        if(this.approvals != null) {
            this.approvals.add(approval);
            this.approvals = new ArrayList<>(new HashSet<String>(this.approvals));
        }
        else
            this.approvals = new ArrayList<String>(Collections.singletonList(approval));
    }

    public void addDenial(String denial){
        if(this.denials != null){
            this.denials.add(denial);
            this.denials = new ArrayList<>(new HashSet<String>(this.denials));
        }
        else
            this.denials = new ArrayList<String>(Collections.singletonList(denial));
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setReqID(String reqID) {
        this.reqID = reqID;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }


    @Exclude
    public Map<String, String> toMap() {
        HashMap<String, String> result = new HashMap<>();
        result.put("ReqID", getReqID());
        result.put("Name", getName());
        result.put("Mail", getMail());
        result.put("URL", getUrl());
        result.put("Approvals", String.valueOf(getApprovals()));
        result.put("Denials", String.valueOf(getDenials()));

        return result;
    }

}
