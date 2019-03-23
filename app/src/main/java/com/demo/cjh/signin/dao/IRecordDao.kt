package com.demo.cjh.signin.dao

import com.demo.cjh.signin.pojo.OldListItem
import com.demo.cjh.signin.pojo.Record
import com.demo.cjh.signin.pojo.StuInfo

/**
 * Created by CJH
 * on 2018/11/22
 */
interface IRecordDao : IDao<Record> {

    /**
     * 删除记录 通过班级编号
     */
    fun deleteByClassId(classId: String) : Int

    /**
     * 通过班级id typeid 获取历史记录
     */
    fun getListByClassIdAndTypeId(classId: String, typeId: String) : ArrayList<OldListItem>

    /**
     * 获取历史记录
     */
    fun getStusByClassIdAndTypeIdAndTitle(classId: String, typeId: String, title: String): ArrayList<StuInfo>

    /**
     * 删除关于typeid的记录
     */
    fun deleteByTypeIdAndClassId(typeId: String,classId: String): Int

    private val TAG: String?
        get() = IRecordDao::class.qualifiedName

    val RECORD_TABLE: String
        get() = "record"

    fun getValuesByClassIdAndTypeIdAndTitleAndStuId(classId: String, typeId: String, stuId: String): Map<String, String>
}