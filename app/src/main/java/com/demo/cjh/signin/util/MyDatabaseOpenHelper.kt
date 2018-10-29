package com.demo.cjh.signin.util

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.arcsoft.facerecognition.AFR_FSDKFace
import com.demo.cjh.signin.obj.*
import kotlin.collections.ArrayList

/**
 * Created by CJH
 * on 2018/5/31
 */
class MyDatabaseOpenHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME,null,1) {
    val TAG = "MyDatabaseOpenHelper"

    /**
     * 单例模式
     */
    companion object {
        var instence: MyDatabaseOpenHelper? = null

        /**
         * 数据库名
         */
        val DB_NAME = "DataBase"
        /**
         * 学生信息表
         */
        val STUINFO = "StuInfo"
        /**
         * 班级信息表
         */
        val CLASSINFO = "ClassInfo"
        /**
         * 学生考勤详情表
         */
        val STUSIGNININFO = "StuSignInInfo"
        /**
         * 学生考勤记录表
         */
        val STUSIGNINLIST = "StuSignInList"
        /**
         * 课堂表现记录
         */
        val GPALIST = "GpaList"
        /**
         * 课堂表现信息
         */
        val GPAINFO = "GpaInfo"
        /**
         * 实验表现记录
         */
        val TESTLIST = "TestList"
        /**
         * 实验表现信息
         */
        val TESTINFO = "TestInfo"

        /**
         * 学生信息表列名
         */
        val StuInfoCell = arrayOf("id","stuId","name","classId","time","face1","face2","face3")

        /**
         * 班级信息表列名
         */
        val ClassInfoCell = arrayOf("id","classId","className","info","time")

        /**
         * 学生考勤详情表列名
         */
        val StuSignInInfoCell = arrayOf("id","stuId","classId","type","no")

        /**
         * 学生考勤记录表列名
         */
        val StuSignInListCell = arrayOf("id","classId","time","num","info")

        val GpaListCell = arrayOf("id","classId","time","num","info")

        val GpaInfoCell = arrayOf("id","stuId","classId","type","no")

        val TestListCell = arrayOf("id","classId","time","num","info")

        val TestInfoCell = arrayOf("id","stuId","classId","type","no")

        @Synchronized
        fun getInstence(context: Context) : MyDatabaseOpenHelper {
            if(instence == null){
                instence = MyDatabaseOpenHelper(context)
            }
            return instence!!
        }
    }
    /**
     * 更新数据库
     */
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    /**
     *  Stu_info : id | stuId | name | classId | time
     * ClassInfo : id | classId | className | info | time
     *
     */
    override fun onCreate(db: SQLiteDatabase?) {

        Log.d(TAG,"createDB ${DB_NAME} ,loading...！")
        var stu_sql = "CREATE TABLE IF NOT EXISTS ${STUINFO} (id INTEGER PRIMARY KEY AUTOINCREMENT,stuId VARCHAR(50) NOT NULL,name VARCHAR(50) NOT NULL,classId VARCHAR(50) NOT NULL,time VARCHAR NOT NULL,face1 BLOB,face2 BLOB,face3 BLOB);"
        var class_sql = "CREATE TABLE IF NOT EXISTS ${CLASSINFO} (id INTEGER PRIMARY KEY AUTOINCREMENT,classId VARCHAR(50) NOT NULL,className VARCHAR(50) NOT NULL,info TEXT,time VARCHAR NOT NULL);"
        var stu_info = "CREATE TABLE IF NOT EXISTS ${STUSIGNININFO} (id INTEGER PRIMARY KEY AUTOINCREMENT,stuId VARCHAR(50) NOT NULL,classId VARCHAR(50) NOT NULL,type VARCHAR(20) NOT NULL,no VARCHAR(50) NOT NULL);"
        var stu_list = "CREATE TABLE IF NOT EXISTS ${STUSIGNINLIST} (id INTEGER PRIMARY KEY AUTOINCREMENT,classId VARCHAR(50) NOT NULL,time VARCHAR NOT NULL,num INT NOT NULL,info VARCHAR(50) NOT NULL);"
        var gpa_list = "CREATE TABLE IF NOT EXISTS ${GPALIST} (id INTEGER PRIMARY KEY AUTOINCREMENT,classId VARCHAR(50) NOT NULL,time VARCHAR NOT NULL,num INT NOT NULL,info VARCHAR(50) NOT NULL);"
        var gpa_info = "CREATE TABLE IF NOT EXISTS ${GPAINFO} (id INTEGER PRIMARY KEY AUTOINCREMENT,stuId VARCHAR(50) NOT NULL,classId VARCHAR(50) NOT NULL,type VARCHAR(20) NOT NULL,no VARCHAR(50) NOT NULL);"
        var test_list = "CREATE TABLE IF NOT EXISTS ${TESTLIST} (id INTEGER PRIMARY KEY AUTOINCREMENT,classId VARCHAR(50) NOT NULL,time VARCHAR NOT NULL,num INT NOT NULL,info VARCHAR(50) NOT NULL);"
        var test_info = "CREATE TABLE IF NOT EXISTS ${TESTINFO} (id INTEGER PRIMARY KEY AUTOINCREMENT,stuId VARCHAR(50) NOT NULL,classId VARCHAR(50) NOT NULL,type VARCHAR(20) NOT NULL,no VARCHAR(50) NOT NULL);"


        db!!.execSQL(stu_sql)
        db.execSQL(class_sql)
        db.execSQL(stu_info)
        db.execSQL(stu_list)
        db.execSQL(gpa_info)
        db.execSQL(gpa_list)
        db.execSQL(test_info)
        db.execSQL(test_list)

        Log.d(TAG,"createDB ${DB_NAME} successful！")

    }

