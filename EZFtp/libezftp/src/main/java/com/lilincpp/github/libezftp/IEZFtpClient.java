package com.lilincpp.github.libezftp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lilincpp.github.libezftp.callback.OnEZCallBack;

import java.util.List;

/**
 * FTP 客户端操作接口
 * @author lilin
 */
interface IEZFtpClient {

    /**
     * Connect FTP Server
     * @param serverIp ftp server ip
     * @param port ftp server port
     * @param userName login username
     * @param password login password
     */
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

    ////////////////////////////////////////

    void getCurDirFileList(@Nullable OnEZCallBack<List<EZFtpFile>> callBack);

    void getCurDirPath(@Nullable OnEZCallBack<String> callBack);

    void changeDirectory(@NonNull String path, @Nullable OnEZCallBack<String> callBack);

    void backup(@Nullable OnEZCallBack<String> callBack);

    void downloadFile(@NonNull String remoteName, @NonNull String localFilePath);

    void uploadFile(@NonNull String localFilePath, @NonNull String remotePath);

    ////////////////////////////////////////

    boolean isConnected();

    boolean curDirIsHomeDir();

    void backToHomeDir(OnEZCallBack<String> callBack);

    ////////////////////////////////////////
}
