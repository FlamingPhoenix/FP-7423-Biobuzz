package com.flamingphoenix.nativebridge;

import org.opencv.core.Mat;

public final class ZipDepth implements AutoCloseable {
    private long nativeHandle;

    public ZipDepth(String modelPath) {
        nativeHandle = NativeBridge.nativeCreate(modelPath);
        if (nativeHandle == 0) {
            throw new IllegalStateException("failed to create ZipDepth model");
        }
    }

    public Mat infer(Mat image) {
        if (nativeHandle == 0) {
            throw new IllegalStateException("ZipDepth model is closed");
        }
        return NativeBridge.nativeInfer(nativeHandle, image);
    }

    @Override
    public void close() {
        if (nativeHandle != 0) {
            NativeBridge.nativeDestroy(nativeHandle);
            nativeHandle = 0;
        }
    }
}
