package com.demo.cjh.signin.service.impl

import android.content.Context
import com.demo.cjh.signin.dao.IRecordDao
import com.demo.cjh.signin.dao.impl.RecordDaoImpl
import com.demo.cjh.signin.pojo.OldListItem
import com.demo.cjh.signin.pojo.Record
import com.demo.cjh.signin.pojo.StuInfo
import com.demo.cjh.signin.service.IRecordService
import com.demo.cjh.signin.util.getNow
import com.demo.cjh.signin.util.getOnlyID

/**
 * Created by CJH
 * on 2018/11/21
 */
class RecordServiceImpl(context: Context) : IRecordService {



    private val recordDao: IRecordDao = RecordDaoImpl(context)

    /**
     * 保存记录
     * @param stus
     * @return
     */
    override fun save(title: String,info: String,subjectId: String,sTime: String,typeId: String,stus: ArrayList<StuInfo>): Int {
        val records = ArrayList<Record>()
        var record: Record
        val now = getNow()
        // 生成记录识别码
        val recordId = getOnlyID()
        // 转换
        stus.forEach {
            record = Record()
            record.apply {
                classId = it.classId
                stuId = it.stuId
                this.subjectId = subjectId
                this.stime = sTime
                this.statu = it.status
                this.typeId = typeId
                this.info = info
                this.title = title
                this.createTime = now
                this.recordId = recordId
            }
            records.add(record)
        }

        // 保存
        records.forEach {
            recordDao.insert(it)
        }
        return records.size
    }

    /**
     * 修改记录
     */
    override fun update(stus: ArrayList<StuInfo>): Int {
        val records = ArrayList<Record>()
        var record: Record
        // 转换
        stus.forEach {
            record = Record()
            record.apply {
                cId = it.id
                this.statu = it.status
            }
            records.add(record)
        }
        records.forEach {
            recordDao.update(it)
        }
        return records.size
    }

    /**
     * 获取记录 通过班级id  和  记录类型
     * @param classId
     * @param typeId
     * @return
     */
    override fun getListByClassIdAndTypeId(classId: String, typeId: String): List<OldListItem> {


        return recordDao.getListByClassIdAndTypeId(classId = classId, typeId = typeId)
    }

    /**
     * 删除记录 ，班级的某一类型的标题为title的
     * @param classId
     * @param title
     * @param typeId
     * @return
     */
    override fun deleteListByClassIdAndTitleAndTypeId(classId: String, title: String, typeId: String): Int {
        return 0
    }

    /**
     * 通过班级id
     * 类型id
     * 标题
     * 查找历史记录
     */
    override fun getStusByClassIdAndTypeIdAndTitle(classId: String, typeId: String, title: String): ArrayList<StuInfo> {
        return recordDao.getStusByClassIdAndTypeIdAndTitle(classId = classId,typeId = typeId,title = title)
    }

    // 获取某学生的所有状态
    override fun getValuesByClassIdAndTypeIdAndTitleAndStuId(classId: String, typeId: String, stuId: String): Map<String, String> {
        return recordDao.getValuesByClassIdAndTypeIdAndTitleAndStuId(classId,typeId,stuId)
    }

}
