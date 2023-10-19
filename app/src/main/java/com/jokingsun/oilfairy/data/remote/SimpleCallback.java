package com.jokingsun.oilfairy.data.remote;

import retrofit2.Call;
import retrofit2.Response;

/**
 * @author cfd058
 */
public class SimpleCallback<T> {
    /**
     * After Connecting
     */
    public void initial() {

    }

    /**
     * After onResponse
     *
     * @param call
     * @param response
     */
    public void onResponse(Call<T> call, Response<T> response) {

    }

    /**
     * After onCallback
     *
     * @param t
     */
    public void onCallback(T t) {

    }

    /**
     * After onFail
     *
     * @param t
     */
    public void onFail(T t) {

    }

    /**
     * After onFail and Throw Exception
     *
     * @param t
     */
    public void onFail(Throwable t) {

    }

    /**
     * After onComplete
     */
    public void onComplete() {

    }
}
