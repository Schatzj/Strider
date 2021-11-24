# Strider
Pedometer app. 

Overview of the application:
Android app. 
The user inputs how how many paces (two steps) are required to travel a distance specified by the user. 
For example it may take 80 paces to cover 100 meters. 

The user then inputs a target distance. 

The application will then notify the user everytime they travel the specified distance (100 meters), and again when the user has reached their target distance. 

WARNINGS!
While this app will likely be as accurate as most other phone pedometers, phones are not good at counting steps. As such the step count WILL be inaccurate. 

Due to Android queueing sensor notifications, there may be some delay between the step and the application regestrering the event. 

In order to try to be as accurate as possible, the application may consume more battery power than expected. 

Finally in order to shut off the app and preserve battery power, make sure you navigate back to the "configuration" screen before you "close" or leave the app. If you simply press home while on the "Activity" page, the app will continue running in the background and consume system resources, and wear down the battery. 
