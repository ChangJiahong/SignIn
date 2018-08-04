package com.demo.cjh.signin

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.content.res.AssetManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * Created by CJH
 * on 2018/6/1
 */
object FileUtil {
    val TAG = "FileUtil"

    val appRootDirectory = Environment.getExternalStorageDirectory().absolutePath+"/SignIn"
    val fileDirectory = appRootDirectory + "/file_recv"
    val imageDirectory = appRootDirectory + "/images"
    val XLS = "xls"
    val XLSX = "xlsx"


    fun isSdCardExist() = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

    fun initFile(){

        // app文件文件夹创建
        var f = File(fileDirectory)
        if(!f.exists()){
            f.mkdirs()
        }

        f = File(imageDirectory)
        if(!f.exists()){
            f.mkdirs()
        }

        Log.v(TAG,"初始化文件夹："+ f.path)

    }

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


    // 创建一个临时目录，用于复制临时文件，如assets目录下的离线资源文件
    fun createTmpDir(context: Context): String {
        val sampleDir = "TTS"
        var tmpDir = "${appRootDirectory}/$sampleDir"
        if (!makeDir(tmpDir)) {
            tmpDir = context.getExternalFilesDir(sampleDir)!!.absolutePath
            if (!makeDir(sampleDir)) {
                throw RuntimeException("create model resources dir failed :$tmpDir")
            }
        }
        return tmpDir
    }

    fun fileCanRead(filename: String): Boolean {
        val f = File(filename)
        return f.canRead()
    }

    fun makeDir(dirPath: String): Boolean {
        val file = File(dirPath)
        return if (!file.exists()) {
            file.mkdirs()
        } else {
            true
        }
    }

    @Throws(IOException::class)
    fun copyFromAssets(assets: AssetManager, source: String, dest: String, isCover: Boolean) {
        val file = File(dest)
        if (isCover || !isCover && !file.exists()) {
            var ios: InputStream? = null
            var fos: FileOutputStream? = null
            try {
                var ios = assets.open(source)
                var fos = FileOutputStream(dest)
                val buffer = ByteArray(1024)
                var size = 0
                size = ios!!.read(buffer, 0,  1024)
                while ((size) >= 0) {
                    fos.write(buffer, 0, size)
                    size = ios.read(buffer, 0,  1024)
                }
            } finally {
                if (fos != null) {
                    try {
                        fos.close()
                    } finally {
                        if (ios != null) {
                            ios.close()
                        }
                    }
                }
            }
        }
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


    fun getRealPathFromURI(context: Context,contentUri: Uri): String? {
        var res: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(contentUri, proj, null, null, null)
        if (null != cursor && cursor.moveToFirst()) {
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            res = cursor.getString(column_index)
            cursor.close()
        }
        return res
    }

    /**
     * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
     */
    @SuppressLint("NewApi")
    fun getPath(context: Context, uri: Uri): String? {


        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT


        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]


                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().absolutePath + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {


                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))


                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]


                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }


                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])


                return getDataColumn(context, contentUri!!, selection, selectionArgs)
            }// MediaProvider
            // DownloadsProvider
        } else if ("content".equals(uri.getScheme(), ignoreCase = true)) {
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.getScheme(), ignoreCase = true)) {
            return uri.path
        }// File
        // MediaStore (and general)
        return null
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    fun getDataColumn(context: Context, uri: Uri, selection: String?,
                      selectionArgs: Array<String>?): String? {


        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)


        try {
            cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            if (cursor != null)
                cursor.close()
        }
        return null
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }


    /**
     * 获取bitmap
     * @param path
     * @return
     */
    fun decodeImage(path: String): Bitmap? {
        val res: Bitmap
        try {
            val exif = ExifInterface(path)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

            val op = BitmapFactory.Options()
            op.inSampleSize = 1
            op.inJustDecodeBounds = false
            //op.inMutable = true;
            res = BitmapFactory.decodeFile(path, op)
            //rotate and scale.
            val matrix = Matrix()

            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                matrix.postRotate(90f)
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                matrix.postRotate(180f)
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                matrix.postRotate(270f)
            }

            val temp = Bitmap.createBitmap(res, 0, 0, res.width, res.height, matrix, true)
            Log.d("com.arcsoft", "check target Image:" + temp.width + "X" + temp.height)

            if (temp != res) {
                res.recycle()
            }
            return temp
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }



}