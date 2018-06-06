package com.demo.cjh.signin

import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import java.io.File

/**
 * Created by CJH
 * on 2018/6/1
 */
object FileUtil {
    val TAG = "FileUtil"

    val appRootDirectory = Environment.getExternalStorageDirectory().absolutePath+"/SignIn"
    val fileDirectory = appRootDirectory + "/file_recv"
    val XLS = "xls"
    val XLSX = "xlsx"


    fun isSdCardExist() = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

    fun getFileList(path: String, vararg name: String) : List<File>{
        var fileList = ArrayList<File>()
        if(isSdCardExist()){
            var directory = File(fileDirectory)
            if(!directory.exists()){
                directory.mkdirs()
            }else{
                for(file in directory.listFiles()){
                    if(file.isDirectory){
                        continue
                    }
                    // 获取文件扩展名
                    var suff = file.name.substringAfterLast(".")
                    // 文件有效标志
                    var isN = false
                    // 匹配是否是需要查找的文件
                    for(su in name){
                        if(suff == su) {
                            isN = true
                            break
                        }
                    }
                    // 是-->添加
                    if(isN){
                        fileList.add(file)
                    }

                    Log.v(TAG,file.name+"-->"+suff)

                }
            }
        }
        return fileList
    }


    fun getPath(uri: Uri) : String{
        //var projection = arrayOf(MediaStore.Video.Media.DATA}
        //var cursor = manage
        return ""
    }

    fun copy(path1: String,path2: String){


        // 自动复制文件到当前APP目录下
//                if(fIn.exists() && filePath != FileUtil.fileDirectory){
//
//                    if(FileUtil.isSdCardExist()){
//
//                        var inputStream = this.contentResolver.openInputStream(uri)
//                        var outputStream = FileOutputStream(fOut)
//                        var b = ByteArray(1024)
//                        while(inputStream.read(b) != -1){
//                            outputStream.write(b)
//                        }
//                        outputStream.flush()
//                        inputStream.close()
//                        outputStream.close()
//                        Log.v(TAG,"Copy successful!")
//                    }
//                }

    }
}