    /**
     * 获取数据库名
     */
    fun getDBName() = when(instence != null){
            true -> instence!!.databaseName
            false -> DB_NAME
        }


    /**
     * 学生信息表,插入
     */
    fun StuInfo_insert(stu: StudentInfo){
        var db = writableDatabase
        var cv = ContentValues()
        cv.put(StuInfoCell[1],stu.stuId)
        cv.put(StuInfoCell[2],stu.name)
        cv.put(StuInfoCell[3],stu.classId)
        cv.put(StuInfoCell[4], getNow())
        db.insert(STUINFO,null,cv)
    }

    /**
     * 学生信息表批量插入
     */
    fun insert_stuInfo(data: ArrayList<StudentInfo>){
        for(stu in data){
            StuInfo_insert(stu)
        }
    }

    /**
     * 班级信息表插入
     */
    fun insert_classInfo(data: ClassInfo){
        var db = writableDatabase
        var cv = ContentValues()
        cv.put(ClassInfoCell[1],data.classId)
        cv.put(ClassInfoCell[2],data.className)
        cv.put(ClassInfoCell[3],data.info)
        cv.put(ClassInfoCell[4], getNow())
        db.insert(CLASSINFO,null,cv)
    }

    /**
     * 学生考勤信息详表 插入
     */
    fun insert_stuSignInInfo(da: StuSignInInfo){

        // 获取考勤编号
        // 考勤编号的计算格式 = YYYY-MM-DD(01)
        //da.no = da.no!!.substring(0,10)+"("+getNum(da.classId!!,da.no!!.substring(0,10))+")"

        var db = writableDatabase
        var cv = ContentValues()
        cv.put(StuSignInInfoCell[1],da.stuId)
        cv.put(StuSignInInfoCell[2],da.classId)
        cv.put(StuSignInInfoCell[3],da.type)
        cv.put(StuSignInInfoCell[4],da.no) // 考勤编号
        db.insert(STUSIGNININFO,null,cv)
        Log.v(TAG,"考勤详情表插入成功")
    }

    /**
     * 学生考勤信息记录表 插入
     */
    fun insert_stuSignInList(da: StuSignInList){
        //da.num = getNum(da.classId!!,da.time!!.substring(0,10))
        var no = da.time!!.substring(0,10)+"("+da.num+")"
        if(da.info!!.isEmpty()){
            da.info = no
        }

        var db = writableDatabase
        var cv = ContentValues()
        cv.put(StuSignInListCell[1],da.classId)
        cv.put(StuSignInListCell[2], da.time)
        cv.put(StuSignInListCell[3],da.num)
        cv.put(StuSignInListCell[4], da.info)
        db.insert(STUSIGNINLIST,null,cv)
        Log.v(TAG,"考勤记录表插入成功")
    }

    /**
     * 学生课堂表现记录表 插入
     */
    fun insert_gpaList(da: StuSignInList){
        //da.num = getNum(da.classId!!,da.time!!.substring(0,10))
        var no = da.time!!.substring(0,10)+"("+da.num+")"
        if(da.info!!.isEmpty()){
            da.info = no
        }

        var db = writableDatabase
        var cv = ContentValues()
        cv.put(StuSignInListCell[1],da.classId)
        cv.put(StuSignInListCell[2], da.time)
        cv.put(StuSignInListCell[3],da.num)
        cv.put(StuSignInListCell[4], da.info)
        db.insert(GPALIST,null,cv)
        Log.v(TAG,"课堂表现记录表插入成功")
    }

