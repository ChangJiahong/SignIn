package com.demo.cjh.signin.Activity

import android.app.ActionBar
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.demo.cjh.signin.*
import com.demo.cjh.signin.Adapter.StuAdapter
import kotlinx.android.synthetic.main.activity_table2.*
import kotlinx.android.synthetic.main.activity_table2_item.view.*
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.jetbrains.anko.*
import java.io.File
import java.net.URI
import java.util.zip.Inflater

/**
 * 查看数据页面 默认打开第一个日期的状态
 */
class Table2Activity : AppCompatActivity() {

    val TAG = "Table2Activity"

    /**
     * 当前打开文件对象
     */
    var file: File? = null

    /**
     * 数据对象
     */
    var contentData = ArrayList<ArrayList<String>>()

    var stuData = ArrayList<StudentInfo>()
    var titleData = ArrayList<String>()

    var mProgressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table2)
        init()

    }

    private fun init() {

        var filename = intent.getStringExtra("filepath")
        file = File(FileUtil.fileDirectory + "/"+filename)
        Log.v(TAG,"app内打开 --> "+file!!.getDirPath()+"  --> "+file!!.name)



        mProgressDialog = ProgressDialog(this)
        mProgressDialog!!.setCancelable(true)
        mProgressDialog!!.setCanceledOnTouchOutside(false)
        mProgressDialog!!.setTitle("请稍后")
        mProgressDialog!!.setButton(DialogInterface.BUTTON_NEGATIVE, "取消") { dialogInterface: DialogInterface, i: Int ->
            finish()
        }
        mProgressDialog!!.setMessage("正在努力加载中...")

        Log.v(TAG, "开始")

        when (file!!.getSuffex()) {
            FileUtil.XLS -> {

                mProgressDialog!!.show()
                // 线程操作
                Task().execute(file!!)

                //Log.v(TAG, sheet.getRow(1).getCell(0).toString())
            }
            FileUtil.XLSX -> {
                // xlsx文档类型待解决
                showDialog("暂不支持xlsx文件格式，请期待更新！")

            }
        }
        Log.v(TAG, "关闭")


        table_name.text = file!!.getFileName()
        Log.v(TAG,contentData.size.toString())
        mTableList.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        mTableList.adapter = StuAdapter(stuData) { position ->

            //            val intent = Intent(this@StuListActivity,SignInActivity::class.java)
//            intent.putExtra("code",0)
//            intent.putExtra("position",position)
//            intent.putExtra("data",data)
//
//            startActivityForResult(intent,REQUEST_REGION_PICK)

            //startActivity(intent)
        }


        mMenu.setOnCheckedChangeListener { group, checkedId ->
            // 切换数据源
            getCellType(checkedId)
            // 刷新数据
            mTableList.adapter.notifyDataSetChanged()
        }


    }


    fun readExcel(file: File): TableInfo{
        var tableInfo = TableInfo()
        var inputStream = file.inputStream()
        var workbook = WorkbookFactory.create(inputStream)
        var sheet = workbook.getSheetAt(0)
        // 总行数
        var rowCount = sheet.physicalNumberOfRows
        // 第一行
        var tabletitle = sheet.getRow(sheet.firstRowNum)
        // 总列数
        var cellCount = tabletitle.lastCellNum.toInt()
        /**
         * 文件格式是否正确
         */
        if (tabletitle.getCell(0).getValue() == "学号" && tabletitle.getCell(1).getValue() == "姓名") {
            // 格式正确  读取数据
            // 读取表头
            for(i in (2..(tabletitle.physicalNumberOfCells-1))){
                tableInfo.title.add(tabletitle.getCell(i).getValue())
            }

            // 读取数据不包括第一行表头
            for (i  in (1..(sheet.physicalNumberOfRows-1))) {
                var rowData = ArrayList<String>()
                for (cell in sheet.getRow(i)) {
                    rowData.add(cell.getValue())
                }
                tableInfo.data.add(rowData)
            }

            tableInfo.status = true
        }else{
            tableInfo.status = false
        }

        return tableInfo
    }

    fun showDialog(msg: String){
        AlertDialog.Builder(this@Table2Activity)
                .setTitle(msg)
                .setPositiveButton("确定"){ dialogInterface: DialogInterface, i: Int ->
                    finish()
                }
                .setCancelable(false)
                .show()
    }


    inner class Task : AsyncTask<File, Void, TableInfo>() {
        override fun doInBackground(vararg params: File?): TableInfo {
            return readExcel(params.first()!!)
        }

        override fun onPostExecute(result: TableInfo?) {
            super.onPostExecute(result)
            if(result!!.status){

                titleData.clear()
                for(da in result.title){
                    titleData.add(da)
                }
                Log.v(TAG,titleData.size.toString())

                //menus.adapter.notifyDataSetChanged()
                //var i =0
                for(i in (0..(result.title.size-1))){
                    var rbBtn = LayoutInflater.from(this@Table2Activity).inflate(R.layout.table2_top_tab,null) as RadioButton
                    rbBtn.text = result.title[i]
                    rbBtn.id = i
                    var params = RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT)
                    mMenu.addView(rbBtn,params)
                }
                mMenu.check(0)

                contentData.clear()
                for(da in result.data){
                    contentData.add(da)
                }
                // 第一次日期的签到情况获取
                getCellType(0)

                toast("加载完成")
                mTableList.adapter.notifyDataSetChanged()

            }else{
                showDialog("导入数据失败，请确认文件格式是否符合规范!")
            }
            mProgressDialog!!.dismiss()
        }

    }

    fun getCellType(index: Int){
        //Log.v(TAG,"type :")
        stuData.clear()
        for(i in (0..(contentData.size-1))){
            // index+2 -> 读取数据的时候去掉了表头 在获取type需要+2保持一致
            try {
                stuData.add(StudentInfo(contentData[i][0],contentData[i][1],contentData[i][index+2],""))
                //Log.v(TAG,"type "+contentData[i][index+2])
            }catch (e: IndexOutOfBoundsException){
                stuData.add(StudentInfo(contentData[i][0],contentData[i][1],"",""))
            }

        }
    }
}
