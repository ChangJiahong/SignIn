package com.demo.cjh.signin.obj

import com.demo.cjh.signin.App

/**
 * Created by CJH
 * on 2018/9/2
 */
class User {
    val TAG = "User"
    val id : String
        get() {
            return App.app!!.sp!!.getString("userid","")
        }
    val name : String
        get() {
            return App.app!!.sp!!.getString("name","")
        }
    val imageUrl : String
        get() {
            return App.app!!.sp!!.getString("imageUrl","")
        }
    val userToken : String
        get() {
            return App.app!!.sp!!.getString("userToken","")
        }

    val pwd : String
        get() {
            return App.app!!.sp!!.getString("pwd","")
        }


}