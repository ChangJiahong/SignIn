package com.demo.cjh.signin.Fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.TextView
import com.demo.cjh.signin.Activity.SignInActivity
import com.demo.cjh.signin.Activity.StuListActivity
import com.demo.cjh.signin.Activity.StuTableActivity
import com.demo.cjh.signin.R
import kotlinx.android.synthetic.main.fragment_menu.*
import org.jetbrains.anko.find


class MenuFragment : Fragment() {

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

        var data = arrayListOf<String>("软件工程一班","网络工程一班","网络工程二班","计科一班","计科二班","计科三班","计科四班")

        mListView.adapter = MenuAdapter(data, activity!!)
        mListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val intent = Intent(activity, StuListActivity::class.java)
            intent.putExtra("id",data[position])
            startActivity(intent)
        }

    }

    class MenuAdapter(  val data : List<String>,val context : Context) : BaseAdapter() {
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

}
