package com.jokingsun.oilfairy.data.remote;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jokingsun.oilfairy.BuildConfig;
import com.jokingsun.oilfairy.R;
import com.jokingsun.oilfairy.base.BaseResponseModel;
import com.jokingsun.oilfairy.common.constant.AppConstant;
import com.jokingsun.oilfairy.utils.NetWorkCheckUtil;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * @author cfd058
 */
public abstract class BaseApiTool<TServices> {

    private String apiDomain = "";

    private final Context context;
    private Retrofit retrofit;
    private TServices services;
    private final Class<TServices> typeClass;
    private OkHttpClient.Builder httpClient;
    private boolean isDialogShowing = false;

    public BaseApiTool(Context context, Class<TServices> typeClass) {
        this.context = context;
        this.typeClass = typeClass;
        initHttpClient();
    }

    public void createDomain(String apiUrlPath) {
        this.apiDomain = apiUrlPath;
        Gson gson = (new GsonBuilder()).serializeNulls().create();
        this.retrofit = (new retrofit2.Retrofit.Builder()).baseUrl(apiDomain)
                .client(this.getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        this.services = this.retrofit.create(typeClass);
    }

    protected TServices getServices() {
        return this.services;
    }

    private void initHttpClient() {
        this.httpClient = new OkHttpClient.Builder();
        this.httpClient.addInterceptor(new RetryInterceptor(this.getTotalRetries()));
        this.httpClient.connectTimeout((long) this.getConnectTimeOutSeconds(), TimeUnit.SECONDS);
        this.httpClient.readTimeout((long) this.getConnectTimeOutSeconds(), TimeUnit.SECONDS);
        this.httpClient.writeTimeout((long) this.getWriteTimeOutSeconds(), TimeUnit.SECONDS);

        if (BuildConfig.DEBUG) {
            //Log 信息攔截器
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            httpClient.addInterceptor(loggingInterceptor);
        }
    }

    private OkHttpClient getOkHttpClient() {
        return this.httpClient.build();
    }

    protected OkHttpClient.Builder getHttpClient() {
        return this.httpClient;
    }

    /**
     * 連線時間
     */
    protected int getConnectTimeOutSeconds() {
        return 30;
    }

    /**
     * 寫入時間
     */
    protected int getWriteTimeOutSeconds() {
        return 30;
    }

    /**
     * 重試連接次數
     */
    protected int getTotalRetries() {
        return 3;
    }

    /**
     * Enqueue Api Process
     */
    protected <T> void runCall(Call<T> call, SimpleCallback<T> callback) {
        try {
            call.enqueue(new InitCallBack<>(callback));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 網路異常，重試 dialog，如重試後確認恢復網路，則發出通知已恢復
     */
    private void hitNetWorkNotAliveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("")
                .setMessage(context.getString(R.string.network_message_content))
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.try_again), null);

        AlertDialog isNetWorkAliveDialog = builder.create();
        if (!isDialogShowing) {
            isNetWorkAliveDialog.show();
            isNetWorkAliveDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
                if (!NetWorkCheckUtil.isNetWorkConnected(BaseApiTool.this.context)) {
                    isDialogShowing = true;
                } else {
                    Intent intent = new Intent(AppConstant.ACTION_NET_WORK_REWORK);
                    context.sendBroadcast(intent);
                    isNetWorkAliveDialog.dismiss();
                    isDialogShowing = false;
                }
            });
        }
    }

    /**
     * Client Interceptor
     */
    public static class RetryInterceptor implements Interceptor {
        private final int maxRetry; //最大重試次數 (MaxRetry+1)
        private int retryNum = 0;

        RetryInterceptor(int maxRetry) {
            this.maxRetry = maxRetry;
        }

        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {

            Request request = chain.request();
            okhttp3.Response response = chain.proceed(request);

            while (!response.isSuccessful() && retryNum < maxRetry) {
                ++this.retryNum;
                response.close(); //關閉上一個回應，以避免發生
                // cannot make a new request because the previous response is still open: please call response.close()
                // 誘發於 OkHttp3 version 3.14.x 以上
                response = chain.proceed(request);
            }


            return response;
        }
    }

    /**
     * Retrofit onResponse and onFail
     */
    private class InitCallBack<T> implements retrofit2.Callback<T> {
        private final SimpleCallback<T> apiCallback;

        private InitCallBack(SimpleCallback<T> apiCallback) {
            this.apiCallback = apiCallback;
            try {
                this.apiCallback.initial();
            } catch (Exception var4) {
                var4.printStackTrace();
            }
        }

        @Override
        public void onResponse(Call<T> call, Response<T> response) {

            try {
                this.apiCallback.onResponse(call, response);
                if (response.isSuccessful() && response.code() == 200) {

                    if (response.body() instanceof ResponseBody) {
                        //取得原json string
                        this.apiCallback.onCallback(response.body());
                        Gson gson = new Gson();
                        String value = gson.toJson(response.body());
                        Logger.d("Api Message josn" + value);

                    } else {
                        //透過GSON自動data binding
                        Gson gson = new Gson();
                        String value = gson.toJson(response.body());
                        Logger.d("Api Message josn" + value);

                        BaseResponseModel responseBase = gson.fromJson(value, BaseResponseModel.class);
                        if (responseBase.isStatus()) {
                            this.apiCallback.onCallback(response.body());

                        } else {
                            this.apiCallback.onFail(response.body());
                        }
                    }

                } else {
                    this.apiCallback.onFail(response.body());
                }

                this.apiCallback.onComplete();

            } catch (Exception var6) {
                Logger.e("Api Read Or Entry Error :  " + var6.toString());
                var6.printStackTrace();
            }

        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {
            Logger.e("Api onFailure\n" + t.getMessage());
            try {
                this.apiCallback.onFail(t);
                this.apiCallback.onComplete();

                if (!NetWorkCheckUtil.isNetWorkConnected(BaseApiTool.this.context)) {
                    hitNetWorkNotAliveDialog();
                }
            } catch (Exception var4) {
                var4.printStackTrace();
            }
        }
    }

}
