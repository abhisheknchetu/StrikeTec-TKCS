package com.striketec.mobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.multidex.MultiDexApplication;
import android.support.v4.content.LocalBroadcastManager;

import com.striketec.mobile.dto.UserDto;
import com.striketec.mobile.restapi.RetrofitSingleton;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.ComboSetUtil;
import com.striketec.mobile.util.CommonUtils;
import com.striketec.mobile.util.DBAdapter;
import com.striketec.mobile.util.FontManager;
import com.striketec.mobile.util.PresetUtil;
import com.striketec.mobile.util.SharedUtils;

/**
 * Created by Qiang on 8/7/2017.
 */

public class StriketecApp extends MultiDexApplication {

    public static UserDto currentUser;
    public static DBAdapter dbAdapter;
    public static boolean isLoggedin = false;
    public static Context mContext;
    private static StriketecApp mInstance;
    private BroadcastReceiver mPushReceiver;

    public static synchronized StriketecApp getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();
        RetrofitSingleton.setApplication(this);
        CommonUtils.init(this);
        SharedUtils.init(this);
        PresetUtil.init(this);
        ComboSetUtil.init(this);
        FontManager.init(this);

        dbAdapter = DBAdapter.getInstance(this);

        mPushReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(AppConst.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    String mes = intent.getStringExtra(AppConst.PUSH_MESSAGE);
                    CommonUtils.showToastMessage(mes);
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(mPushReceiver,
                new IntentFilter(AppConst.PUSH_NOTIFICATION));
    }
}
