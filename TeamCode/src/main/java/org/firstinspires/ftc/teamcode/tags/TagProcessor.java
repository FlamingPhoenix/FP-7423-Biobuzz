package org.firstinspires.ftc.teamcode.tags;

import static org.firstinspires.ftc.robotcore.internal.system.Misc.contains;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagClusterMetadata;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagGameDatabase;
import org.firstinspires.ftc.vision.apriltag.AprilTagLibrary;
import org.firstinspires.ftc.vision.apriltag.AprilTagMetadata;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.apriltag.AprilTagSingleDetection;

import java.util.List;

public class TagProcessor {

    private final AprilTagProcessor atp;
    private final VisionPortal vp;
    private final int[] blueTags = {42,43,44,45,41,40,39,38};
    private final int[] redTags = {30,31,32,33,34,35,36,37};
    public TagProcessor(HardwareMap hw) {
        atp = new AprilTagProcessor.Builder()
                .setTagFamily(AprilTagProcessor.TagFamily.TAG_36h11)
                .setTagLibrary(AprilTagGameDatabase.getBioBuzzTagLibrary())
                .setOutputUnits(DistanceUnit.INCH, AngleUnit.DEGREES)
                .setDrawTagOutline(true)
                .build();
        atp.setDecimation(1.0f);

        vp = new VisionPortal.Builder()
                .setCamera(hw.get(WebcamName.class, "Webcam 1"))
                .addProcessor(atp)
                .build();


    }

    public TagDetection getDetections() {
        List<AprilTagDetection> currentDetections = atp.getDetections();
        for(AprilTagDetection detection : currentDetections) {
            if(detection instanceof AprilTagSingleDetection) {
                AprilTagSingleDetection singleDetection = (AprilTagSingleDetection) detection;
                int id = singleDetection.id;
                TagDetection.TagColor color = contains(blueTags, id) ? TagDetection.TagColor.BLUE : TagDetection.TagColor.RED;

                Pose3D pose = new Pose3D(
                        new Position(
                                DistanceUnit.INCH,
                                singleDetection.ftcPose.x,
                                singleDetection.ftcPose.y,
                                singleDetection.ftcPose.z,
                                0
                        ),
                        new YawPitchRollAngles(
                                AngleUnit.DEGREES,
                                singleDetection.ftcPose.yaw,
                                singleDetection.ftcPose.pitch,
                                singleDetection.ftcPose.roll,
                                0
                        )
                );
                return new TagDetection(color, pose);

            }
        }
        return null;
    }
}
