package com.cetnaline.findproperty.api;

import com.cetnaline.findproperty.BaseApplication;
import com.cetnaline.findproperty.BuildConfig;
import com.cetnaline.findproperty.utils.SHA1;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by diaoqf on 2016/7/15.
 */
public class NetWorkManager {

    private static final int RONG_CLOUD_HEAD = 0;
    private static final int COMMON_HEAD = 1;
    private static final int LOGIN_HEAD = 2;

    public static boolean commonClientRefresh = false;
    private static Retrofit commonClient = null;     //带缓存服务
    private static Retrofit rongClouldClient = null;  //融云服务
    public static boolean noCahceClientRefresh = false;
    private static Retrofit noCahceClient = null;   //无缓存服务

//    private static Retrofit centlineClient = null; //三级市场服务

    private NetWorkManager(){}

    /**
     * 刷新host
     */
    public static void refreshRetrofit() {
        commonClient = new Retrofit.Builder()
                .baseUrl(NetContents.IS_TEST_ENV ? NetContents.BASE_HOST_TEST:NetContents.BASE_HOST)
                .client(getHttpClient())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        noCahceClient = new Retrofit.Builder()
                .baseUrl(NetContents.IS_TEST_ENV ? NetContents.BASE_HOST_TEST:NetContents.BASE_HOST)
                .client(getHttpClient(false))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        NetWorkManager.commonClientRefresh = true;
        NetWorkManager.noCahceClientRefresh = true;
    }

    /**
     * 基础服务
     * @return
     */
    public static Retrofit getCommonClient(){
        if (commonClient == null) {
            commonClient = new Retrofit.Builder()
                    .baseUrl(NetContents.IS_TEST_ENV ? NetContents.BASE_HOST_TEST:NetContents.BASE_HOST)
                    .client(getHttpClient())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return commonClient;
    }

    public static Retrofit getNoCacheClient(){
        if (noCahceClient == null) {
            noCahceClient = new Retrofit.Builder()
                    .baseUrl(NetContents.IS_TEST_ENV ? NetContents.BASE_HOST_TEST:NetContents.BASE_HOST)
                    .client(getHttpClient(false))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return noCahceClient;
    }

    /**
     * 三级市场服务
     * @return
     */
//    public static Retrofit getCentlineClient() {
//        if (centlineClient == null) {
//            centlineClient = new Retrofit.Builder()
//                    .baseUrl(NetContents.CENTLINE_HOST)
//                    .client(getHttpClient(false))
//                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
//        }
//        return centlineClient;
//    }

    /**
     * 融云接口
     * @return
     */
    public static Retrofit getRongCloudClient(){

        if (rongClouldClient == null) {
            rongClouldClient = new Retrofit.Builder()
                    .baseUrl(NetContents.RONG_CLOUD_HOST)
                    .client(getHttpClient(RONG_CLOUD_HEAD, false))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return rongClouldClient;
    }


    private static final String CACHE_CONTROL = "Cache-Control";
    private static final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            int maxAge = 60; // 在线缓存在1分钟内可读取
            return originalResponse.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader(CACHE_CONTROL)
                    .header(CACHE_CONTROL, "public, max-age=" + maxAge)
                    .build();
        }
    };

    private static File httpCacheDirectory = new File(BaseApplication.getContext().getCacheDir(), "FindPropertyCache");
    private static int cacheSize = 10 * 1024 * 1024; // 10 MiB
    private static Cache cache = new Cache(httpCacheDirectory, cacheSize);

    //默认返回带缓存
    public static OkHttpClient getHttpClient(){
        return getHttpClient(0, true, false);
    }

    /**
     * 不添加头部，可以选择是否需要缓存
     * @param hasCache 是否带缓存
     * @return
     */
    public static OkHttpClient getHttpClient(boolean hasCache){
        return getHttpClient(0, hasCache, false);
    }

    /**
     * 添加头部，选择是否需要缓存
     * @param headType 头部类型
     * @param hasCache 是否需要缓存
     * @return
     */
    public static OkHttpClient getHttpClient(int headType, boolean hasCache){
        return getHttpClient(headType, hasCache, true);
    }

    public static OkHttpClient getHttpClient(int headType, boolean hasCache, boolean hasHead){

        //okhttp log intercepter
//        Interceptor okhttpLogInterceptor = new Interceptor() {
//            @Override
//            public Response intercept(Chain chain) throws IOException {
//                Request request = chain.request();
//
////                Logger.i(request.url().toString());
//
//                //add headers
//                if (hasHead){
//                    request = addHeaders(headType, request);
//                }
//
//                if (BuildConfig.DEBUG) {
//                    long t1 = System.nanoTime();
//                    Response response = chain.proceed(request);
//                    long t2 = System.nanoTime();
//
//                    double time = (t2 - t1) / 1e6d;
//
//                    String msg = "\nurl->" + request.url()
//                            + "\ntime->" + time
//                            + "ms\nheaders->" + request.headers()
//                            + "\nresponse code->" + response.code()
//                            + "\nresponse headers->" + response.headers();
//                    // "\nresponse body->" + response.body().string()
//
//                    if (request.method().equals("GET")) {
//                        Logger.i("GET"+msg);
//                    } else if (request.method().equals("POST")) {
//                        Request copyRequest = request.newBuilder().build();
//                        if (copyRequest.body() instanceof FormBody) {
//                            Buffer buffer = new Buffer();
//                            copyRequest.body().writeTo(buffer);
//                            String res = MyUtils.getURLDecoderString(buffer.readUtf8());
////                            res = res.replace("=",":");
////                            res = res.replace("&",",");
//                            Logger.i("request params:" + res);
//                        }
//                        Logger.i("POST"+msg);
//                    } else if (request.method().equals("PUT")) {
//                        Logger.i("PUT"+msg);
//                    } else if (request.method().equals("DELETE")) {
//                        Logger.i("DELETE"+msg);
//                    }
//                    return response;
//                }
//                return chain.proceed(request);
//            }
//        };


        HttpLoggingInterceptor okhttpLogInterceptor = new HttpLoggingInterceptor();
        okhttpLogInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(NetContents.HTTP_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(NetContents.HTTP_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(NetContents.HTTP_READ_TIMEOUT, TimeUnit.MILLISECONDS);

        if (hasCache){
            builder.addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR);
            builder.cache(cache);
        }

        //add headers
        if (hasHead){
            builder.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    Request request = addHeaders(headType, original);
                    return chain.proceed(request);
                }
            });
        }

        //添加日志拦截器
        builder.addInterceptor(okhttpLogInterceptor);

        OkHttpClient client = builder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        }).build();
        return client;
    }

    /**
     * 添加请求头
     * @param type 区分不同的API
     * @param request
     * @return
     */
    private static Request addHeaders(int type, Request request){
        Request.Builder requestBuild = request.newBuilder();
        switch (type) {
            case RONG_CLOUD_HEAD:
                String nonce = (int)(Math.random()*1000000000)+"";
                String timestamp = System.currentTimeMillis()+"";
                String signature = SHA1.encode(BuildConfig.RONG_CLOUD_SECRET+nonce+timestamp);
                requestBuild.addHeader("App-Key", BuildConfig.RONG_CLOUD_KEY)
                        .addHeader("Nonce",nonce)
                        .addHeader("Timestamp",timestamp)
                        .addHeader("Signature",signature);
                break;
            case LOGIN_HEAD:
                break;
        }
        return requestBuild.build();
    }

}