    /**
     * 学生实验表现记录表 插入
     */
    fun insert_testList(da: StuSignInList){
        //da.num = getNum(da.classId!!,da.time!!.substring(0,10))
        var no = da.time!!.substring(0,10)+"("+da.num+")"
        if(da.info!!.isEmpty()){
            da.info = no
        }

        var db = writableDatabase
        var cv = ContentValues()
        cv.put(StuSignInListCell[1],da.classId)
        cv.put(StuSignInListCell[2], da.time)
        cv.put(StuSignInListCell[3],da.num)
        cv.put(StuSignInListCell[4], da.info)
        db.insert(TESTLIST,null,cv)
        Log.v(TAG,"实验表现记录表插入成功")
    }

    /**
     * 学生课堂表现信息详表 插入
     */
    fun insert_gpaInfo(da: StuSignInInfo){

        // 获取考勤编号
        // 考勤编号的计算格式 = YYYY-MM-DD(01)
        //da.no = da.no!!.substring(0,10)+"("+getNum(da.classId!!,da.no!!.substring(0,10))+")"

        var db = writableDatabase
        var cv = ContentValues()
        cv.put(StuSignInInfoCell[1],da.stuId)
        cv.put(StuSignInInfoCell[2],da.classId)
        cv.put(StuSignInInfoCell[3],da.type)
        cv.put(StuSignInInfoCell[4],da.no) // 考勤编号
        db.insert(GPAINFO,null,cv)
        Log.v(TAG,"课堂表现详情表插入成功")
    }

    /**
     * 学生课堂表现信息详表 插入
     */
    fun insert_testInfo(da: StuSignInInfo){

        // 获取考勤编号
        // 考勤编号的计算格式 = YYYY-MM-DD(01)
        //da.no = da.no!!.substring(0,10)+"("+getNum(da.classId!!,da.no!!.substring(0,10))+")"

        var db = writableDatabase
        var cv = ContentValues()
        cv.put(StuSignInInfoCell[1],da.stuId)
        cv.put(StuSignInInfoCell[2],da.classId)
        cv.put(StuSignInInfoCell[3],da.type)
        cv.put(StuSignInInfoCell[4],da.no) // 考勤编号
        db.insert(TESTINFO,null,cv)
        Log.v(TAG,"实验表现详情表插入成功")
    }

    /**
     * 查询：班级信息表，获取班级信息列
     */
    fun query_classInfo(): ArrayList<ClassInfo>{
        var sql = "select * from ${CLASSINFO} ;"
        var db = readableDatabase
        var data = ArrayList<ClassInfo>()
        var cursor = db.rawQuery(sql,null)
        cursor.moveToFirst()
        while(!cursor.isAfterLast){
            var da = ClassInfo()
            da.classId = cursor.getString(1) // classId
            da.className = cursor.getString(2) // className
            da.info = cursor.getString(3) // info
            da.time = cursor.getString(4) // time
            data.add(da)
            Log.v(TAG,"数据库 "+da.className)
            cursor.moveToNext()
        }
        Log.v(TAG,"数据库"+data.size)
        return data
    }

    /**
     * 查询：学生考勤记录表，获取某班级考勤历史考勤记录，通过【班级编号】
     */
    fun query_signInList_by_classId(classId: String): ArrayList<StuSignInList>{
        var sql = "select * from ${STUSIGNINLIST} where classId = ?;"
        var db = readableDatabase
        var data = ArrayList<StuSignInList>()
        var cursor = db.rawQuery(sql, arrayOf(classId))
        cursor.moveToFirst()
        while(!cursor.isAfterLast){
            var da = StuSignInList()
            da.id = cursor.getString(0)  // id
            da.classId = cursor.getString(1) // classId 班级编号
            da.time = cursor.getString(2) // time 时间
            da.num = cursor.getInt(3) // num 当天次数
            da.info = cursor.getString(4) // info 考勤备注
            da.no = da.time!!.substring(0,10)+"("+da.num+")"
            data.add(da)
            cursor.moveToNext()
        }
        Log.v(TAG,"考勤记录表查询成功")
        return data
    }


    /**
     * 查询：学生课堂表现记录表，获取某班级课堂历史表现记录，通过【班级编号】
     */
    fun query_gpaList_by_classId(classId: String): ArrayList<StuSignInList>{
        var sql = "select * from ${GPALIST} where classId = ?;"
        var db = readableDatabase
        var data = ArrayList<StuSignInList>()
        var cursor = db.rawQuery(sql, arrayOf(classId))
        cursor.moveToFirst()
        while(!cursor.isAfterLast){
            var da = StuSignInList()
            da.id = cursor.getString(0)  // id
            da.classId = cursor.getString(1) // classId 班级编号
            da.time = cursor.getString(2) // time 时间
            da.num = cursor.getInt(3) // num 当天次数
            da.info = cursor.getString(4) // info 考勤备注
            da.no = da.time!!.substring(0,10)+"("+da.num+")"
            data.add(da)
            cursor.moveToNext()
        }
        Log.v(TAG,"课堂表现记录表查询成功")
        return data
    }

