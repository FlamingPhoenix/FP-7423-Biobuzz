package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.ServoImplEx;

public class SynchronousServos {
    public static class SynchronousServo {
        ServoImplEx servo;
        double offset;
        public SynchronousServo(ServoImplEx servo, double offset){
            this.servo = servo;
            this.offset = offset;
        }
    }
    SynchronousServo servo1;
    SynchronousServo servo2;
    public SynchronousServos(SynchronousServo servo1, SynchronousServo servo2){
        this.servo1 = servo1;
        this.servo2 = servo2;
    }
    /**
     * servos are facing opposite directions
     * so we need to invert the position of one of them
     * offset in each servo is a position where both servos are at the same position
     * that means when servo1 is at offset1, servo2 is at offset2,
     * then both servos are at the same position
     * use the offsets to calculate the difference required to synchronize
     * copilot autocompleted this method so pls test
     */
    public void setPosition(double position){
        double servo1Position = position + servo1.offset;
        double servo2Position = 1 - (position + servo2.offset);
        servo1.servo.setPosition(servo1Position);
        servo2.servo.setPosition(servo2Position);
    }



}
