package org.firstinspires.ftc.teamcode.tracking;

public class DetectedObject {
    double x;
    double y;
    double radius;

    public static DetectedObject[] parseCenters(double[] centers) {
        if(centers.length % 3 != 0) {
            throw new IllegalArgumentException("Centers array length must be a multiple of 3");
        }
        DetectedObject[] objects = new DetectedObject[centers.length / 3];
        for (int i = 0; i < centers.length; i += 3) {
            DetectedObject obj = new DetectedObject();
            obj.x = centers[i];
            obj.y = centers[i + 1];
            obj.radius = centers[i + 2];
            objects[i / 3] = obj;
        }
        return objects;
    }
}
