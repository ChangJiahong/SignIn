package com.demo.cjh.signin.dao

import com.demo.cjh.signin.pojo.FaceRecord

/**
 * Created by CJH
 * on 2019/1/14
 * @date 2019/1/14
 * @author CJH
 */
interface IFaceRecordDao : IDao<FaceRecord> {


    val TABLE_NAME: String
        get() = "facesData"
}