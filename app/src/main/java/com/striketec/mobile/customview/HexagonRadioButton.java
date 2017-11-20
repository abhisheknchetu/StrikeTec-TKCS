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
import android.widget.RadioButton;

import com.striketec.mobile.R;
import com.striketec.mobile.util.FontManager;

public class HexagonRadioButton extends RadioButton {

    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    private static final int MIDDLE = 2;
    private static final int BOTH = 2;
    private static final int NONE = 3;

    private Path hexagonPath;
    private Path selectorPath;
    private Paint mSelectedPaint;
    private Paint mUnselectedPaint;
    private Paint mSelectorPaint;
    private int mType;
    private int mBorder;

    private int mSelectedTextColor;
    private int mUnselectedTextColor;
    private int mHighlightColor;
    private int mHexagonColor;
    private int mHightlightWidth;
    private int mCoef;
    private boolean fill;

    public HexagonRadioButton(Context context) {
        super(context);
        init(context, null);
    }

    public HexagonRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public HexagonRadioButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        mSelectedTextColor = Color.parseColor("#ffffff");
        mUnselectedTextColor = Color.parseColor("#dfa543");
        mHighlightColor =Color.parseColor("#39E3ED");
        mHexagonColor =Color.parseColor("#5039E3Ed");
        fill = false;
        mHightlightWidth = 100;
        mCoef = 30;

        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs, R.styleable.HexagonRadioButton, 0, 0);
            try {
                mType = a.getInt(R.styleable.HexagonRadioButton_hrb_type, LEFT);
                mBorder = a.getInt(R.styleable.HexagonRadioButton_hrb_divider, NONE);

                mSelectedTextColor = a.getColor(R.styleable.HexagonRadioButton_selected_color, Color.parseColor("#ffffff"));
                mUnselectedTextColor = a.getColor(R.styleable.HexagonRadioButton_unselected_color,Color.parseColor("#dfa543"));
                mHighlightColor = a.getColor(R.styleable.HexagonRadioButton_highlight_color, Color.parseColor("#39E3ED"));
                mHexagonColor = a.getColor(R.styleable.HexagonRadioButton_radio_hexagon_color, Color.parseColor("#5039E3Ed"));

                mCoef = a.getInt(R.styleable.HexagonRadioButton_radio_angle_coef, 30);
                fill = a.getBoolean(R.styleable.HexagonRadioButton_radio_fill, true);
                mHightlightWidth = a.getDimensionPixelSize(R.styleable.HexagonRadioButton_highlight_width, 40);

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
        setButtonDrawable(null);
        setBackgroundColor(getResources().getColor(android.R.color.transparent));
        setTypeface(FontManager.blenderproBook);
        init();
    }

    private void init() {
        this.hexagonPath = new Path();
        this.selectorPath = new Path();

        this.mSelectedPaint = new Paint();
        this.mSelectedPaint.setColor(mHexagonColor);

        if (fill) {
            this.mSelectedPaint.setStyle(Paint.Style.FILL);
        } else {
            this.mSelectedPaint.setStyle(Paint.Style.STROKE);
        }

        this.mSelectedPaint.setStrokeWidth(3f);

        this.mSelectorPaint = new Paint();
        this.mSelectorPaint.setColor(mHighlightColor);
        this.mSelectorPaint.setStyle(Paint.Style.FILL);

        this.mUnselectedPaint = new Paint();
        this.mUnselectedPaint.setColor(mHexagonColor);
        if (fill) {
            this.mUnselectedPaint.setStyle(Paint.Style.FILL);
        } else {
            this.mUnselectedPaint.setStyle(Paint.Style.STROKE);
        }

        if (isChecked()) {
            setTextColor(mSelectedTextColor);
        } else {
            setTextColor(mUnselectedTextColor);
        }
    }

    private void calculatePath() {
        float width = getMeasuredWidth();
        float height = getMeasuredHeight();
        float corner = height * (mCoef / 100f);
        float margin = height * 0.03f;

        this.hexagonPath.reset();

        if (mType == LEFT) {
            this.hexagonPath.moveTo(width, margin);
            this.hexagonPath.lineTo(corner, margin);
            this.hexagonPath.lineTo(margin, height / 2);
            this.hexagonPath.lineTo(corner, height - margin);
            this.hexagonPath.lineTo(width, height - margin);
        } else if (mType == MIDDLE) {
            this.hexagonPath.moveTo(0, margin);
            this.hexagonPath.lineTo(width, margin);
            this.hexagonPath.moveTo(width, height - margin);
            this.hexagonPath.lineTo(0, height - margin);
        } else {
            this.hexagonPath.moveTo(0, margin);
            this.hexagonPath.lineTo(width - corner, margin);
            this.hexagonPath.lineTo(width - margin, height / 2);
            this.hexagonPath.lineTo(width - corner, height - margin);
            this.hexagonPath.lineTo(0, height - margin);
        }

        if (mBorder != NONE) {
            float dHeight = height * (40f / 100f);
            float stY = (height - dHeight) / 2f;
            float endY = height - stY;
            if (mBorder == RIGHT) {
                this.hexagonPath.moveTo(width, stY);
                this.hexagonPath.lineTo(width, endY);
            } else if (mBorder == LEFT) {
                this.hexagonPath.moveTo(0, stY);
                this.hexagonPath.lineTo(0, endY);
            } else if (mBorder == BOTH) {
                this.hexagonPath.moveTo(0, stY);
                this.hexagonPath.lineTo(0, endY);
                this.hexagonPath.moveTo(width, stY);
                this.hexagonPath.lineTo(width, endY);
            }
        }

        this.selectorPath.reset();
        float selectorWidth = mHightlightWidth;
        float middle = width / 2;
        float selectorHeight = 6f;

        this.selectorPath.moveTo(middle - (selectorWidth / 2), margin);
        this.selectorPath.lineTo(middle + (selectorWidth / 2), margin);
        this.selectorPath.lineTo(middle + (selectorWidth / 2), selectorHeight + margin);
        this.selectorPath.lineTo(middle - (selectorWidth / 2), selectorHeight + margin);
        this.selectorPath.close();

        this.selectorPath.moveTo(middle + (selectorWidth / 2), height- (margin));
        this.selectorPath.lineTo(middle - (selectorWidth / 2), height - (margin));
        this.selectorPath.lineTo(middle - (selectorWidth / 2), height - selectorHeight - (margin));
        this.selectorPath.lineTo(middle + (selectorWidth / 2), height - selectorHeight - (margin));
        this.selectorPath.close();

        invalidate();
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        if (isChecked()) {
            setTextColor(mSelectedTextColor);
        } else {
            setTextColor(mUnselectedTextColor);
        }
        invalidate();
    }

    @Override
    public void onDraw(Canvas c) {

        c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.OVERLAY);
        c.drawPath(hexagonPath, mSelectedPaint);

        if (isChecked()) {
            c.drawPath(selectorPath, mSelectorPaint);
        }

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
