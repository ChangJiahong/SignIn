package com.demo.cjh.signin.Activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import com.demo.cjh.signin.*
import com.demo.cjh.signin.Adapter.StuSignInListAdapter
import kotlinx.android.synthetic.main.activity_sign_old.*
import android.widget.TextView
import com.demo.cjh.signin.util.FileUtil
import com.demo.cjh.signin.util.database
import com.demo.cjh.signin.util.getValue
import java.io.File
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.jetbrains.anko.*
import android.os.Build
import com.demo.cjh.signin.util.FileUtil.getPath
import com.demo.cjh.signin.util.FileUtil.getRealPathFromURI
import com.demo.cjh.signin.pojo.*
import com.demo.cjh.signin.service.impl.RecordServiceImpl
import com.demo.cjh.signin.service.impl.StuServiceImpl
import com.demo.cjh.signin.util.fileDirectoryPath

/**
 * 考勤记录页面
 */
class SignOldActivity : AppCompatActivity() {

    val TAG = "SignOldActivity"

    private val REQUEST_REGION_PICK = 1
    private val REQUEST_ADD = 2

    /**
     * 数据对象
     */
    val stuSignInLists = ArrayList<OldListItem>()
    val stuData = ArrayList<StuInfo>()
    lateinit var stuSignInListAdapter: StuSignInListAdapter

    var mProgressDialog: ProgressDialog? = null

    lateinit var addStuByHand: Button // 手动添加数据
    lateinit var addStuByFile: Button // 文件导入

    var classId=""
    var className=""
    var typeId = ""


    lateinit var stuService: StuServiceImpl
    lateinit var recordService: RecordServiceImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_old)

        init()
    }

    private fun init() {

        classId = intent.getStringExtra("classId")
        className = intent.getStringExtra("className")
        typeId = intent.getStringExtra("typeId")
        title = className


        // 配置服务
        stuService = StuServiceImpl(this)
        recordService = RecordServiceImpl(this)

        /**
         * 手动添加学生信息配置
         */
        addStuByHand = empty_view.find(R.id.byHand)
        addStuByFile = empty_view.find(R.id.byFile)
        addStuByHand.setOnClickListener {
            startActivity<TableActivity>("classId" to classId,"className" to className)
        }
        addStuByFile.setOnClickListener {
            val intent =  Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/vnd.ms-excel"//Excel类型限制
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(intent,REQUEST_REGION_PICK)
        }

        /**
         * 配置头view
         */
        var vHead = View.inflate(this@SignOldActivity, R.layout.stu_sign_in_list_item, null)
        val text = vHead.find<TextView>(R.id.info)
        val text1 = vHead.find<TextView>(R.id.time)
        text1.text = ""
        text.text = "新建点名"

        /**
         * 添加头view
         */
        OldList.addHeaderView(vHead)

        /**
         * 初始化适配器
         */
        stuSignInListAdapter = StuSignInListAdapter(stuSignInLists,this@SignOldActivity)
        OldList.adapter = stuSignInListAdapter

        // 显示空
        showEmpty(false)


        // 更新数据
        // Task().execute(classId)
        initUIData()


        /**
         * item list点击事件
         */
        OldList.setOnItemClickListener { parent, view, position, id ->

            when(position){
                0 ->{
                    val intent = Intent(this@SignOldActivity,StuListActivity::class.java)
                    intent.putExtra("classId",classId)
                    intent.putExtra("className",className)
                    intent.putExtra("action",0)
                    startActivity(intent)
                }
                else ->{
                    val intent = Intent(this@SignOldActivity,StuListActivity::class.java)
                    intent.putExtra("classId",classId)
                    intent.putExtra("className",className)
                    intent.putExtra("action",1)
                    // intent.putExtra("no",stuSignInLists[position-1].no)
                    startActivity(intent)
                }
            }
        }
        /**
         * 菜单注册
         */
        registerForContextMenu(OldList)


    }

    private fun initUIData() {
        doAsync {
            stuData.clear()
            stuData.addAll(stuService.getStuInfosByClassId(classId))
            if(stuData.isEmpty()){
                uiThread {
                    showEmpty(true)
                }
            }else {
                var data = recordService.getListByClassIdAndTypeId(classId,typeId) as ArrayList<OldListItem>
                data.reverse()
                stuSignInLists.clear()
                stuSignInLists.addAll(data)
                uiThread {
                    // 刷新
                    stuSignInListAdapter.notifyDataSetChanged()
                }

            }
        }
    }


    private fun showEmpty(show: Boolean) {
        empty_view.setVisibility(if (show) View.VISIBLE else View.GONE)
        view.setVisibility(if (show) View.GONE else View.VISIBLE)
    }

