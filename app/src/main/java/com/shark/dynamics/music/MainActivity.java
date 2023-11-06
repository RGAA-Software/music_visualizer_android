package com.shark.dynamics.music;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.shark.dynamics.audio.AudioFFTMonitor;
import com.shark.dynamics.basic.tips.Tips;
import com.shark.dynamics.music.effect.EffectFragment;
import com.shark.dynamics.music.music.MusicFragment;
import com.shark.dynamics.music.ui.BottomBar;
import com.shark.dynamics.music.ui.BottomPlayView;
import com.shark.dynamics.music.ui.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("native-lib");
    }

    private BottomBar mBottomBar;
    private BottomPlayView mPlayView;

    private List<BaseFragment> mFragments = new ArrayList<>();

    private boolean mStartRotate = true;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_main);

        mBottomBar = new BottomBar(findViewById(R.id.id_bottom_bar_parent));
        mBottomBar.setOnBarClickListener(this::changeFragment);
        mPlayView = mBottomBar.getPlayView();
        initFragments();
        mBottomBar.select(0);

        //mBottomBar.startPlayViewRotate();

        findViewById(R.id.test).setOnClickListener(v-> {
            if (mStartRotate ) {
                mBottomBar.stopPlayViewRotate();
                mStartRotate = false;
            } else {
                mBottomBar.startPlayViewRotate();
                mStartRotate = true;
            }
        });

        requestPermission();

        AudioFFTMonitor.getInstance().init(this);

        showTips();
    }

    private void initFragments() {
        mFragments.add(new EffectFragment());
        MusicFragment musicFragment = new MusicFragment();
        musicFragment.setPlayView(mPlayView);
        mFragments.add(musicFragment);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (Fragment fragment : mFragments) {
            transaction.add(R.id.id_fragment_parent, fragment);
        }
        transaction.commit();
    }

    private void changeFragment(int idx) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (int i = 0; i < mFragments.size(); ++i) {
            if (idx == i) {
                BaseFragment fragment = mFragments.get(idx);
                fragment.onResume();
                transaction.show(fragment);
            } else {
                BaseFragment fragment = mFragments.get(i);
                fragment.onPause();
                transaction.hide(fragment);
            }
        }
        transaction.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean allGranted = true;
        for (int grant : grantResults) {
            if (grant != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }

        if (!allGranted) {
            Tips.tips(this, "权限被拒绝，将无法使用，请允许。");
            requestPermission();
        }

    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.MODIFY_AUDIO_SETTINGS
        }, 101);
    }

    private void showTips() {
        new AlertDialog.Builder(this)
                .setTitle("TIPS")
                .setMessage("随便打开一个音乐软件，酷狗，网易云等，播放音乐(要有声音)，然后查看本软件效果。")
                .setCancelable(false)
                .setPositiveButton("好的", (dialog, which) -> {
                    dialog.dismiss();
                }).create().show();
    }
}