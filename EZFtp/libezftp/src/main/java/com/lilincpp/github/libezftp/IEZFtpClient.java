package com.lilincpp.github.libezftp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lilincpp.github.libezftp.callback.OnEZCallBack;

import java.util.List;

interface IEZFtpClient {

    void connect(@NonNull String serverIp,
                 @NonNull int port,
                 @NonNull String userName,
                 @NonNull String password);

    void connect(@NonNull String serverIp,
                 @NonNull int port,
                 @NonNull String userName,
                 @NonNull String password,
                 @Nullable OnEZCallBack<Void> callBack);

    void disconnect();

    void disconnect(@Nullable OnEZCallBack<Void> callBack);

    boolean isConnected();

    ////////////////////////////////////////

    void getCurDirFileList(@Nullable OnEZCallBack<List<EZFtpFile>> callBack);

    void getCurDirPath(@Nullable OnEZCallBack<String> callBack);

    void changeDirectory(@NonNull String path, @Nullable OnEZCallBack<String> callBack);

    void backup(@Nullable OnEZCallBack<String> callBack);

    void downloadFile(@NonNull String remoteName, @NonNull String localFilePath);

    void uploadFile(@NonNull String localFilePath, @NonNull String remotePath);

    ////////////////////////////////////////
}
