package com.demo.cjh.signin.Activity

import android.content.Context
import android.graphics.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.daivd.chart.component.base.IAxis
import com.daivd.chart.component.base.IComponent
import com.daivd.chart.core.LineChart
import com.daivd.chart.data.BarData
import com.daivd.chart.data.ChartData
import com.daivd.chart.data.LineData
import com.daivd.chart.data.style.FontStyle
import com.daivd.chart.data.style.PointStyle
import com.daivd.chart.provider.component.cross.VerticalCross
import com.daivd.chart.provider.component.mark.IMark
import com.daivd.chart.provider.component.point.LegendPoint
import com.daivd.chart.provider.component.point.Point
import com.daivd.chart.provider.component.tip.SingleLineBubbleTip
import com.daivd.chart.utils.DensityUtils
import com.daivd.chart.utils.DrawUtils
import com.demo.cjh.signin.R
import com.demo.cjh.signin.pojo.CqInfo
import kotlinx.android.synthetic.main.activity_statistics_class.*
import java.util.ArrayList

/**
 * 班级出勤率，汇总
 */
class StatisticsClassActivity : AppCompatActivity() {

    val TAG = StatisticsClassActivity::class.java.name

    // 每日出勤信息
    var Sdata = ArrayList<CqInfo>()
    // 每周出勤信息
    var Swdata = ArrayList<CqInfo>()
    // 每月出勤信息
    var Smdata = ArrayList<CqInfo>()

    lateinit var className: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics_class)

        className = intent.getStringExtra("className")
        Sdata = intent.getSerializableExtra("sData") as ArrayList<CqInfo>
        Swdata = intent.getSerializableExtra("sWData") as ArrayList<CqInfo>
        Smdata = intent.getSerializableExtra("sMData") as ArrayList<CqInfo>

        title = className

        Sdata.forEach {
            it.time = it.time+"日"
        }
        Swdata.forEach {
            it.time = "第"+it.time+"周"
        }
        Smdata.forEach {
            it.time = it.time+"月"
        }

        setChart(lineChart,Sdata,"日出勤率")
        setChart(lineChart1,Swdata,"周出勤率")
        setChart(lineChart2,Smdata,"月出勤率")



    }

    fun setChart(smartChart: LineChart,xData: ArrayList<CqInfo>,title: String){
        var res = resources;
        FontStyle.setDefaultTextSpSize(this, 12);
        var chartYDataList = ArrayList<String>()

        var ColumnDatas =  ArrayList<LineData>();
        var tempList1 = ArrayList<Double>();


        for(da in xData){
            tempList1.add("%.2f".format(da.rate*100).toDouble())
            chartYDataList.add(da.time)
        }
        tempList1.add(0.0);
        chartYDataList.add("0");


        tempList1.add(0.0);
        chartYDataList.add("0");

        tempList1.add(0.0);
        chartYDataList.add("0");

        tempList1.add(0.0);
        chartYDataList.add("0");

        tempList1.add(0.0);
        chartYDataList.add("0");

//        chartYDataList.add("Hong Kong");
//        chartYDataList.add("Singapore");
//        chartYDataList.add("Tokyo");
//        chartYDataList.add("Paris");
//        chartYDataList.add("Hong Kong");
//        chartYDataList.add("Singapore");


//        tempList1.add(-40.0);
//        tempList1.add(10.0);
//        tempList1.add(26.0);
//        tempList1.add(-35.0);
//        tempList1.add(-40.0);
//        tempList1.add(10.0);


        var columnData1 = LineData(title, "%", IAxis.AxisDirection.LEFT, getResources().getColor(R.color.arc3), tempList1);

        ColumnDatas.add(columnData1);
        //ColumnDatas.add(columnData2);


        var chartData2 =  ChartData<LineData>(title, chartYDataList, ColumnDatas);

        smartChart.setLineModel(LineChart.CURVE_MODEL);
        var verticalAxis = smartChart.getLeftVerticalAxis();
        var horizontalAxis = smartChart.getHorizontalAxis();
        var rightAxis = smartChart.getRightVerticalAxis();
        rightAxis.setStartZero(false);
        rightAxis.setMaxValue(100.0);
        rightAxis.setMinValue(0.0);
        //设置竖轴方向
        verticalAxis.setAxisDirection(IAxis.AxisDirection.LEFT);
        //设置网格
        verticalAxis.setDrawGrid(true);
        //设置横轴方向
        horizontalAxis.setAxisDirection(IAxis.AxisDirection.BOTTOM);
        horizontalAxis.setDrawGrid(true);
        //设置线条样式
        verticalAxis.getAxisStyle().setWidth(this, 1);
        var effects =  DashPathEffect(floatArrayOf(1f, 2f, 4f, 8f), 1f);
        verticalAxis.getGridStyle().setWidth(this, 1).setColor(res.getColor(R.color.arc_text)).setEffect(effects);
        horizontalAxis.getGridStyle().setWidth(this, 1).setColor(res.getColor(R.color.arc_text)).setEffect(effects);
        var cross =  VerticalCross();
        var crossStyle = cross.getCrossStyle();
        crossStyle.setWidth(this, 1);
        crossStyle.setColor(res.getColor(R.color.arc21));
        smartChart.getProvider().setCross(cross);
        smartChart.setZoom(true);
        //开启十字架
        smartChart.getProvider().setOpenCross(true);
        //开启MarkView
        smartChart.getProvider().setOpenMark(true);
        //设置MarkView
        smartChart.getProvider().setMarkView( CustomMarkView(this));
        //设置显示点
        var point =  Point();
        point.getPointStyle().setShape(PointStyle.CIRCLE);
        //设置显示点的样式
        smartChart.getProvider().setPoint(point);
        var dp10 = DensityUtils.dp2px(this, 10f);
        smartChart.provider.setText { canvas, value, x, y , position, line, paint ->

            paint.setTextAlign(Paint.Align.CENTER);
            paint.setStyle(Paint.Style.FILL);

            var textSize = DrawUtils.getTextHeight(paint);
            var dis = dp10 / 2 + textSize;
            canvas.drawText(value , x, if(line == 0)  y - dis + textSize / 2 else y + dis, paint);

        }

        var paint =  Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(DensityUtils.sp2px(this, 13f).toFloat());
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);

        //smartChart.getProvider().setTip(tip);
        //设置显示标题
        smartChart.setShowChartName(true);
        smartChart.getMatrixHelper().setWidthMultiple(3f);
        //设置标题方向
        smartChart.getChartTitle().setDirection(IComponent.TOP);
        //设置标题比例
        smartChart.getChartTitle().setPercent(0.2f);
        //设置标题样式
        var fontStyle = smartChart.getChartTitle().getFontStyle();
        fontStyle.setTextColor(res.getColor(R.color.arc_temp));
        fontStyle.setTextSpSize(this, 15);
