package org.firstinspires.ftc.teamcode.utility;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Const;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.pedro.Constants;

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

    @Override
    public void resetYaw() {
        angleOffest = pp.getHeading(AngleUnit.RADIANS);
    }

    @Override
    public double getYaw() {
        return (pp.getHeading(AngleUnit.RADIANS) - angleOffest) % (Math.PI * 2);
    }
}
