package com.lyue.qyrealtimeview;//要记得加入库achartengine-1.0.0.jar，否则好多类不能用哦

/**
 * Created by Lyue on 2016/8/12.
 * Email : staryumou@163.com
 */


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class RtChartsActivity extends Activity {

    int constNum = 100;
    private Timer timer = new Timer();
    private GraphicalView chart;
    private TimerTask task;
    private int addY = -1;
    private long addX;
    private TimeSeries series;
    private XYMultipleSeriesDataset dataset;
    private Handler handler;
    private Random random = new Random();

    Date[] xcache = new Date[constNum];
    int[] ycache = new int[constNum];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rtchart);
        LinearLayout layout1 = (LinearLayout) findViewById(R.id.linearlayout1);
        //生成图表
        chart = ChartFactory.getTimeChartView(this, getDateDemoDataset(), getDemoRenderer(), "mm:ss");
        layout1.addView(chart, new LayoutParams(LayoutParams.WRAP_CONTENT, 380));


        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //刷新图表
                updateChart();
                super.handleMessage(msg);
            }
        };
        task = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 200;
                handler.sendMessage(message);
            }
        };
        timer.schedule(task, 2 * 1000, 1000);
    }

    private void updateChart() {
        //设定长度为20
        int length = series.getItemCount();
        if (length >= constNum) length = constNum;
        addY = random.nextInt() % 5 + 10;
        addX = new Date().getTime();

        //将前面的点放入缓存
        for (int i = 0; i < length; i++) {
            xcache[i] = new Date((long) series.getX(i));
            ycache[i] = (int) series.getY(i);
        }

        series.clear();
        //将新产生的点首先加入到点集中，然后在循环体中将坐标变换后的一系列点都重新加入到点集中
        series.add(new Date(addX), addY);
        for (int k = 0; k < length; k++) {
            series.add(xcache[k], ycache[k]);
        }
        //在数据集中添加新的点集
        dataset.removeSeries(series);
        dataset.addSeries(series);
        //曲线更新
        chart.invalidate();
    }

    private XYMultipleSeriesRenderer getDemoRenderer() {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setChartTitle("网络丢包率图示");//标题
        renderer.setChartTitleTextSize(20);
        renderer.setXTitle("时间");    //x轴说明
        renderer.setYTitle("网络丢包率（‰）");
        renderer.setAxisTitleTextSize(16);
        renderer.setAxesColor(Color.BLACK);
        renderer.setLabelsTextSize(15);    //数轴刻度字体大小
        renderer.setLabelsColor(Color.BLACK);
        renderer.setLegendTextSize(15);    //曲线说明
        renderer.setXLabelsColor(Color.BLACK);
        renderer.setYLabelsColor(0, Color.BLACK);
        renderer.setShowLegend(false);
        renderer.setMargins(new int[]{5, 30, 15, 2});//上左下右{ 20, 30, 100, 0 })
        XYSeriesRenderer r = new XYSeriesRenderer();
        r.setColor(Color.RED);
        r.setChartValuesTextSize(15);
        r.setChartValuesSpacing(3);
        r.setPointStyle(PointStyle.POINT);
        r.setFillBelowLine(true);
        r.setFillBelowLineColor(Color.WHITE);
        r.setFillPoints(true);
        renderer.addSeriesRenderer(r);
        renderer.setMarginsColor(Color.WHITE);
        renderer.setPanEnabled(false, false);
        renderer.setShowGrid(true);
        renderer.setYAxisMax(50);//纵坐标最大值
        renderer.setYAxisMin(-30);//纵坐标最小值
        renderer.setInScroll(true);
        return renderer;
    }

    private XYMultipleSeriesDataset getDateDemoDataset() {//初始化的数据
        dataset = new XYMultipleSeriesDataset();
        final int nr = 10;
        long value = new Date().getTime();
        Random r = new Random();
        series = new TimeSeries("Demo series " + 1);
        for (int k = 0; k < nr; k++) {
            series.add(new Date(value + k * 1000), 20 + r.nextInt() % 10);//初值Y轴以20为中心，X轴初值范围再次定义
        }
        dataset.addSeries(series);
        return dataset;
    }

    @Override
    public void onDestroy() {
        //当结束程序时关掉Timer
        timer.cancel();
        super.onDestroy();
    }

    ;
}

