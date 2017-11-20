package com.striketec.mobile.activity.chat;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.BaseActivity;
import com.striketec.mobile.adapters.NewChatConnectionListAdapter;
import com.striketec.mobile.adapters.RecentChatListAdapter;
import com.striketec.mobile.dto.ChatDto;
import com.striketec.mobile.dto.UserDto;
import com.striketec.mobile.util.CommonUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnTextChanged;

public class NewChatChooseActivity extends BaseActivity {

    @BindView(R.id.title) TextView titleView;
    @BindView(R.id.listview) ListView listView;
    @BindView(R.id.searchview) EditText searchView;

    NewChatConnectionListAdapter adapter;

    private ArrayList<UserDto> userDtos = new ArrayList<>();
    private ArrayList<UserDto> listUserDtos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newchoosechat);

        ButterKnife.bind(this);

        initViews();
    }

    private void initViews(){
        titleView.setText(getResources().getString(R.string.newchat));

        adapter = new NewChatConnectionListAdapter(this, listUserDtos);
        listView.setAdapter(adapter);

        loadConnections();
    }

    @OnClick({R.id.search, R.id.back})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.search:
                searchView.clearFocus();
                hideSoftKeyboard();
                performSearch();
                break;

            case R.id.back:
                finish();
                break;
        }
    }

    @OnEditorAction(R.id.searchview)
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event){

        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            searchView.clearFocus();
            hideSoftKeyboard();
            performSearch();
            return true;
        }

        return false;
    }

    @OnTextChanged({R.id.searchview})
    public void queryChanged(CharSequence s){
        //sort from old
        performSearch();
    }

    private void performSearch(){
        String query = searchView.getText().toString().trim();

        listUserDtos.clear();
        if (TextUtils.isEmpty(query)){
            listUserDtos.addAll(userDtos);
        }else {
            for (int i = 0; i < userDtos.size(); i++){
                String username = userDtos.get(i).mFirstName + " " + userDtos.get(i).mlastName;
                if (username.toLowerCase().contains(query.toLowerCase())){
                    listUserDtos.add(userDtos.get(i));
                }
            }
        }

        adapter.notifyDataSetChanged();
    }



    private void loadConnections(){

        for (int i = 0; i < 100; i++){
            UserDto userDto = new UserDto();
            int randomNam = CommonUtils.getRandomNum(100, 10);

            if (randomNam < 20){
                userDto.mFirstName = "Dus";
                userDto.mlastName = "Tucker";
            }else if(randomNam < 30){
                userDto.mFirstName = "Joe";
                userDto.mlastName = "Tucker";
            }else if(randomNam < 40){
                userDto.mFirstName = "Jeanette";
                userDto.mlastName = "Sanders";
            }else if(randomNam < 50){
                userDto.mFirstName = "Ana";
                userDto.mlastName = "Sanders";
            }else if(randomNam < 60){
                userDto.mFirstName = "Caleb";
                userDto.mlastName = "Shelton";
            }else if(randomNam < 70){
                userDto.mFirstName = "John";
                userDto.mlastName = "Smith";
            }else if(randomNam < 80){
                userDto.mFirstName = "Wes";
                userDto.mlastName = "Elliot";
            }else if(randomNam < 90){
                userDto.mFirstName = "Qiang";
                userDto.mlastName = "Hu";
            }else if(randomNam < 100){
                userDto.mFirstName = "Nawaz";
                userDto.mlastName = "Nawaz";
            }

            userDtos.add(userDto);
        }

        listUserDtos.addAll(userDtos);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
