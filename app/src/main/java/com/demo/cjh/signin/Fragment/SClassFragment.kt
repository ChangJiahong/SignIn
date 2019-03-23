package com.demo.cjh.signin.Fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.*
import com.bin.david.form.data.column.Column
import com.bin.david.form.data.format.IFormat
import com.bin.david.form.data.style.FontStyle
import com.bin.david.form.data.table.TableData
import com.daivd.chart.utils.DensityUtils
import com.demo.cjh.signin.Activity.StatisticsClassActivity
import com.demo.cjh.signin.R
import kotlinx.android.synthetic.main.fragment_sclass.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.uiThread
import com.demo.cjh.signin.pojo.ClassInfo
import com.demo.cjh.signin.pojo.CqInfo
import com.demo.cjh.signin.util.database
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by CJH
 * on 2018/10/21
 */
class SClassFragment : Fragment(), View.OnClickListener  {


    val TAG = "SClassFragment"

    lateinit var day: TextView
    lateinit var week: TextView
    lateinit var month: TextView
    lateinit var lAdapter: LAdapter
    /**
     * 表格数据
     */
    val data = ArrayList<SClass>()

    var sortType = 0 // 排序类别
    var upOrDown = true // 升序||降序

    // 每日出勤信息
    var sData = ArrayList<ArrayList<CqInfo>>()
    // 每周出勤信息
    var sWdata = ArrayList<ArrayList<CqInfo>>()
    // 每月出勤信息
    var sMdata = ArrayList<ArrayList<CqInfo>>()

    val classDa = ArrayList<ClassInfo>()

    private lateinit var classId: String
    private lateinit var className: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_sclass, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    private fun init() {
        //classId = activity!!.intent.getStringExtra("classId")
        //className = activity!!.intent.getStringExtra("className")

        //activity!!.title = className

        // 表格显示


        val format = IFormat<String> { t ->
            if(t!!.length>8){
                return@IFormat t.substring(0,6).plus("...")
            }
            t
        }

        val column1 = Column<String>("班级", "className",format)
        val column2 = Column<String>("日(%)", "day")
        val column3 = Column<String>("周(%)", "week")
        val column4 = Column<String>("月(%)", "month")



        smartTable.setZoom(false) // 设置缩放
        val wm1 = activity!!.windowManager
        val outMetrics = DisplayMetrics()
        wm1.defaultDisplay.getMetrics(outMetrics)
        val width1 = outMetrics.widthPixels
        smartTable.config.minTableWidth = width1
        smartTable.config.isShowTableTitle = false
        smartTable.config.isShowXSequence = false
//        smartTable.config.isShowColumnTitle = false
        smartTable.config.isFixedXSequence = true
        // 设置标题样式
        var titleStyle = FontStyle()
        titleStyle.textSize = DensityUtils.dp2px(context, 16f)
        smartTable.config.columnTitleStyle = titleStyle
        smartTable.config.columnTitleVerticalPadding = DensityUtils.dp2px(context, 10f)


        doAsync {
            // 获取记录数据
            // 获取时间
            // 获取学生信息
            // 查找是出勤的个数
            // 通过一条sql语句完成
            // 返回一个数
            classDa.clear()
            sMdata.clear()
            sWdata.clear()
            sData.clear()
            data.clear()

            classDa.addAll(activity!!.database.query_classInfo())
            for(da in classDa) {
                // 返回每日的数据
                var Sdata = activity!!.database.query_cqInfo(da.classId!!)
                // 每周数据
                var Swdata = ArrayList<CqInfo>()
                // 每月数据
                var Smdata = ArrayList<CqInfo>()

                // 获取班级总人数
                var stuNum = activity!!.database.getStuNumByClassId(da.classId!!)
                if(stuNum.toInt() == 0 || Sdata.size == 0){
                    continue
                }
                var sClass = SClass(da.className!!)
                // 总出勤
                var zo = 0.0
                var wo = 0.0
                var mo = 0.0

                var fw = CqInfo(num = "0",time = Sdata[0].time) // 周分割日期标志

                var fm = CqInfo(num = "0",time = Sdata[0].time.substring(0,7)) // 月分割日期标志 例：2018-10

                Sdata.forEach {
                    // 计算日平均出勤率
                    // 当天记录次数
                    var n = activity!!.database.getNum(da.classId!!,it.time)-1 // 由于获取的次数会自加一，所以要剪掉
                    it.rate = (it.num.toDouble()) / (stuNum.toDouble()*n)
                    Log.v(TAG,"${it.time} 学生数 ${stuNum} 考勤次数 $n 实到总数 ${it.num} 出勤率  ${it.rate}")
                    zo+=it.rate

                    // 分割周日期 计算周平均出勤率
                    if(daysBetween(fw.time,it.time) <7){
                        // 天数差才七天之内的认为是一周
                        wo += it.rate
                        fw.num = (fw.num.toDouble()+1).toString()
                    }else{


                        var w = CqInfo(num = fw.num,time = (Swdata.size+1).toString())
                        w.rate = wo/w.num.toDouble() // 周出勤率
                        Swdata.add(w)

                        wo = it.rate
                        // 新一周，重置标志位
                        fw = it.copy() // 深拷贝
                        fw.num = "1"
                    }

                    // 分割月日期 计算月平均出勤率
                    // 考虑到计算真实月份，切割字符串time前两位
                    if(fm.time == it.time.substring(0,7)){
                        // 如果等于标志表示是一个月的，
                        mo+=it.rate
                        // 次数加+1
                        fm.num = (fm.num.toDouble()+1).toString()
                    }else{
                        // 否则不是一个月

                        var m = CqInfo(num = fm.num,time = it.time.substring(0,7))
                        m.rate = mo/m.num.toDouble()// 月出勤率

                        Smdata.add(m)

                        mo = it.rate // 归零
                        fm = it.copy(num = "1") // 更新标志位

                    }

                }
                // 获得平均日出勤率
                zo /= Sdata.size
                sClass.day = "%.2f".format(zo*100)

                // 获得周出勤率
                // 如果最后算不到一周也算一周
                var w = CqInfo(num = fw.num,time = (Swdata.size+1).toString())
                w.rate = wo/w.num.toDouble()// 周出勤率
                wo = 0.0
                Swdata.add(w)
                Log.v(TAG,"${w.time}周 考勤次数 ${w.num} 出勤率  ${w.rate}")

                sClass.week = "%.2f".format((Swdata.sumByDouble {
                    it.rate
                }*100)/Swdata.size)

                var m = CqInfo(num = fm.num,time = fm.time.substring(0,7))
                m.rate = mo/m.num.toDouble() // 月出勤率
                mo = 0.0 // 归零
                Smdata.add(m)

                Log.v(TAG,"${Smdata[0].time}月 考勤次数 ${Smdata[0].num} 出勤率  ${Smdata[0].rate}")
                sClass.month = "%.2f".format(Smdata.sumByDouble {
                    it.rate
                }*100/Smdata.size)

                data.add(sClass)
                sData.add(Sdata)
                sWdata.add(Swdata)
                sMdata.add(Smdata)
            }

//            var sClass = SClass("16软件工程一班","22.0","32.0","44.0")
//            var sClassT = SClass("中华手动萨芬撒航算法","12.0","23.0","65.0")
//            data.add(sClassT)
//            for(i in 0 .. 60)
//                data.add(sClass)

            uiThread {
                var tableData : TableData<SClass> = TableData("table",data,column1,column2,column3,column4)
                tableData.setOnRowClickListener { column, t, col, row ->
                    startActivity<StatisticsClassActivity>("className" to data[row].className ,
                            "sData" to sData[row] as Serializable,
                            "sWData" to sWdata[row] as Serializable,
                            "sMData" to sMdata[row] as Serializable)
                }

                smartTable.tableData = tableData
            }

        }

    }

