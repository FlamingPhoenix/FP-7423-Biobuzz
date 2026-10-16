package org.firstinspires.ftc.teamcode.tracking;

public class TrackedObject {
    public double x;
    public double y;
    public double radius;

    private double vx, vy;
    private long lastUpdateTime;
    public short missedFrames;
    public int hits;
    public boolean confirmed;
    public double predictedX, predictedY;


    public TrackedObject(int object_id, DetectedObject object, long timestamp) {
        this.x = object.x;
        this.y = object.y;
        this.radius = object.radius;
        this.vx = 0;
        this.vy = 0;
        this.lastUpdateTime = timestamp;
        this.missedFrames = 0;
        this.hits = 1;
        this.confirmed = false;
        this.predictedX = x;
        this.predictedY = y;
    }
    public void predict(long timestamp) {
        double dt = Math.max((timestamp - lastUpdateTime) / 1000.0, 0);
        predictedX = x + vx * dt;
        predictedY = y + vy * dt;
    }
    public void update(DetectedObject object, long timestamp, double velocity_alpha){
        double dt = Math.max((timestamp - lastUpdateTime) / 1000.0, 0);
        if(dt > 0.0) {
            double measuredVx = (object.x - x) / dt;
            double measuredVy = (object.y - y) / dt;

            vx = velocity_alpha * measuredVx + (1 - velocity_alpha) * vx;
            vy = velocity_alpha * measuredVy + (1 - velocity_alpha) * vy;
        }
        this.x = object.x;
        this.y = object.y;
        this.radius = 0.7 * this.radius + 0.3 * object.radius; // Smooth the radius
        this.lastUpdateTime = timestamp;
        this.missedFrames = 0;
        this.hits++;
    }

}
