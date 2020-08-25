# Android-EZ-FTP

EZFTP Library supports you to develop an FTP server and client，it support download and upload file.

## Releases

Coming soon.

## FTP Server

### 1.Start FTP Server

```java
//config
EZFtpUser ftpUser = new EZFtpUser(name, pw, sharePath, EZFtpUserPermission.WRITE);
EZFtpServer ftpServer = new EZFtpServer.Builder()
    .addUser(ftpUser)
    .setListenPort(port)
    .create();
//START
ftpServer.start();
```

### 2.Stop FTP Server

```java
ftpServer.stop();
```

## FTP Client

### 1.Connect to FTP Server

```java

EZFtpClient ftpClient = new EZFtpClient();
//config
final String serverIp = ...;
final String serverPort = ...;

final String username = ...;
final String password = ...;

//connecting
ftpClient.connect(
        serverIp,
        Integer.parseInt(serverPort),
        username,
        password,
        new OnEZFtpCallBack<Void>() {
            @Override
            public void onSuccess(Void response) {
                //Async callback
                //UI thread
            }

            @Override
            public void onFail(int code, String msg) {
                //Async callback
                //UI thread
            }
        }
);

```

### 2.Get remote file list
```java
ftpClient.getCurDirFileList(new OnEZFtpCallBack<List<EZFtpFile>>() {
    @Override
    public void onSuccess(List<EZFtpFile> response) {
        //Async callback
        //UI Thread
    }

    @Override
    public void onFail(int code, String msg) {
        //Async callback
        //UI Thread
    }
});
```

### 3.Download file from remote server

```java
final String saveLocalPath = SAVE_FILE_PATH + "/" + ftpFile.getName();
ftpClient.downloadFile(ftpFile, saveLocalPath, new EZFtpTransferSpeedCallback() {
    @Override
    public void onTransferSpeed(boolean isFinished, long startTime, long endTime, double speed, double averageSpeed) {
        //Async callback
        //UI Thread
    }

    @Override
    public void onStateChanged(int state) {
        super.onStateChanged(state);
    }

    @Override
    public void onTransferred(long fileSize, int transferredSize) {
        super.onTransferred(fileSize, transferredSize);
    }

    @Override
    public void onErr(int code, String msg) {
        super.onErr(code, msg);
    }
});
```

or

```java
ftpClient.downloadFile(ftpFile, saveLocalPath, new OnEZFtpDataTransferCallback() {
    @Override
    public void onStateChanged(int state) {
        
    }

    @Override
    public void onTransferred(long fileSize, int transferredSize) {

    }

    @Override
    public void onErr(int code, String msg) {

    }
});
```

### 4.Upload file to remote server
```java
ftpClient.uploadFile(file.getAbsolutePath(), new EZFtpTransferSpeedCallback() {
    @Override
    public void onTransferSpeed(boolean isFinished, long startTime, long endTime, double speed, double averageSpeed) {
       
    }
});
```

## License

EZ-FTP binaries and source code can be used according to the [Apache License, Version 2.0](https://github.com/lilincpp/Android-EZ-FTP/blob/master/LICENSE).

```
Copyright 2020 lilincpp

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
