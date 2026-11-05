package org.firstinspires.ftc.teamcode;

import static com.pedropathing.api.Paths.*;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;


import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.commands.Commands.waitMs;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import com.pedropathing.paths.interpolator.Interpolator;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.pedro.Constants;

import java.util.ArrayList;


/////////////////// paste code below from visualizer and remove imports

@Autonomous(name = "AutoPath", group = "Autonomous")
public class AutoPath extends LinearOpMode {

    // Motor

    DcMotor frontLeftDrive, frontRightDrive, backLeftDrive, backRightDrive;
    DcMotor shooterLeft, shooterRight, sweeperLeft, sweeperRight, intake;
    GoBildaPinpointDriver odometry;


    //Auto Poses
    private Follower follower;

    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(56.2189, 9.2785, 90);
    private final Pose sR1 = poseFactory.of(58.601, 34.5293, 90);
    private final Pose sR1Segment1Start = poseFactory.of(58.601, 34.5293, 90);
    private final Pose sR1Segment1End = poseFactory.of(58.601, 34.5293, 90);
    private final Pose cR2 = poseFactory.of(16.4857, 18.1759, 270);
    private final Pose cR2Segment1Start = poseFactory.of(16.4857, 18.1759, 90);
    private final Pose cR2Segment1End = poseFactory.of(16.4857, 18.1759, 270);
    private final Pose cR1 = poseFactory.of(19.5337, 46.9165, 180);
    private final Pose cR1Segment1Start = poseFactory.of(19.5337, 46.9165, 90);
    private final Pose cR1Segment1End = poseFactory.of(19.5337, 46.9165, 180);
    private final Pose lZR = poseFactory.of(17.5976, 108.8864, 180);
    private final Pose lZRSegment1Start = poseFactory.of(17.5976, 108.8864, 180);
    private final Pose lZRSegment1End = poseFactory.of(17.5976, 108.8864, 180);
    private final Pose cR3 = poseFactory.of(47.4638, 123.963, 90);
    private final Pose cR3Segment1Start = poseFactory.of(47.4638, 123.963, 90);
    private final Pose cR3Segment1End = poseFactory.of(47.4638, 123.963, 90);
    private final Pose sR2 = poseFactory.of(59.1187, 105.2189, 270);
    private final Pose sR2Segment1Start = poseFactory.of(59.1187, 105.2189, 90);
    private final Pose sR2Segment1End = poseFactory.of(59.1187, 105.2189, 270);

    private ElapsedTime runtime = new ElapsedTime();

    //times
    private ArrayList<Integer> timeList = new ArrayList<Integer>();
    // Autonomous routine
    public Command autoRoutine() {
        return sequential(
                follow(follower, sR1()),
                follow(follower, cR2()),
                follow(follower, cR1()),
                follow(follower, lZR()),
                follow(follower, cR3()),
                waitMs(500),
                follow(follower, sR2())
        );
    }

    @Override
    public void runOpMode() {
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
        shooterLeft = hardwareMap.get(DcMotor.class, "SHTRL");
        shooterRight = hardwareMap.get(DcMotor.class, "SHTRR");
        sweeperLeft = hardwareMap.get(DcMotor.class, "SWPRL");
        sweeperRight = hardwareMap.get(DcMotor.class, "SWPRR");
        intake = hardwareMap.get(DcMotor.class, "INTAKE");




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

        //
        Scheduler.reset();
        follower = Constants.create(hardwareMap);
        follower.setPose(start);
        follower.update();

        waitForStart();
        runtime.reset();
        schedule(autoRoutine());

        while (opModeIsActive()) {
            follower.update();
            Scheduler.execute();


            telemetry.addData("Elapsed Time", "%.2f seconds", runtime.seconds());

            telemetry.addData("x", follower.pose().x());
            telemetry.addData("y", follower.pose().y());
            telemetry.addData("heading", follower.pose().heading());

            if (follower.currentPath() != null) {
                telemetry.addData("Current path distance remaining", follower.distanceToEndpoint());
                telemetry.addData("Path number", follower.pathIndex());
            }

            telemetry.update();
        }
    }

    public Path sR1() {
        timeList.add((int) runtime.seconds());
        return line(start, sR1).heading(Interpolator.piecewise().until(1, Interpolator.linear(sR1Segment1Start, sR1Segment1End)));
    }

    public Path cR2() {
        timeList.add((int) runtime.seconds());
        return line(sR1, cR2).heading(Interpolator.piecewise().until(1, Interpolator.linear(cR2Segment1Start, cR2Segment1End)));
    }

    public Path cR1() {
        timeList.add((int) runtime.seconds());
        return line(cR2, cR1).heading(Interpolator.piecewise().until(1, Interpolator.linear(cR1Segment1Start, cR1Segment1End)));
    }

    public Path lZR() {
        timeList.add((int) runtime.seconds());
        return line(cR1, lZR).heading(Interpolator.piecewise().until(1, Interpolator.linear(lZRSegment1Start, lZRSegment1End)));
    }

    public Path cR3() {
        timeList.add((int) runtime.seconds());
        return line(lZR, cR3).heading(Interpolator.piecewise().until(1, Interpolator.linear(cR3Segment1Start, cR3Segment1End)));
    }

    public Path sR2() {
        timeList.add((int) runtime.seconds());
        return line(cR3, sR2).heading(Interpolator.piecewise().until(1, Interpolator.linear(sR2Segment1Start, sR2Segment1End)));
    }

}
