package com.lilin.ezftp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.lilin.ezftp.databinding.ActivityFtpMainBinding;
import com.lilin.ezftp.ftpclient.FtpClientActivity;
import com.lilin.ezftp.ftpserver.FtpServerActivity;

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
        binding.btnAsClient.setOnClickListener(this);
        binding.btnAsServer.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_as_server:
                startActivity(new Intent(this, FtpServerActivity.class));
                break;
            case R.id.btn_as_client:
                startActivity(new Intent(this, FtpClientActivity.class));
                break;
        }
    }
}
