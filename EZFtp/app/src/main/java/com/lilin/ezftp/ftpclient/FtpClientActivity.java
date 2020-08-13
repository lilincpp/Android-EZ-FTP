package com.lilin.ezftp.ftpclient;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.constant.MemoryConstants;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.lilin.ezftp.FtpConfig;
import com.lilin.ezftp.R;
import com.lilin.ezftp.databinding.ActivityFtpClientBinding;
import com.lilincpp.github.libezftp.EZFtpClient;
import com.lilincpp.github.libezftp.EZFtpFile;
import com.lilincpp.github.libezftp.callback.EZFtpTransferSpeedCallback;
import com.lilincpp.github.libezftp.callback.OnEZFtpCallBack;

import java.util.List;
import java.util.Locale;

/**
 * FTP客户端演示代码
 * FTP Client demo
 *
 * @author lilin
 */
public class FtpClientActivity extends AppCompatActivity {

    private static final String TAG = "FtpClientActivity";

    private static final String SAVE_FILE_PATH
            = PathUtils.getExternalAppFilesPath();

    private EZFtpClient ftpClient;
    private ActivityFtpClientBinding binding;
    private FtpFilesAdapter ftpFilesAdapter;

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
        binding.tvCurDirPath.setText(path);
        binding.btnBackup.setVisibility(ftpClient.curDirIsHomeDir() ? View.INVISIBLE : View.VISIBLE);
    }

    private void updateOutputMsg(String msg) {
        final String oldMsg = binding.tvMsg.getText().toString();
        binding.tvMsg.setText(oldMsg + "\n" + msg);
    }

    /**
     * request ftp server cur dir path
     */
    private void requestFtpCurDirPath() {
        ftpClient.getCurDirPath(new OnEZFtpCallBack<String>() {
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

    /**
     * request ftp server file list
     */
    private void requestFtpFileList() {
        if (ftpClient != null) {
            ftpClient.getCurDirFileList(new OnEZFtpCallBack<List<EZFtpFile>>() {
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

    /**
     * connect ftp server
     */
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
                new OnEZFtpCallBack<Void>() {
                    @Override
                    public void onSuccess(Void response) {
                        updateOutputMsg("Connect success!");
                        requestFtpCurDirPath();
                        requestFtpFileList();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        Log.e(TAG, "connectFtpServer onFail: code = " + code + ",msg = " + msg);
                        updateOutputMsg("Connect Fail,Err:" + msg);
                    }
                }
        );
    }

    /**
     * disconnect ftp server
     */
    private void disconnectFtpServer() {
        if (ftpClient != null) {
            ftpClient.disconnect(new OnEZFtpCallBack<Void>() {
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

    /**
     * the layout item click event
     */
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
                        ftpClient.backup(new OnEZFtpCallBack<String>() {
                            @Override
                            public void onSuccess(String response) {
                                updateOutputMsg("Backup success!");
                                updateCurDirPathView(response);
                                requestFtpFileList();
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

    /**
     * List item click event
     */
    private FtpFilesAdapter.OnItemClickListener onItemClickListener = new FtpFilesAdapter.OnItemClickListener() {
        @Override
        public void onClick(final EZFtpFile ftpFile) {
            //click item
            if (ftpFile.getType() == EZFtpFile.TYPE_DIRECTORY) {
                //if type is dir changed remote path
                final String targetPath;
                final String remoteFileDirPath = ftpFile.getRemotePath();
                if (remoteFileDirPath.endsWith("/")) {
                    targetPath = remoteFileDirPath + ftpFile.getName();
                } else {
                    targetPath = remoteFileDirPath + "/" + ftpFile.getName();
                }
                ftpClient.changeDirectory(targetPath, new OnEZFtpCallBack<String>() {
                    @Override
                    public void onSuccess(String response) {
                        updateOutputMsg("Changed ftp dir[" + targetPath + "] success!");
                        updateCurDirPathView(response);
                        requestFtpFileList();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        Log.e(TAG, "onFail: msg = " + msg);
                        updateOutputMsg("Changed ftp dir[" + targetPath + "] fail,Err:" + msg);
                    }
                });
            } else {
                //file or link
                String msg = getString((R.string.download_file_tips)) + "(" + ftpFile.getName() + ")";
                new AlertDialog.Builder(FtpClientActivity.this)
                        .setMessage(msg)
                        .setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                downloadFile(ftpFile);
                            }
                        })
                        .setPositiveButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            }
        }
    };

    @SuppressLint("MissingPermission")
    private void downloadFile(final EZFtpFile ftpFile) {
        if (ftpClient != null) {
            final String path = SAVE_FILE_PATH + "/" + ftpFile.getName();
            ftpClient.downloadFile(ftpFile, path, new EZFtpTransferSpeedCallback() {
                @Override
                public void onTransferSpeed(boolean isFinished, long startTime, long endTime, double speed, double averageSpeed) {
                    updateDownloadDialog(ftpFile, path, isFinished, startTime, endTime, speed, averageSpeed);
                }
            });
        }
    }

    private AlertDialog downloadAlertDialog;

    private void updateDownloadDialog(EZFtpFile ftpFile, String savePath, boolean isFinished, long startTime, long endTime, double speed, double averageSpeed) {
        if (downloadAlertDialog == null) {
            downloadAlertDialog = new AlertDialog.Builder(this)
                    .setCancelable(true)
                    .create();
        }

        StringBuilder builder = new StringBuilder();

        if (isFinished) {
            builder.append("Completed download!")
                    .append("\n")
                    .append(ftpFile.getName());
        } else {
            builder.append("Downloading")
                    .append("\n")
                    .append(ftpFile.getName());
        }

        builder.append("\nFile Size :")
                .append(ConvertUtils.byte2MemorySize(ftpFile.getSize(), MemoryConstants.MB))
                .append("MB")
                .append("\n")
                .append("File remote path : ")
                .append(ftpFile.getRemotePath())
                .append("\n")
                .append("Local save path : ")
                .append(savePath);

        builder.append("\n\n")
                .append("Start time : ")
                .append(TimeUtils.millis2String(startTime))
                .append("\n")
                .append("End time : ")
                .append(TimeUtils.millis2String(endTime));

        if (isFinished) {
            final long time = endTime - startTime;
            builder.append("\nSpent time : ")
                    .append(ConvertUtils.millis2FitTimeSpan(time, 4))
                    .append("\n")
                    .append("averageSpeed : ")
                    .append(String.format(Locale.CHINA, "%.2fKB/S", averageSpeed));
        }

        builder.append("\n\n")
                .append("Now speed : ")
                .append(String.format(Locale.CHINA, "%.2fKB/S", speed));


        if (!downloadAlertDialog.isShowing()) {
            downloadAlertDialog.show();
        }
    }
}
