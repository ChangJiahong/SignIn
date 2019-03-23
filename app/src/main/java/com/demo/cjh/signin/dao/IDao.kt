package com.demo.cjh.signin.dao

/**
 * Created by CJH
 * on 2018/11/22
 */
interface IDao<T> {
    /**
     * 增
     */
    fun insert(obj: T): Long

    /**
     * 删
     */
    fun deleteById(id: String): Int

    /**
     * 改
     */
    fun update(obj: T): Int

    /**
     * 查
     */
    fun getById(id: String,isTrue:Boolean = false): T?

    /**
     * 更新anchor
     */
    fun updateAnchorById(anchor: Long,cId: String): Int

    /**
     * 更新status
     */
    fun updateStatusById(status: Int,cId: String): Int

    /**
     * 更新status 和 anchor
     */
    fun updateStatusAndAnchorById(status: Int,anchor: Long,cId: String): Int


    /**
     * 获取status<9的未同步记录
     */
    fun getStatusLessThan9(): ArrayList<T>

    /**
     * 获取最大anchor
     */
    fun getMaxAnchor(): Long

    /**
     * 真删除
     */
    fun trueDeleteById(cId: String): Int
//
//    /**
//     * 根据id 获取真正的值
//     */
//    fun selectById(cId: String): T

}