package com.example.pulkit.chatapp1.Models;

/**
 * Created by Pulkit on 12/28/2017.
 */

public class Friends {

    public Friends() {

    }

    public Friends(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String date;
}
