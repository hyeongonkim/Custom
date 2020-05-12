package com.simonkim.custom;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
    }

    @Override
    public void onNewToken(String token) {
    }

    private void scheduleJob() {
    }

    private void handleNow() {
    }

    private void sendRegistrationToServer(String token) {
    }

    private void sendNotification(String messageBody) {
    }
}