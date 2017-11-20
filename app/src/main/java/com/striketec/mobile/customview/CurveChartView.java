package com.striketec.mobile.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.striketec.mobile.R;
import com.striketec.mobile.dto.PunchDto;
import com.striketec.mobile.util.AppConst;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by Qiang on 8/26/2017.
 */

public class CurveChartView extends View{
    public static class Point {
        public static final Comparator<Point> X_COMPARATOR = new Comparator<Point>() {
            @Override
            public int compare(Point lhs, Point rhs) {
                return (int) (lhs.x * 1000 - rhs.x * 1000);
            }
        };

        public float x;
        public float y;

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public Point() {
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }
    }

    private static final float CURVE_LINE_WIDTH = 4f;
    private static final float HALF_TIP_HEIGHT = 16;

    private float CURVE_STROKE_WIDTH = 1;

    private Context mContext;
    private boolean isSpeed;

    private static final String TAG = CurveChartView.class.getSimpleName();

    private ArrayList<PunchDto> punchDatas;
    private ArrayList<Point> pointList;
    private ArrayList<Point> scaledPoints;

    private Paint borderPaint = new Paint();
    private Rect chartRect = new Rect();

    private Paint curvePaint = new Paint();
    private Path curvePath = new Path();
    private Paint fillPaint = new Paint();
    private Path fillPath = new Path();

    private float maxY;

    private float scaleY = 1;
    private float scaleX = 1;

    private Rect avgtextBounds = new Rect();
    private Rect maxtextBounds = new Rect();

    private Paint tipLinePaint = new Paint();
    private Rect avgtipRect = new Rect();
    private RectF avgtipRectF = new RectF();

    private Rect maxtipRect = new Rect();
    private RectF maxtipRectF = new RectF();

    private Paint tipTextPaint = new Paint();

    private int maxXAxisValue = 5000;

    private int chartHeight, chartWidth;

    private float maxSpeed, avgSpeed, maxForce, avgForce;

    private void init(){
        punchDatas = new ArrayList<>();
        pointList = new ArrayList<>();
        scaledPoints = new ArrayList<>();

        borderPaint.setColor(mContext.getResources().getColor(R.color.default_alpha));
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeCap(Paint.Cap.SQUARE);
        borderPaint.setStrokeWidth(CURVE_STROKE_WIDTH);
        borderPaint.setAntiAlias(true);

        curvePaint.setStyle(Paint.Style.STROKE);
        curvePaint.setStrokeCap(Paint.Cap.ROUND);
        curvePaint.setStrokeWidth(CURVE_STROKE_WIDTH);
        curvePaint.setColor(mContext.getResources().getColor(R.color.force_color));
        curvePaint.setAntiAlias(true);
        curvePaint.setAlpha(200);

        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(mContext.getResources().getColor(R.color.force_color));
        fillPaint.setAlpha(50);
        fillPaint.setAntiAlias(true);

        tipLinePaint.setStyle(Paint.Style.STROKE);
        tipLinePaint.setStrokeCap(Paint.Cap.SQUARE);
        tipLinePaint.setStrokeWidth(2 * CURVE_STROKE_WIDTH);
        tipLinePaint.setColor(mContext.getResources().getColor(R.color.punches_text));
        tipLinePaint.setAntiAlias(true);
        tipLinePaint.setAlpha(220);

        tipTextPaint.setColor(Color.WHITE);
        tipTextPaint.setTextSize(mContext.getResources().getInteger(R.integer.tip_textsize));
        tipTextPaint.setAntiAlias(true);
    }

    public void setIsSpeed(boolean isSpeed){
        this.isSpeed = isSpeed;
    }

    public void setPunchDatas(boolean isSpeed, ArrayList punchDatas){
        this.punchDatas = punchDatas;
        this.isSpeed = isSpeed;
        changeGraph(isSpeed);
    }

    public void addPunchData(PunchDto punchDTO){
        punchDatas.add(punchDTO);
        changeGraph(isSpeed);
    }


