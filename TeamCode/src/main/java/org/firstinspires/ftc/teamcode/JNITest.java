package org.firstinspires.ftc.teamcode;

import android.annotation.SuppressLint;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.Utility;
import com.qualcomm.robotcore.util.ElapsedTime;

import com.flamingphoenix.nativebridge.modeled.NativePipeline;
import com.flamingphoenix.nativebridge.modeled.NativeStructs;

import kotlin.OptIn;

@TeleOp
public class JNITest extends LinearOpMode {

    NativeStructs.IKParams ikParams;
    NativeStructs.CameraParams cameraParams;
    NativeStructs.PipelineParams pipelineParams;
    NativeStructs.TrackingParams trackingParams;

    NativePipeline pipeline;
    ElapsedTime timer;

    @Override
    public void runOpMode() {
        ikParams = new NativeStructs.IKParams(0.4,0.4);
        cameraParams = new NativeStructs.CameraParams(
                new double[] {0.0,0.0,1.2},
                new double[] {0.37139067635410367, -0.0, -0.9284766908852592},
                new double[] {0.9284766908852592, 0.0, 0.37139067635410367},
                415.69219381653056,
                320.0,
                240.0
        );
        pipelineParams = new NativeStructs.PipelineParams(
                0.5, 0.1016,  0.15, cameraParams
        );
        trackingParams = new NativeStructs.TrackingParams(50,5,2,0.5,1,1000,false);


        timer = new ElapsedTime();
        waitForStart();
        pipeline = NativePipeline.create(
                ikParams.getParams(),
                trackingParams.getDoubleParams(),
                trackingParams.getIntParams(),
                pipelineParams.getParams(),
                cameraParams.getParams()
        );


        while(opModeIsActive()){
            double[] result = new double[4];
            pipeline.update(new double[] {251, 219, 35},timer.milliseconds(), result);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            telemetry.addData("Result", String.format("status: %.0f,link0: %.2f, link2: %.2f, link3: %.2f", result[0], result[1], result[2], result[3]));
            telemetry.update();
        }


    }
}
