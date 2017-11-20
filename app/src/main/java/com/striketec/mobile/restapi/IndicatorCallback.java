package com.striketec.mobile.restapi;

import android.app.ProgressDialog;
import android.content.Context;

import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Qiang on 8/15/2017.
 */

public abstract class IndicatorCallback<T> implements Callback<T>{

    private static AtomicInteger count = new AtomicInteger();
    private static ProgressDialog mDialog;

    private Context context;
    private String loadingMessage;

    private boolean showIndicator;

    public IndicatorCallback(Context context){
        this(context, "Loading...");
    }

    public IndicatorCallback(Context context, boolean showIndicator){
        this(context, "Loading...", showIndicator);
    }

    public IndicatorCallback(Context context, String message){
        this(context, message, true);
    }

    public IndicatorCallback(Context context, String message, boolean showIndicator){
        this.context = context;
        this.loadingMessage = message;
        this.showIndicator = showIndicator;

        int current = count.getAndIncrement();

        if (current == 0){
            show();
        }
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        int current = count.decrementAndGet();

        if (current == 0)
            dismiss();
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        int current = count.decrementAndGet();
        if (current == 0)
            dismiss();
    }

    private void show() {
        if (showIndicator && mDialog == null && context != null) {
            mDialog = new ProgressDialog(context, ProgressDialog.STYLE_SPINNER);
            mDialog.setMessage(loadingMessage);
            mDialog.setCancelable(false);
            try {
                mDialog.show();
            } catch (Exception e) {}
        }
    }

    private void dismiss() {
        if (mDialog != null) {
            try {
                mDialog.dismiss();
            } catch (Exception e) {}
            mDialog = null;
        }
    }
}
