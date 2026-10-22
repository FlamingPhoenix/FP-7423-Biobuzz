package org.firstinspires.ftc.teamcode.drive;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

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

    YawPitchRollAngles getYawPitchRoll(AngleUnit angleUnit);

}
