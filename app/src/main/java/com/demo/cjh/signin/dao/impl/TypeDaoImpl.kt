package com.demo.cjh.signin.dao.impl

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.demo.cjh.signin.dao.ITypeDao
import com.demo.cjh.signin.pojo.Type
import com.demo.cjh.signin.util.MyDatabaseOpenHelper

/**
 * Created by CJH
 * on 2018/11/24
 */
class TypeDaoImpl(private val context: Context) : ITypeDao {

    // TODO：同步服务
    /**
     * 增
     */
    override fun insert(obj: Type): Long {
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)
        var cv = ContentValues()
        if (!obj.cId.isEmpty()){
            cv.put("id", obj.cId)
        }
        cv.put("title",obj.title)
        cv.put("img",obj.img)
        cv.put("class_id",obj.classId)
        cv.put("is_dialog",obj.isDialog)

        cv.put("status",0)
        cv.put("anchor",0)
        Log.v(TAG,"dialog: ${obj.isDialog}")
        return writeDatabase.insert(TYPE_TABLE,null,cv)
    }

    /**
     * 标记删
     */
    override fun deleteById(id: String): Int {
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)
        val cv = ContentValues()
        cv.put("status",-1)
        return writeDatabase.update(TYPE_TABLE,cv,"id = ?", arrayOf(id))
    }

    /**
     * 标记删除
     */
    override fun deleteByClassId(classId: String): Int {
        val writeDb = MyDatabaseOpenHelper.writeDb(context)
        val cv = ContentValues()
        cv.put("status",-1)
        return writeDb.update(TYPE_TABLE,cv,"class_id = ?", arrayOf(classId))
    }


    /**
     * 改
     */
    override fun update(obj: Type): Int {
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)
        var cv = ContentValues()
        if (obj.classId.isNullOrEmpty()){
            return -1
        }
        if(!obj.title.isNullOrEmpty()){
            cv.put("title",obj.title)
        }
        if(!obj.img.isNullOrEmpty()){
            cv.put("img",obj.img)
        }
        if (!obj.classId.isNullOrEmpty()){
            cv.put("class_id",obj.classId)
        }
        cv.put("is_dialog",obj.isDialog)

        cv.put("status",1)

        return writeDatabase.update(TYPE_TABLE,cv,"id = ?", arrayOf(obj.cId))
    }

    /**
     * 查
     */
    override fun getById(id: String,isTrue: Boolean): Type? {
        val readDatabase = MyDatabaseOpenHelper.readDb(context)
        val sql = "Select id , title , img , class_id , is_dialog , status , anchor From $TYPE_TABLE Where id = ? ${if (isTrue) "" else "and status != -1"}"
        var re = readDatabase.rawQuery(sql, arrayOf(id))
        re.moveToFirst()
        var type: Type? = null
        if (re.isFirst){
            type = Type()
            type.cId = re.getString(re.getColumnIndex("id"))
            type.title = re.getString(re.getColumnIndex("title"))
            type.img = re.getString(re.getColumnIndex("img"))
            type.classId = re.getString(re.getColumnIndex("class_id"))
            type.isDialog = re.getInt(re.getColumnIndex("is_dialog"))
            if (isTrue) {
                type.status = re.getInt(re.getColumnIndex("status"))
                type.anchor = re.getLong(re.getColumnIndex("anchor"))
            }
        }
        return type
    }

    override fun getAll(): ArrayList<Type> {
        val readDatabase = MyDatabaseOpenHelper.readDb(context)
        val sql = "Select id , title , img , class_id , is_dialog  From $TYPE_TABLE Where status != -1"
        var re = readDatabase.rawQuery(sql, null)
        re.moveToFirst()
        var types = ArrayList<Type>()
        var type: Type
        while (!re.isAfterLast){
            type = Type()
            type.cId = re.getString(re.getColumnIndex("id"))
            type.title = re.getString(re.getColumnIndex("title"))
            type.img = re.getString(re.getColumnIndex("img"))
            type.classId = re.getString(re.getColumnIndex("class_id"))
            type.isDialog = re.getInt(re.getColumnIndex("is_dialog"))
            types.add(type)
            re.moveToNext()
        }
        return types
    }

    override fun getTypesByClassId(classId: String): ArrayList<Type> {
        val readDatabase = MyDatabaseOpenHelper.readDb(context)
        val sql = "Select id , title , img , class_id , is_dialog From $TYPE_TABLE Where class_id = ?  and status != -1"
        var re = readDatabase.rawQuery(sql, arrayOf(classId))
        re.moveToFirst()
        var types = ArrayList<Type>()

        var type: Type
        while (!re.isAfterLast){
            type = Type()
            type.cId = re.getString(re.getColumnIndex("id"))
            type.title = re.getString(re.getColumnIndex("title"))
            type.img = re.getString(re.getColumnIndex("img"))
            type.classId = re.getString(re.getColumnIndex("class_id"))
            type.isDialog = re.getInt(re.getColumnIndex("is_dialog"))
            types.add(type)
            re.moveToNext()
        }
        return types
    }

    /**
     * 更新status
     */
    override fun updateStatusById(status: Int,cId: String): Int{
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)
        val cv = ContentValues()
        cv.put("status",status)
        return writeDatabase.update(TYPE_TABLE,cv,"id = ?", arrayOf(cId))
    }

    /**
     * 更新anchor
     */
    override fun updateAnchorById(anchor: Long,cId: String): Int{
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)
        val cv = ContentValues()
        cv.put("anchor",anchor)
        return writeDatabase.update(TYPE_TABLE,cv,"id = ?", arrayOf(cId))
    }

    /**
     * 更新status 和 anchor
     */
    override fun updateStatusAndAnchorById(status: Int, anchor: Long, cId: String): Int {
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)
        val cv = ContentValues()
        cv.put("anchor",anchor)
        cv.put("status",status)
        return writeDatabase.update(TYPE_TABLE,cv,"id = ?", arrayOf(cId))
    }

    /**
     * 获取status<9的未同步记录
     */
    override fun getStatusLessThan9(): ArrayList<Type> {
        val readDatabase = MyDatabaseOpenHelper.readDb(context)
        val sql = "Select id , title , img , class_id , is_dialog , status,anchor From $TYPE_TABLE Where status < 9"
        var re = readDatabase.rawQuery(sql, null)
        re.moveToFirst()
        var types = ArrayList<Type>()
        var type: Type
        while (!re.isAfterLast){
            type = Type()
            type.cId = re.getString(re.getColumnIndex("id"))
            type.title = re.getString(re.getColumnIndex("title"))
            type.img = re.getString(re.getColumnIndex("img"))
            type.classId = re.getString(re.getColumnIndex("class_id"))
            type.isDialog = re.getInt(re.getColumnIndex("is_dialog"))
            type.status = re.getInt(re.getColumnIndex("status"))
            type.anchor = re.getLong(re.getColumnIndex("anchor"))
            types.add(type)
            re.moveToNext()
        }
        return types
    }

    /**
     * 真删除
     */
    override fun trueDeleteById(cId: String): Int {
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)
        return writeDatabase.delete(TYPE_TABLE,"id = ?", arrayOf(cId))
    }

    /**
     * 获取最大anchor
     */
    override fun getMaxAnchor(): Long {
        val readDatabase = MyDatabaseOpenHelper.readDb(context)
        val sql = "SELECT MAX(anchor) maxAnchor FROM $TYPE_TABLE"
        val re = readDatabase.rawQuery(sql,null)
        var maxAnchor = 0L
        re.moveToFirst()
        if (re.isFirst){
            maxAnchor = re.getLong(re.getColumnIndex("maxAnchor"))
        }
        return maxAnchor
    }

    private val TAG = TypeDaoImpl::class.java.name
}