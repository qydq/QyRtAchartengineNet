package com.lyue.qyrealtimeview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wangkun
 * @ClassName: RealTimeView
 * @Description: TODO 实时曲线图
 * @date 2014-8-22 下午7:35:42
 */
public class RealTimeView extends View {

    private static final int CHART_COLOR = 0xFF0099CC;
    private static final int CIRCLE_SIZE = 0;
    private static final float STROKE_SIZE = 1.5f;
    private static final float SMOOTHNESS = 0.35f; // the higher the smoother, but don't go over 0.5

    private final Paint mPaint, vGraidPaint, hGraidPaint, textPaint, fillPaint;
    private final Path mPath;
    private final float mCircleSize;
    private final float mStrokeSize;
    private final float mBorder;
    private float scale = 1.0f;//根据像素密度的变化
    private float perWidth; //每个竖格子之间的距离
    /**
     * 上下有效的坐标区间值为 mMaxVal - paddingButtom
     */
//	private float[] mValues;
    public List<Map<String, Object>> mValues = new ArrayList<Map<String, Object>>();
    public List<Map<String, Object>> mValues2 = new ArrayList<Map<String, Object>>();
    private float mMinVal;
    private float mMaxVal;

    private float paddingLeft;//左边间距
    private float paddingButtom;//底部间距
    private float padding = 5f;//X坐标之间的间距
    private String[] hTitle; //纵轴标题
    private String[] vTitle;//横轴标题
    private boolean isFill = true;
    float height;
    float width;
    //设置阀值的最大值和最小值
    private float max;
    private float min;
    public int showType;
    public Paint paint;

    public RealTimeView(Context context) {
        this(context, null, 0);
    }

    public RealTimeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RealTimeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        scale = context.getResources().getDisplayMetrics().density;

