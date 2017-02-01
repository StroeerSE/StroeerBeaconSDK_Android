# Ströer Proxity SDK

## Purpose of the SDK
With the SDK you are scanning for so called beacons. These are special beacons of the Favendo GmbH. This scanning can also be in the background, while your android application is not visible.

Scanning those beacons enables you to use it offline and fully featured.

The SDK allows full offline functionality. You need to set up the SDK once correctly within your application to allow downloading all necessary data. Once done, it can be set up without an internet connection next time.

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
    compile('de.stroeer:stroeerProxitySdk:x.y.z-Stroeer@aar')
        {
            transitive = true
        }
}
```

## Usage
### Android 6+ changes

Since Android 6, it is neccessary to have Location Permission set for the app. In order to do this, the sdk user has to integrate an dialog which asks for this permission. See https://developer.android.com/training/permissions/requesting.html

Also the Location service has to be enabled in the android settings! Otherwise it is not possible to scan for beacons.

### Prerequisites
To use the whole functionality of the SDK, it is necessary to declare some permissions. This is already done by the sdks own Android-Manifest:
#### Predefined hardware feature by sdk
As the SDK needs to make extensive use of the bluetooth-feature, it is necessary to set it as prerequisite for your app to function.
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
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```
Since Android 6.0 it is necessary to have Location Permission in order to scan for beacons:
```bash
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
```
The sdk is also able to get an outdoor-position in the background. The fact, that the processor of the smartphone will "sleep" after a certain time, makes the following permission needed to wake it up.
```bash
<uses-permission android:name="android.permission.WAKE_LOCK" />
```
### Main Classes
As you can see in the demo-project which is delivered, the important classes are StroeerProxityApi and Gateway.IGatewayListener.

StroeerProxityApi gives access to all functionality and settings of the StroeerProxitySDK.
The purpose of Gateway.IGateWayListener is to inform you about every event and change in status which is done inside of the StroeerProxitySDK.
After you have get an instance of StroeerProxityApi you have to register an instance of Gateway.IGateWayListener with the usage of the registerGatewayListener-Method.
Call resendCurrentState after this (See explanation below the code)
```bash
public class MyActivity extends Activity implements Gateway.IGatewayListener {

    public static final String API_KEY = "type apikey here";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StroeerProxityApi.getInstance(this).setApiKey(API_KEY);
        StroeerProxityApi.getInstance(this).startScan();
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
    public void onMessage(StroeerProxityApi stroeerProxityApi, Message message, boolean isNew) {

    }

    @Override
    public void onStatusGained(StroeerProxityApi stroeerProxityApi, SdkStatus status, boolean isNew) {

    }

    @Override
    public void onStatusRevoked(StroeerProxityApi stroeerProxityApi, SdkStatus status, boolean isNew) {

    }
}
```
Because the Backgroundservice might be running without an activity you have to call resendCurrentState() to restore the current state of the service and to update your GUI if necessary.

### Scan process
#### Start Scanning

The last step is to start scanning for nearby beacons:
```bash
StroeerProxityApi.getInstance(this).startScan();
```

Now the SDK scans for beacons near you and its scanning property will be set to true. This way you can find out whether the SDK is currently scanning or not. Since the SDK is scanning for nearby beacons, you might get notifications from it fairly soon.

#### Stop Scanning

When you're done with scanning, you simply call:
```bash
StroeerProxityApi.getInstance(this).stopScan();
```

#### Leave Duration

It's possible to define the leave duration, which determines how long a beacon should not be scanned before it counts as left:
```bash
StroeerProxityApi.getInstance(this).setLeaveDuration(long timeInMillis);
```
The default value is 30 seconds.

### Advertising Identifier

The Ströer Proxity SDK provides two ways to set an advertising identifier to identify a user across different apps in order to show targeted advertisements.
The first on is to define a custom advertising id which can be every string.
The second way is to let the sdk use the google advertising id.

Please consider google's advertising policy:

https://play.google.com/about/monetization-ads/ads/disruptive/

#### custom advertising id
```bash
StroeerProxityApi.getInstance(this).setCustomAdvertisingId("custom advertising Id")
```
Use this setter to specify your own advertising identifier.

##### system advertising id (google advertising id)
```bash
StroeerProxityApi.getInstance(this).addSystemAdvertisingId(true)
```
Use this method to specify that the sdk should append the google advertising id to each analytics-event

#### DebugMode

If you want to get some debug-information e.g. which beacons got scanned, you have to process the messages received by Gateway.IGatewayListener.onMessage. For more information have a look at the JavaDoc -> ResponseCode. Also there is an option to create a logfile, how to use this function can be found in the JavaDoc.

## Further Information
For further information the whole api is documented as JavaDoc. You can find this JavaDoc inside of the zip-file in folder documentation.
