package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.drive.DriveIMU;
import org.firstinspires.ftc.teamcode.drive.FieldCentricDrive;
import org.firstinspires.ftc.teamcode.drive.PinPointIMU;

@TeleOp
public class TeleOp1 extends OpMode {
    FieldCentricDrive drive;
    DriveIMU imu;

    @Override
    public void init() {
        imu = new PinPointIMU(hardwareMap);
        drive = new FieldCentricDrive(hardwareMap, imu);
    }

    @Override
    public void loop() {


        drive.drive(gamepad1);
    }
}
