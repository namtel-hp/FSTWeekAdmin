package com.qerat.fstweekadmin;


import android.app.Activity;
import android.content.SharedPreferences;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyService extends FirebaseMessagingService {

    public MyService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        FirebaseUtilClass.getDatabaseReference().child("AdminUserTokens").child(s).setValue(true);

    }



}
