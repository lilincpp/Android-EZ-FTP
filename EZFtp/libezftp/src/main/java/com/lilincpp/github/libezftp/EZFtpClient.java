package com.lilincpp.github.libezftp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lilincpp.github.libezftp.callback.OnEZCallBack;

/**
 * 供外界使用的API
 *
 * @author lilin
 */
public final class EZFtpClient implements IEZFtpClient {

    private EZFtpClientIml ftpClientIml;


    @Override
    public void connect(@NonNull String serverIp, @NonNull int port) {
        if (ftpClientIml != null) {
            ftpClientIml.connect(serverIp, port);
        }
    }

    @Override
    public void connect(@NonNull String serverIp, @NonNull int port, @Nullable OnEZCallBack<EZResult> callBack) {
        if (ftpClientIml != null) {
            ftpClientIml.connect(serverIp, port, callBack);
        }
    }

    @Override
    public void disconnect() {
        if (ftpClientIml != null) {
            ftpClientIml.disconnect();
        }
    }

    @Override
    public void disconnect(@Nullable OnEZCallBack<EZResult> callBack) {
        if (ftpClientIml != null) {
            ftpClientIml.disconnect(callBack);
        }
    }

    @Override
    public boolean isConnected() {
        if (ftpClientIml != null) {
            ftpClientIml.isConnected();
        }
        return false;
    }

    @Override
    public void getCurDirList(@NonNull OnEZCallBack<EZFtpFile[]> callBack) {
        if (ftpClientIml != null) {
            ftpClientIml.getCurDirList(callBack);
        }
    }

    @Override
    public void getCurDirPath(@NonNull OnEZCallBack<String> callBack) {
        if (ftpClientIml != null) {
            ftpClientIml.getCurDirPath(callBack);
        }
    }

    @Override
    public void changeDirectory(@NonNull String path, @Nullable OnEZCallBack<EZResult> callBack) {
        if (ftpClientIml != null) {
            ftpClientIml.changeDirectory(path, callBack);
        }
    }

    @Override
    public void backup(@Nullable OnEZCallBack<EZResult> callBack) {
        if (ftpClientIml != null) {
            ftpClientIml.backup(callBack);
        }
    }

    @Override
    public void downloadFile(@NonNull String remoteName, @NonNull String localFilePath) {
        if (ftpClientIml != null) {
            ftpClientIml.downloadFile(remoteName, localFilePath);
        }
    }

    @Override
    public void uploadFile(@NonNull String localFilePath, @NonNull String remotePath) {
        if (ftpClientIml != null) {
            ftpClientIml.uploadFile(localFilePath, remotePath);
        }
    }
}
