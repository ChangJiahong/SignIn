package com.demo.cjh.signin.dao.impl

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.demo.cjh.signin.dao.IFaceRecordDao
import com.demo.cjh.signin.pojo.Classes
import com.demo.cjh.signin.pojo.FaceRecord
import com.demo.cjh.signin.util.MyDatabaseOpenHelper

import java.util.ArrayList

/**
 * 人脸数据dao
 * @author CJH
 * @date 2019/1/14
 */
class FaceRecordDaoImpl(private val context: Context) : IFaceRecordDao {

    val TAG = FaceRecordDaoImpl::class.java.name

    override fun insert(obj: FaceRecord): Long {
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)
        Log.d(TAG,"准备开始")
        val cv = ContentValues()
        if (!obj.cId.isEmpty()){
            cv.put("id", obj.cId)
        }
        cv.put("s_id", obj.sId)
        cv.put("path", obj.path)
        cv.put("create_time", obj.createTime)
        // 插入标志
        cv.put("status",0)
        cv.put("anchor",0)
        return writeDatabase.insert(TABLE_NAME, null, cv)
    }

    /**
     * 假删除
     */
    override fun deleteById(id: String): Int {
        // 不操作，人脸数据不能删除，能允许修改
        return 0
    }


    /**
     * 更新
     */
    override fun update(obj: FaceRecord): Int {
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)
        val cv = ContentValues()
        if (obj.cId.isNullOrEmpty()){
            return -1
        }
        if(!obj.sId.isNullOrEmpty()){
            cv.put("s_id", obj.sId)
        }
        if (!obj.path.isNullOrEmpty()){
            cv.put("path",obj.path)
        }
        if (!obj.createTime.isNullOrEmpty()){
            cv.put("create_time",obj.createTime)
        }
        // 修改标志
        cv.put("status", 1)
        return writeDatabase.update(TABLE_NAME,cv,"id = ?", arrayOf(obj.cId))
    }


    override fun getById(id: String, isTrue: Boolean): FaceRecord? {
        val readDatabase = MyDatabaseOpenHelper.readDb(context)
        val sql = "SELECT id , s_id , path , create_time , status , anchor From $TABLE_NAME Where id = ? ${if (isTrue) "" else "and status != -1"}"
        val re = readDatabase.rawQuery(sql, arrayOf(id))
        var faceRecord: FaceRecord? = null
        re.moveToFirst()
        if(re.isFirst){
            faceRecord = FaceRecord()
            faceRecord.cId = re.getString(re.getColumnIndex("id"))
            faceRecord.path = re.getString(re.getColumnIndex("path"))
            faceRecord.sId = re.getString(re.getColumnIndex("s_id"))
            faceRecord.createTime = re.getString(re.getColumnIndex("create_time"))
            if (isTrue) {
                faceRecord.status = re.getInt(re.getColumnIndex("status"))
                faceRecord.anchor = re.getLong(re.getColumnIndex("anchor"))
            }
        }

        return faceRecord
    }

    override fun updateAnchorById(anchor: Long, cId: String): Int {
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)
        val cv = ContentValues()
        cv.put("anchor",anchor)
        return writeDatabase.update(TABLE_NAME,cv,"id = ?", arrayOf(cId))
    }

    override fun updateStatusById(status: Int, cId: String): Int {
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)
        val cv = ContentValues()
        cv.put("status",status)
        return writeDatabase.update(TABLE_NAME,cv,"id = ?", arrayOf(cId))
    }

    override fun updateStatusAndAnchorById(status: Int, anchor: Long, cId: String): Int {
        val writeDatabase = MyDatabaseOpenHelper.writeDb(context)
        val cv = ContentValues()
        cv.put("anchor",anchor)
        cv.put("status",status)
        return writeDatabase.update(TABLE_NAME,cv,"id = ?", arrayOf(cId))
    }

    /**
     * 获取status<9的未同步记录
     */
    override fun getStatusLessThan9(): ArrayList<FaceRecord> {
        var faces = ArrayList<FaceRecord>()

        val readDatabase = MyDatabaseOpenHelper.readDb(context)
        val sql = "SELECT id , s_id , path , create_time , status , anchor From $TABLE_NAME Where status < 9 "

        val re = readDatabase.rawQuery(sql,null)
        re.moveToFirst()
        while(!re.isAfterLast){
            val faceRecord = FaceRecord()
            faceRecord.cId = re.getString(re.getColumnIndex("id"))
            faceRecord.path = re.getString(re.getColumnIndex("path"))
            faceRecord.sId = re.getString(re.getColumnIndex("s_id"))
            faceRecord.createTime = re.getString(re.getColumnIndex("create_time"))

            faceRecord.status = re.getInt(re.getColumnIndex("status"))
            faceRecord.anchor = re.getLong(re.getColumnIndex("anchor"))

            faces.add(faceRecord)
            re.moveToNext()
        }

        return faces
    }

    override fun getMaxAnchor(): Long {
        val readDatabase = MyDatabaseOpenHelper.readDb(context)
        val sql = "SELECT MAX(anchor) maxAnchor FROM $TABLE_NAME"
        val re = readDatabase.rawQuery(sql,null)
        var maxAnchor = 0L
        re.moveToFirst()
        if (re.isFirst){
            maxAnchor = re.getLong(re.getColumnIndex("maxAnchor"))
        }
        return maxAnchor
    }

    override fun trueDeleteById(cId: String): Int {
        // 人脸数据 不允许删除
        return 0
    }
}
