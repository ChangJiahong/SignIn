package com.demo.cjh.signin.service.impl

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.arcsoft.facerecognition.AFR_FSDKFace
import com.demo.cjh.signin.dao.IFaceRecordDao
import com.demo.cjh.signin.dao.IStuDao
import com.demo.cjh.signin.dao.impl.FaceRecordDaoImpl
import com.demo.cjh.signin.dao.impl.StuDaoImpl
import com.demo.cjh.signin.pojo.FaceRecord
import com.demo.cjh.signin.pojo.FaceRegist
import com.demo.cjh.signin.pojo.Stu
import com.demo.cjh.signin.service.IFaceService
import com.demo.cjh.signin.util.*
import com.guo.android_extend.java.ExtInputStream
import com.guo.android_extend.java.ExtOutputStream
import java.io.*

/**
 * Created by CJH
 * on 2018/12/11
 * @date 2018/12/11
 * @author CJH
 */
class FaceServiceImpl(private val context: Context) : IFaceService {

    private val stuDao: IStuDao = StuDaoImpl(context = context)

    private val faceRecordDao: IFaceRecordDao = FaceRecordDaoImpl(context)

    /**
     * 内部路径
     */
    val facesDirPath = context.filesDir.absolutePath + "/facesData"

    /**
     * 从库中加载数据
     */
    override fun loadFacesByClassId(classId: String): ArrayList<FaceRegist> {
        val faceRegists = ArrayList<FaceRegist>()
        val stus = stuDao.getStusByClassId(classId = classId)

        Log.d(TAG,"stu size: ${stus.size}")

        var face: AFR_FSDKFace
        // 拼装路径 /班级编号/facesData/学号/1、2、3.data
        var faceFileRootPath = FileUtil.getFacesDataPath(context,classId)

        Log.d(TAG,"faceFilePath : $faceFileRootPath")

        for( stu in stus){

            val faceRegist = FaceRegist(stu.stuId,stu.stuName)

            val faceFilePath = faceFileRootPath + "/" + stu.stuId

            if (!stu.stuFace1.isNullOrEmpty()){
                val file = File("$faceFilePath/1.data")
                if (file.exists()) {
                    val fs = FileInputStream(file)
                    val bos = ExtInputStream(fs)
                    face = AFR_FSDKFace()
                    face.featureData = bos.readBytes()
                    faceRegist.face1 = face
                    faceRegist.isNoFace = false
                }
            }
            if (!stu.stuFace2.isNullOrEmpty()){
                val file = File("$faceFilePath/2.data")
                if (file.exists()) {
                    val fs = FileInputStream(file)
                    val bos = ExtInputStream(fs)
                    face = AFR_FSDKFace()
                    face.featureData = bos.readBytes()
                    faceRegist.face2 = face
                    faceRegist.isNoFace = false
                }
            }
            if (!stu.stuFace3.isNullOrEmpty()){
                val file = File("$faceFilePath/3.data")
                if (file.exists()) {
                    val fs = FileInputStream(file)
                    val bos = ExtInputStream(fs)
                    face = AFR_FSDKFace()
                    face.featureData = bos.readBytes()
                    faceRegist.face3 = face
                    faceRegist.isNoFace = false
                }
            }

            if(!faceRegist.isNoFace){
                faceRegists.add(faceRegist)
            }
        }

        Log.d(TAG,"faceRegist size : ${faceRegists.size}")
        return faceRegists
    }

    /**
     * 添加人脸信息
     * @classId  班级编号
     * @stuId   学号
     * @face   人脸对象
     * @faceInt  脸信息的序号
     */
    override fun insertFace(stu: Stu, face: AFR_FSDKFace,bmp: Bitmap, faceInt: Int) {
        val faceRecord = FaceRecord()
        faceRecord.sId = stu.stuId

        // 拼装路径 /班级编号/facesData/学号/1、2、3.data
        val faceFileDirPath = "${FileUtil.getFacesDataPath(context, stu.classId)}/${stu.stuId}"
        val fileDir = File(faceFileDirPath)
        // 不存在就创建
        if (!fileDir.exists()){
            fileDir.mkdirs()
        }
        val faceFilePath = "$faceFileDirPath/$faceInt.data"
        val file = File(faceFilePath)
        if (!file.exists()){
            file.createNewFile()
        }else{
            file.delete()
        }
        val bos = ExtOutputStream(FileOutputStream(file))
        // 写入数据
        bos.writeBytes(face.featureData)
        val pngFileP = "$faceFileDirPath/$faceInt.png"
        bmp.saveToPNG(pngFileP)
        when(faceInt){
            1 -> stu.stuFace1 = pngFileP
            2 -> stu.stuFace2 = pngFileP
            3 -> stu.stuFace3 = pngFileP
        }
        faceRecord.path = pngFileP
        faceRecord.createTime = getNow()
        // 开启事务
        TransactionManager(context).error {
            Log.d(TAG,"insert faceDB error: ${it.message}")
        }.success {
            Log.d(TAG,"insert faceDB successful")
        }.run {
            Log.d(TAG,"开始插入人脸数据")
            // 更新学生表
            stuDao.update(stu)
            // 更新人脸数据表
            faceRecordDao.insert(faceRecord)
            Log.d(TAG,"插入结束")
        }.start()

    }



    private val TAG = FaceServiceImpl::class.java.name
}