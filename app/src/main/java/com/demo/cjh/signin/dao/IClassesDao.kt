package com.demo.cjh.signin.dao

import com.demo.cjh.signin.pojo.Classes

/**
 * Created by CJH
 * on 2018/11/21
 */
interface IClassesDao : IDao<Classes> {

    val CLASSES_TABLE: String
        get() = "classes"

    //    public long insert(Classes classes);
    //
    //    public int deleteById(String classId);
    //
    //    public int update(Classes classes);

    fun all(): List<Classes>

    /**
     * 更加班级编号删除
     */
    fun deleteByClassId(classId: String) : Int


    //    public Classes getById(String classId);
}
