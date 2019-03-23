package com.demo.cjh.signin.Activity

import android.app.AlertDialog
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bin.david.form.data.column.Column
import com.bin.david.form.data.format.draw.ImageResDrawFormat
import com.bin.david.form.data.table.TableData
import com.demo.cjh.signin.R
import com.demo.cjh.signin.pojo.StudentInfo
import com.demo.cjh.signin.util.database
import org.jetbrains.anko.doAsync
import com.bin.david.form.utils.DensityUtils
import com.demo.cjh.signin.pojo.GPAItem
import kotlinx.android.synthetic.main.activity_gpa.*
import org.jetbrains.anko.uiThread
import android.util.DisplayMetrics
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.widget.EditText
import com.bin.david.form.data.format.count.ICountFormat
import com.demo.cjh.signin.pojo.StuSignInList
import com.demo.cjh.signin.util.getNow
import org.jetbrains.anko.alert
import org.jetbrains.anko.find


/**
 * 课堂表现
 */
class GPAActivity : AppCompatActivity() {

    private val TAG = "GPAActivity"

    private val ADD = 0
    private val WATCH = 1

    private lateinit var classId: String
    private lateinit var className: String

    private val stuData = ArrayList<StudentInfo>()

    private val gpaList = ArrayList<GPAItem>()

    private lateinit var tableData: TableData<GPAItem>

    private var type = 0  //
    private var flag = false // 修改标志

    /**
     * 课堂表现编号
     */
    private lateinit var no: String

