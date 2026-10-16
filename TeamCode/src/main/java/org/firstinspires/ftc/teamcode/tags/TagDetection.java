package org.firstinspires.ftc.teamcode.tags;

import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

public class TagDetection {
    public enum TagColor {
        RED,
        BLUE,
    }

    public TagColor color;
    public Pose3D pose;
    public TagDetection(TagColor color, Pose3D pose) {
        this.color = color;
        this.pose = pose;
    }


}