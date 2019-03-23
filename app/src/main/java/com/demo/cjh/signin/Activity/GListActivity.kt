package com.demo.cjh.signin.Activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.demo.cjh.loadinglayoutlib.LoadingLayout
import com.demo.cjh.signin.Adapter.StuSignInListAdapter
import com.demo.cjh.signin.util.FileUtil
import com.demo.cjh.signin.R
import com.demo.cjh.signin.pojo.StuSignInList
import com.demo.cjh.signin.pojo.StudentInfo
import com.demo.cjh.signin.util.database
import com.demo.cjh.signin.util.fileDirectoryPath
import com.demo.cjh.signin.util.getValue
import kotlinx.android.synthetic.main.activity_glist.*
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.jetbrains.anko.*
import java.io.File

/**
 * 课堂表现记录
 */
class GListActivity : AppCompatActivity() {

    val TAG = "GListActivity"
    private val REQUEST_REGION_PICK = 1

    val stuSignInLists = ArrayList<StuSignInList>()
    val stuData = ArrayList<StudentInfo>()
    lateinit var stuSignInListAdapter: StuSignInListAdapter

    lateinit var classId: String
    lateinit var className: String

    private val ADD = 0
    private val WATCH = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_glist)

        classId = intent.getStringExtra("classId")
        className = intent.getStringExtra("className")
        title = className

        var definePage = LayoutInflater.from(this).inflate(R.layout.emptylayout,null)
        loadingProgress.setDefinePage(definePage)
        val addStuByHand = definePage.find<Button>(R.id.byHand)
        val addStuByFile = definePage.find<Button>(R.id.byFile)
        addStuByHand.setOnClickListener {
            startActivity<TableActivity>("classId" to classId,"className" to className)
        }

        addStuByFile.setOnClickListener {
            val intent =  Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/vnd.ms-excel"//无类型限制
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(intent,REQUEST_REGION_PICK)
        }




        var vHead = View.inflate(this@GListActivity, R.layout.stu_sign_in_list_item, null)
        val text = vHead.find<TextView>(R.id.info)
        val text1 = vHead.find<TextView>(R.id.time)
        text1.text = ""
        text.text = "新建课堂表现"

        listView.addHeaderView(vHead)