    public CurveChartView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public CurveChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public void changeGraph(boolean isSpeed){
        this.isSpeed = isSpeed; //if true this graph is for speed, else this graph is for power

        if (!isSpeed){
            curvePaint.setColor(mContext.getResources().getColor(R.color.force_color));
            curvePaint.setAlpha(130);
            fillPaint.setColor(mContext.getResources().getColor(R.color.force_color));
            fillPaint.setAlpha(130);
        }else {
            curvePaint.setColor(mContext.getResources().getColor(R.color.speed_color));
            curvePaint.setAlpha(130);
            fillPaint.setColor(mContext.getResources().getColor(R.color.speed_color));
            fillPaint.setAlpha(130);
        }

        //read data from punch data
        if (pointList != null && pointList.size() > 0)
            pointList.clear();

        if (scaledPoints != null && scaledPoints.size() > 0){
            scaledPoints.clear();
        }

        if (!isSpeed){
            for (int i = 0; i < punchDatas.size(); i++){
                pointList.add(new Point(punchDatas.get(i).getTime(), punchDatas.get(i).getPower()));
            }
        }else {
            for (int i = 0; i < punchDatas.size(); i++){
                pointList.add(new Point(punchDatas.get(i).getTime(), punchDatas.get(i).getSpeed()));
            }
        }

        //refind max y coodinate
        maxY = 0;
        for (int i = 0; i < pointList.size(); i++){
            if (pointList.get(i).y > maxY)
                maxY = pointList.get(i).y;
        }

        this.scaleX = ((float)chartWidth )/ maxXAxisValue;

        if (!isSpeed){
            scaleY = (float)chartHeight / AppConst.POWER_MAX;
        }else
            scaleY = (float)chartHeight / AppConst.SPEED_MAX;

        for (int i = 0; i < pointList.size(); i++){
            Point p = pointList.get(i);

            Point newPoint = new Point();
            newPoint.x = chartRect.left + scaleX * p.x;
            newPoint.y = chartHeight - scaleY * p.y;

            scaledPoints.add(newPoint);
        }

        super.invalidate();
    }

    private void buildPath(Path path){
        path.reset();

        if (scaledPoints.size() > 1) {
            path.moveTo(scaledPoints.get(0).x, scaledPoints.get(0).y);

            for (int i = 0; i < scaledPoints.size() - 1; i++) {
                float pointX = (scaledPoints.get(i).x + scaledPoints.get(i + 1).x) / 2;
                float pointY = (scaledPoints.get(i).y + scaledPoints.get(i + 1).y) / 2;

                float controlX = scaledPoints.get(i).x;
                float controlY = scaledPoints.get(i).y;

                path.quadTo(controlX, controlY, pointX, pointY);
            }

            path.quadTo(scaledPoints.get(scaledPoints.size() - 1).x, scaledPoints.get(scaledPoints.size() - 1).y,
                    scaledPoints.get(scaledPoints.size() - 1).x, scaledPoints.get(scaledPoints.size() - 1).y);
        }
    }

    private void drawCurve(Canvas canvas){
        buildPath(curvePath);
        buildPath(fillPath);

        int pointSize = scaledPoints.size();

        if (pointSize > 1) {
            fillPath.lineTo(scaledPoints.get(pointSize - 1).x, chartRect.bottom);
            fillPath.lineTo(chartRect.left, chartRect.bottom);
            fillPath.close();
        }

        canvas.drawPath(fillPath, fillPaint);
        canvas.drawPath(curvePath, curvePaint);
    }

