# Android-EZ-FTP
FTP Server and Client for Android，Support downloading and uploading files

# How to 

# How to use it

## FTP Server

### Start FTP Server

```java
//config
EZFtpUser ftpUser=new EZFtpUser(name, pw, sharePath, EZFtpUserPermission.WRITE);
EZFtpServer ftpServer = new EZFtpServer.Builder()
    .addUser(ftpUser)
    .setListenPort(port)
    .create();
//START
ftpServer.start();
```

### Stop FTP Server

```java
ftpServer.stop();
```

## FTP Client

### Connect to FTP Server

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

### Get remote file list
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

### Download file from remote server

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

### Upload file to remote server
```java
ftpClient.uploadFile(file.getAbsolutePath(), new EZFtpTransferSpeedCallback() {
    @Override
    public void onTransferSpeed(boolean isFinished, long startTime, long endTime, double speed, double averageSpeed) {
       
    }
});
```
