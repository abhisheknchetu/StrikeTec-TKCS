package com.striketec.mobile.activity.tabs.more;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.activity.BaseActivity;
import com.striketec.mobile.adapters.QuestionListAdapter;
import com.striketec.mobile.dto.QuestionDto;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HelpcenterActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView titleView;
    @BindView(R.id.second_image)
    ImageView secondImageView;
    @BindView(R.id.listview)
    ListView questionListView;

    QuestionListAdapter adapter;
    ArrayList<QuestionDto> questionDtos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helpcenter);

        ButterKnife.bind(this);

        initViews();
    }

    private void initViews(){
        titleView.setText(getResources().getString(R.string.helpcenter));
        secondImageView.setVisibility(View.VISIBLE);
        secondImageView.setImageResource(R.drawable.icon_edit);

        adapter = new QuestionListAdapter(this, questionDtos);
        questionListView.setAdapter(adapter);

        loadQuestions();
    }

    @OnClick({R.id.back, R.id.second_image})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;

            case R.id.second_image:
                Intent writeusIntent = new Intent(HelpcenterActivity.this, ContactusActivity.class);
                startActivity(writeusIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
        }
    }

    private void loadQuestions(){
        int count = 20;
        questionDtos.clear();

        for (int i = 0; i < count; i++){
            questionDtos.add(new QuestionDto(getResources().getString(R.string.question1_subject), getResources().getString(R.string.question1_message)));
        }

        adapter.setData(questionDtos);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadQuestions();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
