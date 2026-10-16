package org.firstinspires.ftc.teamcode.utility;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

public class RevIMU implements DriveIMU {
    IMU imu;
    public RevIMU(HardwareMap hwMap){
        imu = hwMap.get(IMU.class,"imu");
        IMU.Parameters p = new IMU.Parameters(
                new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.BACKWARD,
                        RevHubOrientationOnRobot.UsbFacingDirection.UP
                )
        );
    }
    public RevIMU(HardwareMap hwMap, RevHubOrientationOnRobot o){
        imu = hwMap.get(IMU.class,"imu");
        IMU.Parameters p = new IMU.Parameters(
                o
        );
    }

    @Override
    public void resetYaw() {
        imu.resetYaw();
    }

    @Override
    public double getYaw(){
        return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
    }
}
