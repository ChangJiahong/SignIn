package com.demo.cjh.signin.service

import android.service.quicksettings.Tile
import com.demo.cjh.signin.pojo.OldListItem
import com.demo.cjh.signin.pojo.Record
import com.demo.cjh.signin.pojo.StuInfo

/**
 * Created by CJH
 * on 2018/11/21
 * @author CJH
 */
interface IRecordService {

    /**
     * 保存记录
     * @param stus
     * @return
     */
    fun save(title: String,info: String,subjectId: String,sTime: String,typeId: String,stus: ArrayList<StuInfo>): Int

    /**
     * 修改记录
     */
    fun update(stus: ArrayList<StuInfo>): Int

    /**
     * 通过班级id 类型id 获取历史记录
     * @param classId
     * @param typeId
     * @return
     */
    fun getListByClassIdAndTypeId(classId: String, typeId: String): List<OldListItem>

    /**
     * 通过标题 类型id 删除历史记录
     * @param classId
     * @param title
     * @param typeId
     * @return
     */
    fun deleteListByClassIdAndTitleAndTypeId(classId: String, title: String, typeId: String): Int

    /**
     * 通过班级id
     * 类型id
     * 标题
     * 查找历史记录
     */
    fun getStusByClassIdAndTypeIdAndTitle(classId: String,typeId: String,title: String): ArrayList<StuInfo>

    /**
     * 通过classId
     * typeId
     * title
     * stuId
     * 确定值
     */
    fun getValuesByClassIdAndTypeIdAndTitleAndStuId(classId: String,typeId: String,stuId: String): Map<String, String>
}
