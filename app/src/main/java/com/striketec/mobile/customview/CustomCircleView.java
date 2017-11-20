package com.striketec.mobile.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CustomCircleView extends View {
    private static final int START_ANGLE_POINT = -90;
    private float mDensity;

    private final Paint activePaint;
    float mRadius;
    private final Paint deactivePaint;
    private final Paint innerPaint;
    private RectF rect_in, inner_rect;

    private float angle, inner_angle;
    private int strokeWidth = 18;
    private boolean hasinner = true;

    public CustomCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mDensity = getResources().getDisplayMetrics().density;

        activePaint = new Paint();
        activePaint.setAntiAlias(true);
        activePaint.setStyle(Paint.Style.STROKE);
        activePaint.setStrokeWidth(strokeWidth);

        //Circle color
        activePaint.setColor(Color.WHITE);

        deactivePaint = new Paint();
        deactivePaint.setAntiAlias(true);
        deactivePaint.setStyle(Paint.Style.STROKE);
        deactivePaint.setStrokeWidth(strokeWidth);

        innerPaint = new Paint();
        innerPaint.setAntiAlias(true);
        innerPaint.setStyle(Paint.Style.STROKE);
        innerPaint.setStrokeWidth(strokeWidth);

        innerPaint.setColor(Color.BLUE);

        //Circle color
        deactivePaint.setColor(Color.RED);
        angle = 90;
        inner_angle = 180;
    }

    public void setStrokeWidth(int strokeWidth){
        this.strokeWidth = strokeWidth;
        deactivePaint.setStrokeWidth(strokeWidth);
        activePaint.setStrokeWidth(strokeWidth);
        innerPaint.setStrokeWidth(strokeWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        rect_in = new RectF(strokeWidth / 2, strokeWidth / 2, getWidth() - strokeWidth, getHeight() - strokeWidth);
        canvas.drawArc(rect_in, START_ANGLE_POINT, angle, false, activePaint);
        canvas.drawArc(rect_in, START_ANGLE_POINT + angle, 360 - angle, false, deactivePaint);

        if (hasinner) {
            inner_rect = new RectF(strokeWidth / 2 + strokeWidth + 3, strokeWidth / 2 + strokeWidth + 3, getWidth() - 2 * strokeWidth - 3, getHeight() - 2 * strokeWidth - 3);
            canvas.drawArc(inner_rect, START_ANGLE_POINT, inner_angle, false, innerPaint);
            canvas.drawArc(inner_rect, START_ANGLE_POINT + inner_angle, 360 - inner_angle, false, deactivePaint);
        }
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public float getAngle(){
        return angle;
    }

    public float getInnerAngle(){
        return inner_angle;
    }

    public void setInner(boolean hasinner){
        this.hasinner = hasinner;
    }

    public void setInnerAngle(float inner_angle){
        this.inner_angle = inner_angle;
    }

    public void setDeactivePaint(int color){
        deactivePaint.setColor(color);
    }

    public void setActivePaint(int color){
        activePaint.setColor(color);
    }

    public void setInnerPaint(int color){
        innerPaint.setColor(color);
    }

    private int dip2px (float dpValue){
        return (int)(dpValue *mDensity + 0.5f);
    }
}
