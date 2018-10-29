package com.demo.cjh.signin.obj

import com.demo.cjh.signin.util.getNow
import java.io.Serializable

/**
 * 学生信息实例
 */
class StudentInfo : Serializable {

    constructor(){}

    constructor(stuId : String, name : String,  type : String, classId: String){
        this.stuId = stuId
        this.name = name
        this.type = type
        this.classId = classId
        this.time = getNow()
    }


    var stuId = ""
    var name = ""
    var type = ""
    var classId = ""
    var time = ""
    var no = ""

    var face1: ByteArray? = null

    var face2: ByteArray? = null

    var face3: ByteArray? = null

    fun toStuSignInInfo(): StuSignInInfo {
        return StuSignInInfo(stuId, classId, type, no)
    }
}
