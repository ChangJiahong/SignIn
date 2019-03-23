package com.demo.cjh.signin.service

import android.graphics.Bitmap
import com.arcsoft.facerecognition.AFR_FSDKFace
import com.demo.cjh.signin.pojo.FaceRegist
import com.demo.cjh.signin.pojo.Stu

/**
 * Created by CJH
 * on 2018/12/10
 * @date 2018/12/10
 * @author CJH
 */
/**
 * 人脸识别业务
 */
interface IFaceService {

    /**
     * 从库中加载数据
     */
    fun loadFacesByClassId(classId: String): ArrayList<FaceRegist>

    /**
     * @classId  班级编号
     * @stuId   学号
     * @face   人脸对象
     * @faceInt  脸信息的序号
     */
    fun insertFace(stu: Stu, face: AFR_FSDKFace, bmp: Bitmap, faceInt: Int)
}