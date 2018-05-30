package com.demo.cjh.signin.Fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.demo.cjh.signin.R

/**
 * Created by CJH
 * on 2018/5/29
 */
class MyFragment : Fragment(){
    val TAG = "MyFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_my, container, false)
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