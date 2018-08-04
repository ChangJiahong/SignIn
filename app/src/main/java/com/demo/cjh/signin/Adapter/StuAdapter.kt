package com.demo.cjh.signin.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.demo.cjh.signin.R
import com.demo.cjh.signin.`object`.StudentInfo
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.find

/**
 * Created by CJH
 * on 2018/6/6
 */
class StuAdapter(val mItems : ArrayList<StudentInfo>, internal val didSelectedAtPos:(idx : Int) -> Unit) : RecyclerView.Adapter<StuAdapter.ViewHolder>(){

    val TAG = "StuAdapter"
    internal var mContext : Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        return ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.stu_list_item,parent,false))
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        fun bind(model: StudentInfo){

            holder.idView.text = (position+1).toString()
            holder.stu_id.text = model.stuId
            holder.name.text = model.name
            Log.v("StuAdapter",position.toString()+model.name+" "+model.type)
            unCheckAll(holder)
            when(model.type){
                "出勤" -> holder.dao.backgroundResource = R.drawable.hua2
                "事假" -> holder.shiJia.backgroundResource = R.drawable.hua2
                "病假" -> holder.bingJia.backgroundResource = R.drawable.hua2
                "迟到" -> holder.chiDao.backgroundResource = R.drawable.hua2
                "旷课" -> holder.kuangKe.backgroundResource = R.drawable.hua2
            }

            with(holder.container){
                setOnClickListener {
                    didSelectedAtPos(position)
                }
            }


        }
        val item = mItems[position]
        bind(item)
    }

    /**
     * 全部不选
     */
    private fun unCheckAll(holder: ViewHolder ){
        holder.dao.backgroundResource = 0
        holder.chiDao.backgroundResource = 0
        holder.kuangKe.backgroundResource = 0
        holder.shiJia.backgroundResource = 0
        holder.bingJia.backgroundResource = 0
    }

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        var container = view.find<LinearLayout>(R.id.stu_list_item)
        var idView = view.find<TextView>(R.id.id)
        var stu_id = view.find<TextView>(R.id.stu_id)
        var name = view.find<TextView>(R.id.name)
        var dao = view.find<ImageView>(R.id.dao)
        var shiJia = view.find<ImageView>(R.id.shiJia)
        var bingJia = view.find<ImageView>(R.id.bingJia)
        var chiDao = view.find<ImageView>(R.id.chiDao)
        var kuangKe = view.find<ImageView>(R.id.kuangKe)
    }

}