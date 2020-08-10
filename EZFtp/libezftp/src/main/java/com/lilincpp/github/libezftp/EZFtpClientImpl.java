package com.lilincpp.github.libezftp;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lilincpp.github.libezftp.callback.OnEZFtpCallBack;
import com.lilincpp.github.libezftp.callback.OnEZFtpDataTransferCallback;
import com.lilincpp.github.libezftp.exceptions.EZNoInitException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPFile;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.FTPListParseException;

/**
 * FTP客户端具体实现类
 *
 * @author lilin
 */
final class EZFtpClientImpl implements IEZFtpClient {

    private static final String TAG = "EZFtpClientImpl";
    private static final String HOME_DIR = "/";

    private FTPClient ftpClient;
    private HandlerThread taskThread = new HandlerThread("ftp-task");
    private Handler taskHandler;
    private final Object lock = new Object();
    private boolean isInit = false;
    private String curDirPath;

    EZFtpClientImpl() {
        init();
    }

    private void setCurDirPath(String path) {
        synchronized (lock) {
            this.curDirPath = path;
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
                taskHandler = new Handler(temp.getLooper());
            }
            //初始化FTP客户端
            ftpClient = new FTPClient();
            isInit = true;
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
            if (taskHandler != null) {
                taskHandler.removeCallbacksAndMessages(null);
            }
            isInit = false;
        }
    }

    private void checkInit() {
        if (!isInit) {
            throw new EZNoInitException("EZFtpClient is not init or has been released！");
        }
    }

    private String getBackUpPath() {
        if (TextUtils.isEmpty(curDirPath)) {
            return null;
        }

        //此时是根目录
        if (curDirPath.length() == 1) {
            return curDirPath;
        }

        //获取最后一个文件符斜杠的下标
        final int lastIndex = curDirPath.lastIndexOf("/");
        //substring不会包含最后一个字符，因此如果是[/lilin]
        if (lastIndex == 0) {
            return "/";
        }
        return curDirPath.substring(0, lastIndex);

    }

    @SuppressWarnings("unchecked")
    private void callbackNormalSuccess(@Nullable final OnEZFtpCallBack callBack, @Nullable final Object response) {
        EZFtpSampleCallbackWrapper wrapper = new EZFtpSampleCallbackWrapper(callBack);
        wrapper.onSuccess(response);
    }

    @SuppressWarnings("unchecked")
    private void callbackNormalFail(@Nullable final OnEZFtpCallBack callBack, final int code, final String msg) {
        EZFtpSampleCallbackWrapper wrapper = new EZFtpSampleCallbackWrapper(callBack);
        wrapper.onFail(code, msg);
    }


    @Override
    public void connect(@NonNull final String serverIp, @NonNull final int port, @NonNull final String userName, @NonNull final String password) {
        connect(serverIp, port, userName, password, null);
    }

    @Override
    public void connect(@NonNull final String serverIp, @NonNull final int port, @NonNull final String userName, @NonNull final String password, @Nullable final OnEZFtpCallBack<Void> callBack) {
        checkInit();
        Log.d(TAG, "connect ftp server : serverIp = " + serverIp + ",port = " + port
                + ",user = " + userName + ",pw = " + password);
        taskHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    ftpClient.connect(serverIp, port);
                    ftpClient.login(userName, password);
                    getCurDirPath(null);
                    callbackNormalSuccess(callBack, null);
                } catch (IOException e) {
                    callbackNormalFail(callBack, EZFtpResult.RESULT_EXCEPTION, "IOException");
                } catch (FTPIllegalReplyException e) {
                    callbackNormalFail(callBack, EZFtpResult.RESULT_FAIL, "Read server response fail!");
                } catch (FTPException e) {
                    callbackNormalFail(callBack, EZFtpResult.RESULT_EXCEPTION, e.getMessage());
                } catch (IllegalStateException e) {
                    callbackNormalFail(callBack, EZFtpResult.RESULT_EXCEPTION, e.getMessage());
                }
            }
        });
    }

    @Override
    public void disconnect() {
        disconnect(null);
        release();
    }

    @Override
    public void disconnect(@Nullable final OnEZFtpCallBack<Void> callBack) {
        checkInit();
        taskHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    ftpClient.disconnect(true);
                    callbackNormalSuccess(callBack, null);
                } catch (IOException e) {
                    callbackNormalFail(callBack, EZFtpResult.RESULT_EXCEPTION, "IOException");
                } catch (FTPIllegalReplyException e) {
                    callbackNormalFail(callBack, EZFtpResult.RESULT_FAIL, "Read server response fail!");
                } catch (FTPException e) {
                    callbackNormalFail(callBack, EZFtpResult.RESULT_EXCEPTION, e.getMessage());
                } catch (IllegalStateException e) {
                    callbackNormalFail(callBack, EZFtpResult.RESULT_EXCEPTION, e.getMessage());
                }
            }
        });
    }

    @Override
    public boolean isConnected() {
        return ftpClient != null && ftpClient.isConnected();
    }

    @Override
    public void getCurDirFileList(@Nullable final OnEZFtpCallBack<List<EZFtpFile>> callBack) {
        checkInit();
        taskHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    FTPFile[] ftpFiles = ftpClient.list();
                    List<EZFtpFile> ezFtpFiles = new ArrayList<>();
                    for (FTPFile ftpFile : ftpFiles) {
                        ezFtpFiles.add(
                                new EZFtpFile(
                                        ftpFile.getName(),
                                        curDirPath,
                                        ftpFile.getType(),
                                        ftpFile.getSize(),
                                        ftpFile.getModifiedDate()
                                ));
                    }
                    callbackNormalSuccess(callBack, ezFtpFiles);
                } catch (IOException e) {
                    callbackNormalFail(callBack, EZFtpResult.RESULT_EXCEPTION, "IOException");
                } catch (FTPIllegalReplyException e) {
                    callbackNormalFail(callBack, EZFtpResult.RESULT_FAIL, "Read server response fail!");
                } catch (FTPException e) {
                    callbackNormalFail(callBack, EZFtpResult.RESULT_EXCEPTION, e.getMessage());
                } catch (IllegalStateException e) {
                    callbackNormalFail(callBack, EZFtpResult.RESULT_EXCEPTION, e.getMessage());
                } catch (FTPDataTransferException e) {
                    callbackNormalFail(callBack, EZFtpResult.RESULT_EXCEPTION, e.getMessage());
                } catch (FTPAbortedException e) {
                    callbackNormalFail(callBack, EZFtpResult.RESULT_EXCEPTION, e.getMessage());
                } catch (FTPListParseException e) {
                    callbackNormalFail(callBack, EZFtpResult.RESULT_EXCEPTION, e.getMessage());
                }
            }
        });
    }

    @Override
    public void getCurDirPath(@Nullable final OnEZFtpCallBack<String> callBack) {
        checkInit();
        taskHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    final String path = ftpClient.currentDirectory();
                    setCurDirPath(path);
                    callbackNormalSuccess(callBack, path);
                } catch (IOException e) {
                    callbackNormalFail(callBack, EZFtpResult.RESULT_EXCEPTION, "IOException");
                } catch (FTPIllegalReplyException e) {
                    callbackNormalFail(callBack, EZFtpResult.RESULT_FAIL, "Read server response fail!");
                } catch (FTPException e) {
                    callbackNormalFail(callBack, EZFtpResult.RESULT_EXCEPTION, e.getMessage());
                } catch (IllegalStateException e) {
                    callbackNormalFail(callBack, EZFtpResult.RESULT_EXCEPTION, e.getMessage());
                }
            }
        });
    }

    @Override
    public void changeDirectory(@Nullable final String path, @Nullable final OnEZFtpCallBack<String> callBack) {
        checkInit();
        taskHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (TextUtils.isEmpty(path)) {
                        callbackNormalFail(callBack, EZFtpResult.RESULT_FAIL, "path is empty!");
                    } else {
                        ftpClient.changeDirectory(path);
                        setCurDirPath(path);
                        callbackNormalSuccess(callBack, path);
                    }
                } catch (IOException e) {
                    callbackNormalFail(callBack, EZFtpResult.RESULT_EXCEPTION, "IOException");
                } catch (FTPIllegalReplyException e) {
                    callbackNormalFail(callBack, EZFtpResult.RESULT_FAIL, "Read server response fail!");
                } catch (FTPException e) {
                    callbackNormalFail(callBack, EZFtpResult.RESULT_EXCEPTION, e.getMessage());
                } catch (IllegalStateException e) {
                    callbackNormalFail(callBack, EZFtpResult.RESULT_EXCEPTION, e.getMessage());
                }
            }
        });
    }

    @Override
    public void backup(@Nullable final OnEZFtpCallBack<String> callBack) {
        changeDirectory(getBackUpPath(), callBack);
    }

    @Override
    public void backToHomeDir(OnEZFtpCallBack<String> callBack) {
        changeDirectory(HOME_DIR, callBack);
    }

    @Override
    public void downloadFile(@NonNull final EZFtpFile remoteFile, @NonNull String localFilePath, @Nullable OnEZFtpDataTransferCallback callback) {
        //TODO
        checkInit();

        final File localFile = new File(localFilePath);
        final EZFtpTransferCallbackWrapper callbackWrapper
                = new EZFtpTransferCallbackWrapper(callback);

        taskHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    ftpClient.download(remoteFile.getName(), localFile, new FTPDataTransferListener() {
                        @Override
                        public void started() {
                            callbackWrapper.onStateChanged(OnEZFtpDataTransferCallback.START);
                        }

                        @Override
                        public void transferred(int i) {
                            callbackWrapper.onTransferred(remoteFile.getSize(),i);
                        }

                        @Override
                        public void completed() {
                            callbackWrapper.onStateChanged(OnEZFtpDataTransferCallback.COMPLETE);
                        }

                        @Override
                        public void aborted() {
                            callbackWrapper.onStateChanged(OnEZFtpDataTransferCallback.COMPLETE);
                        }

                        @Override
                        public void failed() {
                            callbackWrapper.onStateChanged(OnEZFtpDataTransferCallback.ERROR);
                            callbackWrapper.onErr(EZFtpResult.RESULT_FAIL, "Download file fail!");
                        }
                    });
                } catch (IOException e) {
                    callbackWrapper.onStateChanged(OnEZFtpDataTransferCallback.ERROR);
                    callbackWrapper.onErr(EZFtpResult.RESULT_EXCEPTION, "IOException");
                } catch (FTPIllegalReplyException e) {
                    e.printStackTrace();
                } catch (FTPException e) {
                    e.printStackTrace();
                } catch (FTPDataTransferException e) {
                    e.printStackTrace();
                } catch (FTPAbortedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void uploadFile(@NonNull String localFilePath, @NonNull String remotePath, @Nullable OnEZFtpDataTransferCallback callback) {
        //TODO
    }

    @Override
    public boolean curDirIsHomeDir() {
        return TextUtils.equals(curDirPath, HOME_DIR);
    }
}
