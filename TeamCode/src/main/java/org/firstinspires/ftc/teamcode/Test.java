package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class Test extends OpMode {

    @Override
    public void init() {
        telemetry.addData("Hiiii","hello");
        telemetry.update();
    }

    @Override
    public void loop() {
        telemetry.addData("Hiii","im running");
        telemetry.update();

    }
}
