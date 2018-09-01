package com.demo.cjh.signin.Activity

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.*
import com.demo.cjh.signin.*
import com.demo.cjh.signin.Adapter.StuAdapter
import com.demo.cjh.signin.`object`.StuSignInList
import com.demo.cjh.signin.`object`.StudentInfo
import com.demo.cjh.signin.util.database
import com.demo.cjh.signin.util.getNow
import kotlinx.android.synthetic.main.activity_stu_list.*
import org.jetbrains.anko.*

/**
 * 点名列表界面
 */
class StuListActivity : AppCompatActivity() {

    val TAG = "StuListActivity"

    private val REQUEST_REGION_PICK = 1 // 手动
    private val REQUEST_CODE_IMAGE_CAMERA = 2  // 相机
    private val REQUEST_CODE_IMAGE_OP = 3  // 相册
    private val REQUEST_CODE_OP = 4 //识别

    var stuData = ArrayList<StudentInfo>()
    var classId: String? = null

    /**
     * 修改标志
     */
    var flag = false
    /**
     * 类型
     * 0 -> 默认，可修改数据，点名
     * 1 -> 可以修改，不可点名
     * 2 -> 不可修改，不可点名
     */
    var type = 0

    /**
     * 签到标志
     */
    var signIn = false // 默认手动

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stu_list)

        init()
    }

    private fun init() {
        type = intent.getIntExtra("type",0)
        classId = intent.getStringExtra("classId")
        var className = intent.getStringExtra("className")
        var no = ""
        if(type != 0)
            no = intent.getStringExtra("no")

        class_name.text = className
        title = className

        stu_list.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))
        stu_list.adapter = StuAdapter(stuData){position ->
            if(type != 2) {
                flag = true
                val intent = Intent(this@StuListActivity, SignInActivity::class.java)
                intent.putExtra("code", 0)
                intent.putExtra("position", position)
                intent.putExtra("data", stuData)

                startActivityForResult(intent, REQUEST_REGION_PICK)
            }
            //startActivity(intent)
        }

        doAsync {
            stuData.clear()
            if(type == 0) {
                stuData.addAll(database.query_stuInfo_by_classId(classId!!)) // 查找学生名单
                // 初始化人脸库
                App.mFaceDB.mRegister.clear()
                App.mFaceDB.loadFaces(classId!!)
                Log.v(TAG,"face Size:"+App.mFaceDB.mRegister.size)
            }else{
                Log.v(TAG,"no:"+no)
                var data = database.query_signInInfo_by_classId_and_no(classId!!,no)
                Log.v(TAG,"data size:"+data.size)
                for(stu in data){
                    Log.v(TAG,stu.name)

                    stuData.add(stu)
                }

            }
            runOnUiThread {
                stu_list.adapter.notifyDataSetChanged()
            }
        }



