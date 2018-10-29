package com.demo.cjh.signin

import com.bin.david.form.annotation.SmartColumn
import com.demo.cjh.signin.util.getNow

/**
 * Created by CJH
 * on 2018/9/11
 */

class KaoQinTableRow {
    val TAG = "KaoQinTableRow"

    constructor(){}

    constructor(stuId : String, name : String,  type : String){
        this.stuId = stuId
        this.name = name
        when(type){
            "出勤" -> this.chuQin = type
            "病假" -> this.bingJia = type
            "事假" -> this.shiJia = type
            "旷课" -> this.kuangKe = type
        }
    }
    var stuId = ""
    var name = ""
    var chuQin = ""
    var bingJia = ""
    var shiJia = ""
    var kuangKe = ""

    var type = ""
}