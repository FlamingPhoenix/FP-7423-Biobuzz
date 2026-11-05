package org.firstinspires.ftc.teamcode.ai;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.android.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

@TeleOp(name = "ZipDepth Inference")
public class ZipDepthTest extends LinearOpMode {
    private static final int INPUT_SIZE = 128;
    private static final String IMAGE_ASSET = "test_image.png";

    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();
        if (isStopRequested()) {
            return;
        }

        Mat input = new Mat();
        Mat rgbInput = new Mat();
        Mat depth = null;
        try {
            Bitmap bitmap = decodeAsset(IMAGE_ASSET);
            Bitmap resized;
            if (bitmap.getWidth() == INPUT_SIZE && bitmap.getHeight() == INPUT_SIZE) {
                resized = bitmap;
            } else {
                resized = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, true);
                bitmap.recycle();
            }
            Utils.bitmapToMat(resized, input);
            resized.recycle();
            Imgproc.cvtColor(input, rgbInput, Imgproc.COLOR_RGBA2RGB);

            try (ZipDepthEasy zipDepth = new ZipDepthEasy(hardwareMap.appContext)) {
                long start = System.nanoTime();
                depth = zipDepth.infer(rgbInput);
                double inferenceMs = (System.nanoTime() - start) / 1_000_000.0;
                File output = saveDepthImage(depth);
                telemetry.addData("Inference time", String.format(Locale.US, "%.3f ms", inferenceMs));
                telemetry.addData("Output image", output.getAbsolutePath());
            }
        } catch (IOException | RuntimeException exception) {
            telemetry.addData("Inference error", exception.getMessage());
        } finally {
            input.release();
            rgbInput.release();
            if (depth != null) {
                depth.release();
            }
        }
        telemetry.update();

        while (opModeIsActive()) {
            idle();
        }
    }

    private Bitmap decodeAsset(String assetName) throws IOException {
        try (InputStream input = hardwareMap.appContext.getAssets().open(assetName)) {
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            if (bitmap == null) {
                throw new IOException("Unable to decode test_image.png");
            }
            return bitmap;
        }
    }

    private File saveDepthImage(Mat depth) throws IOException {
        if (depth == null || depth.empty() || depth.channels() != 1) {
            throw new IOException("Native inference returned an invalid depth Mat");
        }
        int width = depth.cols();
        int height = depth.rows();
        if (width != INPUT_SIZE || height != INPUT_SIZE) {
            throw new IOException("Unexpected depth Mat size: " + width + "x" + height);
        }

        float minimum = Float.POSITIVE_INFINITY;
        float maximum = Float.NEGATIVE_INFINITY;
        float[] value = new float[1];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                depth.get(y, x, value);
                if (Float.isFinite(value[0])) {
                    minimum = Math.min(minimum, value[0]);
                    maximum = Math.max(maximum, value[0]);
                }
            }
        }
        if (!Float.isFinite(minimum) || !Float.isFinite(maximum)) {
            throw new IOException("Native inference returned no finite depth values");
        }

        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        float range = maximum - minimum;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                depth.get(y, x, value);
                float normalized = range == 0 ? 0 : (value[0] - minimum) / range;
                int intensity = Math.round(Math.max(0, Math.min(1, normalized)) * 255);
                image.setPixel(x, y,
                        0xff000000 | (intensity << 16) | (intensity << 8) | intensity);
            }
        }

        File directory = new File(
                hardwareMap.appContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "zipdepth");
        if (!directory.exists() && !directory.mkdirs()) {
            image.recycle();
            throw new IOException("Unable to create output directory: " + directory);
        }
        File output = new File(directory, "zipdepth_depth.png");
        try (FileOutputStream stream = new FileOutputStream(output)) {
            if (!image.compress(Bitmap.CompressFormat.PNG, 100, stream)) {
                throw new IOException("Unable to encode depth image");
            }
        } finally {
            image.recycle();
        }
        return output;
    }
}
