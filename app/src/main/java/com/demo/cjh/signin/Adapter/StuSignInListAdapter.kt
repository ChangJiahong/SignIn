package com.demo.cjh.signin.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.demo.cjh.signin.R
import com.demo.cjh.signin.pojo.OldListItem
import com.demo.cjh.signin.pojo.StuSignInList
import com.demo.cjh.signin.util.parseDate
import com.demo.cjh.signin.util.toStringFormat
import org.jetbrains.anko.find
import org.w3c.dom.Text

/**
 * Created by CJH
 * on 2018/6/29
 */
class StuSignInListAdapter(val data : List<OldListItem>, val context : Context) : BaseAdapter() {

    var inflater : LayoutInflater? = null

    init {
        inflater = LayoutInflater.from(context)
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var holder : Holder
        var v : View
        if(convertView == null){
            v = inflater!!.inflate(R.layout.stu_sign_in_list_item,null)
            holder = Holder(v)
            v.tag = holder
        }else{
            v = convertView
            holder = v.tag as Holder
        }
        val item = data[position]
        holder.title.text = item.title
        holder.time.text = item.time.parseDate().toStringFormat()
        holder.stime.text = item.stime
        holder.info.text = item.info
        holder.subject.text = item.subject

        return v
    }

    override fun getItem(position: Int): Any {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return data.size
    }

    class Holder(v :View) {
        val title = v.find<TextView>(R.id.title)
        val time = v.find<TextView>(R.id.time)
        val stime = v.find<TextView>(R.id.stime)
        val info = v.find<TextView>(R.id.info)
        val subject = v.find<TextView>(R.id.subject)
    }
    val TAG = "StuSignInListAdapter"
}