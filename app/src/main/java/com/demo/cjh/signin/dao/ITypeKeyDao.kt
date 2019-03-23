package com.demo.cjh.signin.dao

import com.demo.cjh.signin.pojo.TypeKey

/**
 * Created by CJH
 * on 2018/12/13
 * @date 2018/12/13
 * @author CJH
 */
interface ITypeKeyDao: IDao<TypeKey> {

    val TABLE_TYPEKEY: String
        get() = "type_key"

    /**
     * 获取每个种类的具体记录种类值
     */
    fun getKeysByTypeId(typeId: String) : ArrayList<String>


}