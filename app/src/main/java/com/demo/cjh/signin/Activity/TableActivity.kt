package com.demo.cjh.signin.Activity

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
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
import java.io.InputStream
import java.net.URI
import android.view.WindowManager
import android.view.LayoutInflater
import android.view.Window.FEATURE_NO_TITLE
import android.widget.EditText

/**
 * 预览|导入数据页面
 */
class TableActivity : AppCompatActivity() {

    val TAG = "TableActivity"
    // 第一行
    var row: Row? = null
    // 总行数
    var rowCount = 0
    // 总列数
    var cellCount = 0
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

    /**
     * 当前打开文件对象
     */
    var file: File? = null

    /**
     * 数据对象
     */
    var data = ArrayList<Array<String>>()

    var mProgressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table)



        init()
    }

    private fun init() {

        var action = intent.action
        if(Intent.ACTION_VIEW == action){
            var str = intent.dataString
            if(str != null){
                var uri = Uri.parse(str)
                var fIn = File(URI(uri.toString()))

                Log.v(TAG,"app外打开 --> "+fIn.getDirPath()+"  --> "+fIn.name)

                file = fIn

            }
        }else{
            var filename = intent.getStringExtra("filepath")
            file = File(FileUtil.fileDirectory + "/"+filename)
            Log.v(TAG,"app内打开 --> "+file!!.getDirPath()+"  --> "+file!!.name)

        }


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
                readExcel(file!!)

                //Log.v(TAG, sheet.getRow(1).getCell(0).toString())
            }
            FileUtil.XLSX -> {
                // xlsx文档类型待解决
                showDialog("暂不支持xlsx文件格式，请期待更新！")

            }
        }
        Log.v(TAG, "关闭")


        table_name.text = file!!.getFileName()
        Log.v(TAG,data.size.toString())
        mTableList.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))
        mTableList.adapter = TableAdapter(data){position ->

//            val intent = Intent(this@StuListActivity,SignInActivity::class.java)
//            intent.putExtra("code",0)
//            intent.putExtra("position",position)
//            intent.putExtra("data",data)
//
//            startActivityForResult(intent,REQUEST_REGION_PICK)

            //startActivity(intent)
        }

    }


    fun readExcel(file: File){
        doAsync {
            var inputStream = file.inputStream()
            var workbook = WorkbookFactory.create(inputStream)
            var sheet = workbook.getSheetAt(0)
            // 总行数
            rowCount = sheet.physicalNumberOfRows
            // 第一行
            row = sheet.getRow(sheet.firstRowNum)
            // 总列数
            cellCount = row!!.lastCellNum.toInt()
            /**
             * 文件格式是否正确
             */
            // hasStuId 拥有学号列
            hasStuId = false
            // hasName 拥有姓名列
            hasName = false

            for (i in (0..(cellCount - 1))) {
                when (row!!.getCell(i).getValue()) {
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

                Log.v(TAG, row!!.getCell(stuIdIndex).toString() + "  " + row!!.getCell(nameIndex).toString())

                for (row in sheet) {
                    var rowData = Array(2, { "" })
                    Log.v(TAG, stuIdIndex.toString() + "   " + nameIndex)
                    rowData[0] = row.getCell(stuIdIndex).getValue()  //getValue(_row.getCell(stuIdCount))
                    rowData[1] = row.getCell(nameIndex).getValue()   //getValue(_row.getCell(nameCount))
                    data.add(rowData)
                }

            } else {
                uiThread {
                    showDialog("导入数据失败，请确认文件格式是否符合规范!")
                }
            }
            mProgressDialog!!.dismiss()
            uiThread {
                toast("加载完成")
                mTableList.adapter.notifyDataSetChanged()
            }
        }
    }

    fun showDialog(msg: String){
        AlertDialog.Builder(this@TableActivity)
                .setTitle(msg)
                .setPositiveButton("确定"){ dialogInterface: DialogInterface, i: Int ->
                    finish()
                }
                .setCancelable(false)
                .show()
    }

    class TableAdapter(val mItems : ArrayList<Array<String>>, internal val didSelectedAtPos:(idx : Int) -> Unit) : RecyclerView.Adapter<TableAdapter.ViewHolder>(){

        internal var mContext : Context? = null
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            mContext = parent.context

            return ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.activity_table_item,parent,false))
        }

        override fun getItemCount(): Int {
            return mItems.size
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            fun bind(model: Array<String>){

                if(position > 0)
                    holder.idView.text = (position).toString()

                holder.stu_id.text = model[0].substringBeforeLast(".")
                holder.name.text = model[1]

                with(holder.container){
                    setOnClickListener {
                        didSelectedAtPos(position)
                    }
                }


            }
            val item = mItems[position]
            bind(item)
        }


        class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
            var container = view.find<LinearLayout>(R.id.table_item)
            var idView = view.find<TextView>(R.id.id)
            var stu_id = view.find<TextView>(R.id.stu_id)
            var name = view.find<TextView>(R.id.name)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.table,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // TODO: 导入数据
        var view = LayoutInflater.from(this).inflate(R.layout.table_dialog, null)
        var input = view.find<EditText>(R.id.input)
        AlertDialog.Builder(this)
                .setTitle("请输入班级名称：")
                .setView(view)
                .setPositiveButton("确定"){ dialogInterface: DialogInterface, i: Int ->
                    toast(input.text)
                }
                .setNegativeButton("取消"){ dialogInterface: DialogInterface, i: Int ->

                }
                .setCancelable(false)
                .show()

        return true
    }

}
