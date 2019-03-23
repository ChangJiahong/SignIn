package com.demo.cjh.signin.util

import java.security.MessageDigest

/**
 * Created by CJH
 * on 2018/12/11
 * 加密算法工具类
 * @date 2018/12/11
 * @author CJH
 */
object MD5 {
    private val TAG = MD5::class.java.name


    public fun getMD5(str: String): String{
        val md5 = MessageDigest.getInstance("MD5");
        md5.update(str.toByteArray())
        val m = md5.digest() //加密
        return getString(m)
    }
    private fun getString(b: ByteArray): String{
        val sb = StringBuffer()
        for(i in b){
            sb.append(i)
        }
        return sb.toString()
    }
}