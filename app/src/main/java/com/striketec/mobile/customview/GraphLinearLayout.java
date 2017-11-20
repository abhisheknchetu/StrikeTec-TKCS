package com.striketec.mobile.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.dto.TrainingSessionDto;
import com.striketec.mobile.util.CommonUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Qiang on 8/26/2017.
 */

public class GraphLinearLayout extends LinearLayout{
    private float CURVE_STROKE_WIDTH = 2;
    private int GRID_COUNT = 6;

    private Context mContext;

    private static final String TAG = GraphLinearLayout.class.getSimpleName();

    private Paint borderPaint = new Paint();
    private Rect chartRect = new Rect();
    private LayoutInflater layoutInflater;

    int maxScore = 0;

    private int chartHeight, chartWidth;

    private ArrayList<TrainingSessionDto> sessionDtos = new ArrayList<>();

    private void init(){
        borderPaint.setColor(mContext.getResources().getColor(R.color.default_alpha));
        borderPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        borderPaint.setStrokeCap(Paint.Cap.SQUARE);
        borderPaint.setStrokeWidth(mContext.getResources().getInteger(R.integer.border_stroke_width));
        borderPaint.setAntiAlias(true);

        layoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public GraphLinearLayout(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public GraphLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    private void drawGrid(Canvas canvas, int height) {
        int gridCount = GRID_COUNT - 1;
        float part_y = (float) height / gridCount;

        for (int i = 1; i <= gridCount; i++) {
            float y = chartRect.top + part_y * i;
            canvas.drawLine(chartRect.left, y - mContext.getResources().getInteger(R.integer.border_stroke_width) + 1, chartRect.right, y - mContext.getResources().getInteger(R.integer.border_stroke_width) + 1, borderPaint);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        getDrawingRect(chartRect);

        chartHeight = chartRect.bottom - chartRect.top;
        chartWidth = chartRect.right - chartRect.left;
        drawGrid(canvas, chartHeight);
    }

    public  void  setData (ArrayList<TrainingSessionDto> data){
        this.sessionDtos = data;

        redrawViews();
    }

    private void calculateMaxScore(){
        maxScore = 0;

        for (int i = 0; i < sessionDtos.size(); i++){
            if (sessionDtos.get(i).mPunchCount > maxScore)
                maxScore = sessionDtos.get(i).mPunchCount;
        }
    }

    private void redrawViews(){
        removeAllViews();
        calculateMaxScore();

        drawBars();
    }

    private void drawBars(){

        for (int i = 0; i < sessionDtos.size(); i++){
            View convertView = layoutInflater.inflate(R.layout.child_bar_chart, null);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(CommonUtils.dpTopx(mContext.getResources().getInteger(R.integer.int_70)), LinearLayout.LayoutParams.MATCH_PARENT);
            convertView.setLayoutParams(params);
            addView(convertView, i);

            BarViewHolder barViewHolder = new BarViewHolder(convertView);
            barViewHolder.itemView.setText(String.valueOf(i+1));
            barViewHolder.barView.setSessionData(maxScore, sessionDtos.get(i));
        }

        requestLayout();
    }

    public class BarViewHolder {
        @BindView(R.id.itemnum) TextView itemView;
        @BindView(R.id.bar) Bar barView;

        public BarViewHolder (View view){
            ButterKnife.bind(this, view);
        }
    }
}

