# Ströer Proxity SDK

<!-- TOC depthFrom:2 depthTo:6 withLinks:1 updateOnSave:1 orderedList:1 -->

1. [Purpose of the SDK](#purpose-of-the-sdk)
2. [Build demo-project](#build-demo-project)
3. [Integrate the SDK into a project](#integrate-the-sdk-into-a-project)
4. [Usage](#usage)
	1. [Android 6+ changes](#android-6-changes)
	2. [Prerequisites](#prerequisites)
		1. [Predefined hardware feature by SDK](#predefined-hardware-feature-by-sdk)
		2. [Predefined permissions by SDK](#predefined-permissions-by-sdk)
		3. [Optional permissions defined by app](#optional-permissions-defined-by-app)
	3. [Step 1: Setup Main Classes](#step-1-setup-main-classes)
	4. [Step 2: Scan Process and Advanced Setup](#step-2-scan-process-and-advanced-setup)
		1. [Advanced Setup](#advanced-setup)
		2. [Stop Scanning](#stop-scanning)
	5. [Advertising Identifier](#advertising-identifier)
	   	1. [Google Advertising ID](#google-advertising-id)
		2. [Custom Advertising ID](#custom-advertising-id)
	6. [DebugMode](#debugmode)
5. [Further Information](#further-information)

<!-- /TOC -->

## Purpose of the SDK
With this SDK your mobile app will be capable of scanning so called "beacons". A beacon is a small piece of hardware that enables the detection of user-location information by using Bluetooth technology.

The SDK is capable of detecting beacons  whether the host-application is in background or foreground. Scanning is also active while your app has no online-connection. All beacon analytics-events are gathered while offline and will be sent to our back-end as soon as the online connectivity is recovered.

After setting up the SDK correctly within you application for the first time all necessary data will be downloaded.

## Build demo-project

To build the demo-project please call ./gradlew.sh on unix systems or gradlew.bat on windows systems.

## Integrate the SDK into a project
Bundled within this repository you will find a demo application. That application shows how to set up the SDK-project.

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
    compile('de.stroeer:stroeerProxitySDK:x.y.z-Stroeer@aar')
        {
            transitive = true
        }
}
```

current version is 2.0.7

## Usage
### Minimum Android Api Version

SDK: The minimum Android Api Version is 14. But beacons will just be scanned with Android Api Version 18. It's because version 18 is the first version which supports bluetooth low energy.

### Android 6+ changes

Since Android 6, it is necessary to have location-permission set for the app. In order to do this, the SDK user has to integrate a dialog which asks for this permission. See https://developer.android.com/training/permissions/requesting.html

Also the location-service has to be enabled in the android settings! Otherwise it is not possible to scan for beacons.

### Prerequisites
> Due to the use of Bluetooth low energy the minimum Android version is 4.3 Jelly Bean (API 18).

To use the whole functionality of the SDK, it is necessary to declare certain permissions. This is already done by the SDKs own Android-Manifest:
#### Predefined hardware feature by SDK
The SDK makes extensive use of the Bluetooth-feature. Therefore it is necessary to set the following feature as prerequisite for your app to function.
```xml
<uses-feature android:name="android.hardware.bluetooth_le" android:required="true" />
```
#### Predefined permissions by SDK
To start and stop scanning for Bluetooth low energy devices:
```xml
<uses-permission android:name="android.permission.BLUETOOTH" />
```
To check if Bluetooth is available and running:
```xml
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
```
To download beacon data from the backend:
```xml
<uses-permission android:name="android.permission.INTERNET" />
```
The SDK features automatic data-update after the internet connection got lost and than reconnects. To recognize those changes the following permissions are needed:
```xml
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```
Since Android 6.0 it is necessary to have location-permission in order to scan for beacons:
```xml
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
```
The SDK is also able to get an outdoor-position in the background. The fact, that the processor of the smartphone will "sleep" after a certain time, makes the following permission needed to wake it up.
```xml
<uses-permission android:name="android.permission.WAKE_LOCK" />
```
#### Optional permissions defined by app
If you like to log all SDK-events into a log file, you must specify this dependency and call the `setLogFile` method on the `StroeerProxityApi`.
```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

> This permission is also used in the demo-project.

### Step 1: Setup Main Classes
As you can see in the demo-project, the important classes are `StroeerProxityApi` and `Gateway.IGatewayListener`.

`StroeerProxityApi` gives access to all functionality and settings of the StroeerProxitySDK.
The purpose of `Gateway.IGateWayListener` is to inform you about every event and change in status which is done inside of the StroeerProxitySDK.
After you received an instance of `StroeerProxityApi` you must register an instance of `Gateway.IGateWayListener` with the usage of the `registerGatewayListener` method.
Call `resendCurrentState` after this (See explanation below the code)
```java
public class MyActivity extends Activity implements Gateway.IGatewayListener {

    public static final String API_KEY = "type apikey here";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // set the API key
        StroeerProxityApi.getInstance(this).setApiKey(API_KEY);
        
        // do advanced stuff (explainded in Step 2)
        ...
        
        // start the scanning process 
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

    /**
     * This method is called whenever the SDK informs you about new messages
     *
     * @param message:              the actual message with description, code and data which is new
     * @param deliverdForFirstTime: determines if this message was sent for the first time (true) or it is a copy (false). This is necessary for resetting the app if it's gone to background and was brought to the front again.
     */
    void onMessage(Message message, boolean deliverdForFirstTime);

    /**
     * is called whenever a new status was gained. Look inside of Dependencies class to inform you about the possible statuses
     *
     * @param status:               the actual status which was gained
     * @param deliverdForFirstTime: determines if this message was sent for the first time (true) or it is a copy (false). This is necessary for resetting the app if it's gone to background and was brought to the front again.
     */
    void onStatusGained(Dependencies status, boolean deliverdForFirstTime);

    /**
     * is called whenever a status was revoked. Look inside of Dependencies class to inform you about the possible statuses
     *
     * @param status:               the actual status which was revoked
     * @param deliverdForFirstTime: determines if this message was sent for the first time (true) or it is a copy (false). This is necessary for resetting the app if it's gone to background and was brought to the front again.
     */
    void onStatusRevoked(Dependencies status, boolean deliverdForFirstTime);
}
```
> Because the background-service might be running without an activity you have to call **`resendCurrentState()`** to restore the current state of the service and to update your GUI if necessary.

### Step 2: Scan Process and Advanced Setup
#### Advanced Setup

The before you start the scanning you might set the Advertising ID handling.
```java
// The SDK will add the Google Advertising ID
StroeerProxityApi.getInstance(this).addSystemAdvertisingId(false)

// Defined a custom advertising ID
StroeerProxityApi.getInstance(this).setCustomAdvertisingId("Your Custom Advertising ID");

// Now start scanning
StroeerProxityApi.getInstance(this).startScan();
```

Now the SDK is scanning for beacons in a radius of approximately 50 meters and its scanning property will be set to true. This way you can find out whether the SDK is currently scanning or not. Since the SDK is scanning for nearby beacons, you might get notifications from it immediately.

#### Stop Scanning
When you're done with scanning, you simply call:
```java
StroeerProxityApi.getInstance(this).stopScan();
```

### Advertising Identifier
The Ströer Proxity SDK provides two ways to set an advertising identifier in order to identify a user across different apps and show targeted advertisements.

1. Use the Google Advertising ID.
2. Define a custom advertising ID which is a custom string.

> **NOTE:** By default the SDK tries to fetch the system advertising ID. According to the Google advertising policy the SDK will not read the advertising ID if the user has enabled `Limit Ad Tracking` on his device.
**Please consider Google's advertising policy:** https://play.google.com/about/monetization-ads/ads/disruptive/

#### Google Advertising ID
Use this method to specify that the SDK should append the Google advertising ID to each analytics-event. If you do not want the SDK to read the advertising ID, you have to set it to `false`.

Setter posts a message with via IGatewayListener in case of a failure with the response code `FAILURE_ADD_SYSTEM_ADVERISING_ID`. This will happen if you want to enable the feature and limit-ad-tracking is enabled on the user's device.

```java
StroeerProxityApi.getInstance(this).addSystemAdvertisingId(true)
```


#### Custom Advertising ID
Use this setter to specify your own advertising identifier.
```java
StroeerProxityApi.getInstance(this).setCustomAdvertisingId("custom advertising Id")
```

### DebugMode
If you want to get some debug-information e.g. which beacons got scanned, you have to process the messages received by `Gateway.IGatewayListener.onMessage`. For more information have a look at the JavaDoc -> `ResponseCode`. Also, there is an option to create a logfile. An example of how to use this function can be found in the JavaDoc.

## Further Information
For further information the whole API is documented as JavaDoc. You can find this JavaDoc inside of the zip-file in folder documentation.
