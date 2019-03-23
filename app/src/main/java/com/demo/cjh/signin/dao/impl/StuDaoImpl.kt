package com.demo.cjh.signin.dao.impl

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import com.demo.cjh.signin.dao.IStuDao
import com.demo.cjh.signin.pojo.Stu
import com.demo.cjh.signin.util.MyDatabaseOpenHelper

/**
 * Created by CJH
 * on 2018/11/21
 */
class StuDaoImpl(private val context: Context) : IStuDao {

    val TAG = StuDaoImpl::class.java.name

    /**
     * 保存一组学生数据
     */
    override fun save(stus: ArrayList<Stu>): Int {
        for (stu in stus) {
            insert(stu)
        }

        return stus.size
    }


    // TODO: 同步服务器

    override fun insert(obj: Stu): Long {
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)
        val cv = ContentValues()
        if (!obj.cId.isEmpty()){
            cv.put("id", obj.cId)
        }
        cv.put("class_id", obj.classId)
        cv.put("stu_id", obj.stuId)
        cv.put("stu_name", obj.stuName)
        cv.put("create_time", obj.createTime)
        // 标记插入
        cv.put("status",0)
        cv.put("anchor",0)
        return writeDatabase.insert(STU_TABLE, null, cv)
    }

    override fun deleteById(id: String): Int {
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)
        val cv = ContentValues()
        cv.put("status",-1)
        return writeDatabase.update(STU_TABLE, cv,"id = ?", arrayOf(id))
    }

    override fun update(obj: Stu): Int {
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)
        val cv = ContentValues()
        if (obj.cId.isNullOrEmpty()) {
            return -1
        }
        if (!obj.classId.isNullOrEmpty()) {
            cv.put("class_id", obj.classId)
        }
        if(!obj.stuId.isNullOrEmpty()){
            cv.put("stu_id", obj.stuId)
        }
        if (!obj.stuName.isNullOrEmpty()) {
            cv.put("stu_name", obj.stuName)
        }
        if(!obj.stuFace1.isNullOrEmpty()) {
            cv.put("stu_face1", obj.stuFace1)
        }
        if(!obj.stuFace2.isNullOrEmpty()) {
            cv.put("stu_face2", obj.stuFace2)
        }
        if(!obj.stuFace3.isNullOrEmpty()) {
            cv.put("stu_face3", obj.stuFace3)
        }
        if(!obj.createTime.isNullOrEmpty()) {
            cv.put("create_time", obj.createTime)
        }
        // 标记修改
        cv.put("status",1)
        return writeDatabase.update(STU_TABLE,cv,"id = ?", arrayOf(obj.cId))
    }

    @SuppressLint("Recycle")
    override fun getById(id: String,isTrue: Boolean): Stu? {
        val readDatabase = MyDatabaseOpenHelper.readDb(context)

        val sql = "Select id , class_id , stu_id , stu_name , stu_face1 , stu_face2 , stu_face3 , create_time , status , anchor From $STU_TABLE Where id = ? ${if (isTrue) "" else "and status != -1"}"
        val re = readDatabase.rawQuery(sql, arrayOf(id))
        var stu: Stu? = null
        re.moveToFirst()
        if(re.isFirst){
            stu = Stu()
            stu.cId = re.getString(re.getColumnIndex("id"))
            stu.classId = re.getString(re.getColumnIndex("class_id"))
            stu.stuId = re.getString(re.getColumnIndex("stu_id"))
            stu.stuName = re.getString(re.getColumnIndex("stu_name"))
            stu.stuFace1 = re.getString(re.getColumnIndex("stu_face1"))
            stu.stuFace2 = re.getString(re.getColumnIndex("stu_face2"))
            stu.stuFace3 = re.getString(re.getColumnIndex("stu_face3"))
            stu.createTime = re.getString(re.getColumnIndex("create_time"))
            if (isTrue) {
                stu.status = re.getInt(re.getColumnIndex("status"))
                stu.anchor = re.getLong(re.getColumnIndex("anchor"))
            }
        }
        return stu

    }

    @SuppressLint("Recycle")
    override fun getStusByClassId(classId: String): ArrayList<Stu>{
        val readDatabase = MyDatabaseOpenHelper.readDb(context)

        val stus = ArrayList<Stu>()

        val sql = "Select id , class_id , stu_id , stu_name , stu_face1 , stu_face2 , stu_face3 , create_time From $STU_TABLE Where class_id = ? and status != -1"
        val re = readDatabase.rawQuery(sql, arrayOf(classId))

        re.moveToFirst()
        while(!re.isAfterLast){
            var stu = Stu()
            stu.cId = re.getString(re.getColumnIndex("id"))
            stu.classId = re.getString(re.getColumnIndex("class_id"))
            stu.stuId = re.getString(re.getColumnIndex("stu_id"))
            stu.stuName = re.getString(re.getColumnIndex("stu_name"))
            stu.stuFace1 = re.getString(re.getColumnIndex("stu_face1"))
            stu.stuFace2 = re.getString(re.getColumnIndex("stu_face2"))
            stu.stuFace3 = re.getString(re.getColumnIndex("stu_face3"))
            stu.createTime = re.getString(re.getColumnIndex("create_time"))
            stus.add(stu)
            re.moveToNext()
        }

        return stus
    }

    /**
     * 根据班级编号删除
     */
    override fun deleteByClassId(classId: String): Int {
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)
        val cv = ContentValues()
        cv.put("status",-1)
        return writeDatabase.update(STU_TABLE,cv,"class_id = ?", arrayOf(classId))
    }


    /**
     * 更新status
     */
    override fun updateStatusById(status: Int,cId: String): Int{
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)
        val cv = ContentValues()
        cv.put("status",status)
        return writeDatabase.update(STU_TABLE,cv,"id = ?", arrayOf(cId))
    }

    /**
     * 更新anchor
     */
    override fun updateAnchorById(anchor: Long,cId: String): Int{
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)
        val cv = ContentValues()
        cv.put("anchor",anchor)
        return writeDatabase.update(STU_TABLE,cv,"id = ?", arrayOf(cId))
    }

    /**
     * 更新status 和 anchor
     */
    override fun updateStatusAndAnchorById(status: Int, anchor: Long, cId: String): Int {
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)
        val cv = ContentValues()
        cv.put("anchor",anchor)
        cv.put("status",status)
        return writeDatabase.update(STU_TABLE,cv,"id = ?", arrayOf(cId))
    }

    /**
     * 获取status<9的未同步记录
     */
    override fun getStatusLessThan9(): ArrayList<Stu> {
        val readDatabase = MyDatabaseOpenHelper.readDb(context)

        val stus = ArrayList<Stu>()

        val sql = "Select id , class_id , stu_id , stu_name , stu_face1 , stu_face2 , stu_face3 , create_time , status , anchor From $STU_TABLE Where status < 9"
        val re = readDatabase.rawQuery(sql, null)
        re.moveToFirst()
        while(!re.isAfterLast){
            val stu = Stu()
            stu.cId = re.getString(re.getColumnIndex("id"))
            stu.classId = re.getString(re.getColumnIndex("class_id"))
            stu.stuId = re.getString(re.getColumnIndex("stu_id"))
            stu.stuName = re.getString(re.getColumnIndex("stu_name"))
            stu.stuFace1 = re.getString(re.getColumnIndex("stu_face1"))
            stu.stuFace2 = re.getString(re.getColumnIndex("stu_face2"))
            stu.stuFace3 = re.getString(re.getColumnIndex("stu_face3"))
            stu.createTime = re.getString(re.getColumnIndex("create_time"))
            stu.status = re.getInt(re.getColumnIndex("status"))
            stu.anchor = re.getLong(re.getColumnIndex("anchor"))
            stus.add(stu)
            re.moveToNext()
        }

        return stus
    }

    /**
     * 真删除
     */
    override fun trueDeleteById(cId: String): Int {
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)
        return writeDatabase.delete(STU_TABLE,"id = ?", arrayOf(cId))
    }

    /**
     * 获取最大anchor
     */
    override fun getMaxAnchor(): Long {
        val readDatabase = MyDatabaseOpenHelper.readDb(context)
        val sql = "SELECT MAX(anchor) maxAnchor FROM $STU_TABLE"
        val re = readDatabase.rawQuery(sql,null)
        var maxAnchor = 0L
        re.moveToFirst()
        if (re.isFirst){
            maxAnchor = re.getLong(re.getColumnIndex("maxAnchor"))
        }
        return maxAnchor
    }
}
