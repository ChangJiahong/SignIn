package com.demo.cjh.signin.dao

import com.demo.cjh.signin.pojo.Stu

/**
 * Created by CJH
 * on 2018/11/21
 */
interface IStuDao : IDao<Stu>{


    val STU_TABLE: String
        get() = "stu"

    /**
     * 根据班级编号删除
     */
    fun deleteByClassId(classId: String) : Int

    fun getStusByClassId(classId: String) : ArrayList<Stu>

    fun save(stus: ArrayList<Stu>): Int

//    public long insert(Stu stu);
//
//    public int deleteById(String Id);
//
//    public int update(Stu stu);
//
//    public Stu getById(String Id);


}
