# Stroeer Android SPX-SDK Changelog

This file lists all notable changes to the Stroeer Android SPX-SDK.

## Table of versions
<!-- TOC depthFrom:2 depthTo:6 withLinks:1 updateOnSave:1 -->

* [2.2.1](#221)
* [2.2.0](#220)
* [2.1.2](#212)
* [2.0.7](#207)
* [2.0.3](#203)
* [2.0.0](#200)
* [1.10.0](#1100)

<!-- /TOC -->


## 2.2.1
released Oct 10, 2017
### Fixed
- Fixed an inconsistency which caused the energy save mode not to start.


## 2.2.0
released Sep 4, 2017
### Added
- Support for linear acceleration sensor and accelerometer to enable the use of energy save mode on a wider span of smartphones.

### Fixed
- Fixed an issue in offline mode which caused decryption keys to be lost when closing an app using the task manager.
- Fixes for API key validation.


## 2.1.2
released Jul 26, 2017
### Added
- Energy save mode using significant motion detector: if the smartphone supports this detector, it will pause the beacon scan when it detects that it did not significantly move for around 10 minutes. 
- New behaviour upon turning bluetooth off: all currently scanned beacons are declared as left and analytics will be sent for each of them. The leave time is determined to be the same as the time when bluetooth was turned off.

### Changed
- If bluetooth low energy is not supported, will throw a warning (scanning beacons requires Android 4.3 and supported bluetooth low energy hardware).

### Fixed
- Closing apps using the task manager won't cause beacons to be lost anymore.
- Fixed an issue which caused the scan not to start in offline mode though the API key was already validated before.


## 2.0.7
released Apr 28, 2017
### Fixed
- Decryption keys won't get lost upon turning the device off anymore.
- Fixed an issue which caused apps using the SDK to continuously report errors when trying to fetch decryption keys due to invalid time settings on the device.


## 2.0.3
released Feb 28, 2017
### Changed
- Sending Google advertising IDs is now enabled by default. To disable sending this ID, use "StroeerProxityApi.getInstance(this).addSystemAdvertisingId(false);"

### Fixed
- Fixed a rare issue which caused scans not to start.
- The send Google advertising ID feature correctly considers whether or not the user has limit ad tracking enabled.


## 2.0.0
released Feb 1, 2017
### Added
- Implemented the CryptoV2 algorithm and ability to download OTSKs.
- Added ability to send google advertising IDs and custom advertising IDs.
- Added methods:
    - setCustomAdvertisingId(String advertisingId) - use this to add a custom advertising identifier.
    - getCustomAdvertisingIdAsync(final AsynCallback<String> callback) - gets the custom ad identifier.
    - getSystemAdvertisingIdAsync(final AsynCallback<String> callback) - get the current google ad identifier.
    - isSystemAdvertisingIdSetAsync(final AsynCallback<Boolean> callback) - get whether the google ad identifier will be sent or not.
    - addSystemAdvertisingId(boolean addSystemAdvertisingId) - get whether the google ad identifier will be sent or not.


## 1.10.0
released Nov 15, 2016
### Added
- Implemented the CryptoV1 algorithm.
- New way to get a StroeerProxityApi instance, use this to access the SDK's methods:
    - StroeerProxityApi.getInstance(Context context)
- Start / stop scan
  - requires location permission
  - hardcoded UUIDs:
    - 37ecc5b5-dc9f-4d5c-b1ee-7f09146572a2 (not encrypted beacons)
    - 88084780-6528-11e6-bdf4-0800200c9a66 (CryptoV1)
  - works in foreground/background, online/offline
- Analytics
  - sent to Azure if online
  - gather events
    - while in foreground/background, online/offline
    - will be persisted until sent
  - beacon leave events contain (among others) enter, leave and stay time
  - turn on/off gathering events
- API-Key
  - configurable by developer
  - will be checked before starting a scan
- State Restoration
  - SDK will keep to scan and send events when the app containing it is closed
- Logfile
  - enable/disable writing messages to a logfile
  - delete created logfile

### Deprecated
- Deprecated old way to get access to the StroeerProxityApi - these methods are now deprecated:
    - StroeerProxityApi(Context context, String title, String subtitle, int icon, Class<? extends Activity> mainClass)
    - StroeerProxityApi(Context context, String title, Class<? extends Activity> mainClass)

### Fixed
- Fixed an issue which occured if location services were turned off and on outside the app.