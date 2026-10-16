package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

public class IntakeController {
    DcMotorEx intakeMotor;

    /** Current draw (amps) above which a stall is suspected. */
    private static final double STALL_CURRENT = 10.0;

    /** Encoder velocity (ticks/sec) below which a stall is suspected. */
    private static final double STALL_VELOCITY = 50.0;

    public IntakeController(HardwareMap hardwareMap){
        intakeMotor = hardwareMap.get(DcMotorEx.class, "intake");
        intakeMotor.setDirection(DcMotorEx.Direction.FORWARD);
        intakeMotor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
    }

    /**
     * Apply the requested power and report whether the intake is stalled.
     *
     * @param running whether the intake should be actively spinning
     * @param power   the requested power in the range [-1, 1]
     * @return true if the motor is stopped while drawing over {@link #STALL_CURRENT} amps
     */
    public boolean update(boolean running, double power){
        intakeMotor.setPower(running ? power : 0);

        if (!running) {
            return false;
        }

        double current = intakeMotor.getCurrent(CurrentUnit.AMPS);
        double velocity = Math.abs(intakeMotor.getVelocity());

        return velocity < STALL_VELOCITY && current > STALL_CURRENT;
    }
}
