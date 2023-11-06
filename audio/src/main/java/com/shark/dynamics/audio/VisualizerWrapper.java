package com.shark.dynamics.audio;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.util.Log;

import androidx.annotation.NonNull;


public class VisualizerWrapper {

    private static final long WAIT_UNTIL_HACK = 500;
	private Visualizer visualizer;
    private Visualizer.OnDataCaptureListener captureListener;
    private int captureRate;
    private long lastZeroArrayTimestamp;

	public VisualizerWrapper(@NonNull Context context, int audioSessionId, @NonNull final OnFftDataCaptureListener onFftDataCaptureListener) {
		visualizer = new Visualizer(audioSessionId);
        visualizer.setEnabled(false);
		visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        captureRate = Visualizer.getMaxCaptureRate();
        captureListener = new Visualizer.OnDataCaptureListener() {
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {

            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
                boolean allZero = allElementsAreZero(fft);
                if (allZero) {
                    return;
                }
                onFftDataCaptureListener.onFftDataCapture(fft);
            }
        };
        visualizer.setEnabled(true);
	}

    public static boolean allElementsAreZero(byte[] array) {
        for (byte b : array) {
            if (b != 0)
                return false;
        }
        return true;
    }

	public void release() {
	    if (visualizer != null) {
            visualizer.setEnabled(false);
            visualizer.release();
            visualizer = null;
        }
	}

	public void setEnabled(final boolean enabled) {
        if(visualizer == null) return;
        visualizer.setEnabled(false);
        if (enabled) {
            visualizer.setDataCaptureListener(captureListener, captureRate, true, true);
        } else {
            visualizer.setDataCaptureListener(null, captureRate, true, false);
        }
        visualizer.setEnabled(true);
	}

	public interface OnFftDataCaptureListener {
		void onFftDataCapture(byte[] fft);
	}
}
