package com.lilin.ezftp;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.lilin.ezftp.databinding.ActivityFtpMainBinding;

public class FtpMainActivity extends BaseActivity implements View.OnClickListener {

    private ActivityFtpMainBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ftp_main);
        initView();
    }

    private void initView() {
        final String builder =
                getString(R.string.help_title) +
                        "\n" +
                        getString(R.string.step1) +
                        "\n" +
                        getString(R.string.step2) +
                        "\n" +
                        getString(R.string.step3) +
                        "\n" +
                        getString(R.string.step4) +
                        "\n";
        binding.tvHelpInfo.setText(builder);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_as_server:
                break;
            case R.id.btn_as_client:
                break;
        }
    }
}
