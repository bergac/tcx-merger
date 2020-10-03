package nl.bergac.tcxmerger;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrackpointT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrainingCenterDatabaseT;

import java.io.File;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class TcxMerger {

    private final TrainingCenterDatabaseT routeData;
    private final TrainingCenterDatabaseT heartData;
    private final TcxXmlUtil tcxXmlUtil;

    public TcxMerger(File route, File heart) {
        requireNonNull(route);
        requireNonNull(heart);
        this.tcxXmlUtil = new TcxXmlUtil();
        this.routeData = tcxXmlUtil.unmarshall(route);
        this.heartData = tcxXmlUtil.unmarshall(heart);
    }

    public void merge() {
        var hrByTime = heartData.getActivities().getActivity().stream()
                .flatMap(activity -> activity.getLap().stream())
                .flatMap(lap -> lap.getTrack().stream())
                .flatMap(track -> track.getTrackpoint().stream())
                .collect(toMap(
                        TrackpointT::getTime,
                        Function.identity(),
                        ((trackpointT, trackpointT2) -> {
                            System.out.println("duplicate key: " + trackpointT.getTime());
                            return trackpointT;
                        })));

        routeData.getActivities().getActivity()
                .forEach(activity -> activity.getLap()
                        .forEach(lap -> lap.getTrack().forEach(track -> {
                                    var trackpoints = track.getTrackpoint();

                                    var newTrackpoints = trackpoints.stream().map(trackpointT -> {
                                        var hrTrackpoint = hrByTime.get(trackpointT.getTime());
                                        if (hrTrackpoint != null) {
                                            return combineTrackpoints(trackpointT, hrTrackpoint);
                                        }
                                        return trackpointT;
                                    }).collect(toList());

                                    trackpoints.clear();
                                    trackpoints.addAll(newTrackpoints);
                                })
                        )
                );


//        routeData.getActivities().getActivity().stream()
//                .map(ActivityT::getLap)
//                .flatMap(Collection::stream)
//                .map(ActivityLapT::getTrack)
//                .flatMap(Collection::stream)
//                .map(TrackT::getTrackpoint)
//                .flatMap(Collection::stream)
//                .map(TrackpointT::getHeartRateBpm)
//                .filter(Objects::nonNull)
//                .map(HeartRateInBeatsPerMinuteT::getValue)
//                .forEach(System.out::println);

        tcxXmlUtil.marshallFile(routeData);
    }

//    private void printTrackpoint(TrackpointT trackpointT) {
//        var hr = trackpointT.getHeartRateBpm() != null ? trackpointT.getHeartRateBpm().getValue() : "null";
//        System.out.println("\n\n");
//        System.out.println("Time: " + trackpointT.getTime());
//        System.out.println("HeartRate: " + hr);
//        System.out.println("Altitude: " + trackpointT.getAltitudeMeters());
//        System.out.println("Cadence: " + trackpointT.getCadence());
//        System.out.println("DistanceM: " + trackpointT.getDistanceMeters());
//        System.out.println("Position: " + trackpointT.getPosition());
//    }

    private TrackpointT combineTrackpoints(TrackpointT mainTrackpoint, TrackpointT extraTrackpoint) {
        var newTrackpoint = new TrackpointT();
        newTrackpoint.setHeartRateBpm(mainTrackpoint.getHeartRateBpm() != null ? mainTrackpoint.getHeartRateBpm() : extraTrackpoint.getHeartRateBpm());
        newTrackpoint.setAltitudeMeters(mainTrackpoint.getAltitudeMeters() != null ? mainTrackpoint.getAltitudeMeters() : extraTrackpoint.getAltitudeMeters());
        newTrackpoint.setCadence(mainTrackpoint.getCadence() != null ? mainTrackpoint.getCadence() : extraTrackpoint.getCadence());
        newTrackpoint.setDistanceMeters(mainTrackpoint.getDistanceMeters() != null ? mainTrackpoint.getDistanceMeters() : extraTrackpoint.getDistanceMeters());
        // TODO combine extensions instead of setting it
        // for now, use the extension of the main trackpoint
        newTrackpoint.setExtensions(mainTrackpoint.getExtensions() != null ? mainTrackpoint.getExtensions() : extraTrackpoint.getExtensions());
        newTrackpoint.setPosition(mainTrackpoint.getPosition() != null ? mainTrackpoint.getPosition() : extraTrackpoint.getPosition());
        newTrackpoint.setSensorState(mainTrackpoint.getSensorState() != null ? mainTrackpoint.getSensorState() : extraTrackpoint.getSensorState());
        newTrackpoint.setTime(mainTrackpoint.getTime());
        return newTrackpoint;
    }
}
