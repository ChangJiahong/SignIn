package com.demo.cjh.signin.service

import com.demo.cjh.signin.pojo.Stu
import com.demo.cjh.signin.pojo.StuInfo

import java.util.ArrayList

/**
 * Created by CJH
 * on 2018/11/21
 * @author CJH
 */
interface IStuService {
    /**
     * 获取学生信息数据
     * @param classId
     * @return
     */
    fun getStuInfosByClassId(classId: String): ArrayList<StuInfo>

    /**
     * 保存学生数据
     * @param stus
     * @return
     */
    fun save(stus: ArrayList<Stu>): Int

    /**
     * 获取学生数据
     */
    fun getStusByClassId(classId: String): ArrayList<Stu>

    /**
     * 获取单个学生数据
     */
    fun  getStuById(id: String): Stu?

}
