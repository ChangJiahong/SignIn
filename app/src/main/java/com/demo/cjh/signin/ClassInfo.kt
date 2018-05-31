package com.demo.cjh.signin

/**
 * Created by CJH
 * on 2018/5/31
 */
class ClassInfo {
    val TAG = "ClassInfo"

    var className: String? = null
    var classId: String? = null
    var info: String? = null

    constructor(){}

    constructor(classId: String, className: String, info: String){
        this.classId = classId
        this.className = className
        this.info = info
    }
}