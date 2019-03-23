package com.demo.cjh.signin.pojo

import org.json.JSONObject
import java.io.Serializable

/**
 * Created by CJH
 * on 2018/9/2
 */

class Result(status: Int,msg: String,data: Any): Serializable{
    var status = status
    var msg = msg
    var data: Any = data
}