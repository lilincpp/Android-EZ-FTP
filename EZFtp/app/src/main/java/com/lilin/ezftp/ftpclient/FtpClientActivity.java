package com.lilin.ezftp.ftpclient;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.NetworkUtils;
import com.lilin.ezftp.R;
import com.lilin.ezftp.databinding.ActivityFtpClientBinding;
import com.lilincpp.github.libezftp.EZFtpClient;
import com.lilincpp.github.libezftp.EZFtpFile;
import com.lilincpp.github.libezftp.callback.OnEZCallBack;

import java.util.List;

public class FtpClientActivity extends AppCompatActivity {

    private static final String TAG = "FtpClientActivity";

    private EZFtpClient ftpClient;
    private ActivityFtpClientBinding binding;
    private FtpFilesAdapter ftpFilesAdapter;
    private String curDirPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ftp_client);
        initView();
    }

    private void initView() {
        ftpFilesAdapter = new FtpFilesAdapter();
        ftpFilesAdapter.setOnItemClickListener(onItemClickListener);
        binding.rvDirList.setAdapter(ftpFilesAdapter);
        binding.rvDirList.setLayoutManager(new LinearLayoutManager(this));

        binding.btnConnect.setOnClickListener(onClickListener);
        binding.btnDisconnect.setOnClickListener(onClickListener);
    }

    private void updateCurDir(String path) {
        curDirPath = path;
        binding.tvCurDirPath.setText(curDirPath);
    }

    private void connectFtpServer() {
        ftpClient = new EZFtpClient();
        final String serverId = NetworkUtils.getServerAddressByWifi();
        ftpClient.connect(
                serverId,
                FtpConfig.DEFAULT_PORT,
                FtpConfig.DEFAULT_USER,
                FtpConfig.DEFAULT_PASSWORD,
                new OnEZCallBack<Void>() {
                    @Override
                    public void onSuccess(Void response) {
                        Log.d(TAG, "connectFtpServer onSuccess: ");

                        ftpClient.getCurDirPath(new OnEZCallBack<String>() {
                            @Override
                            public void onSuccess(String response) {
                                Log.d(TAG, "getCurDirPath onSuccess: ");
                                updateCurDir(response);
                            }

                            @Override
                            public void onFail(int code, String msg) {
                                Log.d(TAG, "getCurDirPath onFail: ");
                            }
                        });

                        ftpClient.getCurDirFileList(new OnEZCallBack<List<EZFtpFile>>() {
                            @Override
                            public void onSuccess(List<EZFtpFile> response) {
                                Log.d(TAG, "getCurDirFileList onSuccess: ");
                                ftpFilesAdapter.setFtpFiles(response);
                            }

                            @Override
                            public void onFail(int code, String msg) {
                                Log.d(TAG, "getCurDirFileList onFail: ");
                            }
                        });
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        Log.d(TAG, "connectFtpServer onFail: code = " + code + ",msg = " + msg);
                    }
                }
        );
    }

    private void disconnectFtpServer() {
        if (ftpClient != null) {
            ftpClient.disconnect(new OnEZCallBack<Void>() {
                @Override
                public void onSuccess(Void response) {
                    Log.d(TAG, "disconnectFtpServer onSuccess: ");
                }

                @Override
                public void onFail(int code, String msg) {
                    Log.d(TAG, "disconnectFtpServer onFail: ");
                }
            });
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_connect:
                    connectFtpServer();
                    break;
                case R.id.btn_disconnect:
                    disconnectFtpServer();
                    break;
                default:
                    break;
            }
        }
    };

    private FtpFilesAdapter.OnItemClickListener onItemClickListener = new FtpFilesAdapter.OnItemClickListener() {
        @Override
        public void onClick(EZFtpFile ftpFile) {
            //click item
            if (ftpFile.getType() == EZFtpFile.TYPE_DIRECTORY) {
                ftpClient.changeDirectory(ftpFile.getRemotePath() + "/" + ftpFile.getName(), new OnEZCallBack<String>() {
                    @Override
                    public void onSuccess(String response) {
                        Log.d(TAG, "onSuccess: response = " + response);
                        updateCurDir(response);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        Log.d(TAG, "onFail: msg = " + msg);
                    }
                });
            }
        }
    };
}
