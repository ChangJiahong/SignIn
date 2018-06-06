package com.demo.cjh.signin

import android.util.Log
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.DateUtil
import java.io.File
import java.text.SimpleDateFormat

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