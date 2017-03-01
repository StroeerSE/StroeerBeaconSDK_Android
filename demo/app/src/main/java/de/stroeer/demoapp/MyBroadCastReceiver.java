package de.stroeer.demoapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import de.stroeer.proxity.gateway.Message;
import de.stroeer.proxity.gateway.MessageIntent;
import de.stroeer.proxity.gateway.StatusIntent;
import de.stroeer.proxity.gateway.Dependencies;

/**
 * Created by mfriedl on 16.10.2014.
 */
public class MyBroadCastReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = MyBroadCastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Receiver", this.toString());
        if (intent.getAction().equals(MessageIntent.ACTION_NAME)) {
            MessageIntent messageIntent = MessageIntent.fromIntent(intent);
            Message message = messageIntent.getMessage();
            switch (message.getCode()) {
                case FAILURE_CONFIGURATION:
                    scanStartFailedSdkNotSetup();
                    break;
                case FAILURE_BLE:
                    onBleInitializationFailed(message.getMessage());
                    break;
                default:
                    unknownMessage(intent);
            }
        }
        if (intent.getAction().equals(StatusIntent.ACTION_NAME)) {
            StatusIntent statusIntent = StatusIntent.fromIntent(intent);
            Dependencies status = statusIntent.getStatus();
            addToDebugView(LOG_TAG, "Status " + status.toString() + (statusIntent.isGained() ? " gained" : " revoked"));
            if (statusIntent.isGained()) {
                switch (status) {
                }
            }
        }
    }

    public void scanStartFailedSdkNotSetup() {
        //you cannot use the sdk because it was not set up correctly before
        addToDebugView(LOG_TAG, "scanStartFailedSdkNotSetup");
    }

    public void unknownMessage(Intent intent) {
        //this methode should not be called, as there should not be unknown messages
        Message message = (Message) intent.getSerializableExtra("message");
        addToDebugView(LOG_TAG, message.getMessage());
    }

    public void onBleInitializationFailed(String bleError) {
        addToDebugView(LOG_TAG, "onBleInitializationFailed: " + bleError);
    }

    protected void addToDebugView(String logtag, String message) {
        Log.d(logtag, message);
    }
}