    fun daysBetween(smdate: String, bdate: String): Int {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val cal = Calendar.getInstance()
        var time1: Long = 0
        var time2: Long = 0

        try {
            cal.time = sdf.parse(smdate)
            time1 = cal.timeInMillis
            cal.time = sdf.parse(bdate)
            time2 = cal.timeInMillis
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val between_days = (time2 - time1) / (1000 * 3600 * 24)
        val len = Integer.parseInt(between_days.toString())
        return Math.abs(len)
    }

    fun getDaysOfMonth(date: String): Int {
        var sdf = SimpleDateFormat("yyyy-MM")
        val calendar = Calendar.getInstance()
        calendar.time = sdf.parse(date)
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item!!.itemId){
            R.id.sort ->{
                // 排序
                var view = View.inflate(activity, R.layout.sort_layout, null)
                var radioGroup = view.find<RadioGroup>(R.id.group)
                var up = view.find<Button>(R.id.up)
                var down = view.find<Button>(R.id.down)
                up.setOnClickListener(this)
                down.setOnClickListener(this)

                var dialog = AlertDialog.Builder(activity)
                        .setTitle("排序")
                        .setView(view)
                        .create()

                var window = dialog.window
                window.setGravity(Gravity.BOTTOM);//这个也很重要，将弹出菜单的位置设置为底部
                window.setWindowAnimations(R.style.animation_bottom_menu);//菜单进入和退出屏幕的动画，实现了上下滑动的动画效果
                dialog.show()

                radioGroup.check(sortType)
                radioGroup.setOnCheckedChangeListener { group, checkedId ->
                    sortType = checkedId
                    // Toast.makeText(activity,sortType,Toast.LENGTH_SHORT).show()
                    Log.v(TAG,""+sortType)
                }

            }
        }


        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.up ->{
                // 升序

            }
            R.id.down ->{
                // 降序

            }
        }
    }


    class LAdapter(val data : List<SClass>, val context : Context) : BaseAdapter(){

        var inflater : LayoutInflater? = null

        init {
            inflater = LayoutInflater.from(context)
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var holder : Holder
            var v : View
            if(convertView == null){
                v = inflater!!.inflate(R.layout.sclass_item,null)
                holder = Holder(v)
                v.tag = holder
            }else{
                v = convertView
                holder = v.tag as Holder
            }
            holder.className.text = data[position].className
            holder.day.text = data[position].day
            holder.week.text = data[position].week
            holder.month.text = data[position].month

            return v
        }

        override fun getItem(position: Int): Any {
            return data[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return data.size
        }

        class Holder(v :View) {
            var className = v.find<TextView>(R.id.class_name)
            var day = v.find<TextView>(R.id.day)
            var week = v.find<TextView>(R.id.week)
            var month = v.find<TextView>(R.id.month)
        }

    }

    data class SClass(var className: String ){
        var day: String = "0.0"
        var week: String = "0.0"
        var month: String = "0.0"
        constructor(className: String,day: String,week: String,month: String ) : this(className) {
            this.day = day
            this.week = week
            this.month = month
        }
    }

}