package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.ai.ZipDepthEasy;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;

import java.io.IOException;
import java.util.Arrays;

@TeleOp(name = "Vision Test")
public class VisionTest extends LinearOpMode {
    static class DepthProcessor extends OpenCvPipeline{
        private static final int INPUT_SIZE = 128;
        ZipDepthEasy zipDepth;

        public DepthProcessor(ZipDepthEasy zipDepth) {
            this.zipDepth = zipDepth;
        }

        @Override
        public Mat processFrame(Mat mat) {
            int width = mat.cols();
            int height = mat.rows();
            double scale = Math.max(
                    (double) INPUT_SIZE / width,
                    (double) INPUT_SIZE / height);
            int resizedWidth = Math.max(INPUT_SIZE, (int) Math.ceil(width * scale));
            int resizedHeight = Math.max(INPUT_SIZE, (int) Math.ceil(height * scale));

            Mat resized = new Mat();
            Mat bgr = new Mat();
            try {
                Imgproc.resize(
                        mat, resized, new org.opencv.core.Size(resizedWidth, resizedHeight));
                int left = (resizedWidth - INPUT_SIZE) / 2;
                int top = (resizedHeight - INPUT_SIZE) / 2;
                Mat cropped = resized.submat(new Rect(left, top, INPUT_SIZE, INPUT_SIZE));
                try {
                    if (cropped.channels() == 4) {
                        Imgproc.cvtColor(cropped, bgr, Imgproc.COLOR_RGBA2BGR);
                    } else if (cropped.channels() == 3) {
                        cropped.copyTo(bgr);
                    } else if (cropped.channels() == 1) {
                        Imgproc.cvtColor(cropped, bgr, Imgproc.COLOR_GRAY2BGR);
                    } else {
                        throw new IllegalArgumentException(
                                "Unsupported camera Mat channels: " + cropped.channels());
                    }
                    Mat nativeDepth = zipDepth.infer(bgr);
                    try {
                        Mat depth8 = new Mat();
                        Mat input8 = new Mat();
                        Mat output = new Mat();
                        try {
                            Core.normalize(
                                    nativeDepth, depth8, 0, 255,
                                    Core.NORM_MINMAX, CvType.CV_8UC1);
                            Imgproc.cvtColor(bgr, input8, Imgproc.COLOR_BGR2GRAY);
                            Core.hconcat(Arrays.asList(input8, depth8), output);
                            return output;
                        } finally {
                            depth8.release();
                            input8.release();
                        }
                    } finally {
                        nativeDepth.release();
                    }
                } finally {
                    cropped.release();
                }
            } finally {
                bgr.release();
                resized.release();
            }
        }
    }

    @Override
    public void runOpMode() throws InterruptedException {
        try {
            int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());

            DepthProcessor depthProcessor = new DepthProcessor(new ZipDepthEasy(hardwareMap.appContext));

            WebcamName webcamName = hardwareMap.get(WebcamName.class, "Webcam 1");
            OpenCvCamera camera = OpenCvCameraFactory.getInstance().createWebcam(webcamName, cameraMonitorViewId);
            waitForStart();
            if (isStopRequested()) {
                return;
            }
            camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
                @Override
                public void onOpened() {
                    camera.setPipeline(depthProcessor);
                    camera.startStreaming(320,240,OpenCvCameraRotation.UPRIGHT);
                }

                @Override
                public void onError(int errorCode) {
                    telemetry.addData("VisionTest", "Camera open error: " + errorCode);
                    telemetry.update();
                }
            });
            while (opModeIsActive()) {
                long start = System.nanoTime();
                double j = 3.0;
                for(int i = 0; i < 10 && opModeIsActive(); i++){
                    j *= Math.sin(i);
                }
                double elapsedMs = (System.nanoTime() - start) / 1_000_000.0;
                telemetry.addData("Loop time", String.format("%.3f ms", elapsedMs));
                telemetry.update();
                sleep(100);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
