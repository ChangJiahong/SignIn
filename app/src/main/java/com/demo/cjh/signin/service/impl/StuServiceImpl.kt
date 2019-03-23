package com.demo.cjh.signin.service.impl

import android.content.Context
import com.demo.cjh.signin.dao.impl.StuDaoImpl
import com.demo.cjh.signin.pojo.Stu
import com.demo.cjh.signin.pojo.StuInfo
import com.demo.cjh.signin.service.IStuService
import com.demo.cjh.signin.util.getNow

/**
 * Created by CJH
 * on 2018/11/21
 */
class StuServiceImpl(private val context: Context) : IStuService {


    private val stuDao = StuDaoImpl(context)

    override fun getStuInfosByClassId(classId: String): ArrayList<StuInfo> {
        var stus = stuDao.getStusByClassId(classId)
        var stuInfos = ArrayList<StuInfo>()
        stus.forEach {
            stuInfos.add(StuInfo(it))
        }
        return stuInfos
    }

    /**
     * 获取学生数据
     */
    override fun getStusByClassId(classId: String): java.util.ArrayList<Stu> {
        return stuDao.getStusByClassId(classId)
    }

    /**
     * 获取单个学生数据
     */
    override fun getStuById(id: String): Stu? {
        return stuDao.getById(id = id)
    }

    override fun save(stus: ArrayList<Stu>): Int {
        stus.forEach {
            it.createTime = getNow()
        }
        return stuDao.save(stus)
    }
}
