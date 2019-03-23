package com.demo.cjh.signin.service.impl

import android.content.Context
import com.demo.cjh.signin.dao.ISubjectDao
import com.demo.cjh.signin.dao.impl.SubjectDaoImpl
import com.demo.cjh.signin.pojo.Subject
import com.demo.cjh.signin.service.IRecordSaveService
import com.demo.cjh.signin.service.IRecordService

/**
 * Created by CJH
 * on 2018/12/8
 * @date 2018/12/8
 * @author CJH
 */
class RecordSaveServiceImpl(private val context: Context) : IRecordSaveService {


    private val subjectDao: ISubjectDao = SubjectDaoImpl(context)

    /**
     * 获取id
     */
    override fun getIdByName(name: String): String {
        return subjectDao.getByName(name)
    }

    /**
     * 获取所有科目
     */
    override fun getAllSubject(): ArrayList<String> {
        val subs = subjectDao.getAll()
        val subss = ArrayList<String>()
        subs.forEach {
            subss.add(it.subject)
        }
        return subss
    }

    /**
     * 新建学科,返回主键
     */
    override fun createSubject(name: String): String {
        val sub = Subject()
        sub.subject = name
        return subjectDao.insert(sub).toString()
    }

    private val TAG = RecordSaveServiceImpl::class.java.name
}