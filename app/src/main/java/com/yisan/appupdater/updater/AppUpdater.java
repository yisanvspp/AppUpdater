package com.yisan.appupdater.updater;

import com.yisan.appupdater.updater.net.INetManager;
import com.yisan.appupdater.updater.net.OkHttpNetManager;

/**
 * @author：wzh
 * @description: 更新类  （做独立模块的功能，对外提供一个类。使用者只能通过该类进行操作。对于使用者具体实现是不可见的）
 * @packageName: com.yisan.appupdater.updater
 * @date：2020/3/12 0012 上午 9:11
 */
public class AppUpdater {

    private static AppUpdater mAppUpdater;


    /**
     * 网络请求，各种网络框架
     * OKHttp，Volley，HttpClient , HttpUrlConnection, xUtils
     * 接口隔离，具体下载的方式由客户自己实现。创建INetManager接口
     */
    private INetManager mNetManager = new OkHttpNetManager();

    /**
     * 设置网络实现者
     *
     * @param manager INetManager
     */
    public void setNetManager(INetManager manager) {
        this.mNetManager = manager;
    }

    /**
     * 获取网络管理
     *
     * @return INetManager
     */
    public INetManager getNetManager() {
        return mNetManager;
    }


    public static AppUpdater getInstance() {
        if (mAppUpdater == null) {
            synchronized (AppUpdater.class) {
                if (mAppUpdater == null) {
                    mAppUpdater = new AppUpdater();
                }
            }
        }
        return mAppUpdater;
    }


}
