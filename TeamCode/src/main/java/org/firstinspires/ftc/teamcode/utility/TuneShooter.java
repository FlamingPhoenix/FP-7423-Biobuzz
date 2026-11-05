package org.firstinspires.ftc.teamcode.utility;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Utility;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.teamcode.robot.ShooterController;

@Configurable
@Utility(name = "Tune Shooter")
public class TuneShooter extends OpMode {
    TelemetryManager telemetry = PanelsTelemetry.INSTANCE.getTelemetry();
    ShooterController shooterController;
    public static double KP = 60;
    public static double KI = 0.1;
    public static double KD = 0.1;
    public static double KF = 17.2;
    public static double TARGET_SPEED = 1700;
    PIDFCoefficients pidf = new PIDFCoefficients(KP, KI, KD, KF);
    @Override
    public void init() {
        shooterController = new ShooterController(hardwareMap);
        shooterController.setPIDCoefficients(pidf);

    }
    @Override
    public void loop() {
        shooterController.setPIDCoefficients(new PIDFCoefficients(KP, KI, KD, KF));
        shooterController.setShooterVelocity(TARGET_SPEED);
        telemetry.addData("Target Speed", TARGET_SPEED);
        double currentSpeed = shooterController.getShooterVelocity();
        telemetry.addData("Current Speed", currentSpeed);
        telemetry.addData("Error", TARGET_SPEED - currentSpeed);
        telemetry.addData("Max Current", shooterController.getMaxCurrent());
        telemetry.addData("Current Power", shooterController.getPower());
        telemetry.update();
    }
}
