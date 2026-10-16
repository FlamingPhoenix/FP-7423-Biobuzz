package org.firstinspires.ftc.teamcode.utility;

import static java.lang.Math.abs;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class FieldCentricDrive {
    DcMotor fl, fr, bl, br;
    DriveIMU imu;
    /**
     * Initialize motors
     * @param hardwareMap hardwareMap from opmode
     */
    public FieldCentricDrive(HardwareMap hardwareMap, DriveIMU imu){
        fl = hardwareMap.dcMotor.get("fl");
        fr = hardwareMap.dcMotor.get("fr");
        bl = hardwareMap.dcMotor.get("bl");
        br = hardwareMap.dcMotor.get("br");
        //reverse motors
        fr.setDirection(DcMotor.Direction.REVERSE);
        br.setDirection(DcMotor.Direction.REVERSE);
        this.imu = imu;
    }
    /**
     * Drive based off of gamepad inputs
     * @param gpx gamepad x input
     * @param gpy gamepad y input
     * @param rx gamepad rotation input
     */
    public void drive(double gpx, double gpy, double rx) {
        double botHeading = -imu.getYaw();//might be degrees
        double rotX = gpx * Math.cos(botHeading) - gpy * Math.sin(botHeading);
        double rotY = gpx * Math.sin(botHeading) + gpy * Math.cos(botHeading);


        double denominator = Math.max(abs(gpy) + abs(gpx) + abs(rx), 1);
        double flp = -(rotY + rotX + rx) / denominator;
        double blp = -(rotY - rotX + rx) / denominator;
        double frp = -(rotY - rotX - rx) / denominator;
        double brp = -(rotY + rotX - rx) / denominator;

        fl.setPower(0.74*flp);
        bl.setPower(0.74*blp);
        fr.setPower(0.74*frp);
        br.setPower(0.74*brp);
    }
    /**
     * Drive based off of gamepad
     * More functionality because of more access to controls
     * @param gamepad1 gamepad 1
     */
    public void drive(Gamepad gamepad1){
        double x = gamepad1.left_stick_x*1.1;
        double y = -gamepad1.left_stick_y;
        double rx = gamepad1.right_stick_x;
        if(gamepad1.options){
            resetIMU();
        }
        double botHeading = -imu.getYaw();//might be degrees
        double rotX = x * Math.cos(botHeading) - y * Math.sin(botHeading);
        double rotY = x * Math.sin(botHeading) + y * Math.cos(botHeading);


        double denominator = Math.max(abs(y) + abs(x) + abs(rx), 1);
        double flp = (rotY + rotX + rx) / denominator;
        double blp = (rotY - rotX + rx) / denominator;
        double frp = (rotY - rotX - rx) / denominator;
        double brp = (rotY + rotX - rx) / denominator;

        flp = flp * (1 - gamepad1.right_trigger);
        blp = blp * (1 - gamepad1.right_trigger);
        frp = frp * (1 - gamepad1.right_trigger);
        brp = brp * (1 - gamepad1.right_trigger);


        fl.setPower(flp);
        bl.setPower(blp);
        fr.setPower(frp);
        br.setPower(brp);
    }
    /**
     * Get robot heading in radians
     * @return robot heading
     */
    public double getHeading(){
        return imu.getYaw();
    }
    /**
     * Reset the IMU
     */
    public void resetIMU(){
        imu.resetYaw();
    }
}