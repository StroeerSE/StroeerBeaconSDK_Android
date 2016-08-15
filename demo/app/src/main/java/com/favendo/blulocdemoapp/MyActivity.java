package com.favendo.blulocdemoapp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import de.stroeer.proxity.StroeerProxityApi;
import de.stroeer.proxity.gateway.Gateway;
import de.stroeer.proxity.gateway.Message;
import de.stroeer.proxity.gateway.SdkStatus;


public class MyActivity extends Activity implements Gateway.IGatewayListener {

    public static final String APP_KEY = "8c75d759-0bb8-4ed9-9ee7-d6d061cda2be";

    private TextView debugView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        this.debugView = (TextView) findViewById(R.id.debugView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getStroeerProxityApi().registerGatewayListener(this);
        MyApplication.getStroeerProxityApi().resendCurrentState();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.getStroeerProxityApi().unregisterGatewayListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onMessage(StroeerProxityApi stroeerProxityApi, Message message, boolean isNew) {
        if (isNew) {
            String text = message.getMessage();

            switch (message.getCode()) {
                case INFO_NEW_OVERALL_STATUS:
                    text = message.getData().toString();
                    break;
            }

            this.debugView.setText(this.debugView.getText() + text + '\n');
        }
    }

    @Override
    public void onStatusGained(StroeerProxityApi stroeerProxityApi, SdkStatus status, boolean isNew) {
        if (isNew) {
            this.debugView.setText(this.debugView.getText() + status.toString() + " gained\n");
        }
        if (status == SdkStatus.API_READY) {
            enableFileLogging(stroeerProxityApi);
        }
    }

    @Override
    public void onStatusRevoked(StroeerProxityApi stroeerProxityApi, SdkStatus status, boolean isNew) {
        if (isNew) {
            this.debugView.setText(this.debugView.getText() + status.toString() + " revoked\n");
        }
    }

    private void enableFileLogging(StroeerProxityApi stroeerProxityApi) {
        try {
            String path = Environment.getExternalStorageDirectory().getPath() + "/";
            File file = new File(path + "logfile.txt");
            if (!file.exists())
                file.createNewFile();
            stroeerProxityApi.setLogFileOutpuStream(new FileOutputStream(file, true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