    /**
     * 查询：学生实验表现记录表，获取某班级课堂历史表现记录，通过【班级编号】
     */
    fun query_testList_by_classId(classId: String): ArrayList<StuSignInList>{
        var sql = "select * from ${TESTLIST} where classId = ?;"
        var db = readableDatabase
        var data = ArrayList<StuSignInList>()
        var cursor = db.rawQuery(sql, arrayOf(classId))
        cursor.moveToFirst()
        while(!cursor.isAfterLast){
            var da = StuSignInList()
            da.id = cursor.getString(0)  // id
            da.classId = cursor.getString(1) // classId 班级编号
            da.time = cursor.getString(2) // time 时间
            da.num = cursor.getInt(3) // num 当天次数
            da.info = cursor.getString(4) // info 考勤备注
            da.no = da.time!!.substring(0,10)+"("+da.num+")"
            data.add(da)
            cursor.moveToNext()
        }
        Log.v(TAG,"实验记录表查询成功")
        return data
    }

    /**
     * 查询：学生考勤详细记录，获取某条记录详细信息，通过【考勤编号】和【班级编号】
     */
    fun query_signInInfo_by_classId_and_no(classId: String,no: String): ArrayList<StudentInfo>{
        var sql = "select ${STUSIGNININFO}.stuId,${STUSIGNININFO}.classId,type,no,name from ${STUSIGNININFO} ,${STUINFO} where ${STUSIGNININFO}.classId = ? and no = ? and ${STUSIGNININFO}.stuId = ${STUINFO}.stuId and ${STUSIGNININFO}.classId = ${STUINFO}.classId;"
        var db = readableDatabase
        var data = ArrayList<StudentInfo>()
        var cursor = db.rawQuery(sql, arrayOf(classId,no))
        cursor.moveToFirst()
        while(!cursor.isAfterLast){
            var da = StudentInfo()
            da.stuId = cursor.getString(0) // stuId 学生学号
            da.classId = cursor.getString(1) // classId 班级编号
            da.type = cursor.getString(2) // type 考勤信息
            da.no = cursor.getString(3) // no 考勤编号
            da.name = cursor.getString(4) // no 考勤编号
            data.add(da)
            cursor.moveToNext()
        }
        Log.v(TAG,"考勤详情表查询成功")
        return data
    }

    /**
     * 查询：学生课堂表现详细记录，获取某条记录详细信息，通过【课堂表现编号】和【班级编号】
     */
    fun query_gpaInfo_by_classId_and_no(classId: String,no: String): ArrayList<StudentInfo>{
        Log.v(TAG, "$classId $no")
        var sql = "select ${GPAINFO}.stuId,${GPAINFO}.classId,type,no,name from ${GPAINFO} ,${STUINFO} where ${GPAINFO}.classId = ? and no = ? and ${GPAINFO}.stuId = ${STUINFO}.stuId and ${GPAINFO}.classId = ${STUINFO}.classId;"
        var db = readableDatabase
        var data = ArrayList<StudentInfo>()
        var cursor = db.rawQuery(sql, arrayOf(classId,no))
        cursor.moveToFirst()
        while(!cursor.isAfterLast){
            var da = StudentInfo()
            da.stuId = cursor.getString(0) // stuId 学生学号
            da.classId = cursor.getString(1) // classId 班级编号
            da.type = cursor.getString(2) // type 课堂表现具体信息
            da.no = cursor.getString(3) // no 课堂表现编号
            da.name = cursor.getString(4) // no 课堂表现编号
            data.add(da)
            cursor.moveToNext()
            Log.v(TAG,da.type)
        }
        Log.v(TAG,"课堂表现详情表查询成功")
        return data
    }


    /**
     * 查询：学生实验表现详细记录，获取某条记录详细信息，通过【课堂表现编号】和【班级编号】
     */
    fun query_testInfo_by_classId_and_no(classId: String,no: String): ArrayList<StudentInfo>{
        Log.v(TAG, "$classId $no")
        var sql = "select ${TESTINFO}.stuId,${TESTINFO}.classId,type,no,name from ${TESTINFO} ,${STUINFO} where ${TESTINFO}.classId = ? and no = ? and ${TESTINFO}.stuId = ${STUINFO}.stuId and ${TESTINFO}.classId = ${STUINFO}.classId;"
        var db = readableDatabase
        var data = ArrayList<StudentInfo>()
        var cursor = db.rawQuery(sql, arrayOf(classId,no))
        cursor.moveToFirst()
        while(!cursor.isAfterLast){
            var da = StudentInfo()
            da.stuId = cursor.getString(0) // stuId 学生学号
            da.classId = cursor.getString(1) // classId 班级编号
            da.type = cursor.getString(2) // type 课堂表现具体信息
            da.no = cursor.getString(3) // no 课堂表现编号
            da.name = cursor.getString(4) // no 课堂表现编号
            data.add(da)
            cursor.moveToNext()
            Log.v(TAG,da.type)
        }
        Log.v(TAG,"实验表现详情表查询成功")
        return data
    }


