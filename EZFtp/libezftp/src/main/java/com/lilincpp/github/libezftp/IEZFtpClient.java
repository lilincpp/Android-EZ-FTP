package com.lilincpp.github.libezftp;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lilincpp.github.libezftp.callback.OnEZFtpCallBack;
import com.lilincpp.github.libezftp.callback.OnEZFtpDataTransferCallback;

import java.util.List;

/**
 * FTP 客户端操作接口
 *
 * @author lilin
 */
interface IEZFtpClient {

    /**
     * Connect FTP Server
     *
     * @param serverIp ftp server ip
     * @param port     ftp server port
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
                 @Nullable OnEZFtpCallBack<Void> callBack);

    void disconnect();

    void disconnect(@Nullable OnEZFtpCallBack<Void> callBack);

    ////////////////////////////////////////

    void getCurDirFileList(@Nullable OnEZFtpCallBack<List<EZFtpFile>> callBack);

    void getCurDirPath(@Nullable OnEZFtpCallBack<String> callBack);

    void changeDirectory(@NonNull String path, @Nullable OnEZFtpCallBack<String> callBack);

    void backup(@Nullable OnEZFtpCallBack<String> callBack);

    void downloadFile(@NonNull EZFtpFile remoteFile, @NonNull String localFilePath, @Nullable OnEZFtpDataTransferCallback callback);

    void uploadFile(@NonNull String localFilePath, @Nullable OnEZFtpDataTransferCallback callback);

    ////////////////////////////////////////

    boolean isConnected();

    boolean curDirIsHomeDir();

    void backToHomeDir(OnEZFtpCallBack<String> callBack);

    ////////////////////////////////////////

    void release();
}
