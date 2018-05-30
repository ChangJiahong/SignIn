package com.demo.cjh.signin.Fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.demo.cjh.signin.R


class FileFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_file, container, false)
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
