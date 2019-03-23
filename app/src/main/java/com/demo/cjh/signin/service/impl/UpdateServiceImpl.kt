package com.demo.cjh.signin.service.impl

import android.content.Context
import com.demo.cjh.signin.dao.IDao
import com.demo.cjh.signin.dao.impl.*
import com.demo.cjh.signin.pojo.Client
import com.demo.cjh.signin.pojo.FaceRecord
import com.demo.cjh.signin.pojo.MaxAnchor
import com.demo.cjh.signin.pojo.Tables
import com.demo.cjh.signin.service.IUpdateService
import org.jetbrains.anko.collections.forEachWithIndex

/**
 * Created by CJH
 * on 2018/12/22
 * @date 2018/12/22
 * @author CJH
 */
class UpdateServiceImpl(private val context: Context) : IUpdateService{

    val classesDao = ClassesDaoImpl(context)

    val recordDao = RecordDaoImpl(context)

    val stuDao = StuDaoImpl(context)

    val subjectDao = SubjectDaoImpl(context)

    val typeDao = TypeDaoImpl(context)

    val typeKeyDao = TypeKeyDaoImpl(context)

    val faceRecordDao = FaceRecordDaoImpl(context)

    /**
     * 获取status小于9的所有数据
     */
    override fun getAllStatusLessThan9(uId: String): Tables {
        val tables = Tables(uId)

        tables.classesList = classesDao.getStatusLessThan9()
        tables.recordList = recordDao.getStatusLessThan9()
        tables.stuList = stuDao.getStatusLessThan9()
        tables.subjectList = subjectDao.getStatusLessThan9()
        tables.typeList = typeDao.getStatusLessThan9()
        tables.typeKeyList = typeKeyDao.getStatusLessThan9()
        tables.faceRecordList = faceRecordDao.getStatusLessThan9()

        return tables
    }

    override fun getFaceFileStatusLessThan9(): ArrayList<FaceRecord> {
        return faceRecordDao.getStatusLessThan9()
    }

    /**
     * 上行同步后后 更新时间戳
     */
    override fun upTables(tables: Tables) {

        upT(tables.classesList,classesDao)
        upT(tables.recordList,recordDao)
        upT(tables.stuList,stuDao)
        upT(tables.subjectList,subjectDao)
        upT(tables.typeList,typeDao)
        upT(tables.typeKeyList,typeKeyDao)

        // 人脸数据表
        // TODO: 进行人脸数据同步
//        val faceRecordList = tables.faceRecordList
//        if (faceRecordList.isNotEmpty()){
//            faceRecordList.forEachWithIndex{i,t->
//                if (t.status != -1){
//                    faceRecordDao.updateStatusAndAnchorById(status = t.status!!, anchor = t.anchor!!, cId = t.cId)
//                }else{
//                    // 物理删除
//                }
//
//            }
//        }

        // 人脸数据的同步
//        upT(tables.faceRecordList,faceRecordDao)



    }

    private fun <T : Client>  upT(list: List<T>,dao: IDao<T>){
        if (list.isNotEmpty()){
            list.forEachWithIndex { i, t ->
                if (t.status != -1) {
                    //
                    dao.updateStatusAndAnchorById(status = t.status!!, anchor = t.anchor!!, cId = t.cId)
                }else{
                    // 如果status = -1 则物理删除
                    dao.trueDeleteById(t.cId)
                }
            }
        }
    }


    /**
     * 获取每张表的最大anchor
     */
    override fun getAllMaxAnchor(uId: String): MaxAnchor {
        val maxAnchor = MaxAnchor(uId)
        maxAnchor.classesMaxAnchor = classesDao.getMaxAnchor()
        maxAnchor.recordMaxAnchor = recordDao.getMaxAnchor()
        maxAnchor.stuMaxAnchor = stuDao.getMaxAnchor()
        maxAnchor.subjectMaxAnchor = subjectDao.getMaxAnchor()
        maxAnchor.typeMaxAnchor = typeDao.getMaxAnchor()
        maxAnchor.typeKeyMaxAnchor = typeKeyDao.getMaxAnchor()


        return maxAnchor

    }

    /**
     * 下行同步
     */
    override fun downTables(tables: Tables) {

        // 班级表
        doT(tables.classesList,classesDao)
        // 记录表
        doT(tables.recordList,recordDao)
        // 学生表
        doT(tables.stuList,stuDao)
        // 学科表
        doT(tables.subjectList,subjectDao)
        // 类型表
        doT(tables.typeList,typeDao)
        // 类型值
        doT(tables.typeKeyList,typeKeyDao)
        // 人脸数据表
        // TODO: 进行人脸数据同步


    }

    private fun <T : Client>  doT(list: List<T>,dao: IDao<T>){
        if (list.isNotEmpty()){
            list.forEachWithIndex { i, t ->

                val ts = dao.getById(t.cId,true)
                if (ts != null) {
                    // 存在 判断本地status是否==9  进行更新
                    if (ts.status == 9){
                        // 已同步的数据 可更新
                        // 开始更新来自服务器的数据
                        if (t.modified != null) {
                            // 更新数据
                            dao.update(t)
                            // 更新时间戳
                            dao.updateStatusAndAnchorById(9, t.modified!!, t.cId)
                        }

                    }else{
                        // 本地发生修改
                        // 产生同步冲突，忽略操作
                    }
                }else{
                    // 不存在，直接插入
                    if (t.modified != null) {
                        dao.insert(t)
                        dao.updateStatusAndAnchorById(9, t.modified!!, t.cId)
                    }
                }
            }
        }
    }

    private val TAG = UpdateServiceImpl::class.java.name
}