# TCX Merger
Merge 2 .tcx files to combine the metrics and measurement. 
The idea came up when I was cycling indoor and did not have an ANT+ stick. I had a Garmin HR band which connects only via ANT+.
I tracked both (indoor cycling via Bluetooth, and HR via Garmin watch), which needed to be merged to get a good overview and analysis of the activity.

## Prerequisites
* Java 14

## Build
`./gradlew clean build`  
Which will create a fat jar.

## Run
`java -jar build/libs/tcx-merger-1.0-SNAPSHOT.jar <file with extra data> <main file>`
