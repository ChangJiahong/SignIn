package com.demo.cjh.signin.Activity

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import com.demo.cjh.signin.*
import kotlinx.android.synthetic.main.activity_table.*
import org.apache.poi.ss.usermodel.*
import org.jetbrains.anko.*
import java.io.File
import android.view.LayoutInflater
import android.widget.EditText
import com.demo.cjh.signin.FileUtil
import com.demo.cjh.signin.obj.ClassInfo
import com.demo.cjh.signin.obj.StudentInfo
import com.demo.cjh.signin.obj.TableInfo
import com.demo.cjh.signin.util.*

/**
 * 预览|导入数据页面|手动添加数据
 */
class TableActivity : AppCompatActivity() {

    val TAG = "TableActivity"

    var flag = false

    /**
     * 数据对象
     */
    var data = ArrayList<StudentInfo>()

    var mProgressDialog: ProgressDialog? = null

    var classId = ""
    var className = ""
    var type = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table)



        init()
    }

    private fun init() {



        var action = intent.action

        if(Intent.ACTION_VIEW == action){
            // app外部加载
            flag = true
            /**
             * 当前打开文件对象
             */
            var file: File? = null

            var str = intent.dataString
            if(str != null){
                var uri = Uri.parse(str)
                var path = ""
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                    path = FileUtil.getPath(this, uri) ?:""
                } else {//4.4以下下系统调用方法
                    path = FileUtil.getRealPathFromURI(this, uri) ?:""
                }

                var fin = File(path)
                file = fin
                Log.v(TAG,"app外打开 --> "+fin.getDirPath()+"  --> "+fin.name)


            }
            // 文件获取数据

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

            table_name.text = file.getFileName()

        }else{

            type = intent.getIntExtra("type",0)
            if(type==0) {
                // app内部加载
                flag = false
                classId = intent.getStringExtra("classId")
                className = intent.getStringExtra("className")

                table_name.text = className
                title = className

                // 手动添加数据

                Log.v(TAG, "app内打开 --> ")
            }else if (type == 1){
                // app内部选择文件加载
                classId = intent.getStringExtra("classId")
                className = intent.getStringExtra("className")

                table_name.text = className
                val path = intent.getStringExtra("path")
                flag = true
                /**
                 * 当前打开文件对象
                 */
                var file: File? = null
                if(path != null) {
                    var fin = File(path)
                    file = fin

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

                }


            }

        }



        Log.v(TAG,data.size.toString())
        mTableList.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))
        mTableList.adapter = TableAdapter(data){position ->

            showDialog(data[position].stuId,data[position].name,position)
//            val intent = Intent(this@StuListActivity,SignInActivity::class.java)
//            intent.putExtra("code",0)
//            intent.putExtra("position",position)
//            intent.putExtra("data",data)
//
//            startActivityForResult(intent,REQUEST_REGION_PICK)

            //startActivity(intent)
        }

    }

    fun readExcel(file: File): TableInfo {
        var tableInfo = TableInfo()
        var inputStream = file.inputStream()
        var workbook = WorkbookFactory.create(inputStream)
        var sheet = workbook.getSheetAt(0)
        // 总行数
        var rowCount = sheet.physicalNumberOfRows
        if(rowCount>300){
            tableInfo.status = -2
            return tableInfo
        }
        // 第一行
        var firstRow = sheet.getRow(sheet.firstRowNum)
        // 总列数
        var cellCount = firstRow.lastCellNum.toInt()
        /**
         * 文件格式是否正确
         */
        // hasStuId 拥有学号列
        var hasStuId = false
        // hasName 拥有姓名列
        var hasName = false

        // 学号列下标 拥有
        var stuIdIndex = -1
        // 姓名列下标
        var nameIndex = -1

        for (i in (0..(cellCount - 1))) {
            when (firstRow!!.getCell(i).getValue()) {
                "学号" -> {
                    stuIdIndex = i
                    hasStuId = true
                }
                "姓名" -> {
                    nameIndex = i
                    hasName = true
                }
            }
            Log.v(TAG, "sad" + i)
        }

        // 获取列正确
        if (hasName && hasStuId && stuIdIndex >= 0 && nameIndex >= 0) {

            Log.v(TAG, firstRow!!.getCell(stuIdIndex).toString() + "  " + firstRow!!.getCell(nameIndex).toString())

            for (i in (1..(rowCount-1))) {
                var row = sheet.getRow(i)
                var rowData = ArrayList<String>()
                Log.v(TAG, stuIdIndex.toString() + "   " + nameIndex)
                rowData.add(row.getCell(stuIdIndex).getValue())  //getValue(_row.getCell(stuIdCount))
                rowData.add(row.getCell(nameIndex).getValue())  //getValue(_row.getCell(nameCount))
                tableInfo.data.add(rowData)
            }

            tableInfo.status = 0
        } else {
            tableInfo.status = -1
        }

        return tableInfo
    }


