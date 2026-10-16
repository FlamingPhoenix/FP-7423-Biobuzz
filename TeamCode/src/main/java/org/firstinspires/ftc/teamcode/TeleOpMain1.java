package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.utility.DriveIMU;
import org.firstinspires.ftc.teamcode.utility.FieldCentricDrive;
import org.firstinspires.ftc.teamcode.utility.PinPointIMU;

@TeleOp
public class TeleOpMain1 extends OpMode {
    FieldCentricDrive fcd;
    DriveIMU driveIMU;

    @Override
    public void init() {
        driveIMU = new PinPointIMU(hardwareMap);
        fcd = new FieldCentricDrive(hardwareMap, driveIMU);

    }

    @Override
    public void loop() {

    }
}
