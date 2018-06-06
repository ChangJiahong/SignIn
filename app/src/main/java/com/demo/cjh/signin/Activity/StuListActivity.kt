package com.demo.cjh.signin.Activity

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.*
import com.demo.cjh.signin.Adapter.StuAdapter
import com.demo.cjh.signin.R
import com.demo.cjh.signin.StudentInfo
import com.demo.cjh.signin.TableInfo
import kotlinx.android.synthetic.main.activity_stu_list.*
import kotlinx.android.synthetic.main.activity_table2_item.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import java.io.Serializable

class StuListActivity : AppCompatActivity() {

    val TAG = "StuListActivity"

    private val REQUEST_REGION_PICK = 1

    /**
     * 数据对象
     */
    var contentData = ArrayList<ArrayList<String>>()

    var stuData = ArrayList<StudentInfo>()
    var titleData = ArrayList<String>()

    var mProgressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stu_list)

        init()
    }

    private fun init() {

        class_name.text = intent.getStringExtra("id")

        var tableInfo = TableInfo()

        for(i in (1..60)){
            stuData.add(StudentInfo(i.toString(),"庄三".plus(i),"",""))

        }
        for(i in (1..10)){
            titleData.add("日期"+i)
        }

        initMenu(titleData)

        mProgressDialog = ProgressDialog(this)
        mProgressDialog!!.setCancelable(true)
        mProgressDialog!!.setCanceledOnTouchOutside(false)
        mProgressDialog!!.setTitle("请稍后")
        mProgressDialog!!.setButton(DialogInterface.BUTTON_NEGATIVE, "取消") { dialogInterface: DialogInterface, i: Int ->
            finish()
        }
        mProgressDialog!!.setMessage("正在努力加载中...")

        stu_list.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))
        stu_list.adapter = StuAdapter(stuData){position ->

                val intent = Intent(this@StuListActivity,SignInActivity::class.java)
                intent.putExtra("code",0)
                intent.putExtra("position",position)
                intent.putExtra("data",stuData)

                startActivityForResult(intent,REQUEST_REGION_PICK)

            //startActivity(intent)
        }

        mMenu.setOnCheckedChangeListener { group, checkedId ->
            // 切换数据源
            getCellType(checkedId)
            // 刷新数据
            stu_list.adapter.notifyDataSetChanged()
        }

        stu_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(dy > 0){
                    Log.v(TAG,"向下")

                }else if(dy <0){
                    Log.v(TAG,"向上")

                }
            }
        })

    }


    fun initMenu(title: ArrayList<String>){
        for(i in (0..(title.size-1))){
            var rbBtn = LayoutInflater.from(this@StuListActivity).inflate(R.layout.table2_top_tab,null) as RadioButton
            rbBtn.text = title[i]
            rbBtn.id = i
            var params = RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT)
            mMenu.addView(rbBtn,params)
        }
        mMenu.check(0)
    }
    inner class Task : AsyncTask<String, Void, TableInfo>() {
        override fun doInBackground(vararg params: String?): TableInfo {

            return getStuDataByClassId(params.first()!!)
        }

        private fun getStuDataByClassId(first: String): TableInfo {
            // 数据库操作

            return null!!
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

                initMenu(result.title)

                contentData.clear()
                for(da in result.data){
                    contentData.add(da)
                }
                // 第一次日期的签到情况获取
                getCellType(0)

                toast("加载完成")
                stu_list.adapter.notifyDataSetChanged()

            }else{
                showDialog("导入数据失败，请确认文件格式是否符合规范!")
            }
            mProgressDialog!!.dismiss()
        }

    }


    fun showDialog(msg: String){
        AlertDialog.Builder(this@StuListActivity)
                .setTitle(msg)
                .setPositiveButton("确定"){ dialogInterface: DialogInterface, i: Int ->
                    finish()
                }
                .setCancelable(false)
                .show()
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        if(keyCode == KeyEvent.KEYCODE_BACK){
            alert {
                title = "提示"
                message = "是否保存"
                positiveButton("是"){
                    val intent = Intent()
                    intent.putExtra("data",stuData as Serializable)
                    setResult(Activity.RESULT_OK,intent)
                    finish()
                }
                negativeButton("否"){

                }
            }.show()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.stu_list,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val intent = Intent(this@StuListActivity,SignInActivity::class.java)

        intent.putExtra("code",1)
        intent.putExtra("id",0)
        intent.putExtra("data",stuData)

        startActivityForResult(intent,REQUEST_REGION_PICK)
        //startActivity(intent)
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, datas: Intent?) {
        super.onActivityResult(requestCode, resultCode, datas)

        if(resultCode == Activity.RESULT_OK){
            if(requestCode == REQUEST_REGION_PICK){
                if(datas != null) {
                    stuData.clear()
                    var da = datas.getSerializableExtra("data")!! as List<StudentInfo>
                    for(item in da){
                        stuData.add(item)
                    }
                    stu_list.adapter.notifyDataSetChanged()
                }
            }
        }
    }


}
