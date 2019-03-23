package com.demo.cjh.signin.Fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.TextView
import com.demo.cjh.signin.Activity.Table2Activity
import com.demo.cjh.signin.util.FileUtil

import com.demo.cjh.signin.R
import com.demo.cjh.signin.util.fileDirectoryPath
import com.demo.cjh.signin.util.getLastTime
import kotlinx.android.synthetic.main.fragment_file.*
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk25.coroutines.onItemClick
import org.jetbrains.anko.support.v4.toast
import java.io.File
import java.util.*


class FileFragment : Fragment() {

    private var fileDatas = ArrayList<Array<String>>()
    private lateinit var adapter: FileAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_file, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    private fun init() {


        var datas = FileUtil.getFileList(context!!,context!!.fileDirectoryPath,"xls","xlsx")
        fileDatas.clear()
        for (file in datas){
            fileDatas.add(arrayOf(file.name, file.getLastTime()))
            Log.v("FileView",file.name)
        }

        fileListView.emptyView = empty_view

        adapter = FileAdapter(fileDatas, activity!!)

        fileListView.adapter = adapter

        fileListView.onItemClick { p0, p1, p2, p3 ->
            val intent = Intent(activity, Table2Activity::class.java)
            intent.putExtra("filepath",fileDatas[p2][0])
            startActivity(intent)
        }

        registerForContextMenu(fileListView)
    }

    class FileAdapter(  val data : List<Array<String>>,val context : Context) : BaseAdapter() {
        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        private var inflater : LayoutInflater = LayoutInflater.from(context)



        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

            var holder : Holder
            var v : View
            if(convertView == null){
                v = inflater.inflate(R.layout.fragment_file_item,null)
                holder = Holder(v)
                v.tag = holder
            }else{
                v = convertView
                holder = v.tag as Holder
            }
            holder.fileName.text = data[position][0]
            holder.time.text = data[position][1]

            return v
        }

        override fun getItem(position: Int): String? {
            return this.data[position][0]
        }

        override fun getCount(): Int {
            return this.data.size
        }

        class Holder(v :View) {
            var fileName = v.find<TextView>(R.id.fileName)
            var time = v.find<TextView>(R.id.time)
        }
    }

    /**
     * 上下文菜单
     */
    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo)
        activity!!.menuInflater.inflate(R.menu.mainmenu, menu)
    }

    /**
     * 上下文菜单Item点击方法
     */
    override fun onContextItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.edit ->{
                val MenuInfo = item.menuInfo as AdapterView.AdapterContextMenuInfo

                var file = File(context!!.fileDirectoryPath+"/"+fileDatas[MenuInfo.position][0])
                Log.v("FileFragment",file.path)
                if(file.exists()){
                    file.delete()
                    Log.v("Delete",file.name+" is deleted!")
                }
                fileDatas.removeAt(MenuInfo.position)
                adapter.notifyDataSetChanged()
                toast("删除成功")

            }
        }
        return super.onContextItemSelected(item)
    }

    companion object {

        private var fragment: FileFragment? = null

        @JvmStatic
        fun getInstance(): FileFragment {

            if (fragment == null) {
                fragment = FileFragment()
            }
            return fragment!!
        }
    }
}
