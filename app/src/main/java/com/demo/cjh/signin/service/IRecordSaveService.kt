package com.demo.cjh.signin.service

import com.demo.cjh.signin.pojo.Record

/**
 * Created by CJH
 * on 2018/12/8
 * @date 2018/12/8
 * @author CJH
 */
interface IRecordSaveService {
    /**
     * 获取id
     */
    fun getIdByName(name: String): String

    /**
     * 获取所有科目
     */
    fun getAllSubject(): ArrayList<String>

    /**
     * 新建学科
     */
    fun createSubject(name: String): String

}