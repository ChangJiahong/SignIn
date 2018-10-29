package com.demo.cjh.signin.util

import android.content.Context
import android.graphics.*
import android.util.Log
import com.demo.cjh.signin.obj.*
import com.guo.android_extend.java.ExtByteArrayOutputStream
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.DateUtil
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.experimental.and


/**
 * Created by CJH
 * on 2018/6/3
 */

/**
 * 扩展函数文件
 */


/**
 * excel列扩展，获得单元格内容
 */
fun Cell.getValue() = when(this.cellType){
    Cell.CELL_TYPE_BOOLEAN -> this.getBooleanCellValue().toString()
    Cell.CELL_TYPE_NUMERIC -> {
        var str = ""
        if(DateUtil.isCellDateFormatted(this)) {
            //  如果是date类型则 ，获取该cell的date值
            str = SimpleDateFormat("yyyy-MM-dd").format(DateUtil.getJavaDate(this.getNumericCellValue()))
            Log.v("DATE123",str)
        } else { // 纯数字
            str = this.getNumericCellValue().toString()
        }
        Log.v("DATE123",str)
        //this.getNumericCellValue().toString()
        str
    }
    else -> this.getStringCellValue().toString()
}
/**
 * 文件扩展名
 */
fun File.getSuffex() = this.name.substringAfterLast(".")
/**
 * 文件所在文件夹路径
 */
fun File.getDirPath() = this.path.substringBeforeLast("/")
/**
 * 文件全名 加扩展名
 */
fun File.getFullName() = this.path.substringAfterLast("/")
/**
 * 文件名 没有扩展名
 */
fun File.getFileName() = this.getFullName().substringBeforeLast(".")

/**
 * 文件最后访问时间
 */
fun File.getLastTime() = SimpleDateFormat("yyyy-MM-dd hh:mm").format(Date(this.lastModified()))


fun getNow(): String{
    var df = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
    var nowTime = df.format(Date()) // 当前系统时间 | 年月天
    return nowTime
}

fun getNow(str: String): String{
    var df = SimpleDateFormat(str)
    var nowTime = df.format(Date()) // 当前系统时间 | 年月天
    return nowTime
}

val Context.database: MyDatabaseOpenHelper
    get() = MyDatabaseOpenHelper.getInstence(applicationContext)


/**
 * 生成班级编号
 */
fun generateRefID(): String {
    val now = Date()
    val sdf = SimpleDateFormat("yyyyMMddHHmmssSSS")
    val prop = Properties(System.getProperties())
    return sdf.format(now)
}


fun NV21_to_bitmap(data: ByteArray): Bitmap? {
    val yuv = YuvImage(data, ImageFormat.NV21, 100 , 100, null)
    val ops = ExtByteArrayOutputStream()
    yuv.compressToJpeg(Rect(0, 0, yuv.getWidth(), yuv.getHeight()), 80, ops)
    val bmp = BitmapFactory.decodeByteArray(ops.byteArray, 0, ops.byteArray.size)
    return bmp

}

fun getreslut(jsonString: String): Result{
    val jsonObject = JSONObject(jsonString)

    return Result(jsonObject.getInt("status"),jsonObject.getString("message"),jsonObject.getString("data"))
}

fun classInfoDateToJson(classInfoData: ArrayList<ClassInfo>) : JSONArray {
        //把一个集合转换成json格式的字符串
    var jsonObject = JSONObject()
    var jsonArray = JSONArray()
    for(classinfo in classInfoData){
        var jo = JSONObject().apply {
            put("classId",classinfo.classId)
            put("className",classinfo.className)
            put("info",classinfo.info)
            put("time",classinfo.time)
        }
        jsonArray.put(jo)
    }
    jsonObject.apply {
        put("classInfos",jsonArray)
    }

    var jsonString = jsonArray.toString()



    Log.i("Other", "classInfos转换成json字符串: $jsonString")
    return jsonArray
}

fun JsonToClassInfoDate(jsonArray: JSONArray) : ArrayList<ClassInfo> {
    //把一个josn转换成集合格式的字符串
    val classInfos = ArrayList<ClassInfo>()

    for(i in 0 until jsonArray.length()){
        var jo = jsonArray.getJSONObject(i)
        var classInfo = ClassInfo().apply {
            classId = jo.getString("classId")
            className = jo.getString("className")
            info = jo.getString("info")
            time = jo.getString("time")
        }
        classInfos.add(classInfo)
    }
    return classInfos
}

