package com.striketec.mobile.activity.tabs.more;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.striketec.mobile.R;
import com.striketec.mobile.StriketecApp;
import com.striketec.mobile.activity.credential.SigninActivity;
import com.striketec.mobile.activity.onboard.OnboardActivity;
import com.striketec.mobile.activity.settings.SettingsActivity;
import com.striketec.mobile.activity.subscription.SubscriptionActivity;
import com.striketec.mobile.activity.tabs.MainActivity;
import com.striketec.mobile.activity.tabs.more.goals.GoalsActivity;
import com.striketec.mobile.activity.tabs.more.myplan.MyTrainingplanActivity;
import com.striketec.mobile.activity.tabs.more.profile.ProfileActivity;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.CommonUtils;
import com.striketec.mobile.util.SharedUtils;

import java.lang.reflect.Field;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MoreFragment extends Fragment {

    public static String TAG = MoreFragment.class.getSimpleName();

    @BindView(R.id.profile) ImageView profileImageView;
    @BindView(R.id.rank) TextView rankView;
    @BindView(R.id.fullname) TextView fullnameView;
    @BindView(R.id.point) TextView pointView;
    @BindView(R.id.skill_image) ImageView skillimageView;
    @BindView(R.id.skill_content) TextView skillcontentView;

    private MainActivity mainActivity;

    public static MoreFragment moreFragment;
    private static Context mContext;

    public static MoreFragment newInstance(Context context) {
        mContext = context;
        moreFragment = new MoreFragment();
        return moreFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mainActivity = (MainActivity)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_more, container, false);

        ButterKnife.bind(this, view);

        initViews();

        return view;
    }

    private void initViews(){
        if (StriketecApp.currentUser != null){
            String fullname = "";
            if (!TextUtils.isEmpty(StriketecApp.currentUser.mFirstName))
                fullname = StriketecApp.currentUser.mFirstName;

            if (!TextUtils.isEmpty(StriketecApp.currentUser.mlastName)){
                if (TextUtils.isEmpty(fullname))
                    fullname = StriketecApp.currentUser.mlastName;
                else
                    fullname = fullname + " " + StriketecApp.currentUser.mlastName;
            }

            if (!TextUtils.isEmpty(StriketecApp.currentUser.mPhoto)){
                Glide.with(this).load(StriketecApp.currentUser.mPhoto).into(profileImageView);
            }

            if (!TextUtils.isEmpty(fullname))
                fullnameView.setText(fullname);
        }
    }

    @OnClick({R.id.profile_layout, R.id.onboard_layout, R.id.settings_layout, R.id.mysubscription_layout, R.id.store_layout,
              R.id.goals_layout, R.id.mytrainingplan_layout, R.id.helpcenter_layout, R.id.contactus_layout, R.id.aboutstrike_layout, R.id.logout_layout})
    public void onClick(View view){
        switch (view.getId()){

            case R.id.profile_layout:
                Intent profileIntent = new Intent(mainActivity, ProfileActivity.class);
                startActivity(profileIntent);
                mainActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;

            case R.id.onboard_layout:
                Intent onboardIntent = new Intent(mainActivity, OnboardActivity.class);
                onboardIntent.putExtra(AppConst.ONBOARD_FROMMORE, true);
                startActivity(onboardIntent);
                mainActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;

            case R.id.settings_layout:
                Intent settingsIntent = new Intent(mainActivity, SettingsActivity.class);
                startActivity(settingsIntent);
                mainActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;

            case R.id.mysubscription_layout:
                Intent subscription = new Intent(mainActivity, SubscriptionActivity.class);
                startActivity(subscription);
                mainActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;

            case R.id.store_layout:
                Intent storeIntent = new Intent(mainActivity, StoreActivity.class);
                startActivity(storeIntent);
                mainActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;

            case R.id.goals_layout:
                Intent goalIntent = new Intent(mainActivity, GoalsActivity.class);
                startActivity(goalIntent);
                mainActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;

            case R.id.mytrainingplan_layout:
                Intent planIntent = new Intent(mainActivity, MyTrainingplanActivity.class);
                startActivity(planIntent);
                mainActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;

            case R.id.helpcenter_layout:
                Intent helpcenterIntent = new Intent(mainActivity, HelpcenterActivity.class);
                startActivity(helpcenterIntent);
                mainActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;

            case R.id.contactus_layout:
                Intent writeusIntent = new Intent(mainActivity, ContactusActivity.class);
                startActivity(writeusIntent);
                mainActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;

            case R.id.aboutstrike_layout:
                Intent aboutIntent = new Intent(mainActivity, AboutusActivity.class);
                startActivity(aboutIntent);
                mainActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;

            case R.id.logout_layout:
                SharedUtils.saveBoolenValue(SharedUtils.REMEMBER_ME, false);
                SharedUtils.saveStringValue(SharedUtils.LOGGEDINTYPE, "");
                SharedUtils.saveStringValue(SharedUtils.EMAIl, "");
                SharedUtils.saveStringValue(SharedUtils.PWD, "");
                SharedUtils.saveStringValue(SharedUtils.AUTH, "");
                SharedUtils.saveStringValue(SharedUtils.FBID, "");

                Intent signinIntent = new Intent(mainActivity, SigninActivity.class);
                startActivity(signinIntent);
                mainActivity.finish();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
