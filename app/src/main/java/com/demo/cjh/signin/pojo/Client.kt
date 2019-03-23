package com.demo.cjh.signin.pojo

import java.io.Serializable

/**
 * @author CJH
 * on 2018/12/17
 */

/**
 * 客户端 时间戳
 */
open class Client : Serializable{


    var uId: String = ""

    /**
     * 客户端id
     */
    var cId: String = ""

    /**
     * 服务端时间戳
     */
    /**
     * 获取时间戳
     *
     * @return modified - 时间戳
     */
    /**
     * 设置时间戳
     *
     * @param modified 时间戳
     */
    var modified: Long? = null


    /**
     * 客户端表的值，
     * 0 本地新增
     * -1 标记删除
     * 1 本地更新
     * 9 已同步
     */
    var status: Int? = null

    /**
     * 客户端同步时间戳
     * 表示上次服务器同步过来的时间
     * 客户端不允许修改
     */
    var anchor: Long? = null

}
