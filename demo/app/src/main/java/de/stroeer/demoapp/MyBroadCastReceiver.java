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
 * <p>
 * If you like to get all information of the sdk, also if the app is in background,
 * you have to implement a BroadcastReceiver and register it for the actions:
 * <p>
 * "de.stroeer.proxity.gateway.StatusIntent"
 * "de.stroeer.proxity.gateway.MessageIntent"
 * <p>
 * See AndroidManifest.xml line 22-29
 * <p>
 * This is small example implementation of this BroadcastReceiver which prints all messages to
 * android-log
 */
public class MyBroadCastReceiver extends BroadcastReceiver {
    //Default Tag of a class. Used to tag the logs for this name
    private static final String LOG_TAG = MyBroadCastReceiver.class.getSimpleName();

   /*
    * Each time the sdk sends a new message or status change this method is called.
    *
    * To get the needed information out of the intent you have to identify the type of intent,
    * according to the actionname (line 48 and line 71). The Action is either MessageIntent.ACTION_NAME
    * or StatusIntent.ACTION_NAME.
    *
    * A convenience method for the MessageIntent and StatusIntent are implemented (line 49 and 72)
    *
    * After you got the corresponding Intent (MessageIntent/StatusIntent) you can get all necessary
    * information out of this instances. (line 52 - 68 and line 75- 77)
    */
    @Override
    public void onReceive(Context context, Intent intent) {
        //Checks if the sent intent is a MessageIntent
        if (intent.getAction().equals(MessageIntent.ACTION_NAME)) {
            //Parse the intent to the corresponding MessageIntent to get easy access to all information
            MessageIntent messageIntent = MessageIntent.fromIntent(intent);
            //Returns the Message into the message variable
            Message message = messageIntent.getMessage();
            //Switch over ResponseCode to define some specific logic for each needed responseCode
            //See JavaDoc of ResponseCode for more information about each Type
            switch (message.getCode()) {
                case FAILURE_CONFIGURATION:
                    //Call helper method scanStartFailedSdkNotSetup which will print some logs
                    scanStartFailedSdkNotSetup();
                    break;
                case FAILURE_BLE:
                    //Call helper method scanStartFailedSdkNotSetup which will print some logs
                    onBleInitializationFailed(message.getMessage());
                    break;
                //For each ResponseCode which isn't FAILURE_CONFIGURATION or FAILURE_BLE call unknownMessage-Method
                default:
                    //Call helper method scanStartFailedSdkNotSetup which will print some logs
                    unknownMessage(message);
            }
        }
        //Check if the sent intent is a StatusIntent
        if (intent.getAction().equals(StatusIntent.ACTION_NAME)) {
            //Parse the intent to the corresponding StatusIntent to get easy access to all information
            StatusIntent statusIntent = StatusIntent.fromIntent(intent);
            //Returns the Status into the status variable
            Dependencies status = statusIntent.getStatus();
            //Prints the given status and whether it was gained or revoked to the android-log
            addToDebugView(LOG_TAG, "Status " + status.toString() + (statusIntent.isGained() ? " gained" : " revoked"));
        }
    }

    //you cannot use the sdk because it was not set up correctly before
    public void scanStartFailedSdkNotSetup() {
        //Prints the given message to the android-log
        addToDebugView(LOG_TAG, "scanStartFailedSdkNotSetup");
    }

    //helper method which will print some logs
    public void unknownMessage(Message message) {
        //Prints the given message to the android-log
        addToDebugView(LOG_TAG, message.getMessage());
    }

    //helper method which will print some logs
    public void onBleInitializationFailed(String bleError) {
        //Prints the given bleError-string to the android-log
        addToDebugView(LOG_TAG, "onBleInitializationFailed: " + bleError);
    }

    //helper method which will print some logs
    protected void addToDebugView(String logtag, String message) {
        //Prints the given message-string to the android-log
        Log.d(logtag, message);
    }
}
