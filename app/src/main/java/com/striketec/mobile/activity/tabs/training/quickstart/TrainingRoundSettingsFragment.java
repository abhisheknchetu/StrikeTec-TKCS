package com.striketec.mobile.activity.tabs.training.quickstart;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.adapters.CustomSpinnerAdapter;
import com.striketec.mobile.adapters.PresetListAdapter;
import com.striketec.mobile.dto.PresetDto;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.CommonUtils;
import com.striketec.mobile.util.PresetUtil;
import com.striketec.mobile.util.SharedUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;


public class TrainingRoundSettingsFragment extends Fragment {

    public static String TAG = TrainingRoundSettingsFragment.class.getSimpleName();

    @BindView(R.id.presetbtn) Button presetBtn;
    @BindView(R.id.rounds_picker) WheelView roundsPicker;
    @BindView(R.id.round_picker) WheelView roundTimePicker;
    @BindView(R.id.rest_picker) WheelView restTimePicker;
    @BindView(R.id.prepare_parent) LinearLayout prepareParentView;
    @BindView(R.id.prepare_time) TextView prepareTimeView;
    @BindView(R.id.warning_parent) LinearLayout warningParentView;
    @BindView(R.id.warning_time) TextView warningTimeView;
    @BindView(R.id.total_time) TextView totalTimeView;
    @BindView(R.id.weight_parent) LinearLayout weightParentView;
    @BindView(R.id.weight) TextView weightView;
    @BindView(R.id.weight_spinner) Spinner weightSpinner;
    @BindView(R.id.glove_parent) LinearLayout gloveParentView;
    @BindView(R.id.glove) TextView gloveView;
    @BindView(R.id.glove_spinner) Spinner gloveSpinner;

    ArrayWheelAdapter roundsAdapter, roundtimeAdapter, resttimeAdapter;
    CustomSpinnerAdapter gloveSpinnerAdapter, weightSpinnerAdapter;

    PresetDto defaultPreset;
    ArrayList<PresetDto> savedPreset;
    int currentPresetPosition = 0;
    int currentPreparePosition = 0;
    int currentWarningPosition = 0;

    public static TrainingRoundSettingsFragment roundSettingsFragment;
    private static Context mContext;

    public static TrainingRoundSettingsFragment newInstance(Context context) {
        mContext = context;
        roundSettingsFragment = new TrainingRoundSettingsFragment();
        return roundSettingsFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_quickstart_round_settings, container, false);

        ButterKnife.bind(this, view);

        initViews();

