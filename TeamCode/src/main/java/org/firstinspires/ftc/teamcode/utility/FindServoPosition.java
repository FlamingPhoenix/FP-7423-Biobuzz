package org.firstinspires.ftc.teamcode.utility;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.Utility;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.List;

/*
 * This is a utility program to find the position of a servo
 * Automatically detects all servos in the hardware map and allows you to control them using the gamepad
 * use dpad left & right to select the servo
 * use dpad up & down for fine control
 * use left stick up & down for coarse control
 * use left & right bumper to change the increment multiplier of dpad
 */
@Utility(name = "Find Servo Position")
public class FindServoPosition extends OpMode {
    String[] servoNames;
    Servo[] servos = {};
    int selectedServo = 0;
    int incrementMultiplier = 1;
    boolean prevDpadRight = false;
    boolean prevDpadLeft = false;
    boolean prevDpadUp = false;
    boolean prevDpadDown = false;
    boolean prevStickUp = false;
    boolean prevStickDown = false;
    @Override
    public void init() {
        List<Servo> servoList = hardwareMap.getAll(Servo.class);
        servos = new Servo[servoList.size()];
        for(int i = 0; i < servoList.size(); i++){
            servos[i] = servoList.get(i);
        }
        servoNames = new String[servoList.size()];
        for(int i = 0; i < servoList.size(); i++){
            servoNames[i] = servoList.get(i).getDeviceName();
        }
    }
    @Override
    public void loop() {
        boolean dpadRight = gamepad1.dpad_right;
        boolean dpadLeft = gamepad1.dpad_left;
        boolean dpadUp = gamepad1.dpad_up;
        boolean dpadDown = gamepad1.dpad_down;
        boolean stickUp = gamepad1.left_stick_y > 0.1;
        boolean stickDown = gamepad1.left_stick_y < -0.1;

        if(gamepad1.right_bumper){
            incrementMultiplier = 10;
        }
        if(gamepad1.left_bumper){
            incrementMultiplier = 1;
        }

        if(dpadRight && !prevDpadRight){
            selectedServo = (selectedServo + 1) % servos.length;
        } else if(dpadLeft && !prevDpadLeft){
            selectedServo = (selectedServo - 1 + servos.length) % servos.length;
        } else if(dpadUp && !prevDpadUp){
            servos[selectedServo].setPosition(servos[selectedServo].getPosition() + 0.01*incrementMultiplier);
        } else if(dpadDown && !prevDpadDown){
            servos[selectedServo].setPosition(servos[selectedServo].getPosition() - 0.01*incrementMultiplier);
        } else if(stickUp && !prevStickUp){
            servos[selectedServo].setPosition(servos[selectedServo].getPosition() + 0.1);
        } else if(stickDown && !prevStickDown){
            servos[selectedServo].setPosition(servos[selectedServo].getPosition() - 0.1);
        }

        prevDpadRight = dpadRight;
        prevDpadLeft = dpadLeft;
        prevDpadUp = dpadUp;
        prevDpadDown = dpadDown;
        prevStickUp = stickUp;
        prevStickDown = stickDown;

        for(int i = 0; i < servos.length; i++){
            telemetry.addData(servoNames[i], servos[i].getPosition());
        }
        telemetry.addData("selected servo", servoNames[selectedServo]);
        telemetry.addData("increment multiplier", incrementMultiplier);
        telemetry.update();
    }
}