//    fun readExcel2(file: File){
//        doAsync {
//            var inputStream = file.inputStream()
//            var workbook = WorkbookFactory.create(inputStream)
//            var sheet = workbook.getSheetAt(0)
//            // 总行数
//            rowCount = sheet.physicalNumberOfRows
//            // 第一行
//            row = sheet.getRow(sheet.firstRowNum)
//            // 总列数
//            cellCount = row!!.lastCellNum.toInt()
//            /**
//             * 文件格式是否正确
//             */
//            // hasStuId 拥有学号列
//            var hasStuId = false
//            // hasName 拥有姓名列
//            var hasName = false
//
//            for (i in (0..(cellCount - 1))) {
//                when (row!!.getCell(i).getValue()) {
//                    "学号" -> {
//                        stuIdIndex = i
//                        hasStuId = true
//                    }
//                    "姓名" -> {
//                        nameIndex = i
//                        hasName = true
//                    }
//                }
//                Log.v(TAG, "sad" + i)
//            }
//
//            // 获取列正确
//            if (hasName && hasStuId && stuIdIndex >= 0 && nameIndex >= 0) {
//
//                Log.v(TAG, row!!.getCell(stuIdIndex).toString() + "  " + row!!.getCell(nameIndex).toString())
//
//                for (row in sheet) {
//                    var rowData = Array(2, { "" })
//                    Log.v(TAG, stuIdIndex.toString() + "   " + nameIndex)
//                    rowData[0] = row.getCell(stuIdIndex).getValue()  //getValue(_row.getCell(stuIdCount))
//                    rowData[1] = row.getCell(nameIndex).getValue()   //getValue(_row.getCell(nameCount))
//                    data.add(rowData)
//                }
//
//            } else {
//                uiThread {
//                    showDialog("导入数据失败，请确认文件格式是否符合规范!")
//                }
//            }
//            mProgressDialog!!.dismiss()
//            uiThread {
//                toast("加载完成")
//                mTableList.adapter.notifyDataSetChanged()
//            }
//        }
//    }



    fun showDialog(msg: String){
        AlertDialog.Builder(this@TableActivity)
                .setTitle(msg)
                .setPositiveButton("确定"){ dialogInterface: DialogInterface, i: Int ->
                    finish()
                }
                .setCancelable(false)
                .show()
    }

    fun showDialog(id: String,name: String,index: Int){
        var view = LayoutInflater.from(this).inflate(R.layout.add_dialog, null)
        var inputName = view.find<EditText>(R.id.inputName)
        var inputId = view.find<EditText>(R.id.inputId)
        inputId.setText(id)
        inputName.setText(name)

        AlertDialog.Builder(this@TableActivity)
                .setTitle("请输入学生信息名称：")
                .setView(view)
                .setPositiveButton("确定") { dialogInterface: DialogInterface, i: Int ->
                    if (inputId.text.isEmpty()){
//                                inputId.error = "学号不为空！"
//                                inputId.requestFocus()
                        toast("插入失败，学号不为空")
                    }else if (inputName.text.isEmpty()){
//                                inputName.error = "姓名不为空"
//                                inputName.requestFocus()
                        toast("插入失败，姓名不为空")
                    }else {
                        var id = inputId.text.toString()
                        var name = inputName.text.toString()

                            if(index >= data.size) { // 插入
                                // 如果没有和id相同的返回true
                                if (data.none { it.stuId == id }) {
                                    data.add(StudentInfo(id, name, "", classId))
                                    mTableList.adapter.notifyDataSetChanged()
                                } else {
                                    toast("插入失败，学号重复")
                                }
                            }else if(index < data.size) { // 更新
                                data[index].stuId = id
                                data[index].name = name
                                mTableList.adapter.notifyDataSetChanged()
                            }

                    }
                }
                .setNegativeButton("取消") { dialogInterface: DialogInterface, i: Int ->

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
            if (result!!.status == 0){
                data.clear()
                for(da in result.data){
                    data.add(StudentInfo(da[0], da[1], "", classId))
                }
                toast("加载完成")
                mTableList.adapter.notifyDataSetChanged()

            }else if(result!!.status == -1){
                showDialog("导入数据失败，请确认文件格式是否符合规范!")
            }else if(result!!.status == -2){
                showDialog("学生数量不得超过300人！")
            }
            mProgressDialog!!.dismiss()
        }

    }

    class TableAdapter(val mItems : ArrayList<StudentInfo>, internal val didSelectedAtPos:(idx : Int) -> Unit) : RecyclerView.Adapter<TableAdapter.ViewHolder>(){

        internal var mContext : Context? = null
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            mContext = parent.context

            return ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.activity_table_item,parent,false))
        }

        override fun getItemCount(): Int {
            return mItems.size+1
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            if(position > 0) {
                fun bind(model: StudentInfo) {

                    holder.idView.text = (position).toString()
                    holder.stu_id.text = model.stuId.substringBeforeLast(".")
                    holder.name.text = model.name

                    with(holder.container) {
                        setOnClickListener {
                            didSelectedAtPos(position-1)
                        }
                    }


                }
                val item = mItems[position-1]
                bind(item)
            }else{
                holder.stu_id.text = "学号"
                holder.name.text = "姓名"
            }


        }


        class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
            var container = view.find<LinearLayout>(R.id.table_item)
            var idView = view.find<TextView>(R.id.id)
            var stu_id = view.find<TextView>(R.id.stu_id)
            var name = view.find<TextView>(R.id.name)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        Log.v(TAG,"加载菜单")
        if(flag)
            menuInflater.inflate(R.menu.table,menu)
        else
            menuInflater.inflate(R.menu.add,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // TODO: 导入数据
        when(item!!.itemId ) {
            R.id.input ->{ // 导入数据
                if(type == 1){
                    for (i in (0..(data.size - 1))) {
                        data[i].classId = classId
                    }
                    // 新建学生
                    database.insert_stuInfo(data)
                    finish()
                }else {
                    var view = LayoutInflater.from(this).inflate(R.layout.table_dialog, null)
                    var inputName = view.find<EditText>(R.id.inputName)
                    var inputInfo = view.find<EditText>(R.id.inputInfo)
                    AlertDialog.Builder(this)
                            .setTitle("请输入班级名称：")
                            .setView(view)
                            .setPositiveButton("确定") { dialogInterface: DialogInterface, i: Int ->
                                toast(inputName.text)
                                var classInfo = ClassInfo(generateRefID(), inputName.text.toString(), inputInfo.text.toString())
                                // 新建班级
                                database.insert_classInfo(classInfo)
                                for (i in (0..(data.size - 1))) {
                                    data[i].classId = classInfo.classId!!
                                }
                                // 新建学生
                                database.insert_stuInfo(data)

                                finish()
                                startActivity<MainActivity>()
                            }
                            .setNegativeButton("取消") { dialogInterface: DialogInterface, i: Int ->

                            }
                            .setCancelable(false)
                            .show()
                }
            }
            R.id.add ->{ // 添加消息框
                showDialog("","",data.size)
            }
            R.id.ok ->{ // 完成
                // 插入数据
                database.insert_stuInfo(data)
                finish()
            }

        }

        return true
    }

}