    /**
     * 查询：获取某班级学生信息
     */
    fun query_stuInfo_by_classId(classId: String): ArrayList<StudentInfo>{
        var sql = "select * from ${STUINFO} where classId = ? ;"
        var db = readableDatabase
        var data = ArrayList<StudentInfo>()
        var cursor = db.rawQuery(sql, arrayOf(classId))
        cursor.moveToFirst()
        while(!cursor.isAfterLast){
            var da = StudentInfo()
            da.stuId = cursor.getString(1) // stuId 学生学号
            da.name = cursor.getString(2) // name 姓名
            da.classId = cursor.getString(3) // classId 班级编号
            data.add(da)
            cursor.moveToNext()
        }
        return data
    }

    /**
     * 查询：获取某班级当天考勤次数
     */
    fun getNum(classId: String,time: String): Int{
        var sql = "select count() from ${STUSIGNINLIST} where classId = ? and substr(time,1,10) = ?;"
        var db = readableDatabase
        var num = 0
        var cursor = db.rawQuery(sql, arrayOf(classId,time))
        cursor.moveToFirst()
        if(!cursor.isAfterLast){
            num = cursor.getInt(0) // num 次数
            cursor.moveToNext()
        }
        num++
        Log.v(TAG,"num"+num)
        return num
    }

    fun getGpaNum(classId: String,time: String): Int{
        var sql = "select count() from ${GPALIST} where classId = ? and substr(time,1,10) = ?;"
        var db = readableDatabase
        var num = 0
        var cursor = db.rawQuery(sql, arrayOf(classId,time))
        cursor.moveToFirst()
        if(!cursor.isAfterLast){
            num = cursor.getInt(0) // num 次数
            cursor.moveToNext()
        }
        num++
        Log.v(TAG,"num"+num)
        return num
    }

    fun getTestNum(classId: String,time: String): Int{
        var sql = "select count() from ${TESTLIST} where classId = ? and substr(time,1,10) = ?;"
        var db = readableDatabase
        var num = 0
        var cursor = db.rawQuery(sql, arrayOf(classId,time))
        cursor.moveToFirst()
        if(!cursor.isAfterLast){
            num = cursor.getInt(0) // num 次数
            cursor.moveToNext()
        }
        num++
        Log.v(TAG,"num"+num)
        return num
    }


    /**
     * 删除班级 及其所包含的数据，学生信息，签到信息等
     */
    fun delete_class(classId: String): Boolean{
        var db = writableDatabase

        /*
            删除学生
         */
        db.delete(STUINFO,"classId = ?", arrayOf(classId))
        /*
            删除学生签到信息
         */
        db.delete(STUSIGNININFO,"classId = ?", arrayOf(classId))
        /*
            删除签到记录
         */
        db.delete(STUSIGNINLIST,"classId = ?", arrayOf(classId))
        /*
            删除班级信息
         */
        db.delete(CLASSINFO,"classId = ?", arrayOf(classId))
        return true
    }

    /**
     * 删除考勤记录
     */
    fun delete_signInList(id: String,classId: String,no: String) {
        var db = writableDatabase
        /*
            删除学生签到信息
         */
        db.delete(STUSIGNININFO,"classId = ? and no = ?", arrayOf(classId,no))
        /*
            删除签到记录
         */
        db.delete(STUSIGNINLIST,"id = ?", arrayOf(id))
    }

    /**
     * 删除课堂表现记录
     */
    fun delete_gpaList(id: String,classId: String,no: String) {
        var db = writableDatabase
        /*
            删除学生签到信息
         */
        db.delete(GPAINFO,"classId = ? and no = ?", arrayOf(classId,no))
        /*
            删除签到记录
         */
        db.delete(GPALIST,"id = ?", arrayOf(id))
    }


    /**
     * 更新学生签到详表
     */
    fun updata_stuSignInInfo(da: StuSignInInfo): Boolean{
        var db = writableDatabase
        var cv = ContentValues()
        if(da.type!=null) { // 不为空
            cv.put(StuSignInInfoCell[3], da.type)
            db.update(STUSIGNININFO, cv, "stuId = ? and no = ?", arrayOf(da.stuId,da.no))
        }else{
            return false
        }
        return true
    }

