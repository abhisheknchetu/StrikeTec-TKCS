package com.striketec.mobile.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.striketec.mobile.R;
import com.striketec.mobile.util.CommonUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Qiang on 8/26/2017.
 */

public class CompareLayout extends LinearLayout{
    private Context mContext;

    private static final String TAG = CompareLayout.class.getSimpleName();

    private LayoutInflater layoutInflater;

    private float maxValue;
    private float firstValue, lastValue;
    private boolean showAsFloat, orderFirst;
    private int firstDrawableResource, lastDrawableResource, textcolorResource;

    private Rect chartRect = new Rect();
    private int chartHeight, chartWidth;

    private void init(){
        layoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public CompareLayout(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public CompareLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        getDrawingRect(chartRect);

        chartHeight = chartRect.bottom - chartRect.top;
        chartWidth = chartRect.right - chartRect.left;
    }

    public  void  setData (float firstValue, float lastValue, boolean showAsFloat, boolean orderFirst, int firstDrawableResource, int lastDrawableResource, int textcolorResource){
        this.firstValue = firstValue;
        this.lastValue = lastValue;
        this.showAsFloat = showAsFloat;
        this.orderFirst = orderFirst;
        this.firstDrawableResource = firstDrawableResource;
        this.lastDrawableResource = lastDrawableResource;
        this.textcolorResource = textcolorResource;

        redrawViews();
    }

    private void calculateMaxScore(){
        if (firstValue > lastValue){
            if (orderFirst){
                maxValue = lastValue;
            }else
                maxValue = firstValue;
        }else {
            if (orderFirst)
                maxValue = firstValue;
            else
                maxValue = lastValue;
        }
    }

    private void redrawViews(){
        removeAllViews();
        calculateMaxScore();

        drawBars();
    }

    private void drawBars(){

        int height = 0;

        //draw first bar
        View convertView = layoutInflater.inflate(R.layout.child_compare_bar_item, null);
        LayoutParams params = new LayoutParams(CommonUtils.dpTopx(mContext.getResources().getInteger(R.integer.int_25)), LayoutParams.WRAP_CONTENT);
        convertView.setLayoutParams(params);

        addView(convertView, 0);

        final BarViewHolder barViewHolder = new BarViewHolder(convertView);

        //draw last bar

        View convertView1 = layoutInflater.inflate(R.layout.child_compare_bar_item, null);
        LayoutParams params1 = new LayoutParams(CommonUtils.dpTopx(mContext.getResources().getInteger(R.integer.int_25)), LayoutParams.WRAP_CONTENT);
        params1.setMargins(CommonUtils.dpTopx(mContext.getResources().getInteger(R.integer.int_5)), 0, 0, 0);
        convertView1.setLayoutParams(params1);
        addView(convertView1, 1);

        final BarViewHolder barViewHolder1 = new BarViewHolder(convertView1);

        if (showAsFloat){
            barViewHolder.itemView.setText(String.valueOf(firstValue));
            barViewHolder1.itemView.setText(String.valueOf(lastValue));
        }else {
            barViewHolder.itemView.setText(String.valueOf((int)firstValue));
            barViewHolder1.itemView.setText(String.valueOf((int)lastValue));
        }

        barViewHolder.itemView.setTextColor(mContext.getResources().getColor(textcolorResource));
        barViewHolder1.itemView.setTextColor(mContext.getResources().getColor(textcolorResource));
        barViewHolder.barView.setBackgroundResource(firstDrawableResource);
        barViewHolder1.barView.setBackgroundResource(lastDrawableResource);

        final LayoutParams barparam1 = (LayoutParams)barViewHolder.barView.getLayoutParams();
        barViewHolder.barView.setLayoutParams(barparam1);

        final LayoutParams barparam2 = (LayoutParams)barViewHolder1.barView.getLayoutParams();
        barViewHolder1.barView.setLayoutParams(barparam2);

        ViewTreeObserver viewTreeObserver = barViewHolder.barView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                barViewHolder.barView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int height = barViewHolder.barView.getMeasuredHeight();
                int height1 = 0, height2 = 0;

                //calculate height of bar1, bar2

                if (orderFirst){
                    if (firstValue > lastValue){
                        height2 = height;
                        height1 = (int)(height2 * lastValue / firstValue);
                    }else {
                        height1 = height;
                        height2 = (int)(height1 * firstValue / lastValue);
                    }
                }else {
                    if (firstValue > lastValue){
                        height1 = height;
                        height2 = (int)(height1 * lastValue / firstValue);
                    }else {
                        height2 = height;
                        height1 = (int)(height2 * firstValue / lastValue);
                    }
                }

                barparam1.height = height1;
                barparam2.height = height2;

                barViewHolder.barView.setLayoutParams(barparam1);
                barViewHolder1.barView.setLayoutParams(barparam2);
            }
        });




    }

    public class BarViewHolder {
        @BindView(R.id.value) TextView itemView;
        @BindView(R.id.bar) View barView;

        public BarViewHolder (View view){
            ButterKnife.bind(this, view);
        }
    }
}

