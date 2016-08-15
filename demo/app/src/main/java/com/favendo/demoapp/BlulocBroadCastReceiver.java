package com.favendo.demoapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import de.stroeer.proxity.gateway.Message;
import de.stroeer.proxity.gateway.MessageIntent;
import de.stroeer.proxity.gateway.SdkStatus;
import de.stroeer.proxity.gateway.StatusIntent;
import de.stroeer.proxity.model.Location;
import de.stroeer.proxity.model.ScanResult;

/**
 * Created by mfriedl on 16.10.2014.
 */
public class BlulocBroadCastReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = BlulocBroadCastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Receiver", this.toString());
        if (intent.getAction().equals(MessageIntent.ACTION_NAME)) {
            MessageIntent messageIntent = MessageIntent.fromIntent(intent);
            Message message = messageIntent.getMessage();
            switch (message.getCode()) {
                case INFO_LOCATION_UPDATE:
                    locationReceived((Location) message.getData());
                    break;
                case FAILURE_CONFIGURATION:
                    scanStartFailedSdkNotSetup();
                    break;
                case INFO_NEW_OVERALL_STATUS:
                    overallScanResultReceived((ScanResult) message.getData());
                    break;
                case FAILURE_BLE:
                    onBleInitializationFailed(message.getMessage());
                    break;
                case INFO_DOWNLOAD_SUCCESSFULL:
                    updateSucceeded();
                    break;
                case FAILURE_DOWNLOAD:
                    updateFailed();
                    break;
                default:
                    unknownMessage(intent);
            }
        }
        if (intent.getAction().equals(StatusIntent.ACTION_NAME)) {
            StatusIntent statusIntent = StatusIntent.fromIntent(intent);
            SdkStatus status = statusIntent.getStatus();
            addToDebugView(LOG_TAG, "Status " + status.toString() + (statusIntent.isGained() ? " gained" : " revoked"));
            if (statusIntent.isGained()) {
                switch (status) {
                    case Ready:
                        setupSucceeded();
                        break;
                    case API_READY:

                        if (!MyApplication.getStroeerProxityApi().isScanning()) {
                            MyApplication.getStroeerProxityApi().setApiKey(MyActivity.APP_KEY);
                            MyApplication.getStroeerProxityApi().startScan();
                        }
                        break;

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

    public void serviceStopped() {
        //informs you that the service has stop, so you could restart if you want
        addToDebugView(LOG_TAG, "serviceStopped");
    }

    public void serviceAlreadyRunning() {
        // if you try to setup the sdk while its already been setup this method gets called
        addToDebugView(LOG_TAG, "serviceAlreadyRunning");
    }

    public void overallScanResultReceived(ScanResult overallScanResult) {
        addToDebugView(LOG_TAG, "Scanned Beacons: \n" + overallScanResult.toString());
    }

    public void onBleInitializationFailed(String bleError) {
        addToDebugView(LOG_TAG, "onBleInitializationFailed: " + bleError);
    }

    public void locationReceived(Location location) {
        //gets called if a new position was found
        if (location != null) {
            addToDebugView(LOG_TAG, "locationReceived: " + location.getLatitude() + "/" + location.getLongitude());
        } else {
            addToDebugView(LOG_TAG, "locationReceived: no location");
        }

    }

    public void updateSucceeded() {
        addToDebugView(LOG_TAG, "updateSucceeded");
    }

    public void updateFailed() {
        addToDebugView(LOG_TAG, "updateFailed");
    }

    public void setupSucceeded() {
        //Everything (sdk setup and data downloaded correctly) works fine you can now receive actions and locations
        addToDebugView(LOG_TAG, "setupSucceeded");
    }

    protected void addToDebugView(String logtag, String message) {
        Log.d(logtag, message);
    }
}
