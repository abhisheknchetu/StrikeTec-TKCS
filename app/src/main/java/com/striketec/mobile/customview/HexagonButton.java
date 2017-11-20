package com.striketec.mobile.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;

import com.striketec.mobile.R;
import com.striketec.mobile.util.FontManager;

public class HexagonButton extends Button {

    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    private static final int NONE = 2;

    private Path hexagonPath;
    private Paint pathPaint;
    private int mColor;
    private int mCoef;
    private boolean fill;
    private int mType;

    public HexagonButton(Context context) {
        super(context);
        init(context, null);
    }

    public HexagonButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public HexagonButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        mColor = Color.YELLOW;
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs, R.styleable.HexagonButton, 0, 0);
            try {
                mColor = a.getColor(R.styleable.HexagonButton_hexagon_color, Color.YELLOW);
                mCoef = a.getInt(R.styleable.HexagonButton_angle_coef, 30);
                fill = a.getBoolean(R.styleable.HexagonButton_fill, true);
                mType = a.getInt(R.styleable.HexagonButton_hb_type, NONE);
                if (mCoef > 100) {
                    mCoef = 100;
                }
            } catch (Exception e) {

            } finally {
                a.recycle();
            }
        }
        setGravity(Gravity.CENTER);
        setBackground(null);
        setTypeface(FontManager.blenderproBook);
        init();
    }

    private void init() {
        this.hexagonPath = new Path();
        this.pathPaint = new Paint();
        this.pathPaint.setColor(mColor);

        if (fill) {
            this.pathPaint.setStyle(Paint.Style.FILL);
        } else {
            this.pathPaint.setStyle(Paint.Style.STROKE);
        }
        this.pathPaint.setStrokeWidth(3f);
    }

    private void calculatePath() {
        float width = getMeasuredWidth();
        float height = getMeasuredHeight();
        float corner = height * (mCoef / 100f);
        float margin = height * 0.03f;

        this.hexagonPath.reset();
        if (mType == NONE) {
            this.hexagonPath.moveTo(corner, margin);
            this.hexagonPath.lineTo(width - corner, margin);
            this.hexagonPath.lineTo(width - margin, height / 2);
            this.hexagonPath.lineTo(width - corner, height - margin);
            this.hexagonPath.lineTo(corner, height - margin);
            this.hexagonPath.lineTo(margin, height / 2);
        } else if (mType == LEFT) {
            this.hexagonPath.moveTo(margin, margin);
            this.hexagonPath.lineTo(width - corner, margin);
            this.hexagonPath.lineTo(width - margin, height / 2);
            this.hexagonPath.lineTo(width - corner, height - margin);
            this.hexagonPath.lineTo(margin, height - margin);
        } else if (mType == RIGHT) {
            this.hexagonPath.moveTo(corner, margin);
            this.hexagonPath.lineTo(width - margin, margin);
            this.hexagonPath.lineTo(width - margin, height - margin);
            this.hexagonPath.lineTo(corner, height - margin);
            this.hexagonPath.lineTo(margin, height / 2);
        }
        this.hexagonPath.close();
        invalidate();
    }

    public void setFill(boolean fill){
        this.fill = fill;
        init();
        calculatePath();
    }

    @Override
    public void onDraw(Canvas c) {
        c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.OVERLAY);
//        c.clipPath(hexagonPath, Region.Op.INTERSECT);
        c.drawPath(hexagonPath, pathPaint);
        super.onDraw(c);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
        calculatePath();
    }
}
