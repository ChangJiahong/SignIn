package com.demo.cjh.signin.Activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import com.demo.cjh.signin.*
import kotlinx.android.synthetic.main.activity_table.*
import org.apache.poi.ss.usermodel.*
import org.jetbrains.anko.*
import java.io.File
import android.view.LayoutInflater
import android.widget.*
import com.bin.david.form.data.format.draw.TextDrawFormat
import com.bin.david.form.data.table.ArrayTableData
import com.demo.cjh.signin.util.FileUtil
import com.demo.cjh.signin.pojo.Stu
import com.demo.cjh.signin.util.*

/**
 * 预览|导入数据页面
 * 表格页面
 */
class TableActivity : AppCompatActivity() {

    val TAG = "TableActivity"

    /**
     * 从外面来
     */
    val ForOther = 0

    /**
     * 从app里来
     */
    val ForApp = 1

    /**
     * 数据对象
     */
    lateinit var data: Array<Array<String>>

    /**
     * 标题数组
     */
    lateinit var titles: Array<String>

    /**
     * 当前打开文件对象
     */
    lateinit var file: File

    var classId = ""
    var className = ""

    lateinit var action: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table)

        init()
    }

    private fun init() {

        /**
         * 加载Excel文件
         */
        init_file()
        /**
         * 加载表格数据
         */
        init_data()

    }

    /**
     * 加载文件
     */
    private fun init_file() {
        var act = intent.action
        action = act?:"0"
        if (Intent.ACTION_VIEW == action) {
            /**
             * app外部加载
             */
            var str = intent.dataString
            if (str != null) {
                var uri = Uri.parse(str)
                var path = FileUtil.getFilePath(this, uri)
                file = File(path)
                /**
                 * 外部文件打开 显示文件名
                 */
                title = file.getFileName()
                Log.d(TAG, "app外打开 --> " + file.getDirPath() + "  --> " + file.name)
            }

        } else {
            /**
             * 内部处理
             */
            // app内部选择文件加载
            classId = intent.getStringExtra("classId")
            className = intent.getStringExtra("className")

            /**
             * 内部文件打开 显示班级名
             */
            title = className

            val path = intent.getStringExtra("path")
            /**
             * 当前打开文件对象
             */
            if (path != null) {
                file = File(path)
            }
        }
    }


    /**
     * 加载数据
     */
    fun init_data(){
        doAsync {
            when(file.getSuffex()){
                FileUtil.XLS ->{
                    data = readExcelToArray(file)
                }
                FileUtil.XLSX ->{
                    // 暂不支持xlsx

                }
            }

            uiThread {
                titles = data[0]
                data = data.toList().drop(1).toTypedArray()
                var tdata = ArrayTableData.transformColumnArray(data)
                var tableData: ArrayTableData<String>
                try {
                    tableData = ArrayTableData.create("Excel表", titles, tdata, TextDrawFormat<String>())
                    table.tableData = tableData

                    val wm1 = this@TableActivity.windowManager
                    val outMetrics = DisplayMetrics()
                    wm1.defaultDisplay.getMetrics(outMetrics)
                    val width1 = outMetrics.widthPixels
                    table.config.minTableWidth = width1
                    Log.d(TAG,"w:"+tableData.arrayColumns[0].computeWidth+"  "+width1.toFloat().toString())
                    table.setZoom(true,2f,0.5f)
                }catch (e: Exception){
                    toast("读取文件出错,检查文件格式是否正确")
                }

            }
        }
    }


    fun readExcelToArray(file: File): Array<Array<String>> {

        var inputStream = file.inputStream()
        var workbook = WorkbookFactory.create(inputStream)
        var sheet = workbook.getSheetAt(0)

        // 总行数
        var rowCount = sheet.physicalNumberOfRows
        // 第一行
        var tabletitle = sheet.getRow(sheet.firstRowNum)
        // 总列数
        var cellCount = tabletitle.physicalNumberOfCells

        var data = Array(rowCount) {Array(cellCount) {""} }

        // 读取数据
        for (i  in 0 until rowCount) {
            var row = sheet.getRow(i)
            var rowData = Array(row.physicalNumberOfCells) {""}

            for (j in 0 until row.physicalNumberOfCells) {
                rowData[j] = sheet.getRow(i).getCell(j).getValue()
            }
            data[i] = rowData
        }

        return data
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        /**
         *  加载导入菜单
         */
        menuInflater.inflate(R.menu.table,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // TODO: 导入数据
        when(item!!.itemId ) {
            R.id.input ->{
                // 导入数据

                var stuIdIndex = -1
                var stuNameIndex = -1


                /**
                 * 选择学生学号列，学生姓名列
                 * 采用智能识别模式，
                 */
                for((index,s) in titles.withIndex()) {
                    if(stuIdIndex != -1 && stuNameIndex != -1){
                        break
                    }
                    when (s ){
                        "学号" -> stuIdIndex = index
                        "姓名" -> stuNameIndex = index
                    }
                }


                if(stuIdIndex == -1 || stuNameIndex == -1){
                    // 手动选择导入列

                    val view = LayoutInflater.from(this@TableActivity).inflate(R.layout.dialog_patrol_start, null)
                    val spinner1 = view.find<Spinner>(R.id.id_spinner)
                    val spinner2 = view.find<Spinner>(R.id.na_spinner)

                    val adapter1 = ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item, titles)
                    val adapter2 = ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item, titles)
                    spinner1.adapter = adapter1
                    spinner2.adapter = adapter2
                    spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }

                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            stuIdIndex = position
                        }

                    }
                    spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }

                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            stuNameIndex = position
                        }

                    }

                    alert("请选择导入列") {
                        this.customView = view

                        positiveButton("确定"){
                            if (stuIdIndex != stuNameIndex){
                                // 获取数据，跳转页面
                                getStuToAddActivity(stuIdIndex, stuNameIndex)
                            }else{
                                toast("不能选择同一列！")
                            }
                        }
                        negativeButton("取消"){

                        }

                    }.show()


                }else {

                    // 识别成功  自动获取列值
                    getStuToAddActivity(stuIdIndex, stuNameIndex)

                }

            }
        }

        return true
    }

    /**
     * 通过列标，获取学生学号 姓名 跳转页面
     */
    private fun getStuToAddActivity(stuIdIndex: Int, stuNameIndex: Int) {
        var stus = ArrayList<Stu>()
        //data = ArrayTableData.transformColumnArray(data)
        var stu: Stu
        for (s in data) {
            stu = Stu(classId, s[stuIdIndex], s[stuNameIndex])
            stus.add(stu)
        }
        /**
         * 跳转页面
         */
//        startActivity<AddStuActivity>("classId" to classId, "className" to className, "stuData" to stus)
        val intent = Intent(this@TableActivity,AddStuActivity::class.java)
        intent.putExtra("classId",classId)
        intent.putExtra("className",className)
        intent.putExtra("stuData",stus)
        intent.putExtra("action",action)
        if (Intent.ACTION_VIEW == action) {
            startActivityForResult(intent, ForOther)
        }else{
            startActivityForResult(intent, ForApp)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                ForOther ->{
                    // 跳转主页面
                    startActivity<MainActivity>()
                }

                ForApp ->{
                    // 结束当前页面
                    finish()
                }

            }
        }


    }

}
