package com.yisan.appupdater.updater.net;

import java.io.File;

/**
 * @author：wzh
 * @description: 下载回调
 * @packageName: com.yisan.appupdater.updater.net
 * @date：2020/3/12 0012 上午 9:25
 */
public interface INetDownloadCallBack {

    void success(File apkFile);

    void progress(int progress);

    void failed(Throwable throwable);
}
