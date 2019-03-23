package com.demo.cjh.signin

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.preference.PreferenceManager
import com.demo.cjh.signin.pojo.FaceRegist
import com.demo.cjh.signin.pojo.User
import com.demo.cjh.signin.util.MyDatabaseOpenHelper
import com.demo.cjh.signin.util.database
import java.util.ArrayList

/**
 * Created by CJH
 * on 2018/8/2
 */
class App : Application() {
    val TAG = "App"

    companion object {

        lateinit var app: App

        private var mRegister = ArrayList<FaceRegist>()

        private var mImage: Uri? = null

        fun setCaptureImage(uri: Uri?) {
            mImage = uri
        }

        fun getCaptureImage(): Uri? {
            return mImage
        }

        fun setRegister (regists: ArrayList<FaceRegist>){
            mRegister.clear()
            mRegister.addAll(regists)
        }
        fun getRegister (): ArrayList<FaceRegist> {
            return mRegister
        }

    }

    lateinit var sp: SharedPreferences
    lateinit var dsp: SharedPreferences
    lateinit var db: MyDatabaseOpenHelper

    var ip: String = ""

    val login
            get() = "$ip/login"

    var downTables = "$ip/downTables"

    var upTables = "$ip/upTables"

    var upTypeImg = "$ip/file/upTypeImg"

    var upUserImg = "$ip/file/upUserImg"

    var alterName = "$ip/alterName"

    var alterPwd = "$ip/alterPwd"

    var upFaceFiles = "$ip/file/upFaceFiles"

    var user = User()


    override fun onCreate() {
        super.onCreate()
        app = this@App
        sp = applicationContext.getSharedPreferences("config", Context.MODE_PRIVATE)
        dsp = PreferenceManager.getDefaultSharedPreferences(this)
        db = database

//        readeDb = db.readableDatabase
//        writeDb = db.writableDatabase
//        classesDao = ClassesDaoImpl(readeDb,writeDb)
//        recordDao = RecordDaoImpl(readeDb,writeDb)
//        stuDao = StuDaoImp(readeDb,writeDb)
//
//        classesService = ClassesServiceImpl(classesDao,recordDao,stuDao)
//        stuService = StuServiceImpl()

    }

}