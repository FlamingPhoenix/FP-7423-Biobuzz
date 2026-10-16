package org.firstinspires.ftc.teamcode.drive;

import static java.lang.Math.abs;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class FieldCentricDrive {
    DcMotor fl, fr, bl, br;
    DriveIMU imu;
    double multiplier = 1.0;
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
     * default speed limited to 0.75
     * right trigger = slow mode, (0.25 to 0.75)
     * left trigger = full speed mode (0.75 to 1.0)
     * options button = reset IMU
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


        // Slow mode with right trigger
        multiplier = 0.75 - (gamepad1.right_trigger * 0.5); // Adjust the multiplier based on the right trigger value
        if(gamepad1.left_trigger > 0.1){
            multiplier = 0.75 + (gamepad1.left_trigger * 0.25);
            //full speed mode with left trigger
        }


        fl.setPower(multiplier*flp);
        bl.setPower(multiplier*blp);
        fr.setPower(multiplier*frp);
        br.setPower(multiplier*brp);
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