        return view;
    }

    private void initViews(){
        //rounds wheel picker
        roundsAdapter = new ArrayWheelAdapter(getActivity(), PresetUtil.roundsList.toArray());
        roundsAdapter.setItemResource(R.layout.wheel_text_item);
        roundsAdapter.setItemTextResource(R.id.text);
        roundsAdapter.setActiveTextColor(Color.parseColor(getResources().getString(R.string.rounds_select)));
        roundsAdapter.setDeactiveTextColor(Color.parseColor(getResources().getString(R.string.rounds_unselect)));
        roundsAdapter.setActiveTextSzie(getResources().getInteger(R.integer.wheel_rounds_active));
        roundsAdapter.setDeactiveTextSize(getResources().getInteger(R.integer.wheel_rounds_deactive));
        roundsPicker.setViewAdapter(roundsAdapter);
        roundsPicker.setVisibleItems(3);

        //set round time picker
        roundtimeAdapter = new ArrayWheelAdapter(getActivity(), PresetUtil.timeList.toArray());
        roundtimeAdapter.setItemResource(R.layout.wheel_roundtime_text_item);
        roundtimeAdapter.setItemTextResource(R.id.text);
        roundtimeAdapter.setActiveTextColor(Color.parseColor(getResources().getString(R.string.roundtime_select)));
        roundtimeAdapter.setDeactiveTextColor(Color.parseColor(getResources().getString(R.string.roundtime_unselect)));
        roundtimeAdapter.setActiveTextSzie(getResources().getInteger(R.integer.wheel_roundtime_active));
        roundtimeAdapter.setDeactiveTextSize(getResources().getInteger(R.integer.wheel_roundtime_deactive));
        roundTimePicker.setViewAdapter(roundtimeAdapter);
        roundTimePicker.setVisibleItems(3);

        //set rest picker
        resttimeAdapter = new ArrayWheelAdapter(getActivity(), PresetUtil.timeList.toArray());
        resttimeAdapter.setItemResource(R.layout.wheel_roundtime_text_item);
        resttimeAdapter.setItemTextResource(R.id.text);
        resttimeAdapter.setActiveTextColor(Color.parseColor(getResources().getString(R.string.resttime_select)));
        resttimeAdapter.setDeactiveTextColor(Color.parseColor(getResources().getString(R.string.resttime_unselect)));
        resttimeAdapter.setActiveTextSzie(getResources().getInteger(R.integer.wheel_roundtime_active));
        resttimeAdapter.setDeactiveTextSize(getResources().getInteger(R.integer.wheel_roundtime_deactive));
        restTimePicker.setViewAdapter(resttimeAdapter);
        restTimePicker.setVisibleItems(3);


        weightSpinnerAdapter = new CustomSpinnerAdapter(getActivity(), R.layout.custom_spinner_digit_with_img, PresetUtil.weightList, AppConst.SPINNER_DIGIT_ORANGE);
        weightSpinner.setAdapter(weightSpinnerAdapter);

        gloveSpinnerAdapter = new CustomSpinnerAdapter(getActivity(), R.layout.custom_spinner_digit_with_img, PresetUtil.gloveList, AppConst.SPINNER_DIGIT_ORANGE);
        gloveSpinner.setAdapter(gloveSpinnerAdapter);

        savedPreset = SharedUtils.getPresetList();
        defaultPreset = new PresetDto("Preset 1", PresetUtil.roundsList.size() / 2, PresetUtil.timeList.size() / 2, PresetUtil.timeList.size() / 2, PresetUtil.timeList.size() / 2,
                PresetUtil.warningTimewithSecList.size() / 2);

        if (savedPreset == null )
            savedPreset = new ArrayList<>();

        if (savedPreset.size() > 0){
            showPresetInfo(savedPreset.get(0));
        }else {
            showPresetInfo(defaultPreset);
        }

        weightSpinner.setSelection(PresetUtil.weightList.size() / 2);
        gloveSpinner.setSelection(PresetUtil.gloveList.size() / 2);

        roundsPicker.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                setTotalTime();
            }
        });

        roundTimePicker.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                setTotalTime();
            }
        });

        restTimePicker.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                setTotalTime();
            }
        });
    }

    @OnClick({R.id.presetbtn, R.id.prepare_parent, R.id.warning_parent, R.id.weight_parent, R.id.glove_parent})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.presetbtn:
                showSavedPresetDialog();
                break;
            case R.id.prepare_parent:
                showWheelPicker(true);
                break;
            case R.id.warning_parent:
                showWheelPicker(false);
                break;
            case R.id.weight_parent:
                weightSpinner.performClick();
                break;
            case R.id.glove_parent:
                gloveSpinner.performClick();
                break;
        }
    }

    @OnItemSelected(R.id.weight_spinner)
    public void weightSelected (int position){
        weightView.setText(weightSpinner.getSelectedItem().toString());
    }

    @OnItemSelected(R.id.glove_spinner)
    public void gloveSelected (int position){
        gloveView.setText(gloveSpinner.getSelectedItem().toString());
    }

    public class WheelDialogViewHolder{
        @BindView(R.id.picker) WheelView picker;
        @BindView(R.id.okbtn) TextView positiveView;

        WheelDialogViewHolder(Dialog dialog){
            ButterKnife.bind(this, dialog);
        }
    }

    private void showWheelPicker(final boolean isprepare){
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_wheelpicker);
        dialog.setCancelable(false);

        WheelDialogViewHolder viewHolder = new WheelDialogViewHolder(dialog);

        //set prepare picker
        ArrayList dataList;

        if (isprepare)
            dataList = PresetUtil.timeList;
        else
            dataList = PresetUtil.warningTimewithSecList;

        ArrayWheelAdapter preparewarningAdapter = new ArrayWheelAdapter(getActivity(), dataList.toArray());

        preparewarningAdapter.setItemResource(R.layout.wheel_roundtime_text_item);
        preparewarningAdapter.setItemTextResource(R.id.text);
        preparewarningAdapter.setActiveTextColor(Color.parseColor(getResources().getString(R.string.prepare_select)));
        preparewarningAdapter.setDeactiveTextColor(Color.parseColor(getResources().getString(R.string.prepare_unselect)));
        preparewarningAdapter.setActiveTextSzie(getResources().getInteger(R.integer.wheel_roundtime_active));
        preparewarningAdapter.setDeactiveTextSize(getResources().getInteger(R.integer.wheel_roundtime_deactive));
        viewHolder.picker.setViewAdapter(preparewarningAdapter);
        viewHolder.picker.setVisibleItems(3);

        if (isprepare)
            viewHolder.picker.setCurrentItem(currentPreparePosition);
        else
            viewHolder.picker.setCurrentItem(currentWarningPosition);

        viewHolder.picker.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                if (isprepare){
                    currentPreparePosition = newValue;
                    prepareTimeView.setText(PresetUtil.timeList.get(newValue));
                    setTotalTime();
                }else {
                    currentWarningPosition = newValue;
                    warningTimeView.setText(PresetUtil.warningTimewithSecList.get(newValue));
                }
            }
        });

        viewHolder.positiveView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }

    private void setTotalTime(){
        int round = Integer.parseInt(PresetUtil.roundsList.get(roundsPicker.getCurrentItem()));
        int roundtime = Integer.parseInt(PresetUtil.timerwitSecsList.get(roundTimePicker.getCurrentItem()));
        int resttime = Integer.parseInt(PresetUtil.timerwitSecsList.get(restTimePicker.getCurrentItem()));
        int preparetime = Integer.parseInt(PresetUtil.timerwitSecsList.get(currentPreparePosition));

        String totalTime = PresetUtil.changeSecondsToMinutes(round * (roundtime + resttime)  + preparetime - resttime);
        totalTimeView.setText(totalTime);
    }

    private void showPresetInfo (PresetDto presetDTO){
        roundsPicker.setCurrentItem(presetDTO.getRounds());
        roundTimePicker.setCurrentItem(presetDTO.getRound_time());
        restTimePicker.setCurrentItem(presetDTO.getRest());
        currentPreparePosition = presetDTO.getPrepare();
        currentWarningPosition = presetDTO.getWarning();
        prepareTimeView.setText(PresetUtil.timeList.get(currentPreparePosition));
        warningTimeView.setText(PresetUtil.warningTimewithSecList.get(currentWarningPosition));

        presetBtn.setText(presetDTO.getName());

        setTotalTime();
    }

    private void showSavedPresetDialog(){
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_presetpopup);

        ListView presetListView = (ListView)dialog.findViewById(R.id.listview);
        final PresetListAdapter adapter = new PresetListAdapter(getActivity(), savedPreset, currentPresetPosition);
        presetListView.setAdapter(adapter);

        presetListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == savedPreset.size()){
                    saveNewDialog();
                    dialog.dismiss();
                }else {
                    PresetDto presetDTO = adapter.getItem(position);
                    currentPresetPosition = position;
                    showPresetInfo(presetDTO);
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    private void saveNewDialog(){
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Window window = dialog.getWindow();

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_savenew);

        final SaveDialogViewHolder saveDialogViewHolder = new SaveDialogViewHolder(dialog);
        saveDialogViewHolder.titleView.setText(getResources().getString(R.string.savenewpreset));
        saveDialogViewHolder.nameView.setHint(getResources().getString(R.string.hint_presetname));

        saveDialogViewHolder.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        saveDialogViewHolder.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(saveDialogViewHolder.nameView.getText().toString().trim())){
                    CommonUtils.showToastMessage(String.format(getResources().getString(R.string.empty_field_message), getResources().getString(R.string.preset)));
                    return;
                }else {
                    saveNewPreset(saveDialogViewHolder.nameView.getText().toString().trim());
                    dialog.dismiss();
                    currentPresetPosition = SharedUtils.getPresetList().size() - 1;
                }
            }
        });

        dialog.show();
    }


    private void saveNewPreset(String presetName){
        PresetDto presetDTO = new PresetDto();
        presetDTO.setRounds(roundsPicker.getCurrentItem());
        presetDTO.setRound_time(roundTimePicker.getCurrentItem());
        presetDTO.setRest(restTimePicker.getCurrentItem());
        presetDTO.setPrepare(currentPreparePosition);
        presetDTO.setWarning(currentWarningPosition);
        presetDTO.setName(presetName);

        savedPreset.add(presetDTO);
        SharedUtils.savePresetLists(savedPreset);
        presetBtn.setText(presetDTO.getName());
    }


    public PresetDto getCurrentPreset(){
        PresetDto presetDTO = new PresetDto();
        presetDTO.setRounds(roundsPicker.getCurrentItem());
        presetDTO.setRound_time(roundTimePicker.getCurrentItem());
        presetDTO.setRest(restTimePicker.getCurrentItem());
        presetDTO.setPrepare(currentPreparePosition);
        presetDTO.setWarning(currentWarningPosition);
        presetDTO.setName(presetBtn.getText().toString().trim());

        return presetDTO;

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

    public class SaveDialogViewHolder {
        @BindView(R.id.title) TextView titleView;
        @BindView(R.id.name)  EditText nameView;
        @BindView(R.id.ok_btn) Button saveBtn;
        @BindView(R.id.cancel_btn) Button cancelBtn;

        SaveDialogViewHolder(Dialog dialog){
            ButterKnife.bind(this, dialog);
        }
    }
}
