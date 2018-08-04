package com.demo.cjh.signin.Fragment

import android.content.Context
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.demo.cjh.signin.R
import android.widget.TextView
import com.bumptech.glide.Glide
import android.widget.BaseAdapter
import android.widget.ImageView
import com.demo.cjh.signin.Activity.AboutMe
import com.demo.cjh.signin.Activity.MyClass
import com.demo.cjh.signin.Activity.SettingsActivity
import kotlinx.android.synthetic.main.fragment_my.*
import kotlinx.android.synthetic.main.list_my_item.*
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast


/**
 * Created by CJH
 * on 2018/5/29
 */
class MyFragment : Fragment(), View.OnClickListener {


    val TAG = "MyFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_my, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }


    private fun init() {

        setting.setOnClickListener(this)
        tongji.setOnClickListener(this)
        guanyu.setOnClickListener(this)
        bangzhu.setOnClickListener(this)
        id_my_text1.setOnClickListener(this)
        id_my_text2.setOnClickListener(this)
        id_my_text3.setOnClickListener(this)
        login.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.setting ->{
                startActivity<SettingsActivity>()
            }
            R.id.tongji ->{
                toast("功能尚未开放，尽请期待！")
            }
            R.id.guanyu ->{
                startActivity<AboutMe>()
            }
            R.id.bangzhu ->{
                toast("功能尚未开放，尽请期待！")
            }
            R.id.id_my_text1 ->{
                startActivity<MyClass>()
            }
            R.id.id_my_text2 ->{
                toast("功能尚未开放，尽请期待！")
            }
            R.id.id_my_text3 ->{
                toast("功能尚未开放，尽请期待！")
            }
            R.id.login ->{
                toast("功能尚未开放，尽请期待！")
            }
        }
    }





    companion object {

        private var fragment: MyFragment? = null

        @JvmStatic
        fun getInstance(): MyFragment {

            if (fragment == null) {
                fragment = MyFragment()
            }
            return fragment!!
        }
    }

}