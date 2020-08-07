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

/**
 * FTP客户端演示代码
 */
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
        binding.btnBackup.setOnClickListener(onClickListener);
    }

    private void updatePathView(String path) {
        curDirPath = path;
        binding.tvCurDirPath.setText(path);
        binding.btnBackup.setVisibility(ftpClient.curDirIsHomeDir() ? View.INVISIBLE : View.VISIBLE);
    }

    private void updateCurDir() {
        ftpClient.getCurDirPath(new OnEZCallBack<String>() {
            @Override
            public void onSuccess(String response) {
                Log.d(TAG, "getCurDirPath onSuccess: ");
                updatePathView(response);
            }

            @Override
            public void onFail(int code, String msg) {
                Log.d(TAG, "getCurDirPath onFail: ");
            }
        });
    }

    private void updateFileList() {
        if (ftpClient != null) {
            ftpClient.getCurDirFileList(new OnEZCallBack<List<EZFtpFile>>() {
                @Override
                public void onSuccess(List<EZFtpFile> response) {
                    ftpFilesAdapter.setFtpFiles(response);
                }

                @Override
                public void onFail(int code, String msg) {

                }
            });
        }
    }

    private void connectFtpServer() {
        ftpClient = new EZFtpClient();
        final String serverId = NetworkUtils.getServerAddressByWifi();
        ftpClient.connect(
                "10.60.226.64",
                FtpConfig.DEFAULT_PORT,
                FtpConfig.DEFAULT_USER,
                FtpConfig.DEFAULT_PASSWORD,
                new OnEZCallBack<Void>() {
                    @Override
                    public void onSuccess(Void response) {
                        Log.d(TAG, "connectFtpServer onSuccess: ");

                        updateCurDir();
                        updateFileList();
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
                case R.id.btn_backup:
                    if (ftpClient != null) {
                        ftpClient.backup(new OnEZCallBack<String>() {
                            @Override
                            public void onSuccess(String response) {
                                updatePathView(response);
                                updateFileList();
                            }

                            @Override
                            public void onFail(int code, String msg) {

                            }
                        });
                    }
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
                        updatePathView(response);
                        updateFileList();
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