    val A = 0
    val B = 1
    val C = 2
    val D = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gpa)

        init()

    }

    private fun init() {
        classId = intent.getStringExtra("classId")
        className = intent.getStringExtra("className")
        type = intent.getIntExtra("type",0)

        title = className

        if(type == WATCH){
            // 查看记录
            no = intent.getStringExtra("no")
        }

        initData()
    }


    fun initData(){
        doAsync {
            stuData.clear()

            if(type == ADD) {
                stuData.addAll(database.query_stuInfo_by_classId(classId)) // 查找学生名单
                stuData.forEach {
                    it.type = "B"
                }
            }else if(type == WATCH){
                stuData.addAll(database.query_gpaInfo_by_classId_and_no(classId,no))
            }

            uiThread {
                if(stuData.isNotEmpty()){
                    val size = DensityUtils.dp2px(this@GPAActivity, 15f) //指定图标大小
                    val column1 = Column<String>("学号", "stuId")
                    val column2 = Column<String>("姓名", "name")
                    val imgres = object : ImageResDrawFormat<Boolean>(size,size){
                        override fun getResourceID(t: Boolean?, value: String?, position: Int): Int {
                            if(t!!){
                                return R.drawable.hua2
                            }
                            return 0
                        }

                        override fun getContext(): Context {
                            return this@GPAActivity
                        }

                    }
                    val column3 = Column<Boolean>("优", "A",imgres)
                    val column4 = Column<Boolean>("良", "B",imgres)
                    val column5 = Column<Boolean>("中", "C",imgres)
                    val column6 = Column<Boolean>("差", "D",imgres)

                    column1.isAutoCount = true
                    column2.isAutoCount = true
                    column3.isAutoCount = true
                    column4.isAutoCount = true
                    column5.isAutoCount = true
                    column6.isAutoCount = true

                    column1.countFormat = Icount<String>{"合计："}
                    column2.countFormat = Icount<String>{"${it}人"}
                    column3.countFormat = Icount<Boolean>{ "优:$it"}
                    column4.countFormat = Icount<Boolean>{ "良:$it"}
                    column5.countFormat = Icount<Boolean>{ "中:$it"}
                    column6.countFormat = Icount<Boolean>{ "差:$it"}


                    stuDataToGpaList()

                    tableData = TableData<GPAItem>(className,gpaList,column1,column2,column3,column4,column5,column6)
                    tableData.isShowCount = true
                    tableData.setOnRowClickListener { column, t, col, row ->

                        var index = -1
                        if(gpaList[row].A)
                            index = 0
                        if (gpaList[row].B)
                            index = 1
                        if (gpaList[row].C)
                            index = 2
                        if (gpaList[row].D)
                            index = 3

                        var inx = index

                        var dialog = AlertDialog.Builder(this@GPAActivity)
                                .setTitle("${gpaList[row].name}同学课堂表现:")
                                .setSingleChoiceItems(arrayOf("优", "良","中","差"),index){dialog, which ->
                                    when(which){
                                        0 -> {
                                            gpaList[row].A = true
                                            gpaList[row].B= false
                                            gpaList[row].C= false
                                            gpaList[row].D= false
                                            index = 0
                                        }
                                        1 -> {
                                            gpaList[row].A = false
                                            gpaList[row].B= true
                                            gpaList[row].C= false
                                            gpaList[row].D= false
                                            index = 1
                                        }
                                        2 -> {
                                            gpaList[row].A = false
                                            gpaList[row].B= false
                                            gpaList[row].C= true
                                            gpaList[row].D= false
                                            index = 2
                                        }
                                        3 -> {
                                            gpaList[row].A = false
                                            gpaList[row].B= false
                                            gpaList[row].C= false
                                            gpaList[row].D= true
                                            index = 3
                                        }
                                    }

                                    if(index != inx ){
                                        // 表示修改
                                        flag = true
                                    }
                                    table.notifyDataChanged()
                                    dialog.dismiss()
                                }.show()
                    }
                    table.tableData = tableData
                    val wm1 = this@GPAActivity.windowManager
                    val outMetrics = DisplayMetrics()
                    wm1.defaultDisplay.getMetrics(outMetrics)
                    val width1 = outMetrics.widthPixels
                    table.config.minTableWidth = width1
                    table.setZoom(true)


                }
            }

        }
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK && flag) {
            // 返回且有修改
            var dialogView = LayoutInflater.from(this@GPAActivity).inflate(R.layout.input_dialog, null)

            alert {
                title = "提示"
                if (stuData.any { it.type.isEmpty() }) {
                    message = "检查到你有未记录的学生，继续保存将默认为良"
                } else {
                    if(type == WATCH){
                        message = "是否保存修改"
                    }else {
                        message = "是否保存"
                    }
                }
                if(type == ADD) {
                    this.customView = dialogView
                }

                positiveButton("是") {
                    doAsync {

                        when(type){
                            ADD ->{
                                var time = getNow()
                                val stuSignInList = StuSignInList(classId, time, null)
                                val num = database.getGpaNum(classId,time.substring(0,10))
                                stuSignInList.num = num
                                stuSignInList.info = dialogView.find<EditText>(R.id.input).text.toString()
                                // 1. 插入课堂表现记录表
                                database.insert_gpaList(stuSignInList)

                                // 2. 插入考勤信息详表记录
                                // 类型转换，学生信息 -> 学生考勤详情
                                // 插入

                                GpaListToStuData()

                                for (stu in stuData) {

                                    stu.time = time
                                    stu.no = time.substring(0,10)+"("+num+")"
                                    database.insert_gpaInfo(stu.toStuSignInInfo())

                                }
                            }
                            WATCH ->{
                                // 更新数据
                                GpaListToStuData()
                                for(stu in stuData){
                                    stu.no = no
                                    database.updata_gpaInfo(stu.toStuSignInInfo())
                                }
                            }
                        }


                        uiThread {
                            finish()
                        }
                    }




                }
                negativeButton("否"){
                    finish()
                }
                neutralPressed("取消"){

                }
            }.show()
            return true
        }

        return super.onKeyDown(keyCode, event)
    }





    fun GpaListToStuData(){
        stuData.clear()
        for(gp in gpaList){
            var studentInfo = StudentInfo()
            studentInfo.stuId = gp.stuId
            studentInfo.classId = classId
            studentInfo.name = gp.name
            when {
                gp.A -> studentInfo.type = "A"
                gp.B -> studentInfo.type = "B"
                gp.C -> studentInfo.type = "C"
                gp.D -> studentInfo.type = "D"
            }
            stuData.add(studentInfo)
        }
    }

    fun stuDataToGpaList(){
        gpaList.clear()
        for(da in stuData){
            var gpa = GPAItem()
            gpa.stuId = da.stuId
            gpa.name = da.name
            when(da.type){
                "A" -> gpa.A = true
                "B" -> gpa.B = true
                "C" -> gpa.C = true
                "D" -> gpa.D = true
            }
            gpaList.add(gpa)
        }
    }



    public class Icount<T>(var setCountS:(count: Long) -> String) :ICountFormat<T,Long>{
        var cou = 0L


        override fun count(t: T?) {
            when(t){
                is Boolean ->{
                    if (t == true) {
                        cou += 1
//                        Log.v(TAG,"+1=$cou")
                    }
                }
                is String ->{
                    cou += 1
                }
            }
        }

        override fun getCount(): Long {
//            Log.v(TAG,"+2")
            return cou
        }

        override fun getCountString(): String {
            return setCountS(cou)
        }

        override fun clearCount() {
            cou = 0
        }


    }

}
