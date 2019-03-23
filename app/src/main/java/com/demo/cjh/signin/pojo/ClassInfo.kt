package com.demo.cjh.signin.pojo

/**
 * Created by CJH
 * on 2018/5/31
 */
/**
 * 班级信息实例
 */
class ClassInfo {
    val TAG = "ClassInfo"

    var className: String? = null
    var classId: String? = null
    var info: String? = null
    var time: String? = null

    constructor(){}

    constructor(classId: String, className: String, info: String){
        this.classId = classId
        this.className = className
        this.info = info
    }
}