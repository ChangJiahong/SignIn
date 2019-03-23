package com.demo.cjh.signin.dao.impl

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase

import com.demo.cjh.signin.dao.IClassesDao
import com.demo.cjh.signin.pojo.Classes
import com.demo.cjh.signin.util.MyDatabaseOpenHelper
import java.security.cert.TrustAnchor

/**
 * Created by CJH
 * on 2018/11/21
 */
class ClassesDaoImpl(private val context: Context) : IClassesDao {


    //TODO: api 同步到服务器 POST /classes

    /**
     * 插入
     * @param obj 班级对象
     * @return
     */
    override fun insert(obj: Classes): Long {
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)

        val cv = ContentValues()
        if (!obj.cId.isEmpty()){
            cv.put("id", obj.cId)
        }
        cv.put("class_id", obj.classId)
        cv.put("class_name", obj.className)
        cv.put("info", obj.info)
        cv.put("institute", obj.institute)
        cv.put("speciality", obj.speciality)
        cv.put("create_time", obj.createTime)
        // 插入标志
        cv.put("status",0)
        cv.put("anchor",0)

        return writeDatabase.insert(CLASSES_TABLE, null, cv)

    }

    /**
     * 标记删除By id
     * @param cId 编号
     * @return
     */
    override fun deleteById(cId: String): Int {
        return updateStatusById(-1,cId)
    }


    /**
     * 修改
     * @param obj 班级对象
     * @return
     */
    override fun update(obj: Classes): Int {
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)
        val cv = ContentValues()
        if (obj.cId.isNullOrEmpty()) {
            return -1
        }
        if(!obj.classId.isNullOrEmpty()){
            cv.put("class_id", obj.classId)
        }
        if (!obj.className.isNullOrEmpty()) {
            cv.put("class_name", obj.className)
        }
        if (!obj.info.isNullOrEmpty()) {
            cv.put("info", obj.info)
        }
        if(!obj.institute.isNullOrEmpty()) {
            cv.put("institute", obj.institute)
        }
        if(!obj.speciality.isNullOrEmpty()) {
            cv.put("speciality", obj.speciality)
        }
        if(!obj.className.isNullOrEmpty()) {
            cv.put("create_time", obj.createTime)
        }
        // 修改标志
        cv.put("status", 1)

        return writeDatabase.update(CLASSES_TABLE,cv,"id = ?", arrayOf(obj.cId))
    }

    /**
     * 更新status
     */
    override fun updateStatusById(status: Int,cId: String): Int{
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)
        val cv = ContentValues()
        cv.put("status",status)
        return writeDatabase.update(CLASSES_TABLE,cv,"id = ?", arrayOf(cId))
    }

    /**
     * 更新anchor
     */
    override fun updateAnchorById(anchor: Long,cId: String): Int{
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)
        val cv = ContentValues()
        cv.put("anchor",anchor)
        return writeDatabase.update(CLASSES_TABLE,cv,"id = ?", arrayOf(cId))
    }



    /**
     * 获取所有班级对象
     * @return
     */
    @SuppressLint("Recycle")
    override fun all(): List<Classes> {
        val readDatabase = MyDatabaseOpenHelper.readDb(context)
        val sql = "SELECT id , class_id , class_name , info , institute , speciality , create_time From $CLASSES_TABLE Where status != -1 "
        val Classes = ArrayList<Classes>()
        val re = readDatabase.rawQuery(sql,null)
        re.moveToFirst()
        while(!re.isAfterLast){
            var classes = Classes()
            classes.cId = re.getString(re.getColumnIndex("id"))
            classes.classId = re.getString(re.getColumnIndex("class_id"))
            classes.className = re.getString(re.getColumnIndex("class_name"))
            classes.info = re.getString(re.getColumnIndex("info"))
            classes.institute = re.getString(re.getColumnIndex("institute"))
            classes.speciality = re.getString(re.getColumnIndex("speciality"))
            classes.createTime = re.getString(re.getColumnIndex("create_time"))
            Classes.add(classes)
            re.moveToNext()
        }
        return Classes
    }

    @SuppressLint("Recycle")
    override fun getById(id: String,isTrue:Boolean): Classes? {
        val readDatabase = MyDatabaseOpenHelper.readDb(context)

        val sql = "SELECT id , class_id , class_name , info , institute , speciality , create_time , status , anchor From $CLASSES_TABLE Where id = ? ${if (isTrue) "" else "and status != -1"}"
        val re = readDatabase.rawQuery(sql, arrayOf(id))
        var classes: Classes? = null
        re.moveToFirst()
        if(re.isFirst){
            classes = Classes()
            classes.cId = re.getString(re.getColumnIndex("id"))
            classes.classId = re.getString(re.getColumnIndex("class_id"))
            classes.className = re.getString(re.getColumnIndex("class_name"))
            classes.info = re.getString(re.getColumnIndex("info"))
            classes.institute = re.getString(re.getColumnIndex("institute"))
            classes.speciality = re.getString(re.getColumnIndex("speciality"))
            classes.createTime = re.getString(re.getColumnIndex("create_time"))
            if (isTrue) {
                classes.status = re.getInt(re.getColumnIndex("status"))
                classes.anchor = re.getLong(re.getColumnIndex("anchor"))
            }
        }

        return classes
    }


    /**
     * 班级编号删除 标记删除
     */
    override fun deleteByClassId(classId: String): Int {
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)
        val cv = ContentValues()
        cv.put("status",-1)
        return writeDatabase.update(CLASSES_TABLE,cv,"class_id = ?", arrayOf(classId))
    }

    /**
     * 更新status 和 anchor
     */
    override fun updateStatusAndAnchorById(status: Int, anchor: Long, cId: String): Int {
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)
        val cv = ContentValues()
        cv.put("anchor",anchor)
        cv.put("status",status)
        return writeDatabase.update(CLASSES_TABLE,cv,"id = ?", arrayOf(cId))
    }


    /**
     * 获取status<9的未同步记录
     */
    override fun getStatusLessThan9(): ArrayList<Classes> {
        val readDatabase = MyDatabaseOpenHelper.readDb(context)
        val sql = "SELECT id , class_id , class_name , info , institute , speciality , create_time , status , anchor From $CLASSES_TABLE Where status < 9 "
        val classe = ArrayList<Classes>()
        val re = readDatabase.rawQuery(sql,null)
        re.moveToFirst()
        while(!re.isAfterLast){
            var classes = Classes()
            classes.cId = re.getString(re.getColumnIndex("id"))
            classes.classId = re.getString(re.getColumnIndex("class_id"))
            classes.className = re.getString(re.getColumnIndex("class_name"))
            classes.info = re.getString(re.getColumnIndex("info"))
            classes.institute = re.getString(re.getColumnIndex("institute"))
            classes.speciality = re.getString(re.getColumnIndex("speciality"))
            classes.createTime = re.getString(re.getColumnIndex("create_time"))
            classes.status = re.getInt(re.getColumnIndex("status"))
            classes.anchor = re.getLong(re.getColumnIndex("anchor"))
            classe.add(classes)
            re.moveToNext()
        }
        return classe
    }

    /**
     * 真删除
     */
    override fun trueDeleteById(cId: String): Int {
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)
        return writeDatabase.delete(CLASSES_TABLE,"id = ?", arrayOf(cId))
    }

    /**
     * 获取最大anchor
     */
    override fun getMaxAnchor(): Long {
        val readDatabase = MyDatabaseOpenHelper.readDb(context)
        val sql = "SELECT MAX(anchor) maxAnchor FROM $CLASSES_TABLE"
        val re = readDatabase.rawQuery(sql,null)
        var maxAnchor = 0L
        re.moveToFirst()
        if (re.isFirst){
            maxAnchor = re.getLong(re.getColumnIndex("maxAnchor"))
        }
        return maxAnchor
    }


}