//
//        var levelLine = LevelLine(20.0);
//        var effects2 = DashPathEffect(floatArrayOf(1f, 2f, 2f, 4f), 1f);
//        levelLine.getLineStyle().setWidth(this, 1).setColor(res.getColor(R.color.arc23)).setEffect(effects);
//        levelLine.getLineStyle().setEffect(effects2);
//        lineChart.getProvider().addLevelLine(levelLine);

        smartChart.getLegend().setDirection(IComponent.BOTTOM);
        var legendPoint = smartChart.getLegend().getPoint() as LegendPoint;
        var style = legendPoint.getPointStyle();
        style.setShape(PointStyle.RECT);
        smartChart.getLegend().setPercent(0.2f);
        smartChart.getHorizontalAxis().setRotateAngle(-90);
        smartChart.setFirstAnim(false);
        smartChart.setChartData(chartData2);
        smartChart.startChartAnim(1000);
        smartChart.setOnClickColumnListener { lineData, i ->

        }
    }
    inner class CustomMarkView<C : BarData>(context: Context) : IMark<C> {

        private lateinit var  bubbleTip: SingleLineBubbleTip<String>
        lateinit var paint: Paint

        init {
            paint = Paint()
            paint.isAntiAlias = true
            paint.textSize = DensityUtils.sp2px(context, 13f).toFloat()
            paint.style = Paint.Style.FILL
            paint.color = Color.WHITE

            bubbleTip = object : SingleLineBubbleTip<String>(context, R.mipmap.round_rect,R.mipmap.triangle,paint){


                public override fun isShowTip(s: String, position: Int): Boolean {
                    return true
                }


                public override fun format(s: String, position: Int): String {
                    return s
                }
            }
            bubbleTip.setColorFilter(Color.parseColor("#F4A460"))
            bubbleTip.setAlpha(0.8f);
        }

        override fun drawMark(canvas: Canvas, x: Float, y: Float, rect: Rect, content: String, data: C, position: Int) {

            val text = "${data.chartYDataList[position]}${data.unit}"
            bubbleTip.drawTip(canvas, x, y, rect, text, position)
        }
    }
}