    /**
     * 更新学生课堂表现详表
     */
    fun updata_gpaInfo(da: StuSignInInfo): Boolean{
        var db = writableDatabase
        var cv = ContentValues()
        if(da.type!=null) { // 不为空
            cv.put(GpaInfoCell[3], da.type)
            db.update(GPAINFO, cv, "stuId = ? and no = ?", arrayOf(da.stuId,da.no))
        }else{
            return false
        }
        return true
    }

    /**
     * 更新学生实验表现详表
     */
    fun updata_testInfo(da: StuSignInInfo): Boolean{
        var db = writableDatabase
        var cv = ContentValues()
        if(da.type!=null) { // 不为空
            cv.put(TestInfoCell[3], da.type)
            db.update(TESTINFO, cv, "stuId = ? and no = ?", arrayOf(da.stuId,da.no))
        }else{
            return false
        }
        return true
    }


    fun queryHisoryList() : ArrayList<HistoryItem>{
        val sql = "select $STUSIGNINLIST.classId,$CLASSINFO.className,$STUSIGNINLIST.time,$STUSIGNINLIST.num,$STUSIGNINLIST.info from $STUSIGNINLIST,$CLASSINFO WHERE $STUSIGNINLIST.classId = $CLASSINFO.classId"
        var db = readableDatabase
        var data = ArrayList<HistoryItem>()
        var cursor = db.rawQuery(sql,null)
        cursor.moveToFirst()
        while(!cursor.isAfterLast){
            var da = HistoryItem()
            da.classId = cursor.getString(0) // classId 班级编号
            da.className = cursor.getString(1) // 班级Name
            da.time = cursor.getString(3) // time
            da.num = cursor.getString(4) // num 当天次数
            da.no = da.time.substring(0,10)+"("+da.num+")" // 考勤编号
            data.add(da)
            cursor.moveToNext()
        }
        Log.v(TAG,"考勤历史查询成功")
        return data
    }
    /**
     * 查询考勤数据
     */
    fun query_data_by_classId(classId: String): ArrayList<ArrayList<StudentInfo>>{
        // 先查通过班级编号查找班级列表
        var list = this.query_signInList_by_classId(classId)
        var stuSign = arrayListOf<ArrayList<StudentInfo>>()
        for(s in list){
            stuSign.add(this.query_signInInfo_by_classId_and_no(s.classId!!,s.no!!))
        }
        return stuSign
    }

    /**
     * 查询课堂表现数据
     */
    fun query_gpadata_by_classId(classId: String): ArrayList<ArrayList<StudentInfo>>{
        // 先查通过班级编号查找班级列表
        var list = this.query_gpaList_by_classId(classId)
        var stuSign = arrayListOf<ArrayList<StudentInfo>>()
        for(s in list){
            stuSign.add(this.query_gpaInfo_by_classId_and_no(s.classId!!,s.no!!))
        }
        return stuSign
    }


    /**
     * 查询实验表现数据
     */
    fun query_testdata_by_classId(classId: String): ArrayList<ArrayList<StudentInfo>>{
        // 先查通过班级编号查找班级列表
        var list = this.query_testList_by_classId(classId)
        var stuSign = arrayListOf<ArrayList<StudentInfo>>()
        for(s in list){
            stuSign.add(this.query_testInfo_by_classId_and_no(s.classId!!,s.no!!))
        }
        return stuSign
    }

    /**
     * // 添加人脸信息
     */
    fun insertFace(classId: String,stuId: String, face: AFR_FSDKFace, no: Int) {
        var db = writableDatabase
        var cv = ContentValues()
        when(no){
            1 -> cv.put(StuInfoCell[5], face.featureData)
            2 -> cv.put(StuInfoCell[6], face.featureData)
            3 -> cv.put(StuInfoCell[7], face.featureData)
        }
        db.update(STUINFO, cv, "stuId = ? and classId = ?", arrayOf(stuId,classId))
        Log.v(TAG,"insert face success!!!")


    }


