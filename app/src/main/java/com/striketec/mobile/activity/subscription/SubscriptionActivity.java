package com.striketec.mobile.activity.subscription;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.vending.billing.IInAppBillingService;
import com.striketec.mobile.R;
import com.striketec.mobile.activity.BaseActivity;
import com.striketec.mobile.adapters.SubscriptionsListAdapter;
import com.striketec.mobile.dto.SubscriptionDto;
import com.striketec.mobile.dto.SubscriptionDtoNew;
import com.striketec.mobile.dto.SubscriptionUserDto;
import com.striketec.mobile.inapp_purchase_util.IabHelper;
import com.striketec.mobile.inapp_purchase_util.IabResult;
import com.striketec.mobile.response.ResponseArray;
import com.striketec.mobile.response.ResponseObject;
import com.striketec.mobile.response.SubscriptionMainResponse;
import com.striketec.mobile.restapi.IndicatorCallback;
import com.striketec.mobile.restapi.RetrofitSingleton;
import com.striketec.mobile.util.CommonUtils;
import com.striketec.mobile.util.InAppConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

public class SubscriptionActivity extends BaseActivity implements SubscriptionsListAdapter.OnSubscriptionClickRequest {

    @BindView(R.id.title)
    TextView titleView;
    @BindView(R.id.second_image)
    ImageView second_image;
    @BindView(R.id.free_plan)
    LinearLayout freeplanLayout;
    @BindView(R.id.monthplan)
    LinearLayout monthplanLayout;
    @BindView(R.id.annualplan)
    LinearLayout annualplanLayout;
    SubscriptionDto freeplanDto, monthplanDto, annualplanDto;
    int selectedPosition = 0;
    IabHelper mHelper;
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener;
    IInAppBillingService mService;
    /**
     * user : rakeshk2
     * date : 10/20/2017
     * description : this initialization handles the service connection states in the In-App Purchase flow.
     **/
    private final ServiceConnection mInAppConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
//            checkIsPlanPurchased();
        }
    };
    @BindView(R.id.subscriptionRecyclerView)
    RecyclerView subscriptionRecyclerView;
    private String mSkuId = null;
    private int mPurchaseId;
    private String TAG = "InApp_Processing";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        ButterKnife.bind(this);

        initViews();

        initInAppVariables();
        bindInAppService();
    }

    private void initViews() {
        titleView.setText(getResources().getString(R.string.mysubsriptions));
        second_image.setVisibility(View.VISIBLE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            second_image.setImageDrawable(getApplicationContext().getDrawable(R.drawable.settings));
        } else {
            second_image.setImageDrawable(getResources().getDrawable(R.drawable.settings));
        }

        updateView();

        loadSubscriptionPlans();
        //sendSubscribeInfoToServer("token_1233", 1);
    }

    /**
     * user : rakeshk2
     * date : 10/23/2017
     * description : Get list of Subscription plans from server.
     **/
    private void loadSubscriptionPlans() {

        if (CommonUtils.isOnline()) {

            RetrofitSingleton.SUBSCRIPTION_REST.getSubscriptions().enqueue(new IndicatorCallback<ResponseArray<SubscriptionDtoNew>>(this, false) {
                @Override
                public void onResponse(Call<ResponseArray<SubscriptionDtoNew>> call, Response<ResponseArray<SubscriptionDtoNew>> response) {
                    super.onResponse(call, response);
                    if (response.body() != null) {
                        try {
                            final ResponseArray<SubscriptionDtoNew> responseBody = response.body();

                            if (responseBody.getError().equalsIgnoreCase(getString(R.string.false_text))) {
                                List<SubscriptionDtoNew> subscriptionList = responseBody.getData();

                                SubscriptionsListAdapter adapter = new SubscriptionsListAdapter(SubscriptionActivity.this, (ArrayList<SubscriptionDtoNew>) subscriptionList);
                                GridLayoutManager mLayoutManager = new GridLayoutManager(SubscriptionActivity.this, 1);
                                subscriptionRecyclerView.setLayoutManager(mLayoutManager);
                                subscriptionRecyclerView.setAdapter(adapter);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseArray<SubscriptionDtoNew>> call, Throwable t) {
                    super.onFailure(call, t);
                    CommonUtils.showToastMessage(t.getLocalizedMessage());
                }
            });
        } else {
            CommonUtils.showToastMessage(getResources().getString(R.string.nointernet));
        }
    }

    @OnClick({R.id.back, R.id.second_image, R.id.next, R.id.free_plan, R.id.monthplan, R.id.annualplan})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;

            case R.id.second_image:
                startNewActivity();
                break;

            case R.id.next:
//                finish();
                subscribePlan(mSkuId, mPurchaseId);
                break;

            case R.id.free_plan:
                if (selectedPosition != 0) {
                    selectedPosition = 0;
                    updateView();
                }
                break;

            case R.id.monthplan:
                if (selectedPosition != 1) {
                    selectedPosition = 1;
                    updateView();
                }
                break;

            case R.id.annualplan:
                if (selectedPosition != 2) {
                    selectedPosition = 2;
                    updateView();
                }
                break;
        }
    }

    /**
     * user : rakeshk2
     * date : 10/25/2017
     * description : Start new activity.
     **/
    private void startNewActivity() {
        Intent intent = new Intent(this, SubscriptionStatusActivity.class);
        startActivity(intent);
    }

    private void updateView() {
        switch (selectedPosition) {
            case 0:
                freeplanLayout.setBackgroundResource(R.drawable.bg_fill_rectangle_punches);
                monthplanLayout.setBackground(new ColorDrawable(getResources().getColor(R.color.transparent)));
                annualplanLayout.setBackground(new ColorDrawable(getResources().getColor(R.color.transparent)));
                freeplanLayout.setAlpha(1);
                monthplanLayout.setAlpha(0.7f);
                annualplanLayout.setAlpha(0.7f);
                break;

            case 1:
                monthplanLayout.setBackgroundResource(R.drawable.bg_fill_rectangle_punches);
                freeplanLayout.setBackground(new ColorDrawable(getResources().getColor(R.color.transparent)));
                annualplanLayout.setBackground(new ColorDrawable(getResources().getColor(R.color.transparent)));
                freeplanLayout.setAlpha(0.7f);
                monthplanLayout.setAlpha(1);
                annualplanLayout.setAlpha(0.7f);
                break;

            case 2:
                annualplanLayout.setBackgroundResource(R.drawable.bg_fill_rectangle_punches);
                monthplanLayout.setBackground(new ColorDrawable(getResources().getColor(R.color.transparent)));
                freeplanLayout.setBackground(new ColorDrawable(getResources().getColor(R.color.transparent)));
                freeplanLayout.setAlpha(0.7f);
                monthplanLayout.setAlpha(0.7f);
                annualplanLayout.setAlpha(1);
                break;
        }
    }

    private void loadPlans() {
        freeplanDto = new SubscriptionDto();
        freeplanDto.mTutorialNum = 5;
        freeplanDto.mTournamentNum = 2;
        freeplanDto.mBattleNum = 2;
        freeplanDto.mTutorialPrice = 1.99f;
        freeplanDto.mTournamentPrice = 1.99f;
        freeplanDto.mBattlePrice = 1.99f;
        freeplanDto.mPlanType = 0;
        freeplanDto.mTotalPrice = 0.0f;


        monthplanDto = new SubscriptionDto();
        monthplanDto.mTutorialNum = 5;
        monthplanDto.mTournamentNum = 2;
        monthplanDto.mBattleNum = 2;
        monthplanDto.mTutorialPrice = 1.99f;
        monthplanDto.mTournamentPrice = 1.99f;
        monthplanDto.mBattlePrice = 1.99f;
        monthplanDto.mPlanType = 1;
        monthplanDto.mTotalPrice = 3.99f;

        annualplanDto = new SubscriptionDto();
        annualplanDto.mTutorialNum = -1;
        annualplanDto.mTournamentNum = -1;
        annualplanDto.mBattleNum = -1;
        annualplanDto.mTutorialPrice = -1f;
        annualplanDto.mTournamentPrice = 1.99f;
        annualplanDto.mBattlePrice = 1.99f;
        annualplanDto.mPlanType = 2;
        annualplanDto.mTotalPrice = 9.99f;


        updateInfo(freeplanLayout, freeplanDto);
        updateInfo(monthplanLayout, monthplanDto);
        updateInfo(annualplanLayout, annualplanDto);
    }

    private void updateInfo(View view, SubscriptionDto subscriptionDto) {
        SubscriptionViewHolder viewHolder = new SubscriptionViewHolder(view);

        if (subscriptionDto.mPlanType == 2) {
            viewHolder.tutorialnumView.setText(getResources().getString(R.string.alltutorials));
            viewHolder.tournamentnumView.setText(getResources().getString(R.string.allchallenges));
            viewHolder.battlenumView.setText(getResources().getString(R.string.allbattles));

            viewHolder.tutorialpriceView.setText(getResources().getString(R.string.subscripiton_annualtutorial));
            viewHolder.tournamentpriceView.setText(String.format(getResources().getString(R.string.subscription_additionalprice), String.valueOf(subscriptionDto.mTournamentPrice)));
            viewHolder.battlepriceView.setText(String.format(getResources().getString(R.string.subscription_additionalprice), String.valueOf(subscriptionDto.mBattlePrice)));
        } else {
            viewHolder.tutorialnumView.setText(String.format(getResources().getString(R.string.subscription_tutorial), subscriptionDto.mTutorialNum));
            viewHolder.tournamentnumView.setText(String.format(getResources().getString(R.string.subscription_tournament), subscriptionDto.mTournamentNum));
            viewHolder.battlenumView.setText(String.format(getResources().getString(R.string.subscription_battle), subscriptionDto.mBattleNum));

            viewHolder.tutorialpriceView.setText(String.format(getResources().getString(R.string.subscription_additionalprice), String.valueOf(subscriptionDto.mTutorialPrice)));
            viewHolder.tournamentpriceView.setText(String.format(getResources().getString(R.string.subscription_additionalprice), String.valueOf(subscriptionDto.mTournamentPrice)));
            viewHolder.battlepriceView.setText(String.format(getResources().getString(R.string.subscription_additionalprice), String.valueOf(subscriptionDto.mBattlePrice)));
        }

        viewHolder.planPriceView.setText("$" + subscriptionDto.mTotalPrice);

        if (subscriptionDto.mPlanType == 0) {
            viewHolder.plantypeView.setText(getResources().getString(R.string.subscription_freeplan));
            viewHolder.plandurationView.setText(getResources().getString(R.string.subscription_freeplanduration));
        } else if (subscriptionDto.mPlanType == 1) {
            viewHolder.plantypeView.setText(getResources().getString(R.string.subscripiton_monthplan));
            viewHolder.plandurationView.setText(getResources().getString(R.string.subscription_monthduration));
        } else {
            viewHolder.plantypeView.setText(getResources().getString(R.string.subscription_annualplan));
            viewHolder.plandurationView.setText(getResources().getString(R.string.subscription_monthduration));
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * user : rakeshk2
     * date : 10/20/2017
     * description : this method binds the service to the activity for the
     * In-App Purchase flow.
     **/
    private void bindInAppService() {
        Intent serviceIntent =
                new Intent(InAppConstants.bIND_BILLING);
        serviceIntent.setPackage(InAppConstants.VENDING);
        bindService(serviceIntent, mInAppConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * user : rakeshk2
     * date : 10/20/2017
     * description : this method initiates all the variables required for In-App Purchases.
     **/
    private void initInAppVariables() {
        // Create the helper, passing it our context and the public key to verify signatures with
        mHelper = new IabHelper(this, InAppConstants.base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {

                if (!result.isSuccess()) {
                    //there was a problem.
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) {
//                    DialogUtils.getInstance().showShortToast(WallpaperPurchaseActivity.this, getResources().getString(R.string.error_occurred));
//                    finish();
                }
            }
        });
    }

    /**
     * user : rakeshk2
     * date : 10/20/2017
     * description : This method starts the purchase flow for a managed product on google play.
     **/
    private void subscribePlan(String skuId, int subscriptionId) {
        try {
            if (skuId != null) {
                Bundle buyIntentBundle = mService.getBuyIntent(3, this.getPackageName(),
                        InAppConstants.ITEM_SKU, IabHelper.ITEM_TYPE_INAPP, InAppConstants.DEVELOPER_PAYLOAD);
                PendingIntent pendingIntent = buyIntentBundle.getParcelable(InAppConstants.INTENT_TYPE_BUY);
                try {
                    if (pendingIntent != null) {
                        startIntentSenderForResult(pendingIntent.getIntentSender(),
                                InAppConstants.REQUEST_CODE_BUY, new Intent(), 0, 0,
                                0, null);
                    }
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == InAppConstants.REQUEST_CODE_SUBSCRIBE) {
            int subscriptionId = -1;
//            int responseCode = data.getIntExtra(EVPurchaseConstants.IN_APP_RESPONSE_CODE, 0);
            String purchaseData = data.getStringExtra(InAppConstants.IN_APP_PURCHASE_DATA);
//            String dataSignature = data.getStringExtra(EVPurchaseConstants.IN_APP_DATA_SIGNATURE);
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                subscriptionId = bundle.getInt(InAppConstants.KEY_SUBSCRIPTION_ID);
            }

            if (resultCode == RESULT_OK) {
                sendSubscribeInfoToServer(purchaseData, subscriptionId);
            }
        }

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_CANCELED) {
                sendSubscribeInfoToServer("", 1);
                CommonUtils.showToastMessage(getString(R.string.payment_canceled));
            }
        } else {
            Log.i(TAG, "onActivityResult handled by IABUtil.");
        }
    }


    /**
     * user : rakeshk2
     * date : 10/23/2017
     * description : Send in app purchase response to the web server.
     **/
    private void sendSubscribeInfoToServer(String purchaseData, int subscriptionId) {
        if (CommonUtils.isOnline()) {
            RetrofitSingleton.SUBSCRIPTION_REST.createSubscription(1, 1/*Integer.parseInt(SharedUtils.USER_SERVER_ID)*/, String.valueOf(subscriptionId), purchaseData, "2017-11-15 21:59:28", 1).enqueue(new IndicatorCallback<ResponseObject<SubscriptionMainResponse>>(this, false) {
                @Override
                public void onResponse(Call<ResponseObject<SubscriptionMainResponse>> call, Response<ResponseObject<SubscriptionMainResponse>> response) {
                    super.onResponse(call, response);
                    if (response.body() != null) {
                        try {
                            final ResponseObject<SubscriptionMainResponse> responseBody = response.body();

                            if (responseBody.getError().equals(getString(R.string.false_text))) {
                                SubscriptionDtoNew subscriptionDtoNew = responseBody.getData().getSubscription();
                                SubscriptionUserDto subscriptionUserDto = responseBody.getData().getUser_subscription();

                                subscriptionDtoNew.getBattle_details();
//                                subscriptionStatusDto.getExpiry_date();
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } else {
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

    @Override
    public void btnClickSeeAllResponse(int id, String sku) {
        //Call back to get user's subscription choice.
        mSkuId = sku;
        mPurchaseId = id;
    }

    public class SubscriptionViewHolder {
        @BindView(R.id.tutorial_number)
        TextView tutorialnumView;
        @BindView(R.id.tutorial_value)
        TextView tutorialpriceView;
        @BindView(R.id.tournament_number)
        TextView tournamentnumView;
        @BindView(R.id.tournament_value)
        TextView tournamentpriceView;
        @BindView(R.id.battle_number)
        TextView battlenumView;
        @BindView(R.id.battle_value)
        TextView battlepriceView;
        @BindView(R.id.plan_type)
        TextView plantypeView;
        @BindView(R.id.plan_value)
        TextView planPriceView;
        @BindView(R.id.plan_duration)
        TextView plandurationView;

        public SubscriptionViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


}
