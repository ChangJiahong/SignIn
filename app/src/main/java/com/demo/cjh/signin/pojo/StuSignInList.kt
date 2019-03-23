package com.demo.cjh.signin.pojo

/**
 * Created by CJH
 * on 2018/6/29
 */
/**
 * 学生考勤记录实例
 */
class StuSignInList {
    val TAG = "StuSignInList"

    /**
     * id
     */
    var id: String? = null
    /**
     * 班级编号
     */
    var classId: String? = null

    /**
     * 时间
     */
    var time: String? = null

    /**
     * 当天次数
     */
    var num: Int? = null

    /**
     * 考勤备注
     */
    var info: String? = null

    /**
     * 考勤编号 no
     */

    var no: String? = null

    constructor(){}

    constructor(classId: String,time: String,info: String?){
        this.classId = classId
        this.time = time
        this.info = info
    }
}