package com.shark.dynamics.music.test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.shark.dynamics.audio.AudioFFTMonitor;
import com.shark.dynamics.audio.SpectrumView;
import com.shark.dynamics.music.R;

public class TestSpectrumActivity extends AppCompatActivity {

    private SpectrumView mSpectrumView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_spectrum);

        mSpectrumView = findViewById(R.id.id_spectrum);

    }

    @Override
    protected void onResume() {
        super.onResume();
        AudioFFTMonitor.getInstance().setFFTCallback((mc, sgs, wa) -> {
            mSpectrumView.updateValue(mc);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSpectrumView.dispose();
    }
}