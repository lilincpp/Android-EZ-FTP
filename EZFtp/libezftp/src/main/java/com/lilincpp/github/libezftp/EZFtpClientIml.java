package com.lilincpp.github.libezftp;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lilincpp.github.libezftp.callback.OnEZCallBack;

import java.io.IOException;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;

class EZFtpClientIml implements IEZFtpClient {

    private FTPClient ftpClient;
    private HandlerThread taskThread = new HandlerThread("ftp-task");
    private FTPTaskHandler handler;
    private final Object lock = new Object();

    private static final class FTPTaskHandler extends Handler {

        public FTPTaskHandler(@NonNull Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
        }
    }

    /**
     * 初始化工作线程
     */
    private void init() {
        synchronized (lock) {
            //初始化工作线程
            final HandlerThread temp = taskThread;
            if (!temp.isAlive()) {
                temp.start();
                handler = new FTPTaskHandler(temp.getLooper());
            }
            //初始化FTP客户端
            ftpClient = new FTPClient();
        }
    }

    /**
     * 释放资源
     */
    private void release() {
        synchronized (lock) {

            //检查FTP客户端是否仍然连接
            //如果仍然在连接的话，则先断开
            if (ftpClient != null && isConnected()) {
                disconnect();
            }

            //释放工作线程
            final HandlerThread temp = taskThread;
            if (temp.isAlive()) {
                temp.quit();
            }
        }
    }


    @Override
    public void connect(@NonNull String serverIp, @NonNull int port) {

    }

    @Override
    public void connect(@NonNull String serverIp, @NonNull int port, @Nullable OnEZCallBack<EZResult> callBack) {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public void disconnect(@Nullable OnEZCallBack<EZResult> callBack) {

    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public void getCurDirList(@NonNull OnEZCallBack<EZFtpFile[]> callBack) {

    }

    @Override
    public void getCurDirPath(@NonNull OnEZCallBack<String> callBack) {

    }

    @Override
    public void changeDirectory(@NonNull String path, @Nullable OnEZCallBack<EZResult> callBack) {

    }

    @Override
    public void backup(@Nullable OnEZCallBack<EZResult> callBack) {

    }

    @Override
    public void downloadFile(@NonNull String remoteName, @NonNull String localFilePath) {

    }

    @Override
    public void uploadFile(@NonNull String localFilePath, @NonNull String remotePath) {

    }
}
