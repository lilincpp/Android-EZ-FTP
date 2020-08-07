package com.lilincpp.github.libezftp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lilincpp.github.libezftp.callback.OnEZCallBack;

import java.util.List;

/**
 * 供外界使用的API
 *
 * @author lilin
 */
public final class EZFtpClient implements IEZFtpClient {

    private EZFtpClientIml ftpClientIml;

    @Override
    public void connect(@NonNull String serverIp, @NonNull int port, @NonNull String userName, @NonNull String password) {
        if (ftpClientIml != null) {
            ftpClientIml.connect(serverIp, port, userName, password);
        }
    }

    @Override
    public void connect(@NonNull String serverIp, @NonNull int port, @NonNull String userName, @NonNull String password, @Nullable OnEZCallBack<Void> callBack) {
        if (ftpClientIml != null) {
            ftpClientIml.connect(serverIp, port, userName, password, callBack);
        }
    }

    @Override
    public void disconnect() {
        if (ftpClientIml != null) {
            ftpClientIml.disconnect();
        }
    }

    @Override
    public void disconnect(@Nullable OnEZCallBack<Void> callBack) {
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
    public void getCurDirFileList(@Nullable OnEZCallBack<List<EZFtpFile>> callBack) {
        if (ftpClientIml != null) {
            ftpClientIml.getCurDirFileList(callBack);
        }
    }

    @Override
    public void getCurDirPath(@Nullable OnEZCallBack<String> callBack) {
        if (ftpClientIml != null) {
            ftpClientIml.getCurDirPath(callBack);
        }
    }

    @Override
    public void changeDirectory(@NonNull String path, @Nullable OnEZCallBack<Void> callBack) {
        if (ftpClientIml != null) {
            ftpClientIml.changeDirectory(path, callBack);
        }
    }

    @Override
    public void backup(@Nullable OnEZCallBack<Void> callBack) {
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
