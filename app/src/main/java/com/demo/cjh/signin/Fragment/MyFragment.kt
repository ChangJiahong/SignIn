package com.demo.cjh.signin.Fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.demo.cjh.signin.R
import android.widget.TextView
import com.bumptech.glide.Glide
import android.widget.BaseAdapter
import android.widget.ImageView
import com.demo.cjh.signin.Activity.*
import com.demo.cjh.signin.App
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

        val sp = App.app!!.sp!!
        if(sp.getBoolean("isLogin",false)) {
            // 登陆成功的初始化
            val name = sp.getString("name", "登陆/注册")
            if (!name.isNullOrEmpty()) {
                id_my_text.text = name
            }
            val img = sp.getString("imgUrl","")
            Log.v(TAG,"imgUrl= $img")
            if(!img.isNullOrEmpty()) {
                Glide.with(context).load(img).into(id_my_icon)
            }

        }
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
                startActivity<StatisticsActivity>()
            }
            R.id.guanyu ->{
                startActivity<AboutMe>()
            }
            R.id.bangzhu ->{
                startActivity<HelpActivity>()
            }
            R.id.id_my_text1 ->{
                startActivity<MyClass>()
            }
            R.id.id_my_text2 ->{
                startActivity<YunActivity>()
            }
            R.id.id_my_text3 ->{
                toast("功能尚未开放，尽请期待！")
            }
            R.id.login ->{
                if(!App.app!!.sp!!.getBoolean("isLogin",false)) {
                    // 未登录
                    var intent = Intent(activity, LoginActivity::class.java)

                    startActivityForResult(intent, 1)
                }else{
                    // 已登录
                    var intent = Intent(activity, UserInfo::class.java)

                    startActivityForResult(intent, 1)

                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.v(TAG,"result "+requestCode+"  : "+resultCode)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == 1){
                // 回调刷新
                var sp = App.app!!.sp!!
                Log.v(TAG,sp.getString("name","1111111111"))
                if(sp.getBoolean("isLogin",false)) {
                    var name = sp.getString("name", "登陆")
                    var img = sp.getString("imgUrl", "")
                    Glide.with(context).load(img).into(id_my_icon)
                    id_my_text.text = name
                }else{
                    id_my_text.text = "登录/注册"
                    id_my_icon.setImageResource(R.drawable.tou)
                }
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