    private void drawGrid(Canvas canvas, int width) {

        int gridCount = AppConst.GRID_COUNT - 1;
        float part = (float) width / gridCount;

        for (int i = 1; i < gridCount; i++) {
            float x = chartRect.left + part * i;
            canvas.drawLine(x, chartRect.top, x, chartRect.bottom, borderPaint);
        }
    }

//    /**
//     * Draw tip on the middle of the chart
//     * @param canvas
//     * @param width
//     * @param height
//     */
    private void drawTip(Canvas canvas, float width, float height) {

        if (scaledPoints == null || scaledPoints.size() == 0)
            return;

        float totalHeight = 0;
        for (Point p : scaledPoints) {
            totalHeight += p.y;
        }

        int average = 0;
        int totalValue = 0;

        for (Point p : pointList){
            totalValue += p.y;
        }

        average = totalValue / pointList.size();

        String maxText, avgText;

        if (isSpeed){
            maxText = "MAX SPEED " + (int)maxY + " MPH";
            avgText = "AVG SPEED " + average + " MPH";
        }else {
            maxText = "MAX POWER " + (int)maxY + " LBS";
            avgText = "AVG POWER " + average + " LBS";
        }

        float tipLineY = totalHeight / scaledPoints.size();

        if (tipLineY + HALF_TIP_HEIGHT >= chartRect.bottom) {
            tipLineY = chartRect.bottom - HALF_TIP_HEIGHT - 4;
        }

        //draw max average text
        tipTextPaint.getTextBounds(avgText, 0, 1, avgtextBounds);

        float centerX = chartRect.left + width / 2;
        float textWidth = getTextWidth(tipTextPaint, avgText);
        float textHeight = getTextHeight(tipTextPaint);

        avgtipRect.right =(int) chartRect.right - 30;
        avgtipRect.left = (int)(avgtipRect.right - textWidth + 30);
        avgtipRect.top = (int)(tipLineY - textHeight);
        avgtipRect.bottom = (int)(tipLineY  - 5);

        avgtipRectF.set(avgtipRect);

//        tipRect.left = (int) (centerX - textWidth / 2 - 23);
//        tipRect.right = (int) (centerX + textWidth / 2 + 23);
//        tipRect.top = (int) (tipLineY - HALF_TIP_HEIGHT);
//        tipRect.bottom = (int) (tipLineY + HALF_TIP_HEIGHT);
//
//        tipRectF.set(tipRect);

        float textX = avgtipRect.right - textWidth;
        float textHeight1 = avgtextBounds.bottom - avgtextBounds.top;
        float textY = tipLineY - textHeight1 + 5;

        canvas.drawLine(chartRect.left, tipLineY, chartRect.right, tipLineY, tipLinePaint);
        canvas.drawText(avgText, textX, textY, tipTextPaint);

        //draw max text
        tipTextPaint.getTextBounds(maxText, 0, 1, maxtextBounds);

        centerX = chartRect.left + width / 2;
        textWidth = getTextWidth(tipTextPaint, maxText);
        textHeight = getTextHeight(tipTextPaint);

        maxtipRect.right =(int)(centerX + textWidth / 2) + 30;
        maxtipRect.left = (int)(maxtipRect.right - textWidth - 30);
        maxtipRect.top = (int)(chartRect.top + 200);
        maxtipRect.bottom = (int)(maxtipRect.top + textHeight);

        maxtipRectF.set(maxtipRect);

        textX = maxtipRect.right - textWidth - 30;
        textY = maxtipRect.top;

        canvas.drawText(maxText, textX, textY, tipTextPaint);
    }

    public Paint getBorderPaint() {
        return borderPaint;
    }

    public Paint getCurvePaint() {
        return curvePaint;
    }

    public Paint getFillPaint() {
        return fillPaint;
    }

    public float getTextHeight(Paint textPaint) {
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        return (float) Math.ceil(fm.descent - fm.ascent) - 2;
    }

    public float getTextWidth(Paint textPaint, String text) {
        return textPaint.measureText(text);
    }

    public Paint getTipLinePaint() {
        return tipLinePaint;
    }

    public Paint getTipTextPaint() {
        return tipTextPaint;
    }

    public void init(ArrayList<PunchDto> punchDatas) {
        this.punchDatas = punchDatas;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        getDrawingRect(chartRect);

        if (punchDatas != null) {
            chartHeight = chartRect.bottom - chartRect.top;
            chartWidth = chartRect.right - chartRect.left;

            drawGrid(canvas, chartWidth);

            drawCurve(canvas);

            canvas.drawRect(chartRect, borderPaint);
            drawTip(canvas, chartWidth, chartHeight);
        }
    }

    public int getMaxXAxisValue (){
        return maxXAxisValue;
    }

    public void setMaxXAxisValue(int xAxisValue){
        this.maxXAxisValue  = xAxisValue;
    }

    public void setBorderPaint(Paint borderPaint) {
        this.borderPaint = borderPaint;
    }

    public void setCurvePaint(Paint curvePaint) {
        this.curvePaint = curvePaint;
    }

    public void setFillPaint(Paint fillPaint) {
        this.fillPaint = fillPaint;
    }

    public void setTipLinePaint(Paint tipLinePaint) {
        this.tipLinePaint = tipLinePaint;
    }

    public void setTipTextPaint(Paint tipTextPaint) {
        this.tipTextPaint = tipTextPaint;
    }
}

