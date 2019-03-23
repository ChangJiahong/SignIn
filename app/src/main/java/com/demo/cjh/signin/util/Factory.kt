package com.demo.cjh.signin.util

import android.content.Context
import android.database.sqlite.SQLiteDatabase

import com.demo.cjh.signin.service.IClassesService

/**
 * Created by CJH
 * on 2018/11/24
 */
class Factory(context: Context) {

    lateinit var readDb: SQLiteDatabase
    lateinit var writeDb: SQLiteDatabase

    init {
        val db = MyDatabaseOpenHelper.getInstence(context)

        readDb = db.readableDatabase
        writeDb = db.writableDatabase
    }

//    fun <T : IClassesService> build(clz: Class<T>): T{
//        var classesService: IClassesService
//        when(clz){
//            is IClassesService ->{
//                classesService = Class.forName(clz.name).newInstance() as IClassesService
//            }
//        }
//        return classesService
//    }

    fun execute(fn: Unit){
        fn
    }

    fun executeTransaction() {

    }

}
