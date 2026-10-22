package org.firstinspires.ftc.teamcode.drive;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.pedro.Constants;

/**
 * PinPointIMU is a wrapper for the GoBildaPinpointDriver that implements the DriveIMU interface.
 * It provides methods to get the yaw of the robot and reset the yaw.
 * Steals parameters from pedroPathing's Constants.localizerConfig to set up the pinpoint driver.
 */
public class PinPointIMU implements DriveIMU{
    GoBildaPinpointDriver pp;
    double angleOffest = 0;

    public PinPointIMU(HardwareMap hwmap){
        pp = hwmap.get(GoBildaPinpointDriver.class, "pinpoint");
        pp.setOffsets(
                Constants.localizerConfig.xPodOffset.get(),
                Constants.localizerConfig.yPodOffset.get(),
                DistanceUnit.INCH
        );
        pp.setEncoderDirections(
                Constants.localizerConfig.xPodDirection.get(),
                Constants.localizerConfig.yPodDirection.get()
        );
        pp.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        pp.resetPosAndIMU();

    }

    /**
     * Reset the yaw of the robot to 0
     * does not use pinpoint hardware reset to avoid calibration error while driving
     */
    @Override
    public void resetYaw() {
        angleOffest = pp.getHeading(AngleUnit.RADIANS);
    }

    /**
     * Get the corrected yaw of the robot in radians, wrapped to (0, 2pi)
     * @return yaw in radians
     */
    @Override
    public double getYaw() {
        // resetting the imu through pinpoint can cause calibration error while driving
        // use the offset and wrap into (0, 2pi) to avoid this

        double adjustedAngle = pp.getHeading(AngleUnit.RADIANS) - angleOffest;
        if (adjustedAngle < 0) {
            adjustedAngle += 2 * Math.PI;
        } else if (adjustedAngle >= 2 * Math.PI) {
            adjustedAngle -= 2 * Math.PI;
        }
        return adjustedAngle;
    }
    public YawPitchRollAngles getYawPitchRoll(AngleUnit unit){
        return new YawPitchRollAngles(
                unit,
                pp.getHeading(unit),
                pp.getPitch(unit),
                pp.getRoll(unit),
                0
        );
    }
}
