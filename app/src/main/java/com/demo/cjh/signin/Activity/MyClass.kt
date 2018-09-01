package com.demo.cjh.signin.Activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import com.demo.cjh.signin.Fragment.MenuFragment
import com.demo.cjh.signin.R
import com.demo.cjh.signin.`object`.ClassInfo
import com.demo.cjh.signin.util.database
import kotlinx.android.synthetic.main.activity_my_class.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MyClass : AppCompatActivity() {

    var adapter: MenuFragment.MenuAdapter? = null
    var data = ArrayList<ClassInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_class)
        init()
    }

    private fun init() {

        //var data = arrayListOf<String>("软件工程一班","网络工程一班","网络工程二班","计科一班","计科二班","计科三班","计科四班")
        doAsync {
            data.clear()
            data.addAll(database.query_classInfo())
            uiThread {
                // 更新数据
                adapter!!.notifyDataSetChanged()
            }
        }

        mListView.emptyView = empty_view

        adapter = MenuFragment.MenuAdapter(data, this)
        mListView.adapter = adapter
        mListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, StuList::class.java)
            intent.putExtra("classId",data[position].classId)
            intent.putExtra("name",data[position].className)
            startActivity(intent)
        }
        registerForContextMenu(mListView)


    }
}
