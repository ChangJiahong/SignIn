package com.demo.cjh.signin.Fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.TextView
import com.demo.cjh.signin.Activity.Table2Activity
import com.demo.cjh.signin.Activity.TableActivity
import com.demo.cjh.signin.FileUtil

import com.demo.cjh.signin.R
import kotlinx.android.synthetic.main.fragment_file.*
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk25.coroutines.onItemClick
import org.jetbrains.anko.sdk25.coroutines.onItemLongClick
import org.jetbrains.anko.support.v4.toast


class FileFragment : Fragment() {

    private var fileDatas = ArrayList<String>()

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

        var datas = FileUtil.getFileList(FileUtil.fileDirectory,"xls","xlsx")
        fileDatas.clear()
        for (file in datas){
            fileDatas.add(file.name)
        }

        fileListView.adapter = FileAdapter(fileDatas, activity!!)
        fileListView.onItemClick { p0, p1, p2, p3 ->
            val intent = Intent(activity, Table2Activity::class.java)
            intent.putExtra("filepath",fileDatas[p2])
            startActivity(intent)
        }

        fileListView.onItemLongClick { p0, p1, p2, p3 ->

        }
    }

    class FileAdapter(  val data : List<String>,val context : Context) : BaseAdapter() {
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
                v = inflater!!.inflate(R.layout.fragment_file_item,null)
                holder = Holder(v)
                v.tag = holder
            }else{
                v = convertView
                holder = v.tag as Holder
            }
            holder.textView.text = data[position]

            return v
        }

        override fun getItem(position: Int): String? {
            return this.data[position]
        }

        override fun getCount(): Int {
            return this.data.size
        }

        class Holder(v :View) {
            var textView : TextView = v.find<TextView>(R.id.item1)
        }
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
