package com.demo.cjh.signin.Activity

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.EditText
import com.demo.cjh.signin.R
import com.demo.cjh.signin.View.PickerView
import com.demo.cjh.signin.service.IRecordSaveService
import com.demo.cjh.signin.service.impl.RecordSaveServiceImpl
import com.demo.cjh.signin.util.TransactionManager
import com.demo.cjh.signin.util.doService
import kotlinx.android.synthetic.main.record_save_msg.*
import org.jetbrains.anko.*

/**
 * Created by CJH
 * on 2018/12/8
 * @date 2018/12/8
 * @author CJH
 */

/**
 * 记录保存信息设置窗口
 */
class RecordSaveMsg : AppCompatActivity(){
    private val TAG = RecordSaveMsg::class.java.name

    private lateinit var recordSaveService: IRecordSaveService

    private lateinit var subs: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.record_save_msg)

        init()

    }

    private fun init() {

        recordSaveService = RecordSaveServiceImpl(context = this)

        mTitle.addTextChangedListener(NoSpace(mTitle))
        mSubject.addTextChangedListener(NoSpace(mSubject))

        TransactionManager(context = this).noTransaction.run {
            subs = recordSaveService.getAllSubject()
        }.success {
            list.setOnClickListener {
                if (subs.isEmpty()){
                    longToast("没有科目可选！自己添加一个吧")
                }else {
                    AlertDialog.Builder(this)
                            .setTitle("选择科目")
                            .setItems(subs.toTypedArray()){dialog, which ->
                                mSubject.setText(subs[which])
                            }.show()

//                    alert("选择科目") {
//                        items(arrayListOf("sad","dasd","das")) { dialogInterface, i ->
//                            mSubject.setText(subs[i])
//                        }
//                    }.show()
                }
            }
        }.start()


        mTime.isCursorVisible = false
        mTime.isFocusable = false
        mTime.isFocusableInTouchMode = false


        mTime.setOnClickListener {
            toast("点击了")
            val v = LayoutInflater.from(this).inflate(R.layout.selector_layout,null)
            val mWeek = v.find<PickerView>(R.id.week)
            val mStart = v.find<PickerView>(R.id.start)
            val mEnd = v.find<PickerView>(R.id.end)
            var w = 0
            var s = 0
            var e = 0
            mWeek.setDataList(arrayListOf("周一","周二","周三","周四","周五","周六","周日"))
            mWeek.setOnScrollChangedListener(object: PickerView.OnScrollChangedListener{
                override fun onScrollChanged(curIndex: Int) {

                }

                override fun onScrollFinished(curIndex: Int) {
                    w = curIndex
                }

            })
            mStart.setDataList(arrayListOf("第1节","第2节","第3节","第4节","第5节","第6节","第7节","第8节","第9节","第10节","第11节","第12节"))
            mStart.setOnScrollChangedListener(object: PickerView.OnScrollChangedListener{
                override fun onScrollChanged(curIndex: Int) {

                }

                override fun onScrollFinished(curIndex: Int) {
                    s = curIndex
                }

            })
            mEnd.setDataList(arrayListOf("到1节","到2节","到3节","到4节","到5节","到6节","到7节","到8节","到9节","到10节","到11节","到12节"))
            mEnd.setOnScrollChangedListener(object: PickerView.OnScrollChangedListener{
                override fun onScrollChanged(curIndex: Int) {

                }

                override fun onScrollFinished(curIndex: Int) {
                    e = curIndex
                }

            })
            alert("选择上课节数") {
                customView = v
                positiveButton("确定"){
                    var str = ""
                    when(w){
                        0 -> str += "周一"
                        1 -> str += "周二"
                        2 -> str += "周三"
                        3 -> str += "周四"
                        4 -> str += "周五"
                        5 -> str += "周六"
                        6 -> str += "周日"
                    }
                    str += "  第"
                    when {
                        s == e -> str += "${s+1}"
                        s > e -> toast("开始节数不能大于结束节数！")
                        else -> str += "${s+1}-${e+1}"
                    }
                    str += "节"
                    mTime.setText(str)
                    it.dismiss()
                }
                negativeButton("取消"){
                    it.dismiss()
                }
            }.show()
        }

        saveBtn.setOnClickListener {
            val sTitle = mTitle.text.toString()
            val sSubject = mSubject.text.toString()
            // 日期格式 周一 第1-2节 = 1-12
            val sTime = mTime.text.toString()
            val sInfo = mInfo.text.toString()
            if(sTitle.isEmpty() || sSubject.isEmpty() || sTime.isEmpty()){
                toast("信息不完整")
            }else if(sInfo.length >= 25){
                toast("备注信息不得超过25字")
            }else{
                // 查找subject id
                var sSubjectId = ""
                TransactionManager(this).noTransaction.run {
                    sSubjectId = recordSaveService.getIdByName(sSubject)
                }.success {
                    if(sSubjectId.isEmpty()){
                        // 如果是空，就是没找到，就新建一个、
                        doService {
                            noTransaction
                            run {
                                sSubjectId = recordSaveService.createSubject(sSubject)
                            }
                            success {
                                // 插入成功
                                val intent = Intent()
                                intent.putExtra("title",sTitle)
                                intent.putExtra("subjectId",sSubjectId)
                                intent.putExtra("time",sTime)
                                intent.putExtra("info",sInfo)
                                setResult(Activity.RESULT_OK,intent)
                                finish()
                            }
                        }.start()

                    }else {

                        // 插入成功
                        val intent = Intent()
                        intent.putExtra("title", sTitle)
                        intent.putExtra("subjectId", sSubjectId)
                        intent.putExtra("time", sTime)
                        intent.putExtra("info", sInfo)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }

                }.start()
            }
        }
    }




    class NoSpace(var edit: EditText) : TextWatcher{

        override fun afterTextChanged(s: Editable?) {

        }


        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s.toString().contains(" ")) {
                var str = s.toString().split(" ")
                var str1 = ""
                for (i in str) {
                    str1 += i
                }
                edit.setText(str1)
                edit.setSelection(start)

            }
        }

    }
}