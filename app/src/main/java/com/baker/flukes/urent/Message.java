package com.baker.flukes.urent;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rflukes on 11/4/17.
 */

public class Message {
    private String mId;
    private String mProperty;
    private String mRecipient;
    private String mSender;
    private String mDate;
    private String mContent;

    public Message(){

    };

    public Message(String address, String sender, String recipient, String date, String content){
        mProperty = address;
        mRecipient = recipient;
        mSender = sender;
        mDate = date;
        mContent = content;
    }

    public String getSender() {
        return mSender;
    }

    public void setSender(String sender) {
        this.mSender = sender;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        this.mDate = date;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        this.mContent = content;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("property", mProperty);
        result.put("date", mDate);
        result.put("content", mContent);
        result.put("sender", mSender);
        result.put("recipient", mRecipient);
        return result;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getProperty() {
        return mProperty;
    }

    public void setProperty(String address) {
        mProperty = address;
    }

    public String getRecipient() {
        return mRecipient;
    }

    public void setRecipient(String recipient) {
        mRecipient = recipient;
    }
}
