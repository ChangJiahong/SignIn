package com.demo.cjh.signin.service.impl

import android.content.Context
import com.demo.cjh.signin.dao.*
import com.demo.cjh.signin.dao.impl.*
import com.demo.cjh.signin.pojo.Classes
import com.demo.cjh.signin.pojo.Type
import com.demo.cjh.signin.pojo.TypeKey
import com.demo.cjh.signin.service.IClassesService

import com.demo.cjh.signin.util.generateRefID
import com.demo.cjh.signin.util.getNow
import org.jetbrains.anko.collections.forEachWithIndex

/**
 * Created by CJH
 * on 2018/11/21
 */
class ClassesServiceImpl(private val context: Context) : IClassesService {

    private val classesDao: IClassesDao = ClassesDaoImpl(context)
    private val recordDao: IRecordDao = RecordDaoImpl(context)
    private val stuDao: IStuDao = StuDaoImpl(context)
    private val typeDao: ITypeDao = TypeDaoImpl(context)
    private val typeKeyDao: ITypeKeyDao = TypeKeyDaoImpl(context)

    /**
     * 新建班级
     */
    override fun saveClasses(className: String, info: String, institute: String, speciality: String): Classes? {
        val classes = Classes()
        classes.classId = generateRefID()
        classes.className = className
        classes.info = info
        classes.institute = institute
        classes.speciality = speciality
        classes.createTime = getNow()

        val re = classesDao.insert(classes)
        // 新建班级后默认创建3个type
        val type1 = Type("课堂考勤", "1", classes.classId, false)
        val type2 = Type("课堂表现", "2", classes.classId, true)
        val type3 = Type("实验表现", "3", classes.classId, true)
        val id1 = typeDao.insert(type1).toString()
        val id2 = typeDao.insert(type2).toString()
        val id3 = typeDao.insert(type3).toString()

        // typeKey 创建
        val status1 = arrayListOf("出勤","事假","病假","迟到","早退","旷课")
        val status2 = arrayListOf("优","良","中","差")
        status1.forEachWithIndex { i, s ->
            typeKeyDao.insert(TypeKey(typeId = id1,typeKey = s,weight = i))
        }
        status2.forEachWithIndex { i, s ->
            typeKeyDao.insert(TypeKey(typeId = id2,typeKey = s,weight = i))
            typeKeyDao.insert(TypeKey(typeId = id3,typeKey = s,weight = i))
        }

        return if (re > 0) {
            classes
        } else null
    }

    override fun deleteById(id: String): Boolean {
        val re = classesDao.deleteById(id)
        return re > 0
    }

    override fun update(classes: Classes): Boolean {
        val re = classesDao.update(classes)
        return re > 0
    }

    override fun getClassesById(id: String): Classes? {

        return classesDao.getById(id)
    }

    override fun getAllClasses(): List<Classes> {
        return classesDao.all()
    }

    /**
     * 删除班级所有信息
     * @param classId
     * @return
     */
    override fun deleteClassesByClassId(classId: String): Boolean {

        // 班级的记录
        val re1 = recordDao.deleteByClassId(classId)

        // 班级的学生
        val re2 = stuDao.deleteByClassId(classId)

        // 班级基本信息
        val re3 = classesDao.deleteByClassId(classId)

        // 获取班级的所有type
        val types = typeDao.getTypesByClassId(classId)

        // 遍历删除
        types.forEach {
            // typeKey 信息
            val re5 = typeKeyDao.deleteById(it.cId)
        }

        // 删除type
        // type 信息
        val re4 = typeDao.deleteByClassId(classId)


        return true
    }


}
