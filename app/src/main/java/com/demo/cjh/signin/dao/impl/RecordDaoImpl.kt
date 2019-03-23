package com.demo.cjh.signin.dao.impl

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.demo.cjh.signin.dao.IRecordDao
import com.demo.cjh.signin.pojo.OldListItem
import com.demo.cjh.signin.pojo.Record
import com.demo.cjh.signin.pojo.StuInfo
import com.demo.cjh.signin.util.MyDatabaseOpenHelper

/**
 * Created by CJH
 * on 2018/11/22
 */
class RecordDaoImpl(private val context: Context) : IRecordDao {


    private val TAG = RecordDaoImpl::class.java.name

    /**
     * 增
     */
    override fun insert(obj: Record): Long {
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)
        val cv = ContentValues()
        if (!obj.cId.isEmpty()){
            cv.put("id", obj.cId)
        }
        cv.put("class_id",obj.classId)
        cv.put("stu_id",obj.stuId)
        cv.put("subject_id",obj.subjectId)
        cv.put("stime",obj.stime)
        cv.put("statu",obj.statu)
        cv.put("type_id",obj.typeId)
        cv.put("info",obj.info)
        cv.put("title",obj.title)
        cv.put("record_id",obj.recordId)
        cv.put("create_time",obj.createTime)
        // 标记插入
        cv.put("status",0)
        cv.put("anchor",0)
        return writeDatabase.insert(RECORD_TABLE,null,cv)
    }

    /**
     * 删
     */
    override fun deleteById(id: String): Int {
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)
        val cv = ContentValues()
        cv.put("status",-1)
        return writeDatabase.update(RECORD_TABLE,cv,"id = ?", arrayOf(id))
    }

    override fun deleteByTypeIdAndClassId(typeId: String,classId: String): Int{
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)
        val cv = ContentValues()
        cv.put("status",-1)
        return writeDatabase.update(RECORD_TABLE,cv,"type_id = ? and class_id = ?", arrayOf(typeId,classId))
    }

    /**
     * 改
     */
    override fun update(obj: Record): Int {
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)
        val cv = ContentValues()
        if(obj.cId.isNullOrEmpty()){
            return -1
        }
        if(!obj.classId.isNullOrEmpty()){
            cv.put("class_id",obj.classId)
        }
        if(!obj.stuId.isNullOrEmpty()){
            cv.put("stu_id",obj.stuId)
        }
        if(!obj.subjectId.isNullOrEmpty()){
            cv.put("subject_id",obj.subjectId)
        }
        if(!obj.stime.isNullOrEmpty()){
            cv.put("stime",obj.stime)
        }
        if(!obj.statu.isNullOrEmpty()){
            cv.put("statu",obj.status)
        }
        if(!obj.typeId.isNullOrEmpty()){
            cv.put("type_id",obj.typeId)
        }
        if(!obj.info.isNullOrEmpty()){
            cv.put("info",obj.info)
        }
        if(!obj.title.isNullOrEmpty()){
            cv.put("title",obj.title)
        }
        if(!obj.createTime.isNullOrEmpty()){
            cv.put("create_time",obj.createTime)
        }
        // 标记修改
        cv.put("status",1)
        return writeDatabase.update(RECORD_TABLE,cv,"id = ?", arrayOf(obj.cId))
    }

    /**
     * 查
     */
    override fun getById(id: String, isTrue:Boolean): Record? {
        val readDatabase = MyDatabaseOpenHelper.readDb(context)
        val sql = "Select id , class_id , stu_id , subject_id , stime , statu , type_id , info , title , create_time , status , anchor" +
                "From $RECORD_TABLE Where id = ? ${if (isTrue) "" else "and status != -1"}"
        val re = readDatabase.rawQuery(sql, arrayOf(id))
        var record: Record? = null
        re.moveToFirst()
        if(re.isFirst){
            record = Record()
            record.cId = re.getString(re.getColumnIndex("id"))
            record.classId = re.getString(re.getColumnIndex("class_id"))
            record.stuId = re.getString(re.getColumnIndex("stu_id"))
            record.subjectId = re.getString(re.getColumnIndex("subject_id"))
            record.stime = re.getString(re.getColumnIndex("stime"))
            record.statu = re.getString(re.getColumnIndex("statu"))
            record.typeId = re.getString(re.getColumnIndex("type_id"))
            record.info = re.getString(re.getColumnIndex("info"))
            record.title = re.getString(re.getColumnIndex("title"))
            record.createTime = re.getString(re.getColumnIndex("create_time"))
            if (isTrue) {
                record.status = re.getInt(re.getColumnIndex("status"))
                record.anchor = re.getLong(re.getColumnIndex("anchor"))
            }

        }
        return record
    }

    /**
     * 标记删除记录 通过班级编号
     */
    override fun deleteByClassId(classId: String): Int {
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)
        val cv = ContentValues()
        cv.put("status",-1)
        return writeDatabase.update(RECORD_TABLE,cv,"class_id = ?", arrayOf(classId))
    }

    /**
     * 通过班级id typeid 获取历史记录
     */
    override fun getListByClassIdAndTypeId(classId: String, typeId: String): ArrayList<OldListItem> {
        val sql = "SELECT class_id,subject,stime,type_id,info,title,create_time " +
                " FROM record , subject " +
                " where class_id = ? and " +
                " type_id = ? and " +
                " subject.id = subject_id and record.status != -1 and subject.status != -1" +
                " GROUP BY title"
        val readDatabase = MyDatabaseOpenHelper.readDb(context)
        val re = readDatabase.rawQuery(sql, arrayOf(classId,typeId))
        re.moveToFirst()
        val oldList = ArrayList<OldListItem>()
        var oldListItem: OldListItem
        while (!re.isAfterLast){
            oldListItem = OldListItem()
            oldListItem.classId = re.getString(re.getColumnIndex("class_id"))
            oldListItem.info = re.getString(re.getColumnIndex("info"))
            oldListItem.stime = re.getString(re.getColumnIndex("stime"))
            oldListItem.subject = re.getString(re.getColumnIndex("subject"))
            oldListItem.title = re.getString(re.getColumnIndex("title"))
            oldListItem.typeId = re.getString(re.getColumnIndex("type_id"))
            oldListItem.time = re.getString(re.getColumnIndex("create_time"))
            Log.d(TAG,oldListItem.title)
            oldList.add(oldListItem)
            re.moveToNext()
        }
        return oldList
    }

    /**
     * 获取历史记录
     */
    override fun getStusByClassIdAndTypeIdAndTitle(classId: String, typeId: String, title: String): ArrayList<StuInfo> {
        val readDb = MyDatabaseOpenHelper.readDb(context)
        val sql = "Select record.id id, record.stu_id stu_id, stu_name, statu, type_id, record.class_id class_id\n" +
                "From record,stu\n" +
                "Where record.stu_id = stu.stu_id and\n" +
                "\t\t\trecord.class_id = ? and\n" +
                "\t\t\trecord.type_id = ? and\n" +
                "\t\t\trecord.title = ? and record.status != -1 and stu.status != -1"
        val re = readDb.rawQuery(sql, arrayOf(classId,typeId,title))
        re.moveToFirst()
        val stus = ArrayList<StuInfo>()
        var stu: StuInfo
        while (!re.isAfterLast){
            stu = StuInfo()
            stu.id = re.getString(re.getColumnIndex("id"))
            stu.stuId = re.getString(re.getColumnIndex("stu_id"))
            stu.stuName = re.getString(re.getColumnIndex("stu_name"))
            stu.status = re.getString(re.getColumnIndex("statu"))
            stu.typeId = re.getString(re.getColumnIndex("type_id"))
            stu.classId = re.getString(re.getColumnIndex("class_id"))
            stus.add(stu)
            re.moveToNext()
        }
        return stus
    }

    override fun getValuesByClassIdAndTypeIdAndTitleAndStuId(classId: String, typeId: String, stuId: String): Map<String, String>{
        val readDb = MyDatabaseOpenHelper.readDb(context)
        val sql = "SELECT statu,title \n" +
                "FROM record\n" +
                "WHERE class_id = ? AND\n" +
                "\t\t\tstu_id = ? AND\n" +
                "\t\t\ttype_id = ? and status != -1"
        val re = readDb.rawQuery(sql, arrayOf(classId,stuId,typeId))
        re.moveToFirst()
        val map = HashMap<String,String>()
        while (!re.isAfterLast){
            map[re.getString(re.getColumnIndex("title"))] = re.getString(re.getColumnIndex("statu"))
            re.moveToNext()
        }
        return map

    }


    /**
     * 更新status
     */
    override fun updateStatusById(status: Int,cId: String): Int{
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)
        val cv = ContentValues()
        cv.put("status",status)
        return writeDatabase.update(RECORD_TABLE,cv,"id = ?", arrayOf(cId))
    }

    /**
     * 更新anchor
     */
    override fun updateAnchorById(anchor: Long,cId: String): Int{
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)
        val cv = ContentValues()
        cv.put("anchor",anchor)
        return writeDatabase.update(RECORD_TABLE,cv,"id = ?", arrayOf(cId))
    }

    /**
     * 更新status 和 anchor
     */
    override fun updateStatusAndAnchorById(status: Int, anchor: Long, cId: String): Int {
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)
        val cv = ContentValues()
        cv.put("anchor",anchor)
        cv.put("status",status)
        return writeDatabase.update(RECORD_TABLE,cv,"id = ?", arrayOf(cId))
    }

    /**
     * 获取status<9的未同步记录
     */
    override fun getStatusLessThan9(): ArrayList<Record> {
        val readDb = MyDatabaseOpenHelper.readDb(context)
        val sql = "Select id,class_id,stu_id,subject_id,stime,statu,type_id,info,title,record_id,create_time,status,anchor From $RECORD_TABLE Where status < 9"
        val recordList = ArrayList<Record>()
        var record: Record
        val re = readDb.rawQuery(sql,null)
        re.moveToFirst()
        while (!re.isAfterLast){
            record = Record()
            record.cId = re.getString(re.getColumnIndex("id"))
            record.classId = re.getString(re.getColumnIndex("class_id"))
            record.stuId = re.getString(re.getColumnIndex("stu_id"))
            record.subjectId = re.getString(re.getColumnIndex("subject_id"))
            record.stime = re.getString(re.getColumnIndex("stime"))
            record.statu = re.getString(re.getColumnIndex("statu"))
            record.typeId = re.getString(re.getColumnIndex("type_id"))
            record.info = re.getString(re.getColumnIndex("info"))
            record.title = re.getString(re.getColumnIndex("title"))
            record.createTime = re.getString(re.getColumnIndex("create_time"))
            record.recordId = re.getString(re.getColumnIndex("record_id"))
            record.status = re.getInt(re.getColumnIndex("status"))
            record.anchor = re.getLong(re.getColumnIndex("anchor"))
            recordList.add(record)
            re.moveToNext()
        }
        return recordList
    }

    /**
     * 真删除
     */
    override fun trueDeleteById(cId: String): Int {
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)
        return writeDatabase.delete(RECORD_TABLE,"id = ?", arrayOf(cId))
    }

    /**
     * 获取最大anchor
     */
    override fun getMaxAnchor(): Long {
        val readDatabase = MyDatabaseOpenHelper.readDb(context)
        val sql = "SELECT MAX(anchor) maxAnchor FROM $RECORD_TABLE"
        val re = readDatabase.rawQuery(sql,null)
        var maxAnchor = 0L
        re.moveToFirst()
        if (re.isFirst){
            maxAnchor = re.getLong(re.getColumnIndex("maxAnchor"))
        }
        return maxAnchor
    }

}