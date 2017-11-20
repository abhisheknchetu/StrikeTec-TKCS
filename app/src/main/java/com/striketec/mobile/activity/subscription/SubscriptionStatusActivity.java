package com.striketec.mobile.activity.subscription;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.BaseActivity;
import com.striketec.mobile.dto.SubscriptionDtoNew;
import com.striketec.mobile.dto.SubscriptionUserDto;
import com.striketec.mobile.response.ResponseObject;
import com.striketec.mobile.response.SubscriptionMainResponse;
import com.striketec.mobile.restapi.IndicatorCallback;
import com.striketec.mobile.restapi.RetrofitSingleton;
import com.striketec.mobile.util.CommonUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * user : rakeshk2
 * date : 11/13/2017
 * description : This class shows remaining data in current subscription plan
 * and Provides option to change or cancel subscription.
 **/
public class SubscriptionStatusActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView titleView;
    @BindView(R.id.txtDetails)
    TextView txtDetails;
    @BindView(R.id.changeSubscription)
    Button changeSubscription;
    @BindView(R.id.cancelSubscription)
    Button cancelSubscription;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_status);

        ButterKnife.bind(this);

        initViews();

    }

    private void initViews() {
        mContext = SubscriptionStatusActivity.this;
        titleView.setText(getResources().getString(R.string.mysubsriptions));

        loadSubscriptionStatus();
    }

    @OnClick({R.id.cancelSubscription, R.id.changeSubscription, R.id.back})
    public void onClick(View view) {
        try {
            switch (view.getId()) {
                case R.id.back:
                    finish();
                    break;

                case R.id.cancelSubscription:
                    showConfirmDialog(getString(R.string.confirm_cancel_subscription));
                    break;

                case R.id.changeSubscription:
                    finish();
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * user : rakeshk2
     * date : 10/25/2017
     * description : Start new activity.
     **/
    private void startNewActivity(Class cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * user : rakeshk2
     * date : 11/14/2017
     * description : Show Confirm dialog.
     **/
    public void showConfirmDialog(String message) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
//        adb.setView(alertDialogView);
        adb.setTitle(R.string.alert);
        adb.setMessage(message);
        adb.setIcon(android.R.drawable.ic_dialog_alert);

        adb.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startNewActivity(ReSubscribeActivity.class);
                cancelSubscriptionAPI("", 1);
            }
        });

        adb.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        adb.show();
    }

    /**
     * user : rakeshk2
     * date : 11/10/2017
     * Description : This method calls the Api to cancel user subscription.
     *
     * @param purchaseData   this param holds the subscription details.
     * @param subscriptionId subscription id
     */
    private void cancelSubscriptionAPI(String purchaseData, int subscriptionId) {
        if (CommonUtils.isOnline()) {
            RetrofitSingleton.SUBSCRIPTION_REST.cancelSubscription("1").enqueue(new IndicatorCallback<ResponseObject<SubscriptionMainResponse>>(this, false) {
                @Override
                public void onResponse(Call<ResponseObject<SubscriptionMainResponse>> call, Response<ResponseObject<SubscriptionMainResponse>> response) {
                    super.onResponse(call, response);
                    if (response.body() != null) {
                        try {
                            final ResponseObject<SubscriptionMainResponse> responseBody = response.body();

                            if (responseBody.getError().equals(getString(R.string.false_text))) {
                                SubscriptionDtoNew subscriptionDtoNew = responseBody.getData().getSubscription();
                                SubscriptionUserDto subscriptionUserDto = responseBody.getData().getUser_subscription();

                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseObject<SubscriptionMainResponse>> call, Throwable t) {
                    super.onFailure(call, t);
                    CommonUtils.showToastMessage(t.getLocalizedMessage());
                }
            });
        } else {
            CommonUtils.showToastMessage(getResources().getString(R.string.nointernet));
        }
    }

    /**
     * user : rakeshk2
     * date : 11/13/2017
     * description : Get current subscription status from web api.
     **/
    private void loadSubscriptionStatus() {

        if (CommonUtils.isOnline()) {
            RetrofitSingleton.SUBSCRIPTION_REST.getSubscriptionStatus("64").enqueue(new IndicatorCallback<ResponseObject<SubscriptionMainResponse>>(this, false) {
                @Override
                public void onResponse(Call<ResponseObject<SubscriptionMainResponse>> call, Response<ResponseObject<SubscriptionMainResponse>> response) {
                    super.onResponse(call, response);
                    if (response.body() != null) {
                        try {
                            final ResponseObject<SubscriptionMainResponse> responseBody = response.body();
                            if (responseBody.getError().equals(getString(R.string.false_text))) {
//                                SubscriptionDtoNew subscriptionDtoNew = responseBody.getData().getSubscription();
                                SubscriptionUserDto subscriptionUserDto = responseBody.getData().getUser_subscription();
                                String status = subscriptionUserDto.getBattles() + "\n" + subscriptionUserDto.getTutorials() + "\n" +
                                        subscriptionUserDto.getTournaments();
                                txtDetails.setText(status);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseObject<SubscriptionMainResponse>> call, Throwable t) {
                    super.onFailure(call, t);
                    CommonUtils.showToastMessage(t.getLocalizedMessage());
                }
            });
        } else {
            CommonUtils.showToastMessage(getResources().getString(R.string.nointernet));
        }
    }

}
