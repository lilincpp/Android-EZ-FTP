package com.lilincpp.github.libezftp;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lilincpp.github.libezftp.callback.OnEZCallBack;
import com.lilincpp.github.libezftp.exceptions.EZNoInitException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPFile;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.FTPListParseException;

/**
 * FTP客户端具体实现类
 */
class EZFtpClientIml implements IEZFtpClient {

    private static final String HOME_DIR = "/";

    private FTPClient ftpClient;
    private HandlerThread taskThread = new HandlerThread("ftp-task");
    private Handler taskHandler;
    private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    private final Object lock = new Object();
    private boolean isInit = false;
    private String curDirPath;

    private static final class CallBackHolder {
        Object callback;
        Object response;

        public CallBackHolder(Object callback, Object response) {
            this.callback = callback;
            this.response = response;
        }
    }

    EZFtpClientIml() {
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
        }
    }

    private void checkInit() {
        if (!isInit) {
            throw new EZNoInitException("EZFtpClient is not init");
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

    private void callbackSuccess(final OnEZCallBack callBack, final Object response) {
        synchronized (lock) {
            if (callBack != null) {
                mainThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onSuccess(response);
                    }
                });
            }
        }
    }

    private void callbackFail(final OnEZCallBack callBack, final int code, final String msg) {
        synchronized (lock) {
            if (callBack != null) {
                mainThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onFail(code, msg);
                    }
                });
            }
        }
    }


    @Override
    public void connect(@NonNull final String serverIp, @NonNull final int port, @NonNull final String userName, @NonNull final String password) {
        connect(serverIp, port, userName, password, null);
    }

    @Override
    public void connect(@NonNull final String serverIp, @NonNull final int port, @NonNull final String userName, @NonNull final String password, @Nullable final OnEZCallBack<Void> callBack) {
        checkInit();
        taskHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    ftpClient.connect(serverIp, port);
                    ftpClient.login(userName, password);
                    getCurDirPath(null);
                    callbackSuccess(callBack, null);
                } catch (IOException e) {
                    callbackFail(callBack, EZResult.RESULT_EXCEPTION, "IOException");
                } catch (FTPIllegalReplyException e) {
                    callbackFail(callBack, EZResult.RESULT_FAIL, "Read server response fail!");
                } catch (FTPException e) {
                    callbackFail(callBack, EZResult.RESULT_EXCEPTION, e.getMessage());
                } catch (IllegalStateException e) {
                    callbackFail(callBack, EZResult.RESULT_EXCEPTION, e.getMessage());
                }
            }
        });
    }

    @Override
    public void disconnect() {
        disconnect(null);
    }

    @Override
    public void disconnect(@Nullable final OnEZCallBack<Void> callBack) {
        checkInit();
        taskHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    ftpClient.disconnect(true);
                    callbackSuccess(callBack, null);
                } catch (IOException e) {
                    callbackFail(callBack, EZResult.RESULT_EXCEPTION, "IOException");
                } catch (FTPIllegalReplyException e) {
                    callbackFail(callBack, EZResult.RESULT_FAIL, "Read server response fail!");
                } catch (FTPException e) {
                    callbackFail(callBack, EZResult.RESULT_EXCEPTION, e.getMessage());
                } catch (IllegalStateException e) {
                    callbackFail(callBack, EZResult.RESULT_EXCEPTION, e.getMessage());
                }
            }
        });
    }

    @Override
    public boolean isConnected() {
        return ftpClient != null && ftpClient.isConnected();
    }

    @Override
    public void getCurDirFileList(@Nullable final OnEZCallBack<List<EZFtpFile>> callBack) {
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
                    callbackSuccess(callBack, ezFtpFiles);
                } catch (IOException e) {
                    callbackFail(callBack, EZResult.RESULT_EXCEPTION, "IOException");
                } catch (FTPIllegalReplyException e) {
                    callbackFail(callBack, EZResult.RESULT_FAIL, "Read server response fail!");
                } catch (FTPException e) {
                    callbackFail(callBack, EZResult.RESULT_EXCEPTION, e.getMessage());
                } catch (IllegalStateException e) {
                    callbackFail(callBack, EZResult.RESULT_EXCEPTION, e.getMessage());
                } catch (FTPDataTransferException e) {
                    callbackFail(callBack, EZResult.RESULT_EXCEPTION, e.getMessage());
                } catch (FTPAbortedException e) {
                    callbackFail(callBack, EZResult.RESULT_EXCEPTION, e.getMessage());
                } catch (FTPListParseException e) {
                    callbackFail(callBack, EZResult.RESULT_EXCEPTION, e.getMessage());
                }
            }
        });
    }

    @Override
    public void getCurDirPath(@Nullable final OnEZCallBack<String> callBack) {
        checkInit();
        taskHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    final String path = ftpClient.currentDirectory();
                    setCurDirPath(path);
                    callbackSuccess(callBack, path);
                } catch (IOException e) {
                    callbackFail(callBack, EZResult.RESULT_EXCEPTION, "IOException");
                } catch (FTPIllegalReplyException e) {
                    callbackFail(callBack, EZResult.RESULT_FAIL, "Read server response fail!");
                } catch (FTPException e) {
                    callbackFail(callBack, EZResult.RESULT_EXCEPTION, e.getMessage());
                } catch (IllegalStateException e) {
                    callbackFail(callBack, EZResult.RESULT_EXCEPTION, e.getMessage());
                }
            }
        });
    }

    @Override
    public void changeDirectory(@NonNull final String path, @Nullable final OnEZCallBack<String> callBack) {
        checkInit();
        taskHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (TextUtils.isEmpty(path)) {
                        callbackFail(callBack, EZResult.RESULT_FAIL, "path is empty!");
                    } else {
                        ftpClient.changeDirectory(path);
                        setCurDirPath(path);
                        callbackSuccess(callBack, path);
                    }
                } catch (IOException e) {
                    callbackFail(callBack, EZResult.RESULT_EXCEPTION, "IOException");
                } catch (FTPIllegalReplyException e) {
                    callbackFail(callBack, EZResult.RESULT_FAIL, "Read server response fail!");
                } catch (FTPException e) {
                    callbackFail(callBack, EZResult.RESULT_EXCEPTION, e.getMessage());
                } catch (IllegalStateException e) {
                    callbackFail(callBack, EZResult.RESULT_EXCEPTION, e.getMessage());
                }
            }
        });
    }

    @Override
    public void backup(@Nullable final OnEZCallBack<String> callBack) {
        checkInit();
        changeDirectory(getBackUpPath(), callBack);
    }

    @Override
    public void downloadFile(@NonNull String remoteName, @NonNull String localFilePath) {
        //TODO
    }

    @Override
    public void uploadFile(@NonNull String localFilePath, @NonNull String remotePath) {
        //TODO
    }

    @Override
    public boolean curDirIsHomeDir() {
        return TextUtils.equals(curDirPath, HOME_DIR);
    }

    @Override
    public void backToHomeDir(OnEZCallBack<String> callBack) {
        changeDirectory(HOME_DIR, callBack);
    }
}
