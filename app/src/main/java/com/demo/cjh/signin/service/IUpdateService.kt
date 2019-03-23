package com.demo.cjh.signin.service

import com.demo.cjh.signin.pojo.FaceRecord
import com.demo.cjh.signin.pojo.MaxAnchor
import com.demo.cjh.signin.pojo.Record
import com.demo.cjh.signin.pojo.Tables

/**
 * Created by CJH
 * on 2018/12/22
 * @date 2018/12/22
 * @author CJH
 */

interface IUpdateService{


    /**
     * 获取status小于9的所有数据
     */
    fun getAllStatusLessThan9(uId: String): Tables

    fun getFaceFileStatusLessThan9(): ArrayList<FaceRecord>

    fun upTables(tables: Tables)


    /**
     * 获取每张表的最大anchor
     */
    fun getAllMaxAnchor(uId: String): MaxAnchor

    fun downTables(tables: Tables)
}