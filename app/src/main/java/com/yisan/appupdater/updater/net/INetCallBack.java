package com.yisan.appupdater.updater.net;

/**
 * @author：wzh
 * @description: 网络回调
 * @packageName: com.yisan.appupdater.updater.net
 * @date：2020/3/12 0012 上午 9:24
 */
public interface INetCallBack {


    void success(String response);

    void failed(Throwable throwable);

}
