package com.demo.cjh.signin.dao

import com.demo.cjh.signin.pojo.Subject


/**
 * Created by CJH
 * on 2018/12/8
 * @date 2018/12/8
 * @author CJH
 */
interface ISubjectDao : IDao<Subject> {
    val SUBJECT_TABLE: String
        get() = "subject"

    /**
     * 通过名字查找id
     */
    fun getByName(name: String): String

    /**
     * 获取全部
     */
    fun getAll(): ArrayList<Subject>

}