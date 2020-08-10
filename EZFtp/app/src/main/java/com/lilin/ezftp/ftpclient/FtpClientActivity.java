package com.lilin.ezftp.ftpclient;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.NetworkUtils;
import com.lilin.ezftp.FtpConfig;
import com.lilin.ezftp.R;
import com.lilin.ezftp.databinding.ActivityFtpClientBinding;
import com.lilincpp.github.libezftp.EZFtpClient;
import com.lilincpp.github.libezftp.EZFtpFile;
import com.lilincpp.github.libezftp.callback.OnEZCallBack;

import java.util.List;

/**
 * FTP客户端演示代码
 * FTP Client demo
 *
 * @author lilin
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

    @Override
    protected void onStop() {
        if (ftpClient != null) {
            ftpClient.disconnect();
        }
        super.onStop();
    }

    private void initView() {
        ftpFilesAdapter = new FtpFilesAdapter();
        ftpFilesAdapter.setOnItemClickListener(onItemClickListener);
        binding.rvDirList.setAdapter(ftpFilesAdapter);
        binding.rvDirList.setLayoutManager(new LinearLayoutManager(this));

        binding.btnConnect.setOnClickListener(onClickListener);
        binding.btnDisconnect.setOnClickListener(onClickListener);
        binding.btnBackup.setOnClickListener(onClickListener);

        binding.etServerIp.setText(NetworkUtils.getServerAddressByWifi());
        binding.etServerPort.setText(FtpConfig.DEFAULT_PORT);
        binding.etUsername.setText(FtpConfig.DEFAULT_USER);
        binding.etPassword.setText(FtpConfig.DEFAULT_PASSWORD);

        binding.tvMsg.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    private void updateCurDirPathView(String path) {
        curDirPath = path;
        binding.tvCurDirPath.setText(path);
        binding.btnBackup.setVisibility(ftpClient.curDirIsHomeDir() ? View.INVISIBLE : View.VISIBLE);
    }

    private void updateOutputMsg(String msg) {
        final String oldMsg = binding.tvMsg.getText().toString();
        binding.tvMsg.setText(oldMsg + "\n" + msg);
    }

    private void updateCurDirPath() {
        ftpClient.getCurDirPath(new OnEZCallBack<String>() {
            @Override
            public void onSuccess(String response) {
                updateOutputMsg("update remote dir success");
                updateCurDirPathView(response);
            }

            @Override
            public void onFail(int code, String msg) {
                Log.e(TAG, "getCurDirPath onFail: ");
                updateOutputMsg("update remote dir fail,Err:" + msg);
            }
        });
    }

    private void updateFileList() {
        if (ftpClient != null) {
            ftpClient.getCurDirFileList(new OnEZCallBack<List<EZFtpFile>>() {
                @Override
                public void onSuccess(List<EZFtpFile> response) {
                    updateOutputMsg("update remote file list success");
                    ftpFilesAdapter.setFtpFiles(response);
                }

                @Override
                public void onFail(int code, String msg) {
                    updateOutputMsg("update remote file list fail,Err:" + msg);
                }
            });
        }
    }

    private void connectFtpServer() {
        ftpClient = new EZFtpClient();
        //获取热点的IP地址
        final String serverIp = binding.etServerIp.getText().toString();
        final String serverPort = binding.etServerPort.getText().toString();
        final String username = binding.etUsername.getText().toString();
        final String password = binding.etPassword.getText().toString();

        updateOutputMsg("Login username:" + username);
        updateOutputMsg("Login password:" + password);
        updateOutputMsg("Connecting to server[" + serverIp + ":" + serverPort + "]");

        ftpClient.connect(
                serverIp,
                FtpConfig.DEFAULT_PORT,
                FtpConfig.DEFAULT_USER,
                FtpConfig.DEFAULT_PASSWORD,
                new OnEZCallBack<Void>() {
                    @Override
                    public void onSuccess(Void response) {
                        updateOutputMsg("Connect success!");
                        updateCurDirPath();
                        updateFileList();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        Log.e(TAG, "connectFtpServer onFail: code = " + code + ",msg = " + msg);
                        updateOutputMsg("Connect Fail,Err:" + msg);
                    }
                }
        );
    }

    private void disconnectFtpServer() {
        if (ftpClient != null) {
            ftpClient.disconnect(new OnEZCallBack<Void>() {
                @Override
                public void onSuccess(Void response) {
                    updateOutputMsg("Disconnect server!");
                }

                @Override
                public void onFail(int code, String msg) {
                    Log.e(TAG, "disconnectFtpServer onFail: ");
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
                                updateOutputMsg("Backup success!");
                                updateCurDirPathView(response);
                                updateFileList();
                            }

                            @Override
                            public void onFail(int code, String msg) {
                                updateOutputMsg("Backup fail,Err:" + msg);
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
                final String targetPath;
                final String remoteFileDirPath = ftpFile.getRemotePath();
                if (remoteFileDirPath.endsWith("/")) {
                    targetPath = remoteFileDirPath + ftpFile.getName();
                } else {
                    targetPath = remoteFileDirPath + "/" + ftpFile.getName();
                }
                ftpClient.changeDirectory(targetPath, new OnEZCallBack<String>() {
                    @Override
                    public void onSuccess(String response) {
                        updateOutputMsg("Changed ftp dir[" + targetPath + "] success!");
                        updateCurDirPathView(response);
                        updateFileList();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        Log.e(TAG, "onFail: msg = " + msg);
                        updateOutputMsg("Changed ftp dir[" + targetPath + "] fail,Err:" + msg);
                    }
                });
            } else {
                //file or link
            }
        }
    };
}
