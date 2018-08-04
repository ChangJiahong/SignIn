package com.demo.cjh.signin.util

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.util.Log
import com.arcsoft.facerecognition.AFR_FSDKFace
import com.guo.android_extend.java.ExtByteArrayOutputStream
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.DateUtil
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributeView
import java.nio.file.attribute.BasicFileAttributes


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