package com.demo.cjh.signin.Activity

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.demo.cjh.signin.R
import com.demo.cjh.signin.`object`.StudentInfo
import com.demo.cjh.signin.util.database
import kotlinx.android.synthetic.main.activity_stu_list2.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivity

class StuList : AppCompatActivity() {

    var stuData = ArrayList<StudentInfo>()
    var classId: String? = null
    lateinit var adapter: StuAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stu_list2)

        init()

    }

    private fun init() {
        classId = intent.getStringExtra("classId")
        var className = intent.getStringExtra("name")
        title = className
        adapter = StuAdapter(this,stuData)

        mListView.emptyView = empty_view
        mListView.adapter = adapter
        mListView.setOnItemClickListener { parent, view, position, id ->
            // 学生信息
            val stuId = stuData[position].stuId
            val name = stuData[position].name
            startActivity<StuInfoActivity>("classId" to classId,"className" to className,"stuId" to stuId,"name" to name)

        }

        doAsync {
            stuData.clear()

            stuData.addAll(database.query_stuInfo_by_classId(classId!!)) // 查找学生名单

            runOnUiThread {
                adapter.notifyDataSetChanged()
            }
        }


    }


    class StuAdapter(context: Context, data: ArrayList<StudentInfo>) : BaseAdapter(){

        private var inflater: LayoutInflater = LayoutInflater.from(context)
        private var data: ArrayList<StudentInfo> = data


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
            holder.name.text = data[position].name
            holder.stuId.text = data[position].stuId

            return v
        }

        class Holder(val v: View) {
            var name = v.find<TextView>(R.id.name)
            var stuId = v.find<TextView>(R.id.stuId)
        }

    }



}
