package com.yisan.appupdater.utils;

import android.content.Context;

/**
 * @author：wzh
 * @description:
 * @packageName: com.yisan.appupdater.utils
 * @date：2020/3/12 0012 下午 2:54
 */
public class ScreenUtils {

    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


}
