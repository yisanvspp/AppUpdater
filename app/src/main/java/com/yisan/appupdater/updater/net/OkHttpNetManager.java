package com.yisan.appupdater.updater.net;

import android.os.Handler;
import android.os.Looper;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author：wzh
 * @description: OkHttp的网络实现
 * @packageName: com.yisan.appupdater.updater.net
 * @date：2020/3/12 0012 上午 9:31
 */
public class OkHttpNetManager implements INetManager {

    private static OkHttpClient okHttpClient;
    /**
     * UI线程的Handler
     */
    private static Handler handler = new Handler(Looper.getMainLooper());

    static {

        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(15000, TimeUnit.MILLISECONDS)
                .readTimeout(5000, TimeUnit.MILLISECONDS)
                .build();
        // TODO: 2020/3/12 0012 ssl证书
    }

    @Override
    public void get(String url, final INetCallBack callBack, Object tag) {
        //每个请求加上一个tag
        Request request = new Request.Builder().url(url).get().tag(tag).build();
        Call call = okHttpClient.newCall(request);
        //异步 非UI现场
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull final IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.failed(e);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {

                //执行过程中可能出现异常
                try {
                    final String string = response.body().string();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.success(string);
                        }
                    });
                } catch (Throwable e) {
                    callBack.failed(e);
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void download(String url, final File targetFile, final INetDownloadCallBack callBack, Object tag) {

        if (!targetFile.exists()) {
            targetFile.getParentFile().mkdirs();
        }

        final Request request = new Request.Builder().url(url).get().tag(tag).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull final IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.failed(e);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                InputStream is = null;
                OutputStream os = null;
                try {
                    final long totalLen = response.body().contentLength();
                    is = response.body().byteStream();
                    os = new FileOutputStream(targetFile);
                    byte[] buffer = new byte[8 * 1024];
                    long curLen = 0;
                    int len = 0;
                    while (!call.isCanceled() &&(len = is.read(buffer)) > 0) {
                        os.write(buffer, 0, len);
                        os.flush();
                        curLen += len;
                        final long finalCurLen = curLen;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                callBack.progress((int) (finalCurLen * 1.0f / totalLen * 100));
                            }
                        });
                    }
                    try {
                        //自身进程下的私有文件、要提供给系统的安装APP 使用，需要打开文件的权限
                        targetFile.setExecutable(true, false);
                        targetFile.setReadable(true, false);
                        targetFile.setWritable(true, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    if (call.isCanceled()){
                        return;
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.success(targetFile);
                        }
                    });

                } catch (final IOException e) {
                    e.printStackTrace();
                    //如果call取消了。回到中activity就不存在了。dialog会奔溃
                    if (call.isCanceled()){
                        return;
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.failed(e);
                        }
                    });
                } finally {
                    if (is != null) {
                        is.close();
                        is = null;
                    }
                    if (os != null) {
                        os.close();
                        os = null;
                    }
                }


            }
        });


    }

    @Override
    public void cancel(Object tag) {
        //在队列中的请求Call
        List<Call> queuedCalls = okHttpClient.dispatcher().queuedCalls();
        if (null != queuedCalls) {
            for (Call call : queuedCalls) {
                if (tag.equals(call.request().tag())) {
                    call.cancel();
                }
            }
        }

        List<Call> runningCalls = okHttpClient.dispatcher().runningCalls();
        if (null != runningCalls){
            for (Call call : runningCalls){
                if (tag.equals(call.request().tag())){
                    call.cancel();
                }
            }
        }


    }
}
