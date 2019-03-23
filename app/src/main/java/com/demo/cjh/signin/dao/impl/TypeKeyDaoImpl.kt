package com.demo.cjh.signin.dao.impl

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.demo.cjh.signin.dao.ITypeKeyDao
import com.demo.cjh.signin.pojo.TypeKey
import com.demo.cjh.signin.util.MyDatabaseOpenHelper

/**
 * Created by CJH
 * on 2018/12/13
 * @date 2018/12/13
 * @author CJH
 */
class TypeKeyDaoImpl(private val context: Context) : ITypeKeyDao {


    /**
     * 获取每个种类的具体记录种类值
     */
    override fun getKeysByTypeId(typeId: String): ArrayList<String> {
        val readDatabase = MyDatabaseOpenHelper.readDb(context)
        val sql = "Select type_id , type_key From $TABLE_TYPEKEY Where type_id = ? ORDER BY weight"
        val re = readDatabase.rawQuery(sql, arrayOf(typeId))
        re.moveToFirst()
        var keys = ArrayList<String>()
        while (!re.isAfterLast){
            var key = re.getString(re.getColumnIndex("type_key"))
            keys.add(key)
            re.moveToNext()
        }
        Log.d(TAG,"key: ${keys.size}")
        return keys
    }


    /**
     * 增
     */
    override fun insert(obj: TypeKey): Long {
        val writeDb = MyDatabaseOpenHelper.writeDb(context)
        val cv = ContentValues()
        if (!obj.cId.isEmpty()){
            cv.put("id", obj.cId)
        }
        cv.put("type_id",obj.typeId)
        cv.put("type_key",obj.typeKey)
        cv.put("weight",obj.weight)
        // 标记插入
        cv.put("status",0)
        cv.put("anchor",0)
        return writeDb.insert(TABLE_TYPEKEY,null,cv)
    }


    /**
     * 删
     */
    override fun deleteById(id: String): Int {
        val writeDb = MyDatabaseOpenHelper.writeDb(context)
        val cv = ContentValues()
        cv.put("status",-1)
        return writeDb.update(TABLE_TYPEKEY,cv,"type_id = ?", arrayOf(id))
    }



    /**
     * 改 没有调用代码
     */
    override fun update(obj: TypeKey): Int {
        val writeDb = MyDatabaseOpenHelper.writeDb(context)
        val cv = ContentValues()
        if (obj.typeId.isNullOrEmpty()){
            return -1
        }
        if (obj.typeKey.isNullOrEmpty()){
            return -1
        }
        cv.put("weight",obj.weight)
        // 标记更新
        cv.put("status",1)
        return  writeDb.update(TABLE_TYPEKEY,cv,"type_id = ? and type_key = ?", arrayOf(obj.typeId,obj.typeKey))
    }

    /**
     * 查  没有调用
     */
    override fun getById(id: String,isTrue: Boolean): TypeKey? {
        val readDatabase = MyDatabaseOpenHelper.readDb(context)
        val sql = "Select type_id , type_key , status , anchor From $TABLE_TYPEKEY Where id = ? ${if (isTrue) "" else "and status != -1"}"
        val re = readDatabase.rawQuery(sql, null)
        var typeKey :TypeKey? = null
        re.moveToFirst()
        if (re.isFirst){
            typeKey = TypeKey(
                    re.getString(re.getColumnIndex("type_id")),
                    re.getString(re.getColumnIndex("type_key")),
                    re.getInt(re.getColumnIndex("weight"))
            )
            typeKey.cId = re.getString(re.getColumnIndex("id"))
            if (isTrue) {
                typeKey.status = re.getInt(re.getColumnIndex("status"))
                typeKey.anchor = re.getLong(re.getColumnIndex("anchor"))
            }
        }
        return typeKey
    }

    /**
     * 更新status
     */
    override fun updateStatusById(status: Int,cId: String): Int{
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)
        val cv = ContentValues()
        cv.put("status",status)
        return writeDatabase.update(TABLE_TYPEKEY,cv,"id = ?", arrayOf(cId))
    }

    /**
     * 更新anchor
     */
    override fun updateAnchorById(anchor: Long,cId: String): Int{
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)
        val cv = ContentValues()
        cv.put("anchor",anchor)
        return writeDatabase.update(TABLE_TYPEKEY,cv,"id = ?", arrayOf(cId))
    }

    /**
     * 更新status 和 anchor
     */
    override fun updateStatusAndAnchorById(status: Int, anchor: Long, cId: String): Int {
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)
        val cv = ContentValues()
        cv.put("anchor",anchor)
        cv.put("status",status)
        return writeDatabase.update(TABLE_TYPEKEY,cv,"id = ?", arrayOf(cId))
    }

    /**
     * 获取status<9的未同步记录
     */
    override fun getStatusLessThan9(): ArrayList<TypeKey> {

        val readDatabase = MyDatabaseOpenHelper.readDb(context)
        val sql = "Select id, type_id , type_key , weight , status , anchor From $TABLE_TYPEKEY Where status < 9"
        val re = readDatabase.rawQuery(sql, null)
        re.moveToFirst()
        var keys = ArrayList<TypeKey>()
        while (!re.isAfterLast){
            val key = TypeKey(
                    re.getString(re.getColumnIndex("type_id")),
                    re.getString(re.getColumnIndex("type_key")),
                    re.getInt(re.getColumnIndex("weight"))
            )

            key.cId = re.getString(re.getColumnIndex("id"))
            key.status = re.getInt(re.getColumnIndex("status"))
            key.anchor = re.getLong(re.getColumnIndex("anchor"))
            keys.add(key)
            re.moveToNext()
        }
        Log.d(TAG,"key: ${keys.size}")
        return keys
    }

    /**
     * 真删除
     */
    override fun trueDeleteById(cId: String): Int {
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)
        return writeDatabase.delete(TABLE_TYPEKEY,"id = ?", arrayOf(cId))
    }

    /**
     * 获取最大anchor
     */
    override fun getMaxAnchor(): Long {
        val readDatabase = MyDatabaseOpenHelper.readDb(context)
        val sql = "SELECT MAX(anchor) maxAnchor FROM $TABLE_TYPEKEY"
        val re = readDatabase.rawQuery(sql,null)
        var maxAnchor = 0L
        re.moveToFirst()
        if (re.isFirst){
            maxAnchor = re.getLong(re.getColumnIndex("maxAnchor"))
        }
        return maxAnchor
    }

    private val TAG = TypeKeyDaoImpl::class.java.name
}