package com.match2blue.blulocdemoapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;
import java.util.Map;

import de.stroeer.proxity.gateway.Message;
import de.stroeer.proxity.gateway.MessageIntent;
import de.stroeer.proxity.gateway.SdkStatus;
import de.stroeer.proxity.gateway.StatusIntent;
import de.stroeer.proxity.model.DeviceAction;
import de.stroeer.proxity.model.Fingerprint;
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
                case INFO_ACTIONS_RECEIVED:
                    actionReceived((List<DeviceAction>) message.getData());
                    break;
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
                            MyApplication.getStroeerProxityApi().setSphereNameAndAppKey(MyActivity.SPHERE_NAME, MyActivity.APP_KEY);
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
        Map<String, Fingerprint> fingerprints = overallScanResult.getCurrentFingerprints();
        String text = "-----scanFinished-----\n";
        for (Fingerprint fingerprint : fingerprints.values()) {
            text += fingerprint.getMajor() + ":" + fingerprint.getMinor() + " rssi: " + fingerprint.getWindowedRssi() + '\n';
        }
        addToDebugView(LOG_TAG, text);
    }

    public void onBleInitializationFailed(String bleError) {
        addToDebugView(LOG_TAG, "onBleInitializationFailed: " + bleError);
    }

    public void actionReceived(List<DeviceAction> blDeviceActions) {
        // this method is being called after a ScanPeriod was finished and new actions were received
        for (DeviceAction action : blDeviceActions) {
            addToDebugView(LOG_TAG, "actionReceived: " + action);
        }
    }


    public void locationReceived(Location blulocLocation) {
        //gets called if a new position was found
        if (blulocLocation != null) {
            addToDebugView(LOG_TAG, "locationReceived: " + blulocLocation.getLatitude() + "/" + blulocLocation.getLongitude() + ":" + blulocLocation.getFloor());
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
        //Every thing (sdk setup and data downloaded correctly) works fine you can now receive actions and locations
        addToDebugView(LOG_TAG, "setupSucceeded");

        // you can also do navigation
        // MyApplication.getBlulocApi().navigateAsync(new FELocation(0, 0, 0), new FELocation(10, 10, 0));
    }

    protected void addToDebugView(String logtag, String message) {
        Log.d(logtag, message);
    }
}
