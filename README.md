farbwerk - 4x RGB LED controller to put color in your life
========

**What is farbwerk?**

The farbwerk is an innovative four channel RGB controller for LED strips. The name derives from the German words "Farbe" (color) and "Werk" (factory/plant/work). Twelve channels in total can be used for up to four RGB strips or as individual channels. Each channel has a resolution of 13 bits (8192 individual steps), resulting in smooth color transitions even at low brightness settings. 

![farbwerk device](/screens/farbwerk_bluetooth1.jpg "farbwerk")

The farbwerk is an autonomous device with an embedded micro processor and can be operated without a PC. Four sensor inputs can be used with compatible temperature sensors (not included in delivery) to visualize temperatures as colors. The controller offers plenty of memory and performance for new ideas. Four analog sensor ports allow a direct control by temperature (-40 °C to +100 °C), voltage (0 to 3.3V) or resistance (100 Ohm to 25kOhm).

![farbwerk device](/screens/farbwerk_bluetooth2.jpg "farbwerk hardware")

The device is also available in a Bluetooth enabled variant so it can be controlled with a smartphone or tablet for example. The demo app (this project) is written for Android and requires version 4.4.2 as minimum.

* [Further information about farbwerk](http://forum.aquacomputer.de/weitere-foren/english-forum/105464-farbwerk-put-color-in-your-life-new-4x-rgb-led-controller/)
* [Get farbwerk from our webshop](http://shop.aquacomputer.de/product_info.php?language=en&products_id=3232)

**How to work with the source code?**

1. Download and install the [Java Development Kit (JDK) 7](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html)

2. Download and and install [Android Studio](https://developer.android.com/sdk/index.html)

3. Use the installed Android SDK manager (SDK Manager.exe) and download the following modues with it:
  - Android SDK Tools
  - Android SDK Platform Tools
  - Android 4.4.2 (API 19)
  - Extras->Android Support Repository
  - Extras->Android Support Library

4. Import this project

5. After the import adjust the SDKs path of local.properties (i.e. to sdk.dir=C:\\android\\sdk)

**Screenshots of the demo app**

![farbwerk color change](/screens/screen2.png "farbwerk color change")

![farbwerk device selection](/screens/screen1.png "farbwerk device selection")

![farbwerk channel selection](/screens/screen3.png "farbwerk channel selection")