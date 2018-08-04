package com.demo.cjh.signin.`object`

/**
 * Created by CJH
 * on 2018/6/29
 */
/**
 * 学生考勤详情实例
 */
class StuSignInInfo {
    val TAG = "StuSignInInfo"

    /**
     * 学号
     */
    var stuId: String? = null
    /**
     * 班级编号
     */
    var classId: String? = null
    /**
     * 考勤信息
     */
    var type: String? = null
    /**
     * 考勤编号
     */
    var no: String? = null

    constructor(){}

    constructor(stuId: String, classId: String, type: String,no: String){
        this.stuId = stuId
        this.classId = classId
        this.type = type
        this.no = no
    }



}