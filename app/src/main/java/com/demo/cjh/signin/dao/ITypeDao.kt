package com.demo.cjh.signin.dao

import com.demo.cjh.signin.pojo.Type

/**
 * Created by CJH
 * on 2018/11/24
 */
interface ITypeDao : IDao<Type>{
    val TYPE_TABLE: String
        get() = "type"

    fun getAll() : ArrayList<Type>

    fun getTypesByClassId(classId: String) : ArrayList<Type>

    fun deleteByClassId(classId: String): Int
}