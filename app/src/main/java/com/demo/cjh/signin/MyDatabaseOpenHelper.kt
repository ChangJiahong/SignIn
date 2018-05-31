package com.demo.cjh.signin

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

/**
 * Created by CJH
 * on 2018/5/31
 */
class MyDatabaseOpenHelper(context: Context, name: String, cursorFactory: SQLiteDatabase.CursorFactory, version: Int) : SQLiteOpenHelper(context,name,cursorFactory,version) {
    val TAG = "MyDatabaseOpenHelper"

    /**
     * 单例模式
     */
    companion object {
        val instence: MyDatabaseOpenHelper? = null

        val DB_NAME = "DataBase"
        val STU_INFO = "Stu_info"
        val CLASSINFO = "classInfo"

        @Synchronized
        fun getInstence(context: Context,version: Int) : MyDatabaseOpenHelper{
            if(instence == null && version > 0){
                instence = MyDatabaseOpenHelper(context, DB_NAME,null!!,version)
            }else if(instence == null){
                instence = MyDatabaseOpenHelper(context, DB_NAME,null!!,1)
            }
            return instence
        }
    }
    /**
     * 更新数据库
     */
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    override fun onCreate(db: SQLiteDatabase?) {

        Log.d(TAG,"createDB $DB_NAME ,loading...！")
        var drop_sql = "Create Table if Exists $STU_INFO;"
        var stu_sql = "CREATE TABLE IF NOT EXISTS $STU_INFO (id INTEGER PRIMARY KEY AUTOINCREMENT,stuId VARCHAR(50) NOT NULL,name VARCHAR(50),type VARCHAR(20),classId VARCHAR(50),time VARCHAR NOT NULL);"
        var class_sql = "CREATE TABLE IF NOT EXISTS $CLASSINFO (id INTEGER PRIMARY KEY AUTOINCREMENT,classId VARCHAR(50) NOT NULL,className VARCHAR(50),info TEXT,time VARCHAR NOT NULL);"
        db!!.execSQL(stu_sql)
        db!!.execSQL(class_sql)
        Log.d(TAG,"createDB $DB_NAME successful！")
    }

    fun getDBName() = when(instence != null){
            true -> instence!!.databaseName
            false -> DB_NAME
        }


}