//        stuSignInListAdapter = StuSignInListAdapter(stuSignInLists,this@GListActivity)
        listView.adapter = stuSignInListAdapter

        listView.setOnItemClickListener { parent, view, position, id ->
            when(position){
                0 ->{
                    val intent = Intent(this@GListActivity,GPAActivity::class.java)
                    intent.putExtra("classId",classId)
                    intent.putExtra("className",className)
                    intent.putExtra("type",ADD)
                    startActivity(intent)
                }
                else ->{
                    val intent = Intent(this@GListActivity,GPAActivity::class.java)
                    intent.putExtra("classId",classId)
                    intent.putExtra("className",className)
                    intent.putExtra("type",WATCH)
                    intent.putExtra("no",stuSignInLists[position-1].no)
                    startActivity(intent)
                }
            }
        }
        registerForContextMenu(listView)

        initData()




    }

    fun initData(){
        loadingProgress.show(LoadingLayout.loading_page)
        doAsync {
            stuData.addAll(database.query_stuInfo_by_classId(classId))
            if(stuData.isEmpty()){
                uiThread {
                    loadingProgress.show(LoadingLayout.define_Page)
                }
            }else{
                val result = database.query_gpaList_by_classId(classId)
                result.reverse()
                stuSignInLists.clear()
                stuSignInLists.addAll(result)
                uiThread {
                    stuSignInListAdapter.notifyDataSetChanged()
                    // 隐藏加载页面
                    loadingProgress.visibility = View.GONE
                    Log.v(TAG,"加载成功")
                }

            }

        }
    }

    /**
     * 上下文菜单
     */
    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.mainmenu, menu)
    }

    /**
     * 上下文菜单Item点击方法
     */
    override fun onContextItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.edit ->{
                val MenuInfo = item.menuInfo as AdapterView.AdapterContextMenuInfo
                if(MenuInfo.position >0) {
                    val item = stuSignInLists[MenuInfo.position - 1]
                    var classId = item.id
                    database.delete_gpaList(item.id!!, item.classId!!, item.no!!)
                    Log.v(TAG, "移除" + item.info + "成功")
                    stuSignInLists.removeAt(MenuInfo.position - 1)
                    stuSignInListAdapter.notifyDataSetChanged()
                }

            }
        }
        return super.onContextItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.stu_list,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.out ->{
                if(stuSignInLists.size <=0) {
                    toast("暂无签到数据，不能导出！")
                    return true
                }
                var fileName = className+System.currentTimeMillis()
                var  dialogView = LayoutInflater.from(this@GListActivity).inflate(R.layout.input_dialog, null)
                var editText = dialogView.find<EditText>(R.id.input)
                editText.setText(fileName)
                alert {
                    title = "文件名："

                    this.customView = dialogView

                    positiveButton("是"){
                        fileName = editText.text.toString()
                        var filePath = fileDirectoryPath+"/"+fileName+"."+ FileUtil.XLS
                        var newFile = File(filePath)
                        if(newFile.exists()){
                            toast("文件名重复！！导出失败")
                        }else {
                            export(fileName)
                        }
                    }
                    negativeButton("否"){

                    }
                }.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun export(fileName: String) {

        // 表头，根据表头插入数据
        var flag = false
        var mProgressDialog = ProgressDialog(this)
        mProgressDialog.setCancelable(true)
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL) // 设置水平进度条
        mProgressDialog.setTitle("请稍后")
        mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消") { dialogInterface: DialogInterface, i: Int ->
            flag = true
            toast("导出失败")
        }
        mProgressDialog.setMax(100)
        mProgressDialog.setMessage("正在努力导出中...")
        mProgressDialog.show()
        doAsync {

            var filePath = fileDirectoryPath + "/" + fileName + "." + FileUtil.XLS
            var newFile = File(filePath)
            if (!newFile.exists()) {
                newFile.createNewFile()
            }
            var workbook = HSSFWorkbook()
            var sheet = workbook.createSheet("sheet1")
            var headRow = sheet.createRow(0)

            var cell = headRow.createCell(0)
            cell.setCellValue("学号")
            cell = headRow.createCell(1)
            cell.setCellValue("姓名")

            var rowCount = stuData.size + 1
            var cellCount = stuSignInLists.size + 2

            stuSignInLists.reverse()
            /**
             * 创建第一行的列
             */
            for (i in 0..(stuSignInLists.size - 1)) {
                cell = headRow.createCell(i + 2)
                cell.setCellValue(stuSignInLists[i].time!!.substring(0, 10) + "(" + stuSignInLists[i].num + ")")
            }

            /**
             * 创建stuData学生的行
             */
            for (i in 1..(rowCount - 1)) {
                var row = sheet.createRow(i)
                var cell = row.createCell(0)
                cell.setCellValue(stuData[i - 1].stuId)
                cell = row.createCell(1)
                cell.setCellValue(stuData[i - 1].name)
            }

            /**
             * 建立数据对象
             * 查询表数据
             * 插入
             */

            var stuSign = database.query_gpadata_by_classId(classId)

            for (i in 1..(rowCount - 1)) { // 行标
                var r = sheet.getRow(i)
                for (j in 2..(cellCount - 1)) { // 列标
                    var c = r.createCell(j)
                    var stuId = r.getCell(0).getValue()
                    var no = headRow.getCell(j).getValue()
                    for (s in stuSign) {
                        s.forEach {
                            Log.v(TAG, "stu:" + stuId + " no:" + no)
                            Log.v(TAG, it.stuId + " " + it.no)
                            if (it.stuId.equals(stuId) && it.no.equals(no)) {
                                var type = ""
                                when (it.type) {
                                    "A" -> type = "95"
                                    "B" -> type = "85"
                                    "C" -> type = "75"
                                    "D" -> type = "65"
                                }
                                c.setCellValue(type)
                                Log.v(TAG, i.toString() + "-------------------" + j)
                            }
                        }
                    }
                }
                if (flag)
                    break
                mProgressDialog.incrementProgressBy(rowCount / 100)
            }

            workbook.write(newFile)
            workbook.close()

            runOnUiThread {
                toast("导出成功")
                mProgressDialog.dismiss()
            }

        }

    }

    override fun onRestart() {
        super.onRestart()
        // 重新加载数据
        initData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if(requestCode == REQUEST_REGION_PICK){
                val uri = data!!.data
                var path = ""
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                    path = FileUtil.getPath(this, uri) ?:""
                } else {//4.4以下下系统调用方法
                    path = FileUtil.getRealPathFromURI(this, uri) ?:""
                }

                Log.d(TAG,path)

                startActivity<TableActivity>("classId" to classId,"className" to className,"type" to 1,"path" to path)
            }
        }

    }
}
