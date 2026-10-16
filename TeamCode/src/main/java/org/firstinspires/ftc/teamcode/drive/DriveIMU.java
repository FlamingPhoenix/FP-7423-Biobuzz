package org.firstinspires.ftc.teamcode.drive;
/**
 * interface class that exposes IMU for field centric drive
 */

public interface DriveIMU {
    /**
     * Resets the current heading to zero
     */
    void resetYaw();

    /**
     * @return the current heading in radians
     */
    double getYaw();

}
