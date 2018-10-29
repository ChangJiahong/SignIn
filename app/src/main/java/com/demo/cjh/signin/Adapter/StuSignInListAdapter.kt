package com.demo.cjh.signin.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.demo.cjh.signin.R
import com.demo.cjh.signin.obj.StuSignInList
import org.jetbrains.anko.find

/**
 * Created by CJH
 * on 2018/6/29
 */
class StuSignInListAdapter(val data : List<StuSignInList>, val context : Context) : BaseAdapter() {

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
        holder.info.text = data[position].info
        holder.time.text = data[position].time

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
        var info = v.find<TextView>(R.id.info)
        var time = v.find<TextView>(R.id.time)
    }
    val TAG = "StuSignInListAdapter"
}