package com.lilin.ezftp;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lilin.ezftp.databinding.ActivityWelcomeBinding;

import java.util.List;

public class WelcomeActivity extends BaseActivity {

    private static final String[] NEED_PERMISSION_LIST =
            {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION};

    private ActivityWelcomeBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_welcome);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermission();
    }

    private void checkPermission() {
        PermissionUtils.permission(NEED_PERMISSION_LIST)
                .callback(new PermissionUtils.FullCallback() {
                    @Override
                    public void onGranted(@NonNull List<String> granted) {
                        if (granted.size() == NEED_PERMISSION_LIST.length) {
                            delayToMainActivity();
                        }
                    }

                    @Override
                    public void onDenied(@NonNull List<String> deniedForever, @NonNull List<String> denied) {
                        ToastUtils.showShort("Please check permission!");
                        finish();
                    }
                })
                .request();
    }


    private void delayToMainActivity() {
        binding.getRoot().postDelayed(new Runnable() {
            @Override
            public void run() {
                toMainActivity();
            }
        }, 1000);
    }

    private void toMainActivity() {
        startActivity(new Intent(this, FTPMainActivity.class));
    }
}
