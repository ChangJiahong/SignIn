package com.demo.cjh.signin.Fragment

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.TextView
import com.demo.cjh.signin.Activity.SelectActivity
import com.demo.cjh.signin.R
import com.demo.cjh.signin.pojo.Classes
import com.demo.cjh.signin.service.IClassesService
import com.demo.cjh.signin.service.impl.ClassesServiceImpl
import com.demo.cjh.signin.util.*
import kotlinx.android.synthetic.main.fragment_menu.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.uiThread
import kotlin.collections.ArrayList


/**
 * 主页，点名页面
 */
class MenuFragment : Fragment() {

    val TAG = "MenuFragment"

    lateinit var adapter: MenuAdapter
    var data = ArrayList<Classes>()

    lateinit var classesService: IClassesService

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    private fun init() {

        classesService = ClassesServiceImpl(activity!!)

        doAsync {
            data.clear()

            data.addAll(classesService.getAllClasses())

            uiThread {
                // 更新数据
                adapter.notifyDataSetChanged()
            }
        }

        mListView.emptyView = empty_view

        adapter = MenuAdapter(data, activity!!)
        mListView.adapter = adapter
        mListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            var item = data[position]
            startActivity<SelectActivity>("classId" to item.classId , "className" to item.className)
        }
        registerForContextMenu(mListView)

        /**
         * 添加班级
         */
        add_btn.setOnClickListener {
            var view = LayoutInflater.from(activity).inflate(R.layout.table_dialog, null)
            var inputName = view.find<EditText>(R.id.inputName)
            var inputInfo = view.find<EditText>(R.id.inputInfo)
            AlertDialog.Builder(activity)
                    .setTitle("请输入班级信息：")
                    .setView(view)
                    .setPositiveButton("确定"){ dialogInterface: DialogInterface, i: Int ->
                        //toast(inputName.text.toString()+"\n"+inputInfo.text+"\n"+generateRefID())
                        var className = inputName.text.toString()
                        var info = inputInfo.text.toString()
                        var institute = ""// 二级学院
                        var speciality = ""// 学校
                        if(inputName.text.toString().isEmpty()){
                            toast("班级名不能为空！")
                        }else {

                            var classes: Classes? = null

//                            TransactionManager(activity!!).noTransaction.error {
//
//                            }.success {
//
//                            }.run {
//
//                            }.start()

                            TransactionManager(activity!!).start(object : TransactionManager.DoTransactionListener{
                                override fun run() {
                                    classes = classesService.saveClasses(className,info,institute,speciality)
                                }

                                override fun error(e: android.database.SQLException) {
                                    toast("创建班级失败")
                                }

                                override fun success() {
                                    if(classes != null){
                                        toast("创建班级成功！")
                                        data.add(classes!!)
                                        adapter.notifyDataSetChanged()
                                    }else{
                                        toast("创建班级失败")
                                    }
                                }

                            })

                        }
                    }
                    .setNegativeButton("取消"){ dialogInterface: DialogInterface, i: Int ->

                    }
                    .setCancelable(false)
                    .show()
        }

    }

    /**
     * 上下文菜单
     */
    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val menuInflater = activity!!.menuInflater
        menuInflater.inflate(R.menu.mainmenu, menu)
    }

    /**
     * 上下文菜单Item点击方法
     * 删除班级
     */
    override fun onContextItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.edit ->{
                val MenuInfo = item.menuInfo as AdapterView.AdapterContextMenuInfo
                var item = data[MenuInfo.position]

                // 删除班级所有信息
                TransactionManager(activity!!).start(object : TransactionManager.DoTransactionListener{
                    /**
                     * 数据库操作
                     */
                    override fun run() {
                        classesService.deleteClassesByClassId(item.classId)
                    }
                    /**
                     * 失败回调
                     */
                    override fun error(e: android.database.SQLException) {
                       toast("删除失败")
                    }
                    /**
                     * 成功回调
                     */
                    override fun success() {
                        toast("删除成功")
                        data.removeAt(MenuInfo.position)
                        adapter.notifyDataSetChanged()
                    }

                })

            }
        }
        return super.onContextItemSelected(item)
    }



    class MenuAdapter(val data : List<Classes>, val context : Context) : BaseAdapter() {
        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        var inflater : LayoutInflater = LayoutInflater.from(context)


        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

            var holder : Holder
            var v : View
            if(convertView == null){
                v = inflater.inflate(R.layout.fragment_menu_item,null)
                holder = Holder(v)
                v.tag = holder
            }else{
                v = convertView
                holder = v.tag as Holder
            }
            val mItem = data[position]
            holder.textView.text = mItem.className
            holder.time.text = mItem.createTime

            if (mItem.info.isNullOrEmpty()){
                holder.info.visibility = View.GONE
            }else {
                holder.info.text = mItem.info
            }

            return v
        }

        override fun getItem(position: Int): Classes? {
            return this.data[position]
        }

        override fun getCount(): Int {
            return this.data.size
        }

        class Holder(v :View) {
            var textView = v.find<TextView>(R.id.item1)
            var time = v.find<TextView>(R.id.time)
            var info = v.find<TextView>(R.id.info)
        }
    }

}
