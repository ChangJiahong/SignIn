package com.demo.cjh.signin.service

import android.app.Dialog
import com.demo.cjh.signin.pojo.Type

/**
 * Created by CJH
 * on 2018/11/24
 */
interface ITypeService {

    /**
     * 获取 记录类型列表
     */
    fun getTypesByClassId(classId: String) : ArrayList<Type>

    /**
     * 获取 记录类型 枚举值
     */
    fun getKeysByTypeId(typeId: String) : ArrayList<String>

    /**
     * 创建 记录 类型
     */
    fun createType(classId: String,title: String,img: String,isDialog: Boolean,status: Array<String>): Int


    fun getTypeById(typeId: String): Type?

    /**
     * 更新操作
     */
    fun updateType(type: Type): Int

    /**
     * 删除
     */
    fun deleteById(typeId: String,classId: String): Int
}