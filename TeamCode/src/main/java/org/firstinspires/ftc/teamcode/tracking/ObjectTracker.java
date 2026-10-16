package org.firstinspires.ftc.teamcode.tracking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ObjectTracker {
    private final double maxDistance;
    private final double maxMissedFrames;
    private final double confirmationThreshold;
    private final double velocityAlpha;
    private final double radiusWeight;
    private double maxSpeed;
    private long lastTimestamp = 0;
    private int nextId = 0;
    private List<TrackedObject> tracks = new ArrayList<>();
    public ObjectTracker(){
        this.maxDistance = 100.0;
        this.maxMissedFrames = 5.0;
        this.confirmationThreshold = 2;
        this.velocityAlpha = 0.4;
        this.radiusWeight = 0.25;
        this.maxSpeed = 1000.0;
    }
    public ObjectTracker(double maxDistance, double maxMissedFrames, double confirmationThreshold, double velocityAlpha, double radiusWeight, double maxSpeed) {
        this.maxDistance = maxDistance;
        this.maxMissedFrames = maxMissedFrames;
        this.confirmationThreshold = confirmationThreshold;
        this.velocityAlpha = velocityAlpha;
        this.radiusWeight = radiusWeight;
        this.maxSpeed = maxSpeed;
    }
    public List<TrackedObject> update(
            DetectedObject[] detections,
            long timestamp
    ) {
        if (timestamp < lastTimestamp) {
            throw new IllegalArgumentException("timestamp must be monotonic");
        }
        lastTimestamp = timestamp;

        final int trackCount = tracks.size();
        final int detectionCount = detections.length;
        final double maxDistanceSquared = maxDistance * maxDistance;

        for (TrackedObject track : tracks) {
            track.predict(timestamp);
        }

        // Candidate matches stored in parallel primitive arrays.
        int capacity = trackCount * detectionCount;
        double[] costs = new double[capacity];
        int[] candidateTracks = new int[capacity];
        int[] candidateDetections = new int[capacity];
        int candidateCount = 0;

        for (int ti = 0; ti < trackCount; ti++) {
            TrackedObject track = tracks.get(ti);
            double px = track.predictedX;
            double py = track.predictedY;
            double radius = track.radius;
            double radiusScale = Math.max(radius, 1.0);

            for (int di = 0; di < detectionCount; di++) {
                DetectedObject detection = detections[di];

                double dx = detection.x - px;
                double dy = detection.y - py;
                double distanceSquared = dx * dx + dy * dy;

                if (distanceSquared > maxDistanceSquared) {
                    continue;
                }

                double radiusDifference =
                        (detection.radius - radius) / radiusScale;

                double cost = distanceSquared
                        + radiusWeight * maxDistanceSquared
                        * radiusDifference * radiusDifference;

                costs[candidateCount] = cost;
                candidateTracks[candidateCount] = ti;
                candidateDetections[candidateCount] = di;
                candidateCount++;
            }
        }

        // Sort candidates by cost using an in-place insertion sort.
        // Stable for equal costs, matching Python's stable sort.
        for (int i = 1; i < candidateCount; i++) {
            double cost = costs[i];
            int ti = candidateTracks[i];
            int di = candidateDetections[i];
            int j = i - 1;

            while (j >= 0 && costs[j] > cost) {
                costs[j + 1] = costs[j];
                candidateTracks[j + 1] = candidateTracks[j];
                candidateDetections[j + 1] = candidateDetections[j];
                j--;
            }

            costs[j + 1] = cost;
            candidateTracks[j + 1] = ti;
            candidateDetections[j + 1] = di;
        }

        boolean[] matchedTracks = new boolean[trackCount];
        boolean[] matchedDetections = new boolean[detectionCount];

        for (int i = 0; i < candidateCount; i++) {
            int ti = candidateTracks[i];
            int di = candidateDetections[i];

            if (matchedTracks[ti] || matchedDetections[di]) {
                continue;
            }

            matchedTracks[ti] = true;
            matchedDetections[di] = true;

            tracks.get(ti).update(
                    detections[di], timestamp, velocityAlpha
            );
        }

        for (int ti = 0; ti < trackCount; ti++) {
            if (!matchedTracks[ti]) {
                tracks.get(ti).missedFrames++;
            }
        }

        for (int ti = trackCount - 1; ti >= 0; ti--) {
            if (tracks.get(ti).missedFrames > maxMissedFrames) {
                tracks.remove(ti);
            }
        }

        for (int di = 0; di < detectionCount; di++) {
            if (matchedDetections[di]) {
                continue;
            }

            TrackedObject track = new TrackedObject(
                    nextId++, detections[di], timestamp
            );

            if (confirmationThreshold <= 1) {
                track.confirmed = true;
            }

            tracks.add(track);
        }

        for (TrackedObject track : tracks) {
            if (track.hits >= confirmationThreshold) {
                track.confirmed = true;
            }
        }

        return new ArrayList<>(tracks);
    }

    public List<TrackedObject> getConfirmedTracks() {
        List<TrackedObject> confirmedTracks = new ArrayList<>();
        for (TrackedObject track : tracks) {
            if (track.confirmed) {
                confirmedTracks.add(track);
            }
        }
        return confirmedTracks;
    }
}
