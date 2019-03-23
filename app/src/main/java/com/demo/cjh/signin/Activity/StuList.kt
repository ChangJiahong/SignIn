package com.demo.cjh.signin.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.demo.cjh.signin.R
import com.demo.cjh.signin.pojo.Stu
import com.demo.cjh.signin.service.IStuService
import com.demo.cjh.signin.service.impl.StuServiceImpl
import com.demo.cjh.signin.util.FileUtil
import kotlinx.android.synthetic.main.activity_stu_list2.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread

/**
 * 学生列表界面
 */
class StuList : AppCompatActivity() {

    private var stuData = ArrayList<Stu>()

    private lateinit var classId: String
    private lateinit var className: String
    /**
     * adapter 学生
     */
    private lateinit var adapter: StuAdapter

    private lateinit var stuService: IStuService


    private lateinit var addStuByHand: Button

    private lateinit var addStuByFile: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stu_list2)

        init()

    }

    private fun init() {
        classId = intent.getStringExtra("classId")
        className = intent.getStringExtra("name")
        // 设置标题
        title = className


        stuService = StuServiceImpl(this)


        initView()

        add_init()

        initData()


    }

    /**
     * 加载view
     */
    private fun initView() {
        adapter = StuAdapter(this, stuData)

        mListView.emptyView = empty_view
        mListView.adapter = adapter
        mListView.setOnItemClickListener { parent, view, position, id ->
            // 学生信息
            val stu = stuData[position]
            startActivity<StuInfoActivity>("classId" to classId, "className" to className, "stu" to stu)
        }

    }

    override fun onRestart() {
        super.onRestart()
        initView()
        initData()
    }

    /**
     * 添加学生信息 的设置初始化
     */
    fun add_init(){

        addStuByHand = empty_view.find(R.id.byHand)
        addStuByFile = empty_view.find(R.id.byFile)
        /**
         * 手动添加学生
         */
        addStuByHand.setOnClickListener {
            startActivity<AddStuActivity>(
                    "classId" to classId,
                    "className" to className,
                    "action" to "0")
        }
        /**
         * 文件添加
         */
        addStuByFile.setOnClickListener {

            val intent =  Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/vnd.ms-excel"//Excel类型限制
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(intent,1)
        }
    }

    /**
     * 加载数据
     */
    private fun initData() {
        doAsync {
            stuData.clear()
            stuData.addAll(stuService.getStusByClassId(classId)) // 查找学生名单

            uiThread {
                adapter.notifyDataSetChanged()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            // 文件系统返回 文件路径
            if(requestCode == 1){
                val uri = data!!.data
                var path = ""
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                    path = FileUtil.getPath(this, uri) ?:""
                } else {//4.4以下下系统调用方法
                    path = FileUtil.getRealPathFromURI(this, uri) ?:""
                }

                /**
                 * 启动导入页面
                 */
                startActivity<TableActivity>(
                        "classId" to classId,
                        "className" to className,
                        "path" to path)
            }
        }
    }

    class StuAdapter(context: Context, private var data: ArrayList<Stu>) : BaseAdapter(){

        private var inflater: LayoutInflater = LayoutInflater.from(context)


        override fun getItem(position: Int): Any {
            return data[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return data.size
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val holder: Holder
            val v: View
            if (convertView == null) {
                v = inflater.inflate(R.layout.stu_list2_item, null)
                holder = Holder(v)
                //holder.name = v!!.findViewById(R.id.text) as TextView
                v.tag = holder
            } else {
                v = convertView
                holder = v.tag as Holder
            }
            holder.name.text = data[position].stuName
            holder.stuId.text = data[position].stuId

            return v
        }

        class Holder(val v: View) {
            var name = v.find<TextView>(R.id.name)
            var stuId = v.find<TextView>(R.id.stuId)
        }

    }



}
