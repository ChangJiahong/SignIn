package com.demo.cjh.signin.dao.impl

import android.content.ContentValues
import android.content.Context
import com.demo.cjh.signin.dao.ISubjectDao
import com.demo.cjh.signin.pojo.Subject
import com.demo.cjh.signin.util.MyDatabaseOpenHelper

/**
 * Created by CJH
 * on 2018/12/8
 * @date 2018/12/8
 * @author CJH
 */
class SubjectDaoImpl(private val context: Context) : ISubjectDao {
    /**
     * 获取全部
     */
    override fun getAll(): ArrayList<Subject> {
        val readDb = MyDatabaseOpenHelper.readDb(context)
        val sql = "Select id,subject From $SUBJECT_TABLE Where status != -1"
        val re = readDb.rawQuery(sql, null)
        re.moveToFirst()
        val subjects = ArrayList<Subject>()
        var subject: Subject
        while (!re.isAfterLast){
            subject = Subject()
            subject.cId = re.getString(re.getColumnIndex("id"))
            subject.subject = re.getString(re.getColumnIndex("subject"))
            subjects.add(subject)
            re.moveToNext()
        }
        return subjects
    }

    /**
     * 通过名字查找id
     */
    override fun getByName(name: String): String {
        val readDb = MyDatabaseOpenHelper.readDb(context)
        val sql = "Select id From $SUBJECT_TABLE Where subject = ? and  status != -1"
        val re = readDb.rawQuery(sql, arrayOf(name))
        var id: String = ""
        re.moveToFirst()
        if(re.isFirst){
            id = re.getString(re.getColumnIndex("id"))
        }
        return id
    }

    /**
     * 增
     */
    override fun insert(obj: Subject): Long {
        val writeDb = MyDatabaseOpenHelper.writeDb(context)
        val cv = ContentValues()
        if (!obj.cId.isEmpty()){
            cv.put("id", obj.cId)
        }
        cv.put("subject",obj.subject)
        // 标记插入
        cv.put("status",0)
        cv.put("anchor",0)
        return writeDb.insert(SUBJECT_TABLE,null,cv)
    }

    /**
     * 删
     */
    override fun deleteById(id: String): Int {
        val writeDb = MyDatabaseOpenHelper.writeDb(context)
        val cv = ContentValues()
        cv.put("status",-1)
        return writeDb.update(SUBJECT_TABLE,cv,"id = ?", arrayOf(id))
    }

    /**
     * 改
     */
    override fun update(obj: Subject): Int {
        val writeDb = MyDatabaseOpenHelper.writeDb(context)
        val cv = ContentValues()
        if (obj.cId.isNullOrEmpty()) {
            return -1
        }
        if(!obj.subject.isNullOrEmpty()){
            cv.put("subject",obj.subject)
        }
        //  标记修改
        cv.put("status",1)
        return writeDb.update(SUBJECT_TABLE,cv,"id = ?", arrayOf(obj.subject))
    }

    /**
     * 查
     */
    override fun getById(id: String,isTrue: Boolean): Subject? {
        val readDb = MyDatabaseOpenHelper.readDb(context)
        val sql = "Select id,subject , status , anchor From $SUBJECT_TABLE Where id = ? ${if (isTrue) "" else "and status != -1"}"
        val re = readDb.rawQuery(sql, arrayOf(id))
        re.moveToFirst()
        var subject: Subject? = null
        if(re.isFirst){
            subject = Subject()
            subject.cId = re.getString(re.getColumnIndex("id"))
            subject.subject = re.getString(re.getColumnIndex("subject"))
            if (isTrue) {
                subject.status = re.getInt(re.getColumnIndex("status"))
                subject.anchor = re.getLong(re.getColumnIndex("anchor"))
            }
        }
        return subject
    }

    /**
     * 更新status
     */
    override fun updateStatusById(status: Int,cId: String): Int{
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)
        val cv = ContentValues()
        cv.put("status",status)
        return writeDatabase.update(SUBJECT_TABLE,cv,"id = ?", arrayOf(cId))
    }

    /**
     * 更新anchor
     */
    override fun updateAnchorById(anchor: Long,cId: String): Int{
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)
        val cv = ContentValues()
        cv.put("anchor",anchor)
        return writeDatabase.update(SUBJECT_TABLE,cv,"id = ?", arrayOf(cId))
    }

    /**
     * 更新status 和 anchor
     */
    override fun updateStatusAndAnchorById(status: Int, anchor: Long, cId: String): Int {
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)
        val cv = ContentValues()
        cv.put("anchor",anchor)
        cv.put("status",status)
        return writeDatabase.update(SUBJECT_TABLE,cv,"id = ?", arrayOf(cId))
    }

    /**
     * 获取status<9的未同步记录
     */
    override fun getStatusLessThan9(): ArrayList<Subject> {
        val readDb = MyDatabaseOpenHelper.readDb(context)
        val sql = "Select id,subject,status,anchor From $SUBJECT_TABLE Where status < 9"
        val re = readDb.rawQuery(sql, null)
        re.moveToFirst()
        val subjects = ArrayList<Subject>()
        var subject: Subject
        while (!re.isAfterLast){
            subject = Subject()
            subject.cId = re.getString(re.getColumnIndex("id"))
            subject.subject = re.getString(re.getColumnIndex("subject"))
            subject.status = re.getInt(re.getColumnIndex("status"))
            subject.anchor = re.getLong(re.getColumnIndex("anchor"))
            subjects.add(subject)
            re.moveToNext()
        }
        return subjects
    }

    /**
     * 真删除
     */
    override fun trueDeleteById(cId: String): Int {
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)
        return writeDatabase.delete(SUBJECT_TABLE,"id = ?", arrayOf(cId))
    }

    /**
     * 获取最大anchor
     */
    override fun getMaxAnchor(): Long {
        val readDatabase = MyDatabaseOpenHelper.readDb(context)
        val sql = "SELECT MAX(anchor) maxAnchor FROM $SUBJECT_TABLE"
        val re = readDatabase.rawQuery(sql,null)
        var maxAnchor = 0L
        re.moveToFirst()
        if (re.isFirst){
            maxAnchor = re.getLong(re.getColumnIndex("maxAnchor"))
        }
        return maxAnchor
    }

    private val TAG = SubjectDaoImpl::class.java.name
}