    /**
     * 查询人脸信息
     */
    fun queryFaces(classId: String): ArrayList<FaceRegist> {
        var sql = "select * from ${STUINFO} where classId = ? ;"
        var db = readableDatabase
        var data = ArrayList<FaceRegist>()
        var cursor = db.rawQuery(sql, arrayOf(classId))
        cursor.moveToFirst()
        while(!cursor.isAfterLast){
            var da = FaceRegist(cursor.getString(1),cursor.getString(2))

            //da.classId = cursor.getString(3) // classId 班级编号
            var face: AFR_FSDKFace? = null
            var fd = cursor.getBlob(5)
            if(fd != null){
                face =  AFR_FSDKFace()
                face.featureData = fd
                da.face1 = face
                da.isNoFace = false
            }
            fd = cursor.getBlob(6)
            if(fd != null){
                face =  AFR_FSDKFace()
                face.featureData = fd
                da.face2 = face
                da.isNoFace  = false
            }
            fd = cursor.getBlob(7)
            if(fd != null){
                face =  AFR_FSDKFace()
                face.featureData = fd
                da.face3 = face
                da.isNoFace = false
            }
            if(!da.isNoFace) {
                // 如果有脸部信息，添加
                data.add(da)
            }
            cursor.moveToNext()
        }

        return data
    }

    fun queryFaces(classId: String,stuId: String): FaceRegist? {
        var sql = "select * from ${STUINFO} where classId = ? and stuId = ? ;"
        var db = readableDatabase
        var cursor = db.rawQuery(sql, arrayOf(classId,stuId))
        cursor.moveToFirst()
        while(!cursor.isAfterLast){
            var da = FaceRegist(cursor.getString(1),cursor.getString(2))
            var face: AFR_FSDKFace? = null
            var fd = cursor.getBlob(5)
            if(fd != null){
                face =  AFR_FSDKFace()
                face.featureData = fd
                da.face1 = face
                da.isNoFace = false
            }
            fd = cursor.getBlob(6)
            if(fd != null){
                face =  AFR_FSDKFace()
                face.featureData = fd
                da.face2 = face
                da.isNoFace  = false
            }
            fd = cursor.getBlob(7)
            if(fd != null){
                face =  AFR_FSDKFace()
                face.featureData = fd
                da.face3 = face
                da.isNoFace = false
            }
            if(!da.isNoFace) {
                // 如果有脸部信息，添加
                return da
            }
        }
        return null
    }

    fun query_stuInfo(): ArrayList<StudentInfo> {
        var sql = "select * from ${STUINFO} ;"
        var db = readableDatabase
        var data = ArrayList<StudentInfo>()
        var cursor = db.rawQuery(sql,null)
        cursor.moveToFirst()
        while(!cursor.isAfterLast){
            var da = StudentInfo()
            da.stuId = cursor.getString(1) // stuId 学生学号
            da.name = cursor.getString(2) // name 姓名
            da.classId = cursor.getString(3) // classId 班级编号
            da.time = cursor.getString(4) // time
            da.face1 = cursor.getBlob(5)
            da.face2 = cursor.getBlob(6)
            da.face3 = cursor.getBlob(7)
            data.add(da)
            cursor.moveToNext()
        }

        return data
    }

    /**
     * 查询全部学生考勤记录表
     */
    fun query_signInList(): ArrayList<StuSignInList>{
        var sql = "select * from ${STUSIGNINLIST} ;"
        var db = readableDatabase
        var data = ArrayList<StuSignInList>()
        var cursor = db.rawQuery(sql, null)
        cursor.moveToFirst()
        while(!cursor.isAfterLast){
            var da = StuSignInList()
            da.classId = cursor.getString(1) // classId 班级编号
            da.time = cursor.getString(2) // time 时间
            da.num = cursor.getInt(3) // num 当天次数
            da.info = cursor.getString(4) // info 考勤备注
            data.add(da)
            cursor.moveToNext()
        }
        Log.v(TAG,"考勤记录表查询成功")
        return data
    }


    fun query_signInInfo() : ArrayList<StuSignInInfo>{
        var sql = "select * from ${STUSIGNININFO} ;"
        var db = readableDatabase
        var data = ArrayList<StuSignInInfo>()
        var cursor = db.rawQuery(sql, null)
        cursor.moveToFirst()
        while(!cursor.isAfterLast){
            var da = StuSignInInfo()
            da.stuId = cursor.getString(1) // stuid 学生编号
            da.classId = cursor.getString(2) // classId 班级编号
            da.type = cursor.getString(3) // type 考勤信息
            da.no = cursor.getString(4) // no 考勤编号
            data.add(da)
            cursor.moveToNext()
        }
        Log.v(TAG,"考勤详情表查询成功")
        return data
    }

