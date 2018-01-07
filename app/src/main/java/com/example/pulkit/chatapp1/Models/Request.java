package com.example.pulkit.chatapp1.Models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pulkit-mac on 07/01/18.
 */

public class Request {
    private List uid = new ArrayList<String>();

    public Request(List uid) {
        this.uid = uid;
    }

    public Request() {
    }

    public List getUid() {
        return uid;
    }

    public void setUid(List uid) {
        this.uid = uid;
    }
}
