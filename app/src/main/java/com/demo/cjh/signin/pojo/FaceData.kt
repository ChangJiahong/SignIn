package com.demo.cjh.signin.pojo

/**
 * Created by CJH
 * on 2018/9/5
 */
class FaceData(val stuId: String,val name: String) {
    val TAG = "FaceData"


        var classId = ""
        var isNoFace = true
        // 人脸识别的脸部信息
        public var face1: ByteArray? = null // 一个人三张脸部信息，保证识别精度
        public var face2: ByteArray? = null
        public var face3: ByteArray? = null

}