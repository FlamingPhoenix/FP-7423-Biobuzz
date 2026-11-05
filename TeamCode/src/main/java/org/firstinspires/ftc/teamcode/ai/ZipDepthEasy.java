package org.firstinspires.ftc.teamcode.ai;

import android.content.Context;

import com.flamingphoenix.nativebridge.ZipDepth;

import org.opencv.core.Mat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class ZipDepthEasy implements AutoCloseable {
    private static final String MODEL_ASSET = "zipdepth_base_128x128.onnx";
    private static final String MODEL_DATA_ASSET = "zipdepth_base_128x128_raw.onnx.data";

    private final ZipDepth zipDepth;

    public ZipDepthEasy(Context context) throws IOException {
        Context appContext = context.getApplicationContext();
        File modelFile = copyAssetToFile(appContext, MODEL_ASSET);
        copyAssetToFile(appContext, MODEL_DATA_ASSET);
        zipDepth = new ZipDepth(modelFile.getAbsolutePath());
    }

    public Mat infer(Mat image) {
        return zipDepth.infer(image);
    }

    private static File copyAssetToFile(Context context, String assetName) throws IOException {
        File file = new File(context.getFilesDir(), assetName);
        try (InputStream input = context.getAssets().open(assetName);
             FileOutputStream output = new FileOutputStream(file)) {
            byte[] buffer = new byte[8192];
            int count;
            while ((count = input.read(buffer)) != -1) {
                output.write(buffer, 0, count);
            }
        }
        return file;
    }

    @Override
    public void close() {
        zipDepth.close();
    }
}
