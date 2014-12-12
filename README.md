farbwerk
========
## 4x RGB LED controller, put color in your life! 

![farbwerk device](/screens/farbwerk_bluetooth1.jpg "farbwerk")
![farbwerk device](/screens/farbwerk_bluetooth2.jpg "farbwerk hardware")

The farbwerk offers 13-bit color resolution of 8192 steps per primary color. 
Because of the high resolution even color transitions with low brightness settings are almost continuously.
We added an USB port to the farbwerk so it can be controlled by the aquasuite software. 
You can connect any number of farbwerk controllers via USB and have them all managed in the aquasuite.
The USB interface also allows firmware updates so that new features can be added. 
The controller offers plenty of memory and performance for new ideas.
Four analog sensor ports allow a direct control by temperature (-40 °C to +100 °C), voltage (0 to 3.3V) or resistance (100 Ohm to 25kOhm).

* Device
http://shop.aquacomputer.de/product_info.php?products_id=3233
http://forum.aquacomputer.de/wasserk-hlung/105463-farbwerk-jetzt-wird-es-bunt-neuer-4x-rgb-led-controller/

* Software to control the device via bluetooth
* Minimum supported android version is: 4.4.2

Build farbwerk from source:

1. install  Java Development Kit (JDK) 7
http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html

2. load and install android studio
https://developer.android.com/sdk/index.html

3. download and install with the android sdk manager (SDK Manager.exe)
Android SDK Tools
Android SDK Platform Tools
Android 4.4.2 (API 19)
Extras->Android Support Repository
Extras->Android Support Library

4. import this project

5. Adjust the path in of sdk in local.properties after import i.e. to (sdk.dir=C\:\\android\\sdk)

App Screenshots:
![farbwerk device selection](/tree/master/screens/screen1.png?raw=true "farbwerk device selection")
![farbwerk color change](/tree/master/screens/screen2.png?raw=true "farbwerk color change")
![farbwerk channel selection](/tree/master/screens/screen3.png?raw=true "farbwerk channel selection")