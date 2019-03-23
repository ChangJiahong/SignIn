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

        myClass.setOnClickListener(this)
        guanyu.setOnClickListener(this)
        bangzhu.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.guanyu ->{
                startActivity<AboutMe>()
            }
            R.id.bangzhu ->{
                startActivity<HelpActivity>()
            }
            R.id.myClass ->{
                startActivity<MyClass>()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.v(TAG,"result "+requestCode+"  : "+resultCode)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == 1){
                // 回调刷新

            }
        }
    }



}