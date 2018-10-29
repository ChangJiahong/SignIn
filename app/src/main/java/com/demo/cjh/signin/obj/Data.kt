package com.demo.cjh.signin.obj

import java.io.Serializable

/**
 * Created by CJH
 * on 2018/10/22
 */

/**
 * 出勤信息数据类
 */
data class CqInfo(var num: String = "",var time: String,var classId: String = "") : Serializable {
    // 出勤率
    var rate = 0.0
}
