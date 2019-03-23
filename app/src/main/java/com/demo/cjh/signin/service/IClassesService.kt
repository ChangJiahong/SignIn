package com.demo.cjh.signin.service

import com.demo.cjh.signin.pojo.Classes

/**
 * Created by CJH
 * on 2018/11/21
 */
interface IClassesService {

    fun getAllClasses(): List<Classes>

    fun saveClasses(className: String, info: String, institute: String, speciality: String): Classes?

    fun deleteById(id: String): Boolean

    fun update(classes: Classes): Boolean

    fun getClassesById(classId: String): Classes?

    /**
     * 删除班级所有信息
     * @param classId
     * @return
     */
    fun deleteClassesByClassId(classId: String): Boolean



}
