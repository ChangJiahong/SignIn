package com.demo.cjh.signin.`object`

import com.arcsoft.facerecognition.AFR_FSDKFace
import java.util.ArrayList

/**
 * Created by CJH
 * on 2018/8/2
 */
class FaceRegist (val stuId: String,val name: String){
    val TAG = "FaceRegist"

    var isNoFace = true
    // 人脸识别的脸部信息
    public var face1: AFR_FSDKFace? = null // 一个人三张脸部信息，保证识别精度
    public var face2: AFR_FSDKFace? = null
    public var face3: AFR_FSDKFace? = null
}