fun stuInfoDataToJson(stuInfos: ArrayList<StudentInfo>): JSONArray {
    var jsonObject = JSONObject()
    var jsonArray = JSONArray()
    for(stuInfo in stuInfos){
        var jo = JSONObject().apply {
            put("stuId",stuInfo.stuId)
            put("name",stuInfo.name)
            put("classId",stuInfo.classId)
            put("time",stuInfo.time)
            //put("face2",String(stuInfo.face2?:"null".toByteArray(charset("ISO-8859-1")), charset("ISO-8859-1")))
            //put("face3",String(stuInfo.face3?:"null".toByteArray(charset("ISO-8859-1")), charset("ISO-8859-1")))
        }
        jsonArray.put(jo)
    }
    jsonObject.apply {
        put("stuInfos",jsonArray)
    }

    var jsonString = jsonArray.toString()
    Log.i("Other", "stuInfos转换成json字符串: $jsonString")

    return jsonArray
}


fun JsonToStuInfoData(jsonArray: JSONArray) : ArrayList<StudentInfo> {
    //把一个josn转换成集合格式的字符串
    val stuInfos = ArrayList<StudentInfo>()

    for(i in 0 until jsonArray.length()){
        var jo = jsonArray.getJSONObject(i)
        var stuInfo = StudentInfo().apply {
            classId = jo.getString("classId")
            stuId = jo.getString("stuId")
            name = jo.getString("name")
            time = jo.getString("time")
        }
        stuInfos.add(stuInfo)
    }
    return stuInfos
}

/**
 * 格式化byte
 *
 * @param b
 * @return
 */
fun bytesToHexString(src: ByteArray?): StringBuilder? {
    val stringBuilder = StringBuilder("")
    if (src == null || src.size <= 0) {
        return null
    }
    for (i in src.indices) {
        val v = (src[i] and 0xFF.toByte()).toInt()
        val hv = Integer.toHexString(v)
        if (hv.length < 2) {
            stringBuilder.append(0)
        }
        stringBuilder.append(hv)
    }
    return stringBuilder
}

fun signInListDataToJson(signInList: ArrayList<StuSignInList>): JSONArray {
    var jsonObject = JSONObject()
    var jsonArray = JSONArray()
    for(signIn in signInList){
        var jo = JSONObject().apply {
            put("classId",signIn.classId)
            put("num",signIn.num)
            put("info",signIn.info)
            put("time",signIn.time)
        }
        jsonArray.put(jo)
    }
    jsonObject.apply {
        put("signInList",jsonArray)
    }

    var jsonString = jsonArray.toString()
    Log.i("Other", "signInList转换成json字符串: $jsonString")

    return jsonArray
}


fun JsonToSignInListData(jsonArray: JSONArray) : ArrayList<StuSignInList> {
    //把一个josn转换成集合格式的字符串
    val signInList = ArrayList<StuSignInList>()

    for(i in 0 until jsonArray.length()){
        var jo = jsonArray.getJSONObject(i)
        var signIn = StuSignInList().apply {
            classId = jo.getString("classId")
            num = jo.getInt("num")
            info = jo.getString("info")
            time = jo.getString("time")
        }
        signInList.add(signIn)
    }
    return signInList
}

fun signInInfoDataToJson( signInInfo: ArrayList<StuSignInInfo>): JSONArray {
    var jsonObject = JSONObject()
    var jsonArray = JSONArray()
    for(signInInfo in signInInfo){
        var jo = JSONObject().apply {
            put("stuId",signInInfo.stuId)
            put("classId",signInInfo.classId)
            put("type",signInInfo.type)
            put("no",signInInfo.no)
        }
        jsonArray.put(jo)
    }
    jsonObject.apply {
        put("signInInfos",jsonArray)
    }

    var jsonString = jsonArray.toString()
    Log.i("Other", "signInInfos转换成json字符串: $jsonString")

    return jsonArray
}

fun JsonToSignInInfoData(jsonArray: JSONArray) : ArrayList<StuSignInInfo> {
    //把一个josn转换成集合格式的字符串
    val signInInfos = ArrayList<StuSignInInfo>()

    for(i in 0 until jsonArray.length()){
        var jo = jsonArray.getJSONObject(i)
        var signInInfo = StuSignInInfo().apply {
            classId = jo.getString("classId")
            stuId = jo.getString("stuId")
            type = jo.getString("type")
            no = jo.getString("no")
        }
        signInInfos.add(signInInfo)
    }
    return signInInfos
}

fun signInDataToJson(uid: String,classInfos: JSONArray,stuInfos: JSONArray,signInList: JSONArray,signInInfos: JSONArray): String {
    var jsonObject = JSONObject().apply {
        put("uid",uid)
        put("classInfos",classInfos)
        put("stuInfos",stuInfos)
        put("signInList",signInList)
        put("signInInfos",signInInfos)
    }
    var jsonString = jsonObject.toString()
    Log.i("Other", "signInData转换成json字符串: $jsonString")

    return jsonString

}

fun JsonToSignInData(json : String){
    var jsonObject = JSONObject(json)
    val classInfoArray = jsonObject.getJSONArray("classInfos")
    val stuInfoArray = jsonObject.getJSONArray("stuInfos")
    val signInListArray = jsonObject.getJSONArray("signInList")
    val signInInfoArray = jsonObject.getJSONArray("signInInfos")

}