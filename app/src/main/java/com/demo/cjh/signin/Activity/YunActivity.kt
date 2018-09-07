package com.demo.cjh.signin.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.demo.cjh.signin.App
import com.demo.cjh.signin.R
import com.demo.cjh.signin.util.*
import kotlinx.android.synthetic.main.activity_yun.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import org.json.JSONObject

class YunActivity : AppCompatActivity(), View.OnClickListener {

    val TAG = "YunActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_yun)

        init()
    }

    private fun init() {
        back_up.setOnClickListener(this)
        restore.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if(!App.app!!.sp!!.getBoolean("isLogin",false)){
            toast("还未登陆，先登陆试试！")

            return
        }
        when(v!!.id){
            R.id.back_up ->{
                // 备份
                // 遍历数据库，读取
                // 先读班级信息表（classInfo) -> 学生信息表（StuInfo） -> 学生考勤记录表（StuSignInList） -> 学生考勤信息详表（StuSignInInfo）

                progressView.visibility = View.VISIBLE
                back_up.isEnabled = false
                doAsync {
                    val classInfos = database.query_classInfo()
                    val classDataString = classInfoDateToJson(classInfos)
                    //Log.v(TAG,classDataString)

                    val stuInfos = database.query_stuInfo()
                    val stuDataString = stuInfoDataToJson(stuInfos)

                    val signInList = database.query_signInList()
                    val signInListDataString = signInListDataToJson(signInList)

                    val signInInfo = database.query_signInInfo()
                    val signInInfoDataString = signInInfoDataToJson(signInInfo)

                    val signInDataString =  signInDataToJson(App.app!!.user.id,classDataString,stuDataString,signInListDataString,signInInfoDataString)

                    /**
                     * TODO: 备份人脸信息
                     */
                    //val faces = database.queryFaces()
                    val resultString = Http.backUp(signInDataString)
                    //val resultString1 = Http.setFace(faces)

                    val result = getreslut(resultString)
                    uiThread {
                        progressView.visibility = View.GONE
                        back_up.isEnabled = true
                        if(result.status == 1){
                            toast(result.msg)
                        }else{
                            toast(result.msg)
                        }
                    }

                }



            }

            R.id.restore ->{
                // 恢复
                progressView.visibility = View.VISIBLE
                restore.isEnabled = false
                doAsync {
                    val resultString = Http.reStore()
                    Log.v(TAG,resultString)


                    // 解析数据
                    val result = getreslut(resultString)
                    if(result.status == 1){
                        val data = result.data
                        var jsonObject = JSONObject(data)
                        val classInfoArray = jsonObject.getJSONArray("classInfos")
                        val stuInfoArray = jsonObject.getJSONArray("stuInfos")
                        val signInListArray = jsonObject.getJSONArray("signInList")
                        val signInInfoArray = jsonObject.getJSONArray("signInInfos")

                        val classInfos = JsonToClassInfoDate(classInfoArray)
                        val stuInfos = JsonToStuInfoData(stuInfoArray)
                        val signInList = JsonToSignInListData(signInListArray)
                        val signInInfos = JsonToSignInInfoData(signInInfoArray)

                        // 清空数据
                        database.cleanDataBase()
                        // 插入数据
                        database.insertClassInfo(classInfos)
                        database.insertStuInfos(stuInfos)
                        database.insertStuSignInList(signInList)
                        database.insertStuSignINInfo(signInInfos)

                        uiThread {
                            progressView.visibility = View.GONE
                            restore.isEnabled = true
                            toast("恢复成功")
                        }

                    }else {

                        uiThread {
                            progressView.visibility = View.GONE
                            restore.isEnabled = true
                            toast("恢复失败")
                        }
                    }
                }



            }
        }
    }



}