        mCircleSize = scale * CIRCLE_SIZE;
        mStrokeSize = scale * STROKE_SIZE;
        mBorder = mCircleSize;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mStrokeSize);

        fillPaint = new Paint();
        fillPaint.setAntiAlias(true);
        fillPaint.setStrokeWidth(mStrokeSize);
        fillPaint.setColor((Color.parseColor("#66FF66") & 0xFFFFFF) | 0x50000000);

        mPath = new Path();

        vGraidPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        vGraidPaint.setStrokeWidth(1 * scale);
        vGraidPaint.setColor(Color.parseColor("#10111d"));
        vGraidPaint.setAlpha(100);
        PathEffect effects = new DashPathEffect(new float[]{4 * scale, 4 * scale, 4 * scale, 4 * scale}, 2 * scale);
        vGraidPaint.setStyle(Style.STROKE);
        vGraidPaint.setPathEffect(effects);

        hGraidPaint = new Paint();
        hGraidPaint.setColor(Color.parseColor("#10111d"));
        hGraidPaint.setAlpha(100);
        hGraidPaint.setStrokeWidth(1 * scale);

        textPaint = new Paint();
        textPaint.setColor(Color.parseColor("#333333"));
        textPaint.setAlpha(80);
        textPaint.setStrokeWidth(1 * scale);
        textPaint.setTextSize(13 * scale);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawColor(Color.TRANSPARENT);
        paddingLeft = 5;//(float)getWidth()/8f;
        paddingButtom = (float) getHeight() / 20f;
        height = (float) getHeight();
        width = (float) getWidth() - paddingLeft;

        //draw graid
        drawGraidHorizontal(hTitle, paddingLeft, canvas, height, width);
        drawGraidVertical(vTitle, paddingButtom, canvas, height, width);
        drawRect(max, min, canvas);
        mPath.reset();
        if (mValues == null || mValues.size() == 0) {

        } else {
            drawLine(canvas, mValues);
        }
        if (mValues2 == null || mValues2.size() == 0) {

        } else {
            drawLine2(canvas, mValues2);
        }
    }


    public void setMaxY(int max) {
        mMaxVal = max;
    }

    public void setHTitle(String[] titles) {
        hTitle = titles;
    }

    public void setVTitle(String[] titles) {
        vTitle = titles;
    }


    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
    }

    public float getMin() {
        return min;
    }

    public void setMin(float min) {
        this.min = min;
    }

    public void addData(float value, int number) {
        Map<String, Object> datad = new HashMap<String, Object>();
        datad.put("f", value);
        datad.put("i", number);
        mValues.add(mValues.size(), datad);
        //if(mValues.size()>100){
        //	mValues.remove(mValues.size()-1);
        //}
        postInvalidate();
    }

    public void addData2(float value, int number) {
        Map<String, Object> datad = new HashMap<String, Object>();
        datad.put("f", value);
        datad.put("i", number);
        mValues2.add(mValues2.size(), datad);
        //if(mValues2.size()>100){
        //	mValues2.remove(mValues2.size()-1);
        //}
        postInvalidate();
    }

    public void setIsFill(boolean isFill) {
        this.isFill = isFill;
    }

    /*
     * 横格
     */
    public void drawGraidVertical(String[] graids, float paddingButtom, Canvas canvas, float height, float width) {
        if (graids == null) {
            return;
        }
        float temp = (height) / (graids.length + 1);
        for (int i = 0; i < graids.length; i++) {
            canvas.drawLine(paddingLeft, height - temp * (i + 1),
                    getWidth(), height - temp * (i + 1), hGraidPaint);
        }

    }

    //画出阀值的矩形框
    public void drawRect(float max, float min, Canvas canvas) {
        float maxY = height - max * (height / mMaxVal);
        float minY = height - min * (height / mMaxVal);
        Path path = new Path();
        path.moveTo(paddingLeft, maxY);
        path.lineTo(getWidth(), maxY);
        path.lineTo(getWidth(), minY);
        path.lineTo(paddingLeft, minY);
        path.close();
        canvas.drawPath(path, fillPaint);
    }

    /*
     * 竖格
     */
    public void drawGraidHorizontal(String[] graids, float paddingLeft, Canvas canvas, float height, float width) {
        if (graids == null) {
            return;
        }
        perWidth = 2 * scale;
        for (int i = 0; i < graids.length; i++) {
            //canvas.drawLine(perWidth*(i)+paddingLeft, 0,  perWidth*(i)+paddingLeft, height, hGraidPaint);
            //canvas.drawText(graids[i], perWidth*(i)+paddingLeft, getHeight(), textPaint);
        }
    }

    /*
     * 坐标
     */
    private void drawText(float left, float top, float right, float buttom, Canvas canvas, String text, Paint paint) {
        Rect targetRect = new Rect((int) left, (int) top, (int) right, (int) buttom);
        FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        int baseline = targetRect.top + (targetRect.bottom - targetRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
        canvas.drawText(text, targetRect.centerX(), baseline, paint);
    }

    /*
     * 画出曲线
     */
    private void drawLine(Canvas canvas, List<Map<String, Object>> values) {
        int size = values.size();
        // calculate point coordinates
        List<PointF> points = new ArrayList<PointF>(size);

        for (int i = 0; i < size; i++) {
            Map<String, Object> datad = mValues.get(i);
            float x = Integer.parseInt(datad.get("i").toString()) * (perWidth) + paddingLeft;
            float y = height - Float.parseFloat(datad.get("f").toString()) * (height / mMaxVal);
            points.add(new PointF(x, y));
        }

        // calculate smooth path
        float lX = 0, lY = 0;
        mPath.moveTo(points.get(0).x, points.get(0).y);
        float maxY = 0;
        List yList = new ArrayList<Float>();
        for (int i = 1; i < size; i++) {
            PointF p = points.get(i);    // current point

            // first control point
            PointF p0 = points.get(i - 1);    // previous point
            float x1 = p0.x + lX;
            float y1 = p0.y + lY;

            // second control point
            PointF p1 = points.get(i + 1 < size ? i + 1 : i);    // next point
            lX = (p1.x - p0.x) / 2 * SMOOTHNESS;        // (lX,lY) is the slope of the reference line
            lY = (p1.y - p0.y) / 2 * SMOOTHNESS;
            float x2 = p.x - lX;
            float y2 = p.y - lY;
            //防止画到0点以下
            y1 = y1 > height ? height : y1;
            y2 = y2 > height ? height : y2;
            p.y = p.y > height ? height : p.y;
            // add line
            mPath.cubicTo(x1, y1, x2, y2, p.x, p.y);

            // get maxY
            yList.add(y1);
            yList.add(y2);
            yList.add(p.y);
            Collections.sort(yList);
            maxY = (Float) yList.get(yList.size() - 1);
        }
        //划线的颜色
        // draw path
        if (showType == 0) {
            mPaint.setColor(Color.parseColor("#1170ee"));

        } else {
            // mPaint.setColor(Color.parseColor("#ee1111"));
            mPaint.setColor(Color.parseColor("#ef4e88"));
        }
        mPaint.setStyle(Style.STROKE);
        canvas.drawPath(mPath, mPaint);

        // draw area
        if (size > 0 && isFill) {
            fillPaint.setStyle(Style.FILL);
//			LinearGradient lg=new LinearGradient(0,maxY,0,getHeight(),Color.parseColor("#66FF66"),Color.TRANSPARENT, Shader.TileMode.MIRROR);
//			fillPaint.setShader(lg);
//			fillPaint.setColor((Color.parseColor("#66FF66") & 0xFFFFFF) | 0x50000000);
            mPath.lineTo(points.get(size - 1).x, height + mBorder);
            mPath.lineTo(points.get(0).x, height + mBorder);
            mPath.close();
            canvas.drawPath(mPath, fillPaint);
        }
    }


    /*
     * 画出曲线
     */
    private void drawLine2(Canvas canvas, List<Map<String, Object>> values) {
        int size = values.size();
        // calculate point coordinates
        List<PointF> points = new ArrayList<PointF>(size);

        for (int i = 0; i < size; i++) {
            Map<String, Object> datad = mValues2.get(i);
            float x = Integer.parseInt(datad.get("i").toString()) * (perWidth) + paddingLeft;
            float y = height - Float.parseFloat(datad.get("f").toString()) * (height / mMaxVal);
            points.add(new PointF(x, y));
        }

        // calculate smooth path
        float lX = 0, lY = 0;
        mPath.moveTo(points.get(0).x, points.get(0).y);
        float maxY = 0;
        List yList = new ArrayList<Float>();
        for (int i = 1; i < size; i++) {
            PointF p = points.get(i);    // current point

            // first control point
            PointF p0 = points.get(i - 1);    // previous point
            float x1 = p0.x + lX;
            float y1 = p0.y + lY;

            // second control point
            PointF p1 = points.get(i + 1 < size ? i + 1 : i);    // next point
            lX = (p1.x - p0.x) / 2 * SMOOTHNESS;        // (lX,lY) is the slope of the reference line
            lY = (p1.y - p0.y) / 2 * SMOOTHNESS;
            float x2 = p.x - lX;
            float y2 = p.y - lY;
            //防止画到0点以下
            y1 = y1 > height ? height : y1;
            y2 = y2 > height ? height : y2;
            p.y = p.y > height ? height : p.y;
            // add line
            mPath.cubicTo(x1, y1, x2, y2, p.x, p.y);
            // get maxY
            yList.add(y1);
            yList.add(y2);
            yList.add(p.y);
            Collections.sort(yList);
            maxY = (Float) yList.get(yList.size() - 1);
        }

        // draw path
        if (showType == 0) {
            mPaint.setColor(Color.parseColor("#1170ee"));

        } else {
            mPaint.setColor(Color.parseColor("#ef4e88"));
        }
        mPaint.setStyle(Style.STROKE);
        canvas.drawPath(mPath, mPaint);

        // draw area
        if (size > 0 && isFill) {
            fillPaint.setStyle(Style.FILL);
//			LinearGradient lg=new LinearGradient(0,maxY,0,getHeight(),Color.parseColor("#66FF66"),Color.TRANSPARENT, Shader.TileMode.MIRROR);
//			fillPaint.setShader(lg);
//			fillPaint.setColor((Color.parseColor("#66FF66") & 0xFFFFFF) | 0x50000000);
            mPath.lineTo(points.get(size - 1).x, height + mBorder);
            mPath.lineTo(points.get(0).x, height + mBorder);
            mPath.close();
            canvas.drawPath(mPath, fillPaint);
        }
    }


/*
    class DrawLineThread extends Thread{

        DrawLineThread(){

        }

        @Override
        public void run() {

        }
    }
*/


}
