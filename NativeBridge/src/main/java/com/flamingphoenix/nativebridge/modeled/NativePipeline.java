package com.flamingphoenix.nativebridge.modeled;

import android.content.Context;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

import java.io.File;

public final class NativePipeline implements AutoCloseable {
    static {
        Context context = AppUtil.getInstance().getApplication();

        File library = new File(
                context.getApplicationInfo().nativeLibraryDir,
                "libmodeled_jni.so"
        );

        android.util.Log.i("NativePipeline",
                "Native library path: " + library.getAbsolutePath()
                        + ", exists=" + library.exists()
        );
        android.util.Log.i(
                "NativePipeline",
                "ClassLoader=" + NativePipeline.class.getClassLoader()
        );
        System.load(library.getAbsolutePath());
    }

    private long handle;

    private NativePipeline(long handle) {
        this.handle = handle;
    }

    public static NativePipeline create(
            double[] ik,
            double[] tracking,
            int[] trackingInts,
            double[] pipeline,
            double[] camera) {
        long handle = nativeCreate(ik, tracking, trackingInts, pipeline, camera);
        if (handle == 0L) {
            throw new IllegalStateException("nativeCreate returned a null handle");
        }
        return new NativePipeline(handle);
    }

    public synchronized int update(double[] detections, double timestamp, double[] out) {
        checkOpen();
        if (out == null || out.length < 4) {
            throw new IllegalArgumentException("out must contain at least 4 values");
        }
        int status = nativeUpdate(handle, detections, timestamp, out);
        if (status != 0) {
            throw new IllegalStateException("nativeUpdate failed with status " + status);
        }
        return (int) out[0];
    }

    public synchronized void setTrueRadius(double radius) {
        checkOpen();
        int status = nativeSetTrueRadius(handle, radius);
        if (status != 0) {
            throw new IllegalStateException(
                    "nativeSetTrueRadius failed with status " + status);
        }
    }

    @Override
    public synchronized void close() {
        if (handle != 0L) {
            nativeDestroy(handle);
            handle = 0L;
        }
    }

    private void checkOpen() {
        if (handle == 0L) {
            throw new IllegalStateException("pipeline is closed");
        }
    }

    private static native long nativeCreate(
            double[] ik,
            double[] tracking,
            int[] trackingInts,
            double[] pipeline,
            double[] camera);

    private static native int nativeUpdate(
            long handle,
            double[] detections,
            double timestamp,
            double[] out);

    private static native int nativeSetTrueRadius(long handle, double radius);

    private static native void nativeDestroy(long handle);
}
