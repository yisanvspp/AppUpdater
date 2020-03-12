package com.yisan.appupdater.updater.net;

import java.io.File;

/**
 * @author：wzh
 * @description:
 * @packageName: com.yisan.appupdater.updater
 * @date：2020/3/12 0012 上午 9:20
 */
public interface INetManager {


    void get(String url, INetCallBack callBack, Object tag);

    void download(String url, File targetFile, INetDownloadCallBack callBack, Object tag);

    void cancel(Object tag);
}
