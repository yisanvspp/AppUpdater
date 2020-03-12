package com.yisan.appupdater.updater.bean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * @author：wzh
 * @description: app下载bean
 * @packageName: com.yisan.appupdater.updater.bean
 * @date：2020/3/12 0012 上午 10:58
 */
public class AppDownloadBean implements Serializable {


    public String title;
    public String content;
    public String url;
    public String md5;
    public String versionCode;


    public static AppDownloadBean parse(String json){
        AppDownloadBean bean = null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            String title = (String) jsonObject.opt("title");
            String content = (String) jsonObject.opt("content");
            String url = (String) jsonObject.opt("url");
            String md5 = (String) jsonObject.opt("md5");
            String versionCode = (String) jsonObject.opt("versionCode");

            bean = new AppDownloadBean();
            bean.title = title;
            bean.content = content;
            bean.url = url;
            bean.md5 = md5;
            bean.versionCode = versionCode;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return bean;
    }
}