    fun queryFaces(): ArrayList<FaceData> {
        val faces = ArrayList<FaceData>()
        var sql = "select * from ${STUINFO};"
        var db = readableDatabase
        var cursor = db.rawQuery(sql, null)
        cursor.moveToFirst()
        while(!cursor.isAfterLast){
            var da = FaceData(cursor.getString(1),cursor.getString(2))
            da.classId = cursor.getString(3)

            var fd = cursor.getBlob(cursor.getColumnIndex("face1"))
            if(fd != null){
                da.face1 = fd
                da.isNoFace = false
            }
            fd = cursor.getBlob(6)
            if(fd != null){
                da.face1 = fd
                da.isNoFace  = false
            }
            fd = cursor.getBlob(7)
            if(fd != null){
                da.face1 = fd
                da.isNoFace = false
            }
            if(!da.isNoFace) {
                // 如果有脸部信息，添加
                faces.add(da)
            }
        }
        Log.v(TAG,"查找到脸部数据")
        return faces
    }

    fun insertStuInfos(stuInfos: ArrayList<StudentInfo>){
        var db = writableDatabase
        for(stuInfo in stuInfos){
            var cv = ContentValues()
            cv.put(StuInfoCell[1],stuInfo.stuId)
            cv.put(StuInfoCell[2],stuInfo.name)
            cv.put(StuInfoCell[3],stuInfo.classId)
            cv.put(StuInfoCell[4],stuInfo.time)
            db.insert(STUINFO,null,cv)
        }
    }

    fun insertClassInfo(classInfos: ArrayList<ClassInfo>) {
        var db = writableDatabase
        for(classInfo in classInfos) {
            var cv = ContentValues()
            cv.put(ClassInfoCell[1], classInfo.classId)
            cv.put(ClassInfoCell[2], classInfo.className)
            cv.put(ClassInfoCell[3], classInfo.info)
            cv.put(ClassInfoCell[4], classInfo.time)
            db.insert(CLASSINFO, null, cv)
        }

    }

    fun insertStuSignInList(signInList: ArrayList<StuSignInList>) {

        var db = writableDatabase
        for(signIn in signInList) {
            var cv = ContentValues()
            cv.put(StuSignInListCell[1], signIn.classId)
            cv.put(StuSignInListCell[2], signIn.time)
            cv.put(StuSignInListCell[3], signIn.num)
            cv.put(StuSignInListCell[4], signIn.info)
            db.insert(STUSIGNINLIST, null, cv)
        }
        Log.v(TAG,"考勤记录表插入成功")
    }

    fun insertStuSignINInfo(signInInfos: ArrayList<StuSignInInfo>) {

        for(signInInfo in signInInfos) {
            insert_stuSignInInfo(signInInfo)
        }
    }

    /**
     * 通过班级id获取班级总人数
     */
    fun getStuNumByClassId(classId: String): String{
        val sql = "select count(${StuInfoCell[1]}) count from $STUINFO where ${StuInfoCell[3]} = ?"
        var db = readableDatabase
        var data = "0"
        var cursor = db.rawQuery(sql, arrayOf(classId))
        cursor.moveToFirst()
        if(!cursor.isAfterLast){
            data = cursor.getString(0) //

        }
        Log.v(TAG,"班级人数查询成功")
        return data
    }

    /**
     * 查询某个状态的人数和时间
     */
    fun query_cqInfo(classId: String,type: String = "出勤") : ArrayList<CqInfo>{
        var sql = "select count(stuId) count, substr(${StuSignInInfoCell[4]},1,10) time from $STUSIGNININFO where ${StuSignInInfoCell[2]} = ? and ${StuSignInInfoCell[3]} = ? group by substr(${StuSignInInfoCell[4]},1,10)  ;"
        var db = readableDatabase
        var data = ArrayList<CqInfo>()
        var cursor = db.rawQuery(sql, arrayOf(classId,type))
        cursor.moveToFirst()
        while(!cursor.isAfterLast){
            var da = CqInfo(
                    cursor.getString(0),  // 出勤人数
                    cursor.getString(1), // 时间
                    classId
            )
            data.add(da)
            cursor.moveToNext()
        }
        Log.v(TAG,"出勤信息查询成功")
        return data
    }
//
//    /**
//     * 日考勤次数查询
//     */
//    fun getDNumByTime(classId: String,time: String): String{
//        val sql = "select count(*) count from $STUSIGNINLIST where ${StuSignInListCell[1]} = $classId and substr(${StuSignInListCell[2]},1,10) = ${time}"
//        var db = readableDatabase
//        var data = "0"
//        var cursor = db.rawQuery(sql, null)
//        cursor.moveToFirst()
//        if(!cursor.isAfterLast){
//            data = cursor.getString(0) //
//        }
//        Log.v(TAG,"日考勤次数查询成功")
//        return data
//    }

    fun cleanDataBase(){
        var db = writableDatabase

        db.delete(STUSIGNININFO,null,null)
        db.delete(STUSIGNINLIST,null,null)
        db.delete(STUINFO,null,null)
        db.delete(CLASSINFO,null,null)

    }
}