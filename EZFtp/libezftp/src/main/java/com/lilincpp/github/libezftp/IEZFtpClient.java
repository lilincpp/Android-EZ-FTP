package com.lilincpp.github.libezftp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lilincpp.github.libezftp.callback.OnEZCallBack;

interface IEZFtpClient {

    void connect(@NonNull String serverIp, @NonNull int port);

    void connect(@NonNull String serverIp, @NonNull int port, @NonNull OnEZCallBack<EZResult> callBack);

    void login(String user, String password, OnEZCallBack<EZResult> callBack);

    void disconnect();

    void disconnect(@Nullable OnEZCallBack<EZResult> callBack);

    boolean isConnected();

    ////////////////////////////////////////

    void getCurDirList(@NonNull OnEZCallBack<EZFtpFile[]> callBack);

    void getCurDirPath(@NonNull OnEZCallBack<String> callBack);

    void changeDirectory(@NonNull String path, @Nullable OnEZCallBack<EZResult> callBack);

    void backup(@Nullable OnEZCallBack<EZResult> callBack);

    void downloadFile(@NonNull String remoteName, @NonNull String localFilePath);

    void uploadFile(@NonNull String localFilePath, @NonNull String remotePath);

    ////////////////////////////////////////
}
