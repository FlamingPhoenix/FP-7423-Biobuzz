package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

public class ShooterController {
    DcMotorEx shooterMotor1;
    DcMotorEx shooterMotor2;

    PIDFCoefficients defaultPIDF = new PIDFCoefficients(0.1, 0, 0, 1);
    public ShooterController(HardwareMap hardwareMap){
        initMotors(hardwareMap);
        setPIDCoefficients(defaultPIDF);
    }
    public ShooterController(HardwareMap hardwareMap, PIDFCoefficients pidf){
        initMotors(hardwareMap);
        setPIDCoefficients(pidf);
    }
    private void initMotors(HardwareMap hardwareMap){
        shooterMotor1 = hardwareMap.get(DcMotorEx.class, "shooter1");
        shooterMotor2 = hardwareMap.get(DcMotorEx.class, "shooter2");
        shooterMotor1.setDirection(DcMotorEx.Direction.REVERSE);
        shooterMotor2.setDirection(DcMotorEx.Direction.FORWARD);
        shooterMotor1.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
        shooterMotor2.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
    }
    public void setShooterPower(double power){
        shooterMotor1.setPower(power);
        shooterMotor2.setPower(power);
    }
    public void setShooterVelocity(double velocity){
        shooterMotor1.setVelocity(velocity);
        shooterMotor2.setVelocity(velocity);
    }
    public void setPIDCoefficients(double p, double i, double d, double f){
        shooterMotor1.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(p, i, d, f));
        shooterMotor2.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(p, i, d, f));
    }
    public void setPIDCoefficients(PIDFCoefficients pidf){
        shooterMotor1.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, pidf);
        shooterMotor2.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, pidf);
    }
    /**
     * Get the average velocity of the shooter motors
     * @return average velocity in ticks per second
     */
    public int getShooterVelocity(){
        // average both motors' velocities to get a more accurate reading
        return ((int)shooterMotor1.getVelocity() + (int)shooterMotor2.getVelocity()) / 2;
    }
    /**
     * Get the maximum current draw of the shooter motors
     * @return max current draw in amps
     */
    public double getMaxCurrent(){
        return Math.max(shooterMotor1.getCurrent(CurrentUnit.AMPS), shooterMotor2.getCurrent(CurrentUnit.AMPS));
    }
}
