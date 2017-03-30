package de.stroeer.demoapp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;

import java.io.File;

import de.stroeer.model.Beacon;
import de.stroeer.proxity.StroeerProxityApi;
import de.stroeer.proxity.gateway.Gateway;
import de.stroeer.proxity.gateway.Message;
import de.stroeer.proxity.gateway.ResponseCode;
import de.stroeer.proxity.gateway.Dependencies;


public class MyActivity extends Activity implements Gateway.IGatewayListener {

    public static final String API_KEY = "Type Api-Key here";

    // A simple TextView which will show all given messages to the user
    private TextView debugView;

    /*
     * onCreate-Method of the Activity
     *
     * see Android-Documentation for more information:
     * https://developer.android.com/reference/android/app/Activity.html
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //calls the onCreate-Method of the super class "Activity"
        super.onCreate(savedInstanceState);

        //Sets the layout for this activity
        setContentView(R.layout.activity_my);

        //returns the instance of the TextView
        this.debugView = (TextView) findViewById(R.id.debugView);

        //Defines the ApiKey used by the sdk
        StroeerProxityApi.getInstance(this).setApiKey(API_KEY);

        //Defines a custom advertisingID
        StroeerProxityApi.getInstance(this).setCustomAdvertisingId("Type Custom Advertising Id here");

        //The Sdk will add the google advertisingId to each Analytics Event
        StroeerProxityApi.getInstance(this).addSystemAdvertisingId(true);

        //Get default external storage path from system
        String path = Environment.getExternalStorageDirectory().getPath() + "/";

        //Set Path of the log-file
        StroeerProxityApi.getInstance(this).setLogFile(new File(path + "logfile.txt"));

        //Start scan for beacons
        StroeerProxityApi.getInstance(this).startScan();
    }

    /*
     * onResume-Method of the Activity
     *
     * see Android-Documentation for more information:
     * https://developer.android.com/reference/android/app/Activity.html
     */
    @Override
    protected void onResume() {
        //calls the onResume-Method of the super class "Activity"
        super.onResume();

        //register the GatewayListener which will get all information sent by the sdk
        StroeerProxityApi.getInstance(this).registerGatewayListener(this);

        //get all states of the sdk again (synchronize app and sdk)
        StroeerProxityApi.getInstance(this).resendCurrentState();
    }

    /*
     * onPause-Method of the Activity
     *
     * see Android-Documentation for more information:
     * https://developer.android.com/reference/android/app/Activity.html
     */
    @Override
    protected void onPause() {
        //calls the onPause-Method of the super class "Activity"
        super.onPause();

        //unregister the GatewayListener
        StroeerProxityApi.getInstance(this).unregisterGatewayListener(this);
    }

    /**
     * This method is called whenever the sdk has to inform you about new messages
     *
     * @param message:              the actual message with descripion, code and data which is new
     * @param deliverdForFirstTime: determines if this message was sent for the first time (true) or it is a copy (false). This is neccessary for reseting the app if its gone to background and was brought to the front again.
     */
    @Override
    public void onMessage(Message message, boolean deliverdForFirstTime) {

        //If the message was delivered for the first time print this message to the android-log
        if (deliverdForFirstTime) {
            //Print the given message to the android-log
            this.debugView.setText(this.debugView.getText() + message.getMessageType().toString() + ": " + message.getMessage() + '\n');
        }

        //Check if the message is a INFO_ENTERED_BEACON_REGION
        if (message.getCode() == ResponseCode.INFO_ENTERED_BEACON_REGION) {
            //Returns the entered beacon into the enteredBeacon variable
            Beacon enteredBeacon = (Beacon) message.getData();

            //Do some logic with the enteredBeacon
        }

        //Check if the message is a INFO_LEFT_BEACON_REGION
        if (message.getCode() == ResponseCode.INFO_LEFT_BEACON_REGION) {
            //Returns the left beacon into the leftBeacon variable
            Beacon leftBeacon = (Beacon) message.getData();

            //Do some logic with the leftBeacon
        }
    }

    /**
     * is called whenever a new status was gained. Look inside of Dependencies class to inform you about the possible statuses
     *
     * @param status:               the actual status which was gained
     * @param deliverdForFirstTime: determines if this message was sent for the first time (true) or it is a copy (false). This is neccessary for reseting the app if its gone to background and was brought to the front again.
     */
    @Override
    public void onStatusGained(Dependencies status, boolean deliverdForFirstTime) {
        //Prints the given status to the android-log
        this.debugView.setText(this.debugView.getText() + status.toString() + " gained [deliverdForFirstTime: " + deliverdForFirstTime + "]\n");
    }

    /**
     * is called whenever a status was revoked. Look inside of Dependencies class to inform you about the possible statuses
     *
     * @param status:               the actual status which was revoked
     * @param deliverdForFirstTime: determines if this message was sent for the first time (true) or it is a copy (false). This is neccessary for reseting the app if its gone to background and was brought to the front again.
     */
    @Override
    public void onStatusRevoked(Dependencies status, boolean deliverdForFirstTime) {
        //If the message was delivered for the first time print this message to the android-log
        if (deliverdForFirstTime) {
            //Prints the given status to the android-log
            this.debugView.setText(this.debugView.getText() + status.toString() + " revoked\n");
        }
    }
}
