# Stroeer Android SPX-SDK Changelog

This file lists all notable changes to the Stroeer Android SPX-SDK.

## Table of versions
<!-- TOC depthFrom:2 depthTo:6 withLinks:1 updateOnSave:1 -->

* [2.2.4](#224)
* [2.2.1](#221)
* [2.2.0](#220)
* [2.1.2](#212)
* [2.0.7](#207)
* [2.0.3](#203)
* [2.0.0](#200)
* [1.10.0](#1100)

<!-- /TOC -->

# 2.2.4
released ...
### Changed
- Improved the message sent from the SDK when turning bluetooth off.
- In case a smartphone only has an accelerometer for motion detection: if the sensor malfunctions, the scan will not be paused anymore. Once the sensor works again, the scan runs in energy save mode again.
- In case that Google Play Services are inoperative/disabled on a device, the Android Advertising ID won't be added to the analytics.

### Fixed
- Fixed a crash regarding binding background services.
- Fixed a crash when trying to send analytics on a device on which Google Play Services are inoperative/disabled.
- Fixed crashes with devices using Android versions below 4.3.

- Fixed an issue which caused the energy save mode not to function properly.
- Fixed an issue which caused beacons to be not re-entered by turning off and on bluetooth on certain smartphones.
- Fixed an issue with expired API-key validations which may have triggered unnecessary API-key validations, hindering the SDK from starting the scan when internet is turned off.
- Fixed decryption keys sometimes not being downloaded due to missing internet connection when starting the scan.
- Fixed analytics having wrong beacon enter times after bluetooth was turned on.


# 2.2.1
released Oct 10, 2017
### Fixed
- Fixed an inconsistency which caused the energy save mode not to start.


# 2.2.0
released Sep 4, 2017
### Added
Now, the SDK is using gyroscope and accelerometer in addition to detect a significant movement of a device. Thus, the energy save mode can be applied on a wider range of smartphones.

### Fixed
- Fixed an issue which caused decryption keys to be lost when closing an app using the task manager.
- Fixes for API key validation.


# 2.1.2
released Jul 26, 2017
### Added
Now, the SDK provides an energy save mode using significant motion detector: if the smartphone supports this detector, it will pause the beacon scan when it detects that it did not significantly moved for around 10 minutes.

There is a new behavior if bluetooth was turned off: all currently scanned beacons are declared as left and analytics will be sent for each of them. The leave time is determined to be the same as the time when bluetooth was turned off.

### Changed
- If bluetooth low energy is not supported, will throw a warning (scanning beacons requires Android 4.3 and supported bluetooth low energy hardware).

### Fixed
- Closing apps which use the SDK using the task manager won't cause beacons to be lost anymore.
- Fixed an issue which caused the scan not to start without internet connection though the API key was already validated before.


# 2.0.7
released Apr 28, 2017
### Fixed
- Decryption keys won't get lost upon turning the device off anymore.
- Fixed an issue which caused apps using the SDK to continuously report errors when trying to fetch decryption keys due to invalid time settings on the device.


# 2.0.3
released Feb 28, 2017
### Changed
- Sending Google advertising IDs is now enabled by default. To disable sending this ID, use `addSystemAdvertisingId(false)`

### Fixed
- Fixed a rare issue which caused scans not to start.
- The sent Google advertising ID feature correctly considers whether or not the user has limit ad tracking enabled. When attempting to call `addSystemAdvertisingId(true)` when the Android system is limiting ad identifier usage, the SDK won't set this setting.


# 2.0.0
released Feb 1, 2017
### Added
- _CryptoV2:_

  The Android SPX-SDK is able to scan and decrypt beacons using the encryption v2. To recognize these beacons they use a fixed UUID c2340cb0-d412-11e6-bf26-cec0c932ce01. Beacons with encryption v2 change their major and minor values from day to day, so the SDK needs to fetch the required decryption keys from the server. Upon starting the scan, the SDK will download these decryption keys. If needed, the SDK automatically downloads more valid keys.

- _Analytics:_

  You can now specify a custom advertising identifier for the analytics your app will send. This allows you to distinguish your events from others. Use `setCustomAdvertisingId` to set your custom ad identifier.
  You can also enable / disable to send the advertising identifier from Android which will be added to each analytics event. These won't be sent by default and you need to call the method `addSystemAdvertisingId` to enable/disable this function.

  Other added methods:
    - `getCustomAdvertisingIdAsync` - gets the custom ad identifier.
    - `getSystemAdvertisingIdAsync` - get the current google ad identifier.
    - `isSystemAdvertisingIdSetAsync` - get whether the google ad identifier will be sent or not.


# 1.10.0
released Nov 15, 2016
### Added
- _Compatibility:_

  The Android SPX-SDK is running under Android 4.3 and later. The SDK can be integrated into apps running under Android 4.0 but then beacon scanning is not possible.

  To use the SDK, use the method `StroeerProxityApi.getInstance` and call the methods you need on this instance.

- _Scanning:_

  To scan for a beacon the app integrating the SPX-SDK needs location permissions and bluetooth. The SDK provides functions to start (`startScan`) and to stop (`stopScan`) beacon scanning. Upon starting the scan for the first time, the app requires an internet connection once to verify the API-key. The SDK detects only beacons with the following UUIDs:
    - 37ecc5b5-dc9f-4d5c-b1ee-7f09146572a2
    - 88084780-6528-11e6-bdf4-0800200c9a66

  If the beacon scanning is started the SDK scans for beacons no matter if the app integrating the SDK is in foreground or background. Furthermore, beacon scanning will still be performed when the app was closed. Beacon scanning also does not need a permanent internet connection.

  Furthermore, the SDK can scan for beacons which shuffle the major and minor values on a random basis (Crypto V1 procedure). The SDK is able to decrypt the shuffled values and provide the original major and minor values.

- _Analytics:_

  The SDK creates analytics events no matter if the app using the SDK is in foreground or background. An event contains among others the following information:
    - the point in time when a new beacon region was entered,
    - the point in time when a beacon region was left,
    - the stay time in a beacon region.

  All created analytics events are persisted on the device and afterwards are sent to an IoT-hub if the device has an internet connection. It is possible to turn on or off the creation of analytics events.

- _Logfile:_

  The Android SPX-SDK can write a logfile containing all actions the SDK has performed, e.g.
    - the point in time when beacon scanning was started,
    - the point in time when beacon scanning was stopped,
    - the point in time when a beacon region was entered,
    - the point in time when a beacon region was left,
    - the point in time when an analytics event was sent to the IoT-hub
    - all errors that occured.

  Writing such a logfile on the device can be enabled or disabled using `setLogFile`. Call it with `null` to disable it.


### Deprecated
- Deprecated old way to get access to the StroeerProxityApi - these methods are now deprecated:
    - `StroeerProxityApi(Context context, String title, String subtitle, int icon, Class<? extends Activity> mainClass)`
    - `StroeerProxityApi(Context context, String title, Class<? extends Activity> mainClass)`

### Fixed
- Fixed an issue which occured if location service permissions were turned off and on outside the app.
