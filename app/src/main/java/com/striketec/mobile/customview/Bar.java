package com.striketec.mobile.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.striketec.mobile.R;
import com.striketec.mobile.dto.TrainingSessionDto;
import com.striketec.mobile.util.CommonUtils;

/**
 * Created by Qiang on 8/26/2017.
 */

public class Bar extends View{

    private Context mContext;
    private TrainingSessionDto sessionDto;
    private int maxScore = 1;
    private Paint fillPaint = new Paint();
    private Rect barchartRect = new Rect();

    private int barchartHeight, barchartWidth;

    public Bar(Context context) {
        super(context);
        init(context);
    }

    public Bar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Bar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setSessionData(int maxScore, TrainingSessionDto sessionData){
        this.sessionDto = sessionData;
        this.maxScore = maxScore;
        initBar();
    }

    private void init(Context context){
        this.mContext = context;
        fillPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        fillPaint.setStrokeCap(Paint.Cap.SQUARE);
        fillPaint.setAntiAlias(true);
    }



    private void initBar(){

        int compareResult = CommonUtils.compareCount(sessionDto);

        if(compareResult == 0){
            fillPaint.setColor(mContext.getResources().getColor(R.color.red));
        }else if(compareResult == 1){
            fillPaint.setColor(mContext.getResources().getColor(R.color.orange));
        }else {
            fillPaint.setColor(mContext.getResources().getColor(R.color.speed_color));
        }

        fillPaint.setAlpha(180);

        invalidate();
    }

    private void drawbar(Canvas canvas, int width, int height){
        Rect rect = new Rect();
        rect.left = barchartRect.left + width / 3;
        rect.right = barchartRect.right - width / 3;
        rect.bottom = barchartRect.bottom;

        float ratio = sessionDto.mPunchCount / (maxScore * 1.1f);
        rect.top = barchartRect.bottom - (int)( height * ratio);
        canvas.drawRect(rect, fillPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (sessionDto != null){
            getDrawingRect(barchartRect);

            barchartHeight = barchartRect.bottom - barchartRect.top;
            barchartWidth = barchartRect.right - barchartRect.left;
            drawbar(canvas, barchartWidth, barchartHeight);
        }
    }
}

