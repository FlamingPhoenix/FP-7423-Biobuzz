package com.flamingphoenix.nativebridge;

import org.opencv.core.Mat;

public final class NativeBridge {
    static {
        System.loadLibrary("zipdepth_jni");
    }

    private NativeBridge() {
    }

    public static native long nativeCreate(String modelPath);

    public static native void nativeDestroy(long handle);

    public static native Mat nativeInfer(long handle, long matAddress);

    public static Mat nativeInfer(long handle, Mat image) {
        if (image == null) {
            throw new IllegalArgumentException("image is null");
        }
        return nativeInfer(handle, image.getNativeObjAddr());
    }
}