/*
    inner class Task : AsyncTask<String, Void, ArrayList<OldListItem>>() {
        override fun doInBackground(vararg params: String?): ArrayList<OldListItem>? {

            // 更新数据

            var classId = params.first()!!
            var typeId = ""

            //stuData.clear()
            // stuData.addAll(database.query_stuInfo_by_classId(params.first()!!))
            //TODO：构建stu服务层
            stuData.clear()
            stuData.addAll(stuService.getStusByClassId(classId))

            if(stuData.isEmpty()){
                //showEmpty(true)
                return null
            }else {
                return recordService.getListByClassIdAndTypeId(classId,typeId) as ArrayList<OldListItem>?
            }

        }

        override fun onPostExecute(result: ArrayList<OldListItem>?) {
            super.onPostExecute(result)

            if(result != null){
                result.reverse()
                stuSignInLists.clear()
                stuSignInLists.addAll(result)

                stuSignInListAdapter!!.notifyDataSetChanged()

            }else{
                showEmpty(true)
            }

        }

    }

*/


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.stu_list,menu)
        return super.onCreateOptionsMenu(menu)
    }

    // TODO: 导出Excel 待重构
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.out ->{
                if(stuSignInLists.size <=0) {
                    toast("暂无签到数据，不能导出！")
                    return true
                }
                var fileName = className+System.currentTimeMillis()
                var  dialogView = LayoutInflater.from(this@SignOldActivity).inflate(R.layout.input_dialog, null)
                var editText = dialogView.find<EditText>(R.id.input)
                editText.setText(fileName)
                alert {
                    title = "文件名："

                    this.customView = dialogView

                    positiveButton("是"){
                        fileName = editText.text.toString()
                        var filePath = "$fileDirectoryPath/$fileName.${FileUtil.XLS}"
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


    /**
     * 导出记录
     * TODO: 待重构
     */
    fun export(fileName: String){
        // TODO: 查询表中所有数据，保存
        // 表头，根据表头插入数据
        var flag = false
        mProgressDialog = ProgressDialog(this)
        mProgressDialog!!.setCancelable(true)
        mProgressDialog!!.setCanceledOnTouchOutside(false)
        mProgressDialog!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL) // 设置水平进度条
        mProgressDialog!!.setTitle("请稍后")
        mProgressDialog!!.setButton(DialogInterface.BUTTON_NEGATIVE, "取消") { dialogInterface: DialogInterface, i: Int ->
            flag = true
            toast("导出失败")
        }
        mProgressDialog!!.setMax(100);
        mProgressDialog!!.setMessage("正在努力导出中...")
        mProgressDialog!!.show()
        doAsync {
            var filePath = "$fileDirectoryPath/$fileName.${FileUtil.XLS}"
            var newFile = File(filePath)
            if(!newFile.exists()){
                newFile.createNewFile()
            }
            var workbook = HSSFWorkbook()
            var sheet = workbook.createSheet("sheet1")
            var headRow = sheet.createRow(0)

            var cell = headRow.createCell(0)
            cell.setCellValue("学号")
            cell = headRow.createCell(1)
            cell.setCellValue("姓名")

            var rowCount = stuData.size+1
            var cellCount = stuSignInLists.size+2

            stuSignInLists.reverse()
            /**
             * 创建第一行的列
             */
            for(i in 0..(stuSignInLists.size-1)){
                cell = headRow.createCell(i+2)
                cell.setCellValue(stuSignInLists[i].time)
            }

            /**
             * 创建stuData学生的行
             */
            for(i in 1..(rowCount-1)){
                var row = sheet.createRow(i)
                var cell = row.createCell(0)
                cell.setCellValue(stuData[i-1].stuId)
                cell = row.createCell(1)
                cell.setCellValue(stuData[i-1].stuName)
            }

            /**
             * 建立数据对象
             * 查询表数据
             * 插入
             */

            var stuSign = database.query_data_by_classId(classId)

            for(i in 1..(rowCount-1)){ // 行标
                var r = sheet.getRow(i)
                for(j in 2..(cellCount-1)){ // 列标
                    var c = r.createCell(j)
                    var stuId = r.getCell(0).getValue()
                    var no = headRow.getCell(j).getValue()
                    for(s in stuSign){
                        s.forEach{
                            Log.v(TAG,"stu:"+stuId+" no:"+no)
                            Log.v(TAG,it.stuId+" "+it.no)
                            if(it.stuId.equals(stuId)&&it.no.equals(no)){
                                c.setCellValue(it.type)
                                Log.v(TAG,i.toString()+"-------------------"+j)
                            }
                        }
                    }
                }
                if(flag)
                    break
                mProgressDialog!!.incrementProgressBy(rowCount/100)
            }

            workbook.write(newFile)
            workbook.close()

            runOnUiThread {
                toast("导出成功")
                mProgressDialog!!.dismiss()
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
     * 删除
     */
    override fun onContextItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.edit ->{
                val MenuInfo = item.menuInfo as AdapterView.AdapterContextMenuInfo
                if(MenuInfo.position >0) {
                    val item = stuSignInLists[MenuInfo.position - 1]

                    //TODO:删除记录

                    // database.delete_signInList(item.id!!, item.classId!!, item.no!!)
                    doAsync {

                        var re = recordService.deleteListByClassIdAndTitleAndTypeId(classId,item.title,typeId)

                        Log.v(TAG, "移除" + item.title + "成功")

                        uiThread {
                            stuSignInLists.removeAt(MenuInfo.position - 1)
                            stuSignInListAdapter.notifyDataSetChanged()
                        }
                    }


                }

            }
        }
        return super.onContextItemSelected(item)
    }

    override fun onRestart() {
        super.onRestart()

        showEmpty(false)
        // 更新数据
        //Task().execute(classId)
        initUIData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if(requestCode == REQUEST_REGION_PICK){
                val uri = data!!.data
                var path = ""
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                    path = getPath(this, uri)?:""
                } else {//4.4以下下系统调用方法
                    path = getRealPathFromURI(this,uri)?:""
                }

                Log.d(TAG,path)

                startActivity<TableActivity>("classId" to classId,"className" to className,"type" to 1,"path" to path)
            }
        }

    }


}
