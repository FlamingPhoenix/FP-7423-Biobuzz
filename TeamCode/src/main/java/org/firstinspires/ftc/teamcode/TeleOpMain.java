package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.teamcode.robot.IntakeController;
import org.firstinspires.ftc.teamcode.robot.ShooterController;
import org.firstinspires.ftc.teamcode.robot.SynchronousServos;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

@TeleOp(name = "TeleOpMain", group = "Robot")
public class TeleOpMain extends OpMode {
    // This declares the four motors needed
    DcMotor frontLeftDrive, frontRightDrive, backLeftDrive, backRightDrive;
    DcMotor sweeperLeft, sweeperRight ; // add back intake, shooterLeft, shooterRight here for reg DcMotor usage rather than custom function
    GoBildaPinpointDriver odometry;
    IntakeController intakeController;
    ShooterController shooterController;
    SynchronousServos synchronousServos;

    @Override
    public void init() {

        //drivetrain motor init
        frontLeftDrive = hardwareMap.get(DcMotor.class, "fl");
        frontRightDrive = hardwareMap.get(DcMotor.class, "fr");
        backLeftDrive = hardwareMap.get(DcMotor.class, "bl");
        backRightDrive = hardwareMap.get(DcMotor.class, "br");

        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);

        // This uses RUN_USING_ENCODER to be more accurate.
        frontLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //all other motors init
        //shooterLeft = hardwareMap.get(DcMotor.class, "SHTRL");
        //shooterRight = hardwareMap.get(DcMotor.class, "SHTRR");


        //sweeperLeft = hardwareMap.get(DcMotor.class, "SWPRL");
        //sweeperRight = hardwareMap.get(DcMotor.class, "SWPRR");
        //intake = hardwareMap.get(DcMotor.class, "INTAKE");


        IntakeController intake;

        //orientation of our robot

        odometry = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
        RevHubOrientationOnRobot.LogoFacingDirection logoDirection =
                RevHubOrientationOnRobot.LogoFacingDirection.UP;
        RevHubOrientationOnRobot.UsbFacingDirection usbDirection =
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD;

        RevHubOrientationOnRobot orientationOnRobot = new
                RevHubOrientationOnRobot(logoDirection, usbDirection);
        odometry.initialize();

        odometry.setOffsets(-84.0d,-12.0d, DistanceUnit.CM);
        odometry.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        odometry.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.FORWARD);

        odometry.resetPosAndIMU();

    }

    @Override
    public void loop() {
        telemetry.addLine("Press down on /\\ to reset Yaw");
        telemetry.addLine("Hold left bumper to drive in robot relative");
        telemetry.addLine("The left joystick sets the robot direction");
        telemetry.addLine("Moving the right joystick left and right turns the robot");
        Pose2D pos = odometry.getPosition();
        telemetry.addData("X: " , pos.getX(DistanceUnit.CM));
        telemetry.addData("Y: " , pos.getY(DistanceUnit.CM));
        telemetry.update();

        // If you press the A button, then you reset the Yaw to be zero from the way
        // the robot is currently pointing

        // If you press the left bumper, you get a drive from the point of view of the robot
        // (much like driving an RC vehicle)

        if (gamepad1.left_bumper) {
            drive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
        }
        else {
            driveFieldRelative(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
        }

        if (gamepad1.cross) { //intake
            intakeController.update(true, 1);


        }
        else if (gamepad1.circle) { //intake (reversed)

            intakeController.update(true, -1);

        }
        else {
            intakeController.update(true, 0);

        }
        if (gamepad1.square) { //shooter
            shooterController.setShooterPower(1);
        }
        else if (gamepad1.triangle) {
            shooterController.setShooterPower(-1);

        }
        else {
            shooterController.setShooterPower(0);
        }

        if (gamepad1.dpad_up) {
            odometry.resetPosAndIMU();
        }

        if (gamepad1.dpad_down) {
            sweeperLeft.setPower(1.0);
            sweeperRight.setPower(-1.0);
        }
        else if (gamepad1.dpad_right) {
            sweeperLeft.setPower(-1.0);
            sweeperRight.setPower(1.0);
        }
        else {
            sweeperLeft.setPower(0.0);
            sweeperRight.setPower(0.0);
        }










    }


    //DRIVETRAIN METHODS




    // This routine drives the robot field relative
    private void driveFieldRelative(double forward, double right, double rotate) {
        // First, convert direction being asked to drive to polar coordinates
        double theta = Math.atan2(forward, right);
        double r = Math.hypot(right, forward);

        // Second, rotate angle by the angle the robot is pointing
        theta = AngleUnit.normalizeRadians(theta - odometry.getHeading(AngleUnit.RADIANS));

        // Third, convert back to cartesian
        double newForward = r * Math.sin(theta);
        double newRight = r * Math.cos(theta);

        // Finally, call the drive method with robot relative forward and right amounts
        drive(newForward, newRight, rotate);
    }

    // Thanks to FTC16072 for sharing this code!!
    public void drive(double forward, double right, double rotate) {
        // This calculates the power needed for each wheel based on the amount of forward,
        // strafe right, and rotate
        double frontLeftPower = forward + right + rotate;
        double frontRightPower = forward - right - rotate;
        double backRightPower = forward + right - rotate;
        double backLeftPower = forward - right + rotate;

        double maxPower = 1.0;
        double maxSpeed = 1.0;  // make this slower for outreaches

        // This is needed to make sure we don't pass > 1.0 to any wheel
        // It allows us to keep all of the motors in proportion to what they should
        // be and not get clipped
        maxPower = Math.max(maxPower, Math.abs(frontLeftPower));
        maxPower = Math.max(maxPower, Math.abs(frontRightPower));
        maxPower = Math.max(maxPower, Math.abs(backRightPower));
        maxPower = Math.max(maxPower, Math.abs(backLeftPower));

        // We multiply by maxSpeed so that it can be set lower for outreaches
        // When a young child is driving the robot, we may not want to allow full
        // speed.
        frontLeftDrive.setPower(maxSpeed * (frontLeftPower / maxPower));
        frontRightDrive.setPower(maxSpeed * (frontRightPower / maxPower));
        backLeftDrive.setPower(maxSpeed * (backLeftPower / maxPower));
        backRightDrive.setPower(maxSpeed * (backRightPower / maxPower));
    }
}
