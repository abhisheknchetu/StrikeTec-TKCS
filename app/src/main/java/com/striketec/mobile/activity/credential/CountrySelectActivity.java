package com.striketec.mobile.activity.credential;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.BaseActivity;
import com.striketec.mobile.adapters.CountryListAdapter;
import com.striketec.mobile.dto.CountryStateCityDto;
import com.striketec.mobile.dto.ServerCountryStateCityResponseDto;
import com.striketec.mobile.restapi.IndicatorCallback;
import com.striketec.mobile.restapi.RetrofitSingleton;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.CommonUtils;
import com.striketec.mobile.util.PresetUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnItemClick;
import butterknife.OnTextChanged;
import retrofit2.Call;
import retrofit2.Response;

public class CountrySelectActivity extends BaseActivity {

    public static String TAG = CountrySelectActivity.class.getSimpleName();

    @BindView(R.id.cancelview) TextView searchCancelView;
    @BindView(R.id.searchview) EditText searchView;
    @BindView(R.id.listview) ListView listView;

    CountryListAdapter adapter;

    String type;
    int data;
    int currentData;
    boolean isfilter = false;

    private ArrayList<CountryStateCityDto>  dataList = new ArrayList<>();
    private ArrayList<CountryStateCityDto>  searchResultList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_select);

        ButterKnife.bind(this);

        isfilter = getIntent().getBooleanExtra(AppConst.FILTER, false);

        type = getIntent().getStringExtra(AppConst.TYPE);

        if (type.equals(AppConst.STATE)){
            data = getIntent().getIntExtra(AppConst.COUNTRY, -1);
            currentData = getIntent().getIntExtra(AppConst.STATE, -1);
        }else if (type.equals(AppConst.CITY)){
            data = getIntent().getIntExtra(AppConst.STATE, -1);
            currentData = getIntent().getIntExtra(AppConst.CITY, -1);
        }else if (type.equals(AppConst.COUNTRY)){
            data = -1;
            currentData = getIntent().getIntExtra(AppConst.COUNTRY, -1);
        }

        initViews();
        setKeyboardAction(this.getWindow().getDecorView().getRootView());
    }

    @OnClick({R.id.cancelview})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.cancelview:

                hideSoftKeyboard();

                searchView.setText("");
                searchView.clearFocus();
                searchQuery();
                break;
        }
    }

    @OnTextChanged(R.id.searchview)
    public void onTextChanged(CharSequence s){
        if (AppConst.DEBUG)
            Log.e(TAG, "query view on text chagned");

        searchQuery();
    }

    @OnEditorAction(R.id.searchview)
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event){

        if (AppConst.DEBUG)
            Log.e(TAG, "on editor action");

        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            hideSoftKeyboard();
            return true;
        }

        return false;
    }

    @OnItemClick(R.id.listview)
    public void onItemClicked(int position){
        finishWithResult(searchResultList.get(position));
    }

    private void initViews(){
        adapter = new CountryListAdapter(this, searchResultList);
        listView.setAdapter(adapter);
        adapter.setCurrentPosition(currentData);

        searchView.clearFocus();

        loadDatas();
        searchCancelView.performClick();
    }

    private void searchQuery(){
        String queryText = searchView.getText().toString().trim();

        searchResultList.clear();

        if (TextUtils.isEmpty(queryText)){
            searchResultList.addAll(dataList);
        }else {
            for (int i = 0; i < dataList.size(); i++){
                if (dataList.get(i).mName.toLowerCase().contains(queryText.toLowerCase()))
                    searchResultList.add(dataList.get(i));
            }
        }

        adapter.setData(searchResultList);
        adapter.notifyDataSetChanged();
    }

    public void finishWithResult(CountryStateCityDto data){
        Intent intent = new Intent();

        intent.putExtra(type, data);

        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void loadDatas(){
        if (type.equals(AppConst.COUNTRY)){
            if (PresetUtil.countryList != null && PresetUtil.countryList.size() > 0){
                dataList.clear();
                if (!isfilter) {
                    dataList.addAll(PresetUtil.countryList);
                }else {
                    dataList.addAll(PresetUtil.worldcountryList);
                }
                searchQuery();
            }else {
                if (CommonUtils.isOnline()){
                    RetrofitSingleton.DATA_REST.getCountries().enqueue(new IndicatorCallback<ServerCountryStateCityResponseDto>(this, false) {
                        @Override
                        public void onResponse(Call<ServerCountryStateCityResponseDto> call, Response<ServerCountryStateCityResponseDto> response) {
                            super.onResponse(call, response);
                            if (response.body() != null) {
                                final ServerCountryStateCityResponseDto responseDto = response.body();

                                if (!responseDto.mError){
                                    dataList.clear();

                                    PresetUtil.countryList.clear();
                                    PresetUtil.countryList.addAll(responseDto.mData);

                                    CountryStateCityDto world = new CountryStateCityDto();
                                    world.mName = getResources().getString(R.string.world);
                                    world.mId = 0;
                                    PresetUtil.worldcountryList.add(0, world);

                                    if (!isfilter) {
                                        dataList.addAll(PresetUtil.countryList);
                                    }else {
                                        dataList.addAll(PresetUtil.worldcountryList);
                                    }

                                    searchQuery();
                                }else {
                                    if (!TextUtils.isEmpty(responseDto.mMessage)){
                                        CommonUtils.showAlert(CountrySelectActivity.this, responseDto.mMessage);
                                    }
                                }
                            } else {

                            }
                        }

                        @Override
                        public void onFailure(Call<ServerCountryStateCityResponseDto> call, Throwable t) {
                            super.onFailure(call, t);
                            CommonUtils.showToastMessage(t.getLocalizedMessage());
                        }
                    });
                }else {
                    CommonUtils.showToastMessage(getResources().getString(R.string.nointernet));
                }
            }

        }else if (type.equals(AppConst.STATE) || type.equals(AppConst.CITY)){
            if (CommonUtils.isOnline()){
                Call call = null;

                if (type.equals(AppConst.STATE)){
                    call = RetrofitSingleton.DATA_REST.getStates(data);
                }else {
                    call = RetrofitSingleton.DATA_REST.getCities(data);
                }

                if (call != null){
                    call.enqueue(new IndicatorCallback<ServerCountryStateCityResponseDto>(this, false) {
                        @Override
                        public void onResponse(Call<ServerCountryStateCityResponseDto> call, Response<ServerCountryStateCityResponseDto> response) {
                            super.onResponse(call, response);
                            if (response.body() != null) {
                                final ServerCountryStateCityResponseDto responseDto = response.body();

                                if (!responseDto.mError){
                                    dataList.clear();

                                    dataList.addAll(responseDto.mData);
                                    searchQuery();
                                }else {
                                    if (!TextUtils.isEmpty(responseDto.mMessage)){
                                        CommonUtils.showAlert(CountrySelectActivity.this, responseDto.mMessage);
                                    }
                                }
                            } else {

                            }
                        }

                        @Override
                        public void onFailure(Call<ServerCountryStateCityResponseDto> call, Throwable t) {
                            super.onFailure(call, t);
                            CommonUtils.showToastMessage(t.getLocalizedMessage());
                        }
                    });
                }
            }else {
                CommonUtils.showToastMessage(getResources().getString(R.string.nointernet));
            }
        }
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


}
