package com.demo.cjh.signin.Fragment

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.TextView
import com.demo.cjh.signin.Activity.SignOldActivity
import com.demo.cjh.signin.`object`.ClassInfo
import com.demo.cjh.signin.R
import com.demo.cjh.signin.util.database
import com.demo.cjh.signin.util.generateRefID
import kotlinx.android.synthetic.main.fragment_menu.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.uiThread
import kotlin.collections.ArrayList


class MenuFragment : Fragment() {

    val TAG = "MenuFragment"
    companion object {

        private var fragment: MenuFragment? = null

        @JvmStatic
        fun getInstance(): MenuFragment {

            if (fragment == null) {
                fragment = MenuFragment()
            }
            return fragment!!
        }
    }


    var adapter: MenuAdapter? = null
    var data = ArrayList<ClassInfo>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    private fun init() {

        //var data = arrayListOf<String>("软件工程一班","网络工程一班","网络工程二班","计科一班","计科二班","计科三班","计科四班")
        doAsync {
            data.clear()
            data.addAll(activity!!.database.query_classInfo())
            uiThread {
                // 更新数据
                adapter!!.notifyDataSetChanged()
            }
        }

        mListView.emptyView = empty_view

        adapter = MenuAdapter(data, activity!!)
        mListView.adapter = adapter
        mListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val intent = Intent(activity, SignOldActivity::class.java)
            intent.putExtra("classId",data[position].classId)
            intent.putExtra("name",data[position].className)
            startActivity(intent)
        }
        registerForContextMenu(mListView)

        add_btn.setOnClickListener {
            var view = LayoutInflater.from(activity).inflate(R.layout.table_dialog, null)
            var inputName = view.find<EditText>(R.id.inputName)
            var inputInfo = view.find<EditText>(R.id.inputInfo)
            AlertDialog.Builder(activity)
                    .setTitle("请输入班级信息：")
                    .setView(view)
                    .setPositiveButton("确定"){ dialogInterface: DialogInterface, i: Int ->
                        //toast(inputName.text.toString()+"\n"+inputInfo.text+"\n"+generateRefID())

                        if(inputName.text.toString().isEmpty()){
                            toast("班级名不能为空！")
                        }else {
                            doAsync {
                                var classInfo = ClassInfo(generateRefID(), inputName.text.toString(), inputInfo.text.toString())
                                activity!!.database.insert_classInfo(classInfo)
                                data.clear()
                                data.addAll(activity!!.database.query_classInfo())
                                uiThread {
                                    // 更新数据
                                    adapter!!.notifyDataSetChanged()
                                    Log.v(TAG, "刷新")
                                }

                            }
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
     */
    override fun onContextItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.edit ->{
                val MenuInfo = item.menuInfo as AdapterView.AdapterContextMenuInfo
                var classId = data[MenuInfo.position].classId
                activity!!.database.delete_class(classId!!)
                Log.v(TAG,"移除"+data[MenuInfo.position].className+"成功")
                data.removeAt(MenuInfo.position)
                adapter!!.notifyDataSetChanged()

            }
        }
        return super.onContextItemSelected(item)
    }



    class MenuAdapter(val data : List<ClassInfo>, val context : Context) : BaseAdapter() {
        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        var inflater : LayoutInflater? = null

        init {
            inflater = LayoutInflater.from(context)
        }


        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

            var holder : Holder
            var v : View
            if(convertView == null){
                v = inflater!!.inflate(R.layout.fragment_menu_item,null)
                holder = Holder(v)
                v.tag = holder
            }else{
                v = convertView
                holder = v.tag as Holder
            }
            val mItem = data[position]
            holder.textView.text = mItem.className
            holder.time.text = mItem.time?.substringBeforeLast(":")
            if (mItem.info.isNullOrEmpty()){
                holder.info.visibility = View.GONE
            }else {
                holder.info.text = mItem.info
            }

            return v
        }

        override fun getItem(position: Int): ClassInfo? {
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
