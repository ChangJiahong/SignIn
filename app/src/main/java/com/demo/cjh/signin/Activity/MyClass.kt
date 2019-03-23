package com.demo.cjh.signin.Activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import com.demo.cjh.signin.Fragment.MenuFragment
import com.demo.cjh.signin.R
import com.demo.cjh.signin.pojo.ClassInfo
import com.demo.cjh.signin.pojo.Classes
import com.demo.cjh.signin.service.IClassesService
import com.demo.cjh.signin.service.impl.ClassesServiceImpl
import com.demo.cjh.signin.util.database
import kotlinx.android.synthetic.main.activity_my_class.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread

/**
 * 我的班级界面
 */
class MyClass : AppCompatActivity() {

    private lateinit var adapter: MenuFragment.MenuAdapter

    var data = ArrayList<Classes>()

    private lateinit var classService: IClassesService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_class)
        init()
    }

    private fun init() {

        // 初始化服务
        classService = ClassesServiceImpl(this)


        //var data = arrayListOf<String>("软件工程一班","网络工程一班","网络工程二班","计科一班","计科二班","计科三班","计科四班")

        doAsync {
            data.clear()

            data.addAll(classService.getAllClasses())

            uiThread {
                // 更新数据
                adapter.notifyDataSetChanged()
            }
        }

        mListView.emptyView = empty_view

        adapter = MenuFragment.MenuAdapter(data, this)
        mListView.adapter = adapter
        mListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
//            val intent = Intent(this, StuList::class.java)
//            intent.putExtra("classId",data[position].classId)
//            intent.putExtra("name",data[position].className)
//            startActivity(intent)
            startActivity<StuList>("classId" to data[position].classId ,"name" to data[position].className)
        }
        registerForContextMenu(mListView)


    }
}
