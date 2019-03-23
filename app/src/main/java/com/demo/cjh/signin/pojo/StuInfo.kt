package com.demo.cjh.signin.pojo

import java.io.Serializable
import java.sql.ClientInfoStatus

/**
 * Created by CJH
 * on 2018/11/25
 */
class StuInfo: Serializable {

    /**
     * 主键
     */
    var id = ""

    var stuId = ""
    var stuName = ""
    var status = "-1"
    var typeId = ""
    var classId = ""

    constructor(){}

    constructor(stu: Stu){
        this.stuId = stu.stuId
        this.stuName = stu.stuName
        this.classId = stu.classId
        this.status = "-1"
    }

    constructor(stuId: String, stuName: String, status: String, typeId: String, classId: String) {
        this.stuId = stuId
        this.stuName = stuName
        this.status = status
        this.typeId = typeId
        this.classId = classId
    }

    var face1 = ""

    var face2 = ""

    var face3 = ""


}