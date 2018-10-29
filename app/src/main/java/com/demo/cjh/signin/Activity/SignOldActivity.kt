package com.demo.cjh.signin.Activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import com.demo.cjh.signin.*
import com.demo.cjh.signin.Adapter.StuSignInListAdapter
import kotlinx.android.synthetic.main.activity_sign_old.*
import android.widget.TextView
import com.demo.cjh.signin.FileUtil
import com.demo.cjh.signin.obj.StuSignInList
import com.demo.cjh.signin.obj.StudentInfo
import com.demo.cjh.signin.util.database
import com.demo.cjh.signin.util.getValue
import java.io.File
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.jetbrains.anko.*
import android.os.Build
import com.demo.cjh.signin.FileUtil.getPath
import com.demo.cjh.signin.FileUtil.getRealPathFromURI


class SignOldActivity : AppCompatActivity() {

    val TAG = "SignOldActivity"

    private val REQUEST_REGION_PICK = 1
    private val REQUEST_ADD = 2

    /**
     * 数据对象
     */
    val stuSignInLists = ArrayList<StuSignInList>()
    val stuData = ArrayList<StudentInfo>()
    var stuSignInListAdapter: StuSignInListAdapter? = null

    var mProgressDialog: ProgressDialog? = null

    var addStuByHand: Button? = null // 手动添加数据
    var addStuByFile: Button? = null // 文件导入

    var classId=""
    var className=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_old)

        init()
    }

    private fun init() {

        classId = intent.getStringExtra("classId")
        className = intent.getStringExtra("className")
        title = className


        addStuByHand = empty_view.find(R.id.byHand)
        addStuByFile = empty_view.find(R.id.byFile)
        addStuByHand!!.setOnClickListener {
            startActivity<TableActivity>("classId" to classId,"className" to className)
        }
        addStuByFile?.setOnClickListener {
            val intent =  Intent(Intent.ACTION_GET_CONTENT)
            //intent.setType(“image/*”);//选择图片
            //intent.setType(“audio/*”); //选择音频
            //intent.setType(“video/*”); //选择视频 （mp4 3gp 是android支持的视频格式）
            //intent.setType(“video/*;image/*”);//同时选择视频和图片
            intent.type = "application/vnd.ms-excel"//无类型限制
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(intent,REQUEST_REGION_PICK)
        }

        var vHead = View.inflate(this@SignOldActivity, R.layout.stu_sign_in_list_item, null)
        val text = vHead.find<TextView>(R.id.info)
        val text1 = vHead.find<TextView>(R.id.time)
        text1.text = ""
        text.text = "新建点名"

        OldList.addHeaderView(vHead)

        stuSignInListAdapter = StuSignInListAdapter(stuSignInLists,this@SignOldActivity)
        OldList.adapter = stuSignInListAdapter

        showEmpty(false)
        // 更新数据
        Task().execute(classId)

        OldList.setOnItemClickListener { parent, view, position, id ->

            when(position){
                0 ->{
                    val intent = Intent(this@SignOldActivity,StuListActivity::class.java)
                    intent.putExtra("classId",classId)
                    intent.putExtra("className",className)
                    intent.putExtra("type",0)
                    startActivity(intent)
                }
                else ->{
                    val intent = Intent(this@SignOldActivity,StuListActivity::class.java)
                    intent.putExtra("classId",classId)
                    intent.putExtra("className",className)
                    intent.putExtra("type",1)
                    intent.putExtra("no",stuSignInLists[position-1].no)
                    startActivity(intent)
                }
            }
        }
        registerForContextMenu(OldList)


    }


    private fun showEmpty(show: Boolean) {
        empty_view.setVisibility(if (show) View.VISIBLE else View.GONE)
        view.setVisibility(if (show) View.GONE else View.VISIBLE)
    }


    inner class Task : AsyncTask<String, Void, ArrayList<StuSignInList>>() {
        override fun doInBackground(vararg params: String?): ArrayList<StuSignInList>? {

            stuData.clear()
            stuData.addAll(database.query_stuInfo_by_classId(params.first()!!))
            if(stuData.isEmpty()){
                //showEmpty(true)
                return null
            }else {
                return database.query_signInList_by_classId(params.first()!!)
            }

        }

        override fun onPostExecute(result: ArrayList<StuSignInList>?) {
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
                var  dialogView = LayoutInflater.from(this@SignOldActivity).inflate(R.layout.input_dialog, null)
                var editText = dialogView.find<EditText>(R.id.input)
                editText.setText(fileName)
                alert {
                    title = "文件名："

                    this.customView = dialogView

                    positiveButton("是"){
                        fileName = editText.text.toString()
                        var filePath = FileUtil.fileDirectory+"/"+fileName+"."+ FileUtil.XLS
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
            var filePath = FileUtil.fileDirectory+"/"+fileName+"."+ FileUtil.XLS
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
                cell.setCellValue(stuSignInLists[i].time!!.substring(0,10)+"("+stuSignInLists[i].num+")")
            }

            /**
             * 创建stuData学生的行
             */
            for(i in 1..(rowCount-1)){
                var row = sheet.createRow(i)
                var cell = row.createCell(0)
                cell.setCellValue(stuData[i-1].stuId)
                cell = row.createCell(1)
                cell.setCellValue(stuData[i-1].name)
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
     */
    override fun onContextItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.edit ->{
                val MenuInfo = item.menuInfo as AdapterView.AdapterContextMenuInfo
                if(MenuInfo.position >0) {
                    val item = stuSignInLists[MenuInfo.position - 1]
                    var classId = item.id
                    database.delete_signInList(item.id!!, item.classId!!, item.no!!)
                    Log.v(TAG, "移除" + item.info + "成功")
                    stuSignInLists.removeAt(MenuInfo.position - 1)
                    stuSignInListAdapter!!.notifyDataSetChanged()
                }

            }
        }
        return super.onContextItemSelected(item)
    }

    override fun onRestart() {
        super.onRestart()

        showEmpty(false)
        // 更新数据
        Task().execute(classId)
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
