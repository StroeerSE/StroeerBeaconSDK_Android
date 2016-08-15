# Ströer Proxity SDK

## Purpose of the SDK
With the SDK you are scanning for so called beacons. These are special beacons of the Favendo GmbH. This scanning can also be in the background, while your android application is not visible.

Scanning those beacons enables you to use it offline and fully featured.

The SDK allows full offline functionality. You need to set up the SDK once correctly within your application to allow downloading all necessary data. Once done, it can be set up without an internet connection next time.

The download strategy is "Around Me" - With this approach every beacon will be downloaded in a certain radius around the current user location.

## Integrate the SDK into a project
In the GitHub you will find a demo application. That application shows how to set up the sdk-project.

Here is a quick guide that tells you what to do:

First add the following lines to the "build.gradle"-file of your root project
```bash
allprojects {
    repositories {
        jcenter()
        maven {
             url 'http://maven.match2blue.com/nexus/content/repositories/StroeerGroup/'
            credentials {
                username 'StroeerUser'
                password 'StroeerPW2016'
            }
        }
    }
}
```
Then add the following lines to the "build.gradle"-file of the app-module. Be aware that you might have to update the version numbers according to the aars you found in the demo-application.
```bash
dependencies {
    compile('de.stroeer:stroeerProxitySdk:1.8.0-Stroeer@aar')
        {
            transitive = true
        }
}
```

## Lifecycle
![Lifecycle](AndroidSDK_lifecycle.jpg)

## Usage
### Prerequisites
To use the whole functionality of the SDK, it is necessary to declare some permissions. This is already done by the sdks own Android-Manifest:
#### Needed hardware features
As the SDK needs to make extensive use of the bluetooth-feature, you need to set it as prerequisite for your app to function.
```bash
<uses-feature android:name="android.hardware.bluetooth_le" android:required="true" />
```
#### Predefined permissions by sdk
To start and stop scanning for bluetooth low energy devices:
```bash
<uses-permission android:name="android.permission.BLUETOOTH" />
```
To check if bluetooth is available and running:
```bash
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
```
To download beacon data from the backend:
```bash
<uses-permission android:name="android.permission.INTERNET" />
```
The SDK features automatic data-update after the internet connection got lost and than reconnects. To recognize those changes the following permissions are needed:
```bash
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```
To get an outdoor-position, the following permission is needed:
```bash
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
```
The sdk is also able to get an outdoor-position in the background. The fact, that the processor of the smartphone will "sleep" after a certain time, makes the following permission needed to wake it up.
```bash
<uses-permission android:name="android.permission.WAKE_LOCK" />
```
### Main Classes
As you can see in the demo-project which is delivered, the important classes are StroeerProxityApi and Gateway.IGatewayListener.

StroeerProxityApi gives access to all functionality and settings of the StroeerProxitySDK.
The purpose of Gateway.IGateWayListener is to inform you about every event and change in status which is done inside of the StroeerProxitySDK.
After you created an instance of StroeerProxityApi you have to register an instance of Gateway.IGateWayListener with the usage of the registerGatewayListener-Method.

Creating an instance of StroeerProxityApi inside of the Application class is recommended for now, because in other cases it can happen to get a Leaked-ServiceConnection-Exception because the Context(Activity) was closed before the connection was unbound.
```bash
public class ShowCaseApplication extends Application {

    private static StroeerProxityApi stroeerProxityApi;

    @Override
    public void onCreate() {
        super.onCreate();
        stroeerProxityApi = new StroeerProxityApi(this, "StroeerProxityTechdemo", "Im scanning
                     for beacons.", R.drawable.icon, MainActivity.class);
    }

    public static StroeerProxityApi getStroeerProxityApi() {
        return ShowCaseApplication.stroeerProxityApi;
    }
}
```
Now register the Gateway.IGateWayListener and call resendCurrentState (See explanation below the code)
```bash
public class MyActivity extends Activity implements Gateway.IGatewayListener {

    public static final String API_KEY = "type apikey here";

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
    public void onMessage(StroeerProxityApi stroeerProxityApi, Message message, boolean isNew) {

    }

    @Override
    public void onStatusGained(StroeerProxityApi stroeerProxityApi, SdkStatus status, boolean isNew) {
        if (status == SdkStatus.API_READY){
            //If you got this Status you are able to use all functions of the StroeerProxityApi now
            MyApplciation.getStroeerProxityApi().setApiKey(MyActivity.API_KEY);
            //for correct authentication use spherename and appkey
        }
    }

    @Override
    public void onStatusRevoked(StroeerProxityApi stroeerProxityApi, SdkStatus status, boolean isNew) {

    }
}
```
Because this StroeerProxitySDK runs in a Service you have to wait until this service is up and running. For this you have to wait for the SDKStatus.ApiReady, which gets send in the onStatusGained-Method of the Gateway.IGateWayListener. Because it could be, that the service is started before you have registered the Gateway.IGateWayListener to the api  you have to trigger the StroeerProxityApi to send all status which have been sent till now. (StroeerProxityApi.resendCurrentState()). Now you are able to use all functionalities.

### Scan process
#### Start Scanning

The last step is to start scanning for nearby beacons:
```bash
MyApplication.getStroeerProxityApi().startScan();
```

Now the SDK scans for beacons near you and its scanning property will be set to true. This way you can find out whether the SDK is currently scanning or not. Since the SDK is scanning for nearby beacons, you might get notifications from it fairly soon.

To reduce the huge server load, the sdk only works in supported regions. Its not necessary to scan outside of those because there are no beacons to scan. If the app user is outside of a supported region, the sdk will do nothing until the location is within germany.

#### Stop Scanning

When you're done with scanning, you simply call:
```bash
MyApplication.getStroeerProxityApi().stopScan();
```

#### Scan Period

It's possible to define a period of time which is used to collect raw beacon data before they will be analysed:
```bash
MyApplication.getStroeerProxityApi().setScanningPeriod(long timeInMillis);
```
The default value is five seconds. In this time the SDK will collect all scanned beacon information in your vicinity. After this time span you get informed if there were any results, e.g. a received action.

#### Wakelock

Beacause of the huge batterydrain that may caused by a wakelock, we don't do this for scanning inside of the sdk. If you want to make sure that the sdk gets a wakelock for scanning you have to do this by your own for now.

PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakelockTag");
wakeLock.acquire();

and to release this wakelock, call

wakeLock.release();

#### Update data

At each time one of the setup methods is called the SDK will update the local data with the latest data on the server. If you want to update the data manually you can use this method:
```bash
MyApplication.getStroeerProxityApi().updateData();
```

#### DebugMode

If you want to get some debug-information e.g. which beacons were downloaded from the server or which beacons got scanned, you have to process the messages received by Gateway.IGatewayListener.onMessage. For more information have a look at the JavaDoc -> ResponseCode. Also there is an option to create a logfile, how to use this function can be found in the JavaDoc.


## Further Information
For further information the whole api is documented as JavaDoc. You can find this JavaDoc inside of the zip-file in folder documentation.