//        for(i in (1..60)){
//            stuData.add(StudentInfo(i.toString(),"庄三".plus(i),"",""))
//
//        }





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

        Log.v(TAG,"初始化成功")

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

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        Log.v(TAG, "flag:$flag")
        if(keyCode == KeyEvent.KEYCODE_BACK && flag){
            Log.v(TAG,"返回键")
            var  dialogView = LayoutInflater.from(this@StuListActivity).inflate(R.layout.input_dialog, null)

            alert {
                title = "提示"
                if(stuData.any{it.type.isEmpty()}){
                    if(signIn)
                        message = "检查到你有未点名的学生，继续保存将默认为旷课"
                    else
                        message = "检查到你有未点名的学生，继续保存将默认为出勤"
                }else {
                    message = "是否保存"
                }
                if(type == 0){
                    this.customView = dialogView
                }
                positiveButton("是"){

                    var mProgressDialog = ProgressDialog(this@StuListActivity)
                    mProgressDialog.setCancelable(true)
                    mProgressDialog.setCanceledOnTouchOutside(false)
                    mProgressDialog.setTitle("请稍后...")
                    mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消") { dialogInterface: DialogInterface, i: Int ->

                        toast("保存失败")
                    }
                    mProgressDialog.show()
                    doAsync {
                        when(type) {
                            0 -> {// 新建记录
                                // 保存操作
                                // 1. 插入考勤记录表记录

                                var time = getNow()
                                val stuSignInList = StuSignInList(classId!!, time, null)
                                val num = database.getNum(classId!!,time.substring(0,10))
                                stuSignInList.num = num
                                stuSignInList.info = dialogView.find<EditText>(R.id.input).text.toString()
                                database.insert_stuSignInList(stuSignInList)

                                // 2. 插入考勤信息样表记录
                                // 类型转换，学生信息 -> 学生考勤详情
                                // 插入
                                for (stu in stuData) {
                                    if (stu.type.isEmpty()) {
                                        if(signIn)
                                            stu.type = "旷课"
                                        else
                                            stu.type = "出勤"
                                    }
                                    stu.time = time
                                    stu.no = time.substring(0,10)+"("+num+")"
                                    database.insert_stuSignInInfo(stu.toStuSignInInfo())
                                }
                            }
                            1 -> {
                                // 更新记录
                                for(stu in stuData){
                                    database.updata_stuSignInInfo(stu.toStuSignInInfo())
                                }
                            }
                        }
                        runOnUiThread{
                            mProgressDialog.dismiss()

//                    val intent = Intent()
//                    intent.putExtra("data",stuData as Serializable)
//                    setResult(Activity.RESULT_OK,intent)
                            finish()
                        }
                    }


                }
                negativeButton("否"){
                    finish()
                }
            }.show()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(type == 0)
            menuInflater.inflate(R.menu.dian_ming,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        flag = true
        when(item!!.itemId){
            R.id.dm ->{
                // 手动点名
                val intent = Intent(this@StuListActivity,SignInActivity::class.java)

                intent.putExtra("code",1)
                intent.putExtra("id",0)
                intent.putExtra("data",stuData)

                startActivityForResult(intent,REQUEST_REGION_PICK)

            }
            R.id.facedm ->{

                // 人脸识别
                // 判断是否存在人脸
                if(App.mFaceDB.mRegister.isEmpty()) {
                    // 显示为空
                    showDialog("暂无学生人脸数据，请先录入")
                }else{
                    AlertDialog.Builder(this@StuListActivity)
                            .setTitle("请选择相机")
                            .setItems(arrayOf("后置相机", "前置相机")) { dialog, which ->

                                val it = Intent(this@StuListActivity, SignInByFace::class.java)
                                it.putExtra("Camera", which)
                                startActivityForResult(it, REQUEST_CODE_OP)
                            }
                            .show()

                }

            }
        }

        //startActivity(intent)
        return super.onOptionsItemSelected(item)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, datas: Intent?) {
        super.onActivityResult(requestCode, resultCode, datas)

        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                REQUEST_REGION_PICK ->{
                    if(datas != null) {
                        stuData.clear()
                        var da = datas.getSerializableExtra("data")!! as List<StudentInfo>
                        for(item in da){
                            stuData.add(item)
                        }
                        Log.v(TAG,"回调  "+stuData[0].type)
                        stu_list.adapter.notifyDataSetChanged()
                    }
                }
                REQUEST_CODE_OP ->{
                    // 人脸识别到的学生姓名
                    if(datas != null){
                        flag = true
                        signIn = true

                        val da = datas.getStringArrayListExtra("stuIds")
                        stuData.forEach {
                            var its = it
                            Log.v(TAG," | " +its.stuId+" "+its.type)
                            if(da.any { its.stuId == it }){
                                // 存在
                                its.type = "出勤"
                            }

                            Log.v(TAG,its.stuId+" "+its.type)
                        }
                        Log.v(TAG,"人脸识别回调  "+stuData[0].type)
                        stu_list.adapter.notifyDataSetChanged()


                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 清空脸库
        App.mFaceDB.mRegister.clear()
        Log.v(TAG,"清空脸库")
    }


}
