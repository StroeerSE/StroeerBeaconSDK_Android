package de.stroeer.demoapp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;

import java.io.File;

import de.stroeer.proxity.StroeerProxityApi;
import de.stroeer.proxity.gateway.Dependencies;
import de.stroeer.proxity.gateway.Gateway;
import de.stroeer.proxity.gateway.Message;
import de.stroeer.proxity.gateway.ResponseCode;
import de.stroeer.proxity.model.Beacon;


public class MyActivity extends Activity implements Gateway.IGatewayListener {

    public static final String API_KEY = ">>>Type Api-Key here<<<";

    private TextView debugView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        this.debugView = (TextView) findViewById(R.id.debugView);

        StroeerProxityApi.getInstance(this).setApiKey(API_KEY);
        StroeerProxityApi.getInstance(this).startScan();

        this.enableFileLogging();
    }

    @Override
    protected void onResume() {
        super.onResume();
        StroeerProxityApi.getInstance(this).registerGatewayListener(this);
        StroeerProxityApi.getInstance(this).resendCurrentState();
    }

    @Override
    protected void onPause() {
        super.onPause();
        StroeerProxityApi.getInstance(this).unregisterGatewayListener(this);
    }

    @Override
    public void onMessage(Message message, boolean isNew) {
        if (isNew) {
            this.debugView.setText(this.debugView.getText() + message.getMessage() + '\n');
        }

        if (message.getCode() == ResponseCode.INFO_ENTERED_BEACON_REGION) {
            Beacon enteredBeacon = (Beacon) message.getData();
        }

        if (message.getCode() == ResponseCode.INFO_LEFT_BEACON_REGION) {
            Beacon left = (Beacon) message.getData();
        }
    }

    @Override
    public void onStatusGained(Dependencies status, boolean deliverdForFirstTime) {
        this.debugView.setText(this.debugView.getText() + status.toString() + " gained [deliverdForFirstTime: " + deliverdForFirstTime + "]\n");
    }

    @Override
    public void onStatusRevoked(Dependencies status, boolean deliverdForFirstTime) {
        if (deliverdForFirstTime) {
            this.debugView.setText(this.debugView.getText() + status.toString() + " revoked\n");
        }
    }

    private void enableFileLogging() {
        String path = Environment.getExternalStorageDirectory().getPath() + "/";
        StroeerProxityApi.getInstance(this).setLogFile(new File(path + "logfile.txt"));
    }
}
