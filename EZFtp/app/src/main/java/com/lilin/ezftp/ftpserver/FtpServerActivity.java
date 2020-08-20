package com.lilin.ezftp.ftpserver;


import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;

import com.blankj.utilcode.util.NetworkUtils;
import com.lilin.ezftp.BaseActivity;
import com.lilin.ezftp.FtpConfig;
import com.lilin.ezftp.R;
import com.lilin.ezftp.databinding.ActivityFtpServerBinding;
import com.lilincpp.github.libezftp.EZFtpServer;
import com.lilincpp.github.libezftp.user.EZFtpUser;
import com.lilincpp.github.libezftp.user.EZFtpUserPermission;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author lilin
 */
public class FtpServerActivity extends BaseActivity {

    private static final String TAG = "FtpServerActivity";
    private ActivityFtpServerBinding binding;

    private ClickHolder clickHolder = new ClickHolder(this);

    private ObservableField<String> msg = new ObservableField<>();
    private ObservableField<String> userName = new ObservableField<>();
    private ObservableField<String> userPassword = new ObservableField<>();
    private ObservableField<String> sharePath = new ObservableField<>();
    private ObservableField<Integer> serverPort = new ObservableField<>();


    public static final class ClickHolder {
        private WeakReference<FtpServerActivity> activity;
        private EZFtpServer ftpServer;

        public ClickHolder(FtpServerActivity activity) {
            this.activity = new WeakReference<>(activity);
        }

        public void startFtpServer(String name, String pw, String sharePath, int port) {
            Log.d(TAG, "startFtpServer: " + Thread.currentThread().getName());
            if (ftpServer == null) {
                ftpServer = new EZFtpServer.Builder()
                        .addUser(new EZFtpUser(name, pw, sharePath, EZFtpUserPermission.WRITE))
                        .setListenPort(port)
                        .create();
                ftpServer.start();
            } else {
                if (ftpServer.isStopped()) {
                    ftpServer.start();
                }
            }

            final String serverIp = NetworkUtils.getIPAddress(true) + ":" + port;


            if (activity.get() != null) {
                activity.get().uploadMsg(
                        "username=" + name + "\n"
                                + "pw=" + pw + "\n"
                                + "share path=" + sharePath + "\n"
                                + "serverIp=" + serverIp + "\n\n"
                                + "Ftp Server is running!" + "\n"
                                + "1.Browser open url: ftp://" + serverIp + "\n"
                                + "2.Use this or other ftp client connect server\n");
            }
        }

        public void stopFtpServer() {
            if (ftpServer != null) {
                ftpServer.stop();
            }
            if (activity.get() != null) {
                activity.get().uploadMsg("Ftp server is stopped!\n");
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ftp_server);
        initData();
    }

    private void initData() {
        binding.tvMsg.setMovementMethod(ScrollingMovementMethod.getInstance());

        msg.set("History Msg:");
        userName.set(FtpConfig.DEFAULT_USER);
        userPassword.set(FtpConfig.DEFAULT_PASSWORD);
        serverPort.set(FtpConfig.DEFAULT_PORT);
        sharePath.set(FtpConfig.DEFAULT_SHARE_PATH);

        binding.setMsg(msg);
        binding.setUserName(userName);
        binding.setUserPassword(userPassword);
        binding.setServerPort(serverPort);
        binding.setSharePath(sharePath);

        binding.setClickHolder(clickHolder);

        uploadMsg("Hotspot is opened ? " + isWifiApOpen(this));
    }

    private void uploadMsg(String msg) {
        String old = this.msg.get();
        this.msg.set(old + "\n" + msg);
    }

    public static boolean isWifiApOpen(Context context) {
        try {
            WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            //通过放射获取 getWifiApState()方法
            Method method = manager.getClass().getDeclaredMethod("getWifiApState");
            //调用getWifiApState() ，获取返回值
            int state = (int) method.invoke(manager);
            //通过放射获取 WIFI_AP的开启状态属性
            Field field = manager.getClass().getDeclaredField("WIFI_AP_STATE_ENABLED");
            //获取属性值
            int value = (int) field.get(manager);
            //判断是否开启
            if (state == value) {
                return true;
            } else {
                return false;
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return